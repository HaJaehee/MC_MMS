package kr.ac.kaist.mms_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/* -------------------------------------------------------- */
/** 
File name : SecureMMSPollHandler.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-25
Version : 0.5.0
*/
/* -------------------------------------------------------- */

class SecureMMSPollHandler {

	SecurePollingHandler ph = null;
	//HJH
	private static final String USER_AGENT = "MMSClient/0.5.0";
	private String clientMRN = null;
	
	SecureMMSPollHandler(String clientMRN, String dstMRN, String svcMRN, int interval, int clientPort, int msgType, Map<String,String> headerField) throws IOException{
		ph = new SecurePollingHandler(clientMRN, dstMRN, svcMRN, interval, clientPort, msgType, headerField);
		if(MMSConfiguration.LOGGING)System.out.println("Polling handler is created");
	}
	
    //HJH
    class SecurePollingHandler extends Thread{
		private int interval = 0;
		private String clientMRN = null;
		private String dstMRN = null;
		private String svcMRN = null;
		private int clientPort = 0;
		private int clientModel = 0;
		private Map<String,String> headerField = null;
		SecureMMSClientHandler.PollingResponseCallback myCallback = null;
		private HostnameVerifier hv = null;
		
    	SecurePollingHandler (String clientMRN, String dstMRN, String svcMRN, int interval, int clientPort, int clientModel, Map<String,String> headerField){
    		this.interval = interval;
    		this.clientMRN = clientMRN;
    		this.dstMRN = dstMRN;
    		this.svcMRN = svcMRN;
    		this.clientPort = clientPort;
    		this.clientModel = clientModel;
    		this.headerField = headerField;
    	}
    	
    	void setPollingResponseCallback(SecureMMSClientHandler.PollingResponseCallback callback){
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
			
			hv = getHV();
			
			String url = "https://"+MMSConfiguration.MMS_URL+"/polling"; // MMS Server
			URL obj = new URL(url);
			String data;
			if (svcMRN != null){
				data = (clientPort + ":" + clientModel + ":" + svcMRN); //To do: add geographical info, channel info, etc. 
			} else {
				data = (clientPort + ":" + clientModel + ":");
			}
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setHostnameVerifier(hv);
			
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
			List<String> responseCodes = new ArrayList<String>();
			responseCodes.add(responseCode+"");
			if(MMSConfiguration.LOGGING){
				System.out.println("\nSending 'POST' request to URL : " + url);
				System.out.println("Polling...");
				System.out.println("Response Code : " + responseCode);
			}
			
			Map<String,List<String>> inH = con.getHeaderFields();
			inH = getModifiableMap(inH);
			inH.put("Response-code",responseCodes);
			BufferedReader inB = new BufferedReader(
			        new InputStreamReader(con.getInputStream(),Charset.forName("UTF-8")));
			String inputLine;
			
			StringBuffer response = new StringBuffer();
			while ((inputLine = inB.readLine()) != null) {
				response.append(inputLine.trim() + "\n");
			}
			
			
			inB.close();
			
			String res = response.toString();		

			processResponse(inH, res);
		}
		
		private void processResponse(Map<String,List<String>> headerField, String message) {
    		this.myCallback.callbackMethod(headerField, message);
    	}
		
		HostnameVerifier getHV (){
			// Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[]{
	            new X509TrustManager() {
	                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                    return null;
	                }

	                public void checkClientTrusted(
	                    java.security.cert.X509Certificate[] certs, String authType) {
	                }

	                public void checkServerTrusted(
	                    java.security.cert.X509Certificate[] certs, String authType) {
	                }
	            }
	        };
	        // Install the all-trusting trust manager
	        try {
	            SSLContext sc = SSLContext.getInstance("SSL");
	            sc.init(null, trustAllCerts, new java.security.SecureRandom());
	            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        } catch (Exception e) {
	        	if(MMSConfiguration.LOGGING)System.out.println("Error" + e);
	        }
	        
	        HostnameVerifier hv = new HostnameVerifier() {
	            public boolean verify(String urlHostName, SSLSession session) {
	            	if(MMSConfiguration.LOGGING)System.out.println("Warning: URL Host: " + urlHostName + " vs. "
	                        + session.getPeerHost());
	                return true;
	            }
	        };
	        
	        return hv;
		}
		
		private Map<String, List<String>> getModifiableMap (Map<String, List<String>> map) {
			Map<String, List<String>> ret = new HashMap<String, List<String>>();
			Set<String> resHeaderKeyset = map.keySet(); 
			for (Iterator<String> resHeaderIterator = resHeaderKeyset.iterator();resHeaderIterator.hasNext();) {
				String key = resHeaderIterator.next();
				List<String> values = map.get(key);
				ret.put(key, values);
			}
		
			return ret;
		}
	}
    //HJH end
}
