package com.kaist.ServiceConsumer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.Charset;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class MMSRcvHandler {
	public MyHandler mh;
	//OONI
	public GetHandler gh;
	//OONI
	public PollHandler ph;
	//HJH
	private final String USER_AGENT = "MMSClient/0.1";
	private String myMRN;
	
	public MMSRcvHandler(int port) throws IOException{
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		mh = new MyHandler();
        server.createContext("/", mh);
        //OONI
        gh = new GetHandler();
        server.createContext("/get", gh);
        //OONI
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	public MMSRcvHandler(String myMRN, String destMRN, int interval) throws IOException{
		ph = new PollHandler(myMRN, destMRN, interval);
		ph.start();
	}
    static class MyHandler implements HttpHandler {
    	com.kaist.ServiceConsumer.MMSClientHandler.reqCallBack myreqCallBack;
    	public void setReqCallBack(com.kaist.ServiceConsumer.MMSClientHandler.reqCallBack callback){
    		this.myreqCallBack = callback;
    	}
    	
        @Override
        public void handle(HttpExchange t) throws IOException {
        	//System.out.println("File request");
        	InputStream in = t.getRequestBody();
            ByteArrayOutputStream _out = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            int read = 0;
            while ((read = in.read(buf)) != -1) {
                _out.write(buf, 0, read);
            }
            //System.out.println(new String( buf, Charset.forName("UTF-8") ));
            String response = this.processRequest(new String( buf, Charset.forName("UTF-8")));
            //String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        private String processRequest(String data) {
    		String ret = this.myreqCallBack.callbackMethod(data);
    		return ret;
    	}
    }
    //OONI
    static class GetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
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
            os.close();
        }
    }
    //OONI
 
    //HJH
	public class PollHandler extends Thread{
		private int interval;
		private String myMRN;
		private String destMRN;
		com.kaist.ServiceConsumer.MMSClientHandler.reqCallBack myreqCallBack;
    	public void setReqCallBack(com.kaist.ServiceConsumer.MMSClientHandler.reqCallBack callback){
    		this.myreqCallBack = callback;
    	}
		
    	public PollHandler (String myMRN, String destMRN, int interval){
    		this.interval = interval;
    		this.myMRN = myMRN;
    		this.destMRN = destMRN;
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
    	
		public void Poll() throws Exception {
			
			String url = "http://"+MMSConfiguration.MMSURL+"/polling"; // MMS Server
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			//add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("srcMRN", myMRN);
			con.setRequestProperty("dstMRN", destMRN);

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			//System.out.println("\nSending 'POST' request to URL : " + url);
			//System.out.println("Polling...");
			//System.out.println("Response Code : " + responseCode);
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
			in.close();
			
			String res = response.toString();
			if (!res.equals("EMPTY")){
				processRequest(res);
			}else {
				//processRequest("");
			}
		}
		
		private String processRequest(String data) {
    		String ret = this.myreqCallBack.callbackMethod(data);
    		return ret;
    	}
	}
    //HJH
}
