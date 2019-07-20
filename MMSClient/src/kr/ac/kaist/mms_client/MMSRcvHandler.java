package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : MMSRcvHandler.java
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03


Rev. history : 2017-02-01
Version : 0.3.01
	Added setting header field features. 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-02-14
	fixed http get request bugs
	fixed http get file request bugs
	added setting context features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	Moved PollHandler class into MMSPollHandler.java
	
Rev. history : 2017-05-02
Version : 0.5.4
	Added setting response header
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-04-23
Version : 0.7.1
	Removed RESOURCE_LEAK hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	

Rev. history : 2018-07-27
Version : 0.7.2
	Revised setting header field function.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-04-29
Version : 0.8.2
	Revised Base64 Encoder/Decoder.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-22
Version : 0.9.1
	Add server stop function.
Modifier : Yunho Choi (choiking10@kaist.ac.kr)

Rev. history : 2019-07-21
Version : 0.9.4
	Moved write stream close() to the line before input stream close().
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


class MMSRcvHandler {
	HttpServer server = null;
	
	HttpReqHandler hrh = null;
	//OONI
	FileReqHandler frh = null;
	//OONI
	private static final String USER_AGENT = MMSConfiguration.USER_AGENT;
	private static final int NO_OF_THREADPOOL = 1;
	private ExecutorService serverExecutor;
	private String TAG = "[MMSRcvHandler";
	private String clientMRN = null;
	
	MMSRcvHandler(int port) throws IOException{
		server = HttpServer.create(new InetSocketAddress(port), 0);
		hrh = new HttpReqHandler();
        server.createContext("/", hrh);
        if(MMSConfiguration.DEBUG) {System.out.println(TAG+"Context \"/\" is created");}

        serverExecutor = Executors.newFixedThreadPool(NO_OF_THREADPOOL);
        server.setExecutor(serverExecutor); 
        server.start();
	}

	MMSRcvHandler(int port, String context) throws IOException {
		server = HttpServer.create(new InetSocketAddress(port), 0);
		hrh = new HttpReqHandler();
		if (!context.startsWith("/")){
			context = "/" + context;
		}
		
        server.createContext(context, hrh);
        if(MMSConfiguration.DEBUG) {System.out.println(TAG+"Context \""+context+"\" is created");}

        serverExecutor = Executors.newFixedThreadPool(NO_OF_THREADPOOL);
        server.setExecutor(serverExecutor); 
        server.start();
	}
	
	MMSRcvHandler(int port, String fileDirectory, String fileName) throws IOException {
		server = HttpServer.create(new InetSocketAddress(port), 0);
        //OONI
        frh = new FileReqHandler();
        if (!fileDirectory.startsWith("/")){
        	fileDirectory = "/" + fileDirectory;
		}
        if(!fileDirectory.endsWith("/")&&!fileName.startsWith("/")){
        	fileName = "/" + fileName;
        }
        if(fileDirectory.endsWith("/")&&fileName.startsWith("/")){
        	fileName = fileName.substring(1);
        }
        server.createContext(fileDirectory+fileName, frh);
        if(MMSConfiguration.DEBUG) {System.out.println(TAG+"Context \""+fileDirectory+fileName+"\" is created");}
        //OONI

        serverExecutor = Executors.newFixedThreadPool(NO_OF_THREADPOOL);
        server.setExecutor(serverExecutor); 
        server.start();
	}
	
	void addContext (String context) {
		if (server == null) {
			System.out.println(TAG+"Server is not created!");
			return;			
		}
		if (hrh == null) {
			hrh = new HttpReqHandler();
		}
		if (!context.startsWith("/")){
			context = "/" + context;
		}
        server.createContext(context, hrh);
        if(MMSConfiguration.DEBUG) {System.out.println(TAG+"Context \""+context+"\" is added");}
	}
	
	void addFileContext (String fileDirectory, String fileName) {
		if (server == null) {
			System.out.println(TAG+"Server is not created!");
			return;
		}
		if (frh == null) {
			frh = new FileReqHandler();
		}
        if (!fileDirectory.startsWith("/")){
        	fileDirectory = "/" + fileDirectory;
		}
        if(!fileDirectory.endsWith("/")&&!fileName.startsWith("/")){
        	fileName = "/" + fileName;
        }
        if(fileDirectory.endsWith("/")&&fileName.startsWith("/")){
        	fileName = fileName.substring(1);
        }
        server.createContext(fileDirectory+fileName, frh);
        if(MMSConfiguration.DEBUG) {System.out.println(TAG+"Context \""+fileDirectory+fileName+"\" is added");}
	}
	
	public void stopRcv(int arg0) {
		if (server == null) {
			System.out.println(TAG+"Server is not created!");
			return;
		}
		server.stop(0); 
		serverExecutor.shutdownNow();
	}
	class HttpReqHandler implements HttpHandler {
    	
    	MMSClientHandler.RequestCallback myReqCallback;
    	
    	public void setRequestCallback(MMSClientHandler.RequestCallback callback){
    		this.myReqCallback = callback;
    	}
    	
        @Override
        public void handle(HttpExchange t) throws IOException {
        	URI uri = t.getRequestURI();
        	List<String> uris = new ArrayList<String>();
        	uris.add(uri.toString());
        	String httpMethod = t.getRequestMethod();
        	List<String> httpMethods = new ArrayList<String>();
        	httpMethods.add(httpMethod);
        	
        	InputStream inB = t.getRequestBody();
        	Map<String,List<String>> inH = t.getRequestHeaders();
            ByteArrayOutputStream _out = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            int read = 0;
            while ((read = inB.read(buf)) != -1) {
                _out.write(buf, 0, read);
            }
            
            String receivedData = new String( _out.toByteArray(), Charset.forName("UTF-8")).trim();
            
            String message = receivedData;
            inH.put("Http-method", httpMethods);
            inH.put("Uri", uris);
            
            String response = this.processRequest(inH, message);
            Map<String,List<String>> myHdr = setResponseHeader();
            Map<String,List<String>> resHdr = t.getResponseHeaders();
            
    		if (myHdr != null) {
    			if(MMSConfiguration.DEBUG) {System.out.println(TAG+"set headerfield[");}
    			for (Iterator keys = myHdr.keySet().iterator() ; keys.hasNext() ;) {
    				String key = (String) keys.next();
    				List<String> valueList = (List<String>) myHdr.get(key);
    				for (String value : valueList) {
    					if(MMSConfiguration.DEBUG) {System.out.println(key+":"+value);}
    				}
    				resHdr.put(key, valueList);
    			}
    			if(MMSConfiguration.DEBUG) {System.out.println("]");}
    			
    		} 
            
            
            t.sendResponseHeaders(setResponseCode(), response.length());
            OutputStream os = t.getResponseBody();
            BufferedWriter wr = new BufferedWriter(
    				new OutputStreamWriter(os,Charset.forName("UTF-8")));
            wr.write(response);
            wr.flush();
            inB.close();
            wr.close();
            os.close();
        }
        
        private String processRequest(Map<String,List<String>> headerField, String message) {
    		return this.myReqCallback.respondToClient(headerField, message);
    	}
        
        private int setResponseCode() {
        	return this.myReqCallback.setResponseCode();
        }
        
        private Map<String,List<String>> setResponseHeader(){
        	return this.myReqCallback.setResponseHeader();
        }
    }
    //OONI created
	//Jaehee modified
    class FileReqHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	URI uri = null;
        	InputStream in = null;
        	ByteArrayOutputStream byteOutStream = null;
        	OutputStream os = null;
        	byte encodeBytes[] = null;
        	try {
	        	uri = t.getRequestURI();
	        	String fileName = uri.toString();
	        	if(MMSConfiguration.DEBUG) {System.out.println(TAG+"File request: "+fileName);}
	        	
	            fileName = System.getProperty("user.dir")+fileName.trim();
	            File file = new File (fileName);
	            Base64.Encoder base64Encoder = Base64.getEncoder();
	            in = new FileInputStream(file);
	
	            byteOutStream=new ByteArrayOutputStream();
	
	            int len=0;
	
	            byte[] buf = new byte[1024];
	
	            while((len=in.read(buf)) != -1){
	            	byteOutStream.write(buf, 0, len);
	            }
	
	            byte fileArray[]=byteOutStream.toByteArray();
	            encodeBytes=base64Encoder.encode(fileArray); 
        	} finally {
        		if (in != null) {
        			in.close();
        		}
        		if (byteOutStream != null) {
        			byteOutStream.close();
        		}
                // ok, we are ready to send the response.
        	}
        	try {
                t.sendResponseHeaders(200, encodeBytes.length);
                os = t.getResponseBody();
                if (encodeBytes != null) {
                	os.write(encodeBytes,0,encodeBytes.length);
                }
                os.flush();
        	} finally {
        		if (os != null) {
        			os.close();
        		}
        	}
            
        }
    }
    //OONI end
 
}
