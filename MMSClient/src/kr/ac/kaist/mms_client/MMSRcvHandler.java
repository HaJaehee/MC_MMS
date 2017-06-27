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
*/
/* -------------------------------------------------------- */

import java.io.BufferedInputStream;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class MMSRcvHandler {
	HttpServer server = null;
	
	HttpReqHandler hrh = null;
	//OONI
	FileReqHandler frh = null;
	//OONI
	private static final String USER_AGENT = "MMSClient/0.5.0";
	private String TAG = "[MMSRcvHandler";
	private String clientMRN = null;
	
	MMSRcvHandler(int port) throws IOException{
		server = HttpServer.create(new InetSocketAddress(port), 0);
		hrh = new HttpReqHandler();
        server.createContext("/", hrh);
        if(MMSConfiguration.LOGGING)System.out.println(TAG+"Context \"/\" is created");
        server.setExecutor(null); // creates a default executor
        server.start();
	}

	MMSRcvHandler(int port, String context) throws IOException {
		server = HttpServer.create(new InetSocketAddress(port), 0);
		hrh = new HttpReqHandler();
		if (!context.startsWith("/")){
			context = "/" + context;
		}
		
        server.createContext(context, hrh);
        if(MMSConfiguration.LOGGING)System.out.println(TAG+"Context \""+context+"\" is created");
        server.setExecutor(null); // creates a default executor
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
        if(MMSConfiguration.LOGGING)System.out.println(TAG+"Context \""+fileDirectory+fileName+"\" is created");
        //OONI
        server.setExecutor(null); // creates a default executor
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
        if(MMSConfiguration.LOGGING)System.out.println(TAG+"Context \""+context+"\" is added");
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
        if(MMSConfiguration.LOGGING)System.out.println(TAG+"Context \""+fileDirectory+fileName+"\" is added");
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
    			if(MMSConfiguration.LOGGING)System.out.println(TAG+"set headerfield[");
    			for (Iterator keys = myHdr.keySet().iterator() ; keys.hasNext() ;) {
    				String key = (String) keys.next();
    				ArrayList<String> value = (ArrayList<String>) myHdr.get(key);
    				if(MMSConfiguration.LOGGING)System.out.println(key+":"+value);
    				resHdr.put(key, value);
    			}
    			if(MMSConfiguration.LOGGING)System.out.println("]");
    			
    		} 
            
            
            t.sendResponseHeaders(setResponseCode(), response.length());
            OutputStream os = t.getResponseBody();
            BufferedWriter wr = new BufferedWriter(
    				new OutputStreamWriter(os,Charset.forName("UTF-8")));
            wr.write(response);
            wr.flush();
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
    //OONI
    class FileReqHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	URI uri = t.getRequestURI();
        	String fileName = uri.toString();
        	if(MMSConfiguration.LOGGING)System.out.println(TAG+"File request: "+fileName);
        	
            fileName = System.getProperty("user.dir")+fileName.trim();
            File file = new File (fileName);
            byte [] bytearray  = new byte [(int)file.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            bis.read(bytearray, 0, bytearray.length);
            
            // ok, we are ready to send the response.
            t.sendResponseHeaders(200, file.length());
            OutputStream os = t.getResponseBody();
            os.write(bytearray,0,bytearray.length);
            os.flush();
            os.close();
            
            bis.close();
        }
    }
    //OONI end
 
}
