package kr.ac.kaist.mms_client;

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
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class MMSRcvHandler {
	HttpReqHandler hrh;
	//OONI
	FileReqHandler frh;
	//OONI
	PollingHandler ph;
	//HJH
	private static final String USER_AGENT = "MMSClient/0.1";
	private String clientMRN;
	
	MMSRcvHandler(int port) throws IOException{
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		hrh = new HttpReqHandler();
        server.createContext("/", hrh);
        //OONI
        frh = new FileReqHandler();
        server.createContext("/get", frh);
        //OONI
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	MMSRcvHandler(String clientMRN, String dstMRN, int interval, int clientPort, int msgType, JSONObject headerField) throws IOException{
		ph = new PollingHandler(clientMRN, dstMRN, interval, clientPort, msgType, headerField);
		ph.start();
	}
	
	static class HttpReqHandler implements HttpHandler {
    	private MMSDataParser dataParser = new MMSDataParser();
    	MMSClientHandler.ReqCallBack myReqCallBack;
    	
    	public void setReqCallBack(MMSClientHandler.ReqCallBack callback){
    		this.myReqCallBack = callback;
    	}
    	
        @Override
        public void handle(HttpExchange t) throws IOException {
        	InputStream in = t.getRequestBody();
            ByteArrayOutputStream _out = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            int read = 0;
            while ((read = in.read(buf)) != -1) {
                _out.write(buf, 0, read);
            }
            if(MMSConfiguration.logging)System.out.println(new String( buf, Charset.forName("UTF-8") ));
            String receivedData = new String( buf, Charset.forName("UTF-8"));
            
//            System.out.println("rcvhandler: " + receivedData);
            
            ArrayList<MMSData> list = dataParser.processParsing(receivedData.trim());
            
//            String response = this.processRequest(receivedData.trim());
            String response = this.processRequest(list.get(0).getData());
            
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.flush();
            os.close();
        }
        private String processRequest(String data) {
    		String ret = this.myReqCallBack.callbackMethod(data);
    		return ret;
    	}
    }
    //OONI
    static class FileReqHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	if(MMSConfiguration.logging)System.out.println("File request");
        	InputStream in = t.getRequestBody();
            ByteArrayOutputStream _out = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            int read = 0;
            while ((read = in.read(buf)) != -1) {
                _out.write(buf, 0, read);
            }
            String fileName = new String( buf, Charset.forName("UTF-8"));
            fileName = fileName.trim();
            File file = new File (fileName);
            byte [] bytearray  = new byte [(int)file.length()];
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(bytearray, 0, bytearray.length);
            // ok, we are ready to send the response.
            t.sendResponseHeaders(200, file.length());
            OutputStream os = t.getResponseBody();
            os.write(bytearray,0,bytearray.length);
            os.flush();
            os.close();
        }
    }
    //OONI end
 
    //HJH
    static class PollingHandler extends Thread{
		private int interval;
		private String clientMRN;
		private String dstMRN;
		private int clientPort;
		private int msgType;
		private MMSDataParser dataParser;
		private JSONObject headerField;
		MMSClientHandler.ReqCallBack myReqCallBack;
		
    	PollingHandler (String clientMRN, String dstMRN, int interval, int clientPort, int msgType, JSONObject headerField){
    		this.interval = interval;
    		this.clientMRN = clientMRN;
    		this.dstMRN = dstMRN;
    		this.clientPort = clientPort;
    		this.msgType = msgType;
    		this.dataParser = new MMSDataParser();
    		this.headerField = headerField;
    	}
    	
    	void setReqCallBack(MMSClientHandler.ReqCallBack callback){
    		this.myReqCallBack = callback;
    	}
    	
    	public void run(){
    		while (true){
    			try{
	    			Thread.sleep(interval);
	    			Poll();
    			}catch (Exception e){
    				e.printStackTrace();
    			}
    		}
    	}
    	
		void Poll() throws Exception {
			
			String url = "http://"+MMSConfiguration.MMSURL+"/polling"; // MMS Server
			URL obj = new URL(url);
			String data = (clientPort + ":" + msgType); //To do: add geographical info, channel info, etc. 
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
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

//			System.out.println("MMSRcvHandler, polling message");
			
			// Send post request
			con.setDoOutput(true);
			BufferedWriter wr = new BufferedWriter(
					new OutputStreamWriter(con.getOutputStream(),Charset.forName("UTF-8")));
			wr.write(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			if(MMSConfiguration.logging)System.out.println("\nSending 'POST' request to URL : " + url);
			if(MMSConfiguration.logging)System.out.println("Polling...");
			if(MMSConfiguration.logging)System.out.println("Response Code : " + responseCode);
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream(),Charset.forName("UTF-8")));
			String inputLine;
			
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine.trim() + "\n");
			}
			
			
			in.close();
			
			String res = response.toString();
			if (!res.equals("EMPTY\n")){
				ArrayList<MMSData> list = dataParser.processParsing(res);
				
				for(int i = 0; i < list.size(); i++) {
					processRequest(list.get(i).getData());
				}
			} else {
				//processRequest(res);
			}
		}
		
		private String processRequest(String data) {
    		String ret = this.myReqCallBack.callbackMethod(data);
    		return ret;
    	}
	}
    //HJH end
}
