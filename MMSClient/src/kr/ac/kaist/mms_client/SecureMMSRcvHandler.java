package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : SecureMMSRcvHandler.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-21
Version : 0.4.0
*/
/* -------------------------------------------------------- */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;

public class SecureMMSRcvHandler {
	HttpsServer server = null;
	
	HttpsReqHandler hrh = null;
	//OONI
	SecureFileReqHandler frh = null;
	//OONI
	SecurePollingHandler ph = null;
	//HJH
	private static final String USER_AGENT = "MMSClient/0.4.0";
	private String clientMRN = null;
	
	SecureMMSRcvHandler(int port) throws IOException{
		server = HttpsServer.create(new InetSocketAddress(port), 0);
		hrh = new HttpsReqHandler();
        server.createContext("/", hrh);
        if(MMSConfiguration.LOGGING)System.out.println("Context \"/\" is created");
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	SecureMMSRcvHandler(String clientMRN, String dstMRN, int interval, int clientPort, int msgType, Map<String,String> headerField) throws IOException{
		ph = new SecurePollingHandler(clientMRN, dstMRN, interval, clientPort, msgType, headerField);
		if(MMSConfiguration.LOGGING)System.out.println("Polling handler is created");
		ph.start();
	}
	
	SecureMMSRcvHandler(int port, String context) throws IOException {
		server = HttpsServer.create(new InetSocketAddress(port), 0);
		hrh = new HttpsReqHandler();
		if (!context.startsWith("/")){
			context = "/" + context;
		}
		
        server.createContext(context, hrh);
        if(MMSConfiguration.LOGGING)System.out.println("Context \""+context+"\" is created");
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	SecureMMSRcvHandler(int port, String fileDirectory, String fileName) throws IOException {
		server = HttpsServer.create(new InetSocketAddress(port), 0);
        //OONI
        frh = new SecureFileReqHandler();
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
        if(MMSConfiguration.LOGGING)System.out.println("Context \""+fileDirectory+fileName+"\" is created");
        //OONI
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	void addContext (String context) {
		if (server == null) {
			System.out.println("Server is not created!");
			return;			
		}
		if (hrh == null) {
			hrh = new HttpsReqHandler();
		}
		if (!context.startsWith("/")){
			context = "/" + context;
		}
        server.createContext(context, hrh);
        if(MMSConfiguration.LOGGING)System.out.println("Context \""+context+"\" is added");
	}
	
	void addFileContext (String fileDirectory, String fileName) {
		if (server == null) {
			System.out.println("Server is not created!");
			return;
		}
		if (frh == null) {
			frh = new SecureFileReqHandler();
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
        if(MMSConfiguration.LOGGING)System.out.println("Context \""+fileDirectory+fileName+"\" is added");
	}
	
	class HttpsReqHandler implements HttpHandler {
    	private MMSDataParser dataParser = new MMSDataParser();
    	SecureMMSClientHandler.Callback myReqCallback;
    	
    	public void setReqCallback(SecureMMSClientHandler.Callback callback){
    		this.myReqCallback = callback;
    	}
    	
        @Override
        public void handle(HttpExchange t) throws IOException {
        	URI uri = t.getRequestURI();
        	String httpMethod = t.getRequestMethod();
        	
        	InputStream inB = t.getRequestBody();
        	Map<String,List<String>> inH = t.getRequestHeaders();
            ByteArrayOutputStream _out = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            int read = 0;
            while ((read = inB.read(buf)) != -1) {
                _out.write(buf, 0, read);
            }
            Iterator<String> iter = inH.keySet().iterator();
			while (iter.hasNext()){
				String key = iter.next();
			}
            String receivedData = new String( buf, Charset.forName("UTF-8")).trim();
            
            ArrayList<MMSData> list = null;
            if (receivedData!=null&&!receivedData.equals("")) {
            	list = dataParser.processParsing(receivedData);
            }
            
            String httpBody = (list!=null)?list.get(0).getData():"";
            httpBody = (httpBody.startsWith("{")||httpBody.startsWith("["))?httpBody:"\""+httpBody+"\"";
            
            String message = "{\"Request URI\":\""+uri.toString()+"\","+
							"\"HTTP Method\":\""+httpMethod+"\","+
							"\"HTTP Body\":"+httpBody+"}";
            String response = this.processRequest(inH, message);
            
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.flush();
            os.close();
        }
        
        private String processRequest(Map<String,List<String>> headerField, String message) {
    		String ret = this.myReqCallback.callbackMethod(headerField, message);
    		return ret;
    	}
    }
    //OONI
    class SecureFileReqHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	URI uri = t.getRequestURI();
        	String fileName = uri.toString();
        	if(MMSConfiguration.LOGGING)System.out.println("File request: "+fileName);
        	
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
 
    //HJH
    class SecurePollingHandler extends Thread{
		private int interval = 0;
		private String clientMRN = null;
		private String dstMRN = null;
		private int clientPort = 0;
		private int clientModel = 0;
		private MMSDataParser dataParser = null;
		private Map<String,String> headerField = null;
		SecureMMSClientHandler.Callback myCallback = null;
		
    	SecurePollingHandler (String clientMRN, String dstMRN, int interval, int clientPort, int clientModel, Map<String,String> headerField){
    		this.interval = interval;
    		this.clientMRN = clientMRN;
    		this.dstMRN = dstMRN;
    		this.clientPort = clientPort;
    		this.clientModel = clientModel;
    		this.dataParser = new MMSDataParser();
    		this.headerField = headerField;
    	}
    	
    	void setCallback(SecureMMSClientHandler.Callback callback){
    		this.myCallback = callback;
    	}
    	
    	public void run(){
    		while (true){
    			try{
	    			Thread.sleep(interval);
	    			Poll();
    			}catch (Exception e){
    				if(MMSConfiguration.LOGGING)e.printStackTrace();
    			}
    		}
    	}
    	
		void Poll() throws Exception {
			
			String url = "http://"+MMSConfiguration.MMS_URL+"/polling"; // MMS Server
			URL obj = new URL(url);
			String data = (clientPort + ":" + clientModel); //To do: add geographical info, channel info, etc. 
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			
			//add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Charset", "UTF-8");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("srcMRN", clientMRN);
			con.setRequestProperty("dstMRN", dstMRN);
			if (headerField != null) {
				for (Iterator keys = headerField.keySet().iterator() ; keys.hasNext() ;) {
					String key = (String) keys.next();
					String value = (String) headerField.get(key);
					con.setRequestProperty(key, value);
				}
			}
			String urlParameters = data;

			// Send post request
			con.setDoOutput(true);
			BufferedWriter wr = new BufferedWriter(
					new OutputStreamWriter(con.getOutputStream(),Charset.forName("UTF-8")));
			wr.write(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			if(MMSConfiguration.LOGGING)System.out.println("\nSending 'POST' request to URL : " + url);
			if(MMSConfiguration.LOGGING)System.out.println("Polling...");
			if(MMSConfiguration.LOGGING)System.out.println("Response Code : " + responseCode);
			
			Map<String,List<String>> inH = con.getHeaderFields();
			BufferedReader inB = new BufferedReader(
			        new InputStreamReader(con.getInputStream(),Charset.forName("UTF-8")));
			String inputLine;
			
			StringBuffer response = new StringBuffer();
			while ((inputLine = inB.readLine()) != null) {
				response.append(inputLine.trim() + "\n");
			}
			
			
			inB.close();
			
			String res = response.toString();
			if (!res.equals("EMPTY\n")){
				ArrayList<MMSData> list = dataParser.processParsing(res);
				
				for(int i = 0; i < list.size(); i++) {
					processRequest(inH, list.get(i).getData());
				}
			} else {
				//processRequest(res);
			}
		}
		
		private String processRequest(Map<String,List<String>> headerField, String message) {
    		String ret = this.myCallback.callbackMethod(headerField, message);
    		return ret;
    	}
	}
    //HJH end
}
