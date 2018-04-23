package kr.ac.kaist.mms_client;
/* -------------------------------------------------------- */
/** 
File name : SecureMMSPollHandler.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-25
Version : 0.5.0

Rev. history : 2017-06-18
Version : 0.5.7
	Changed the variable Map<String,String> headerField to Map<String,List<String>>
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-07-28
Version : 0.5.9
	MMS replies message array into JSONArray form. And messages are encoded by URLEncoder, UTF-8.
	SecureMMSPollHandler parses JSONArray and decodes messages by URLDecoder, UTF-8.
	Changed from PollingResponseCallback.callbackMethod(Map<String,List<String>> headerField, message) 
	     to PollingResponseCallback.callbackMethod(Map<String,List<String>> headerField, List<String> messages) 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)


Rev. history : 2017-11-16
Version : 0.7.0
	 Add boolean variable "interrupted" in PollHandler. This variable is used to mark that this pollingHandler must be stopped.  
	 This variable is proofed in the run() method.
	 changed  from while(!Thread.currentThread().isInterrupted())
	           to  while(!Thread.currentThread().isInterrupted() && interrupted == false)
Modifier : Jaehyun Park (jae519@kaist.ac.kr)

Rev. history : 2018-04-23
Version : 0.7.1
	Removed IMPROPER_CHECK_FOR_UNUSUAL_OR_EXCEPTIONAL_CONDITION, EXPOSURE_OF_SYSTEM_DATA hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;


class SecureMMSPollHandler {

	SecurePollingHandler ph = null;
	//HJH
	private String TAG = "[SecureMMSPollHandler] ";
	private static final String USER_AGENT = "MMSClient/0.7.0";
	private String clientMRN = null;
	
	SecureMMSPollHandler(String clientMRN, String dstMRN, String svcMRN, int interval, int clientPort, int msgType, Map<String,List<String>> headerField) throws IOException{
		ph = new SecurePollingHandler(clientMRN, dstMRN, svcMRN, interval, clientPort, msgType, headerField);
		if(MMSConfiguration.LOGGING)System.out.println(TAG+"Polling handler is created");
	}
	
    //HJH
    class SecurePollingHandler extends Thread{
		private int interval = 0;
		private String clientMRN = null;
		private String dstMRN = null;
		private String svcMRN = null;
		private int clientPort = 0;
		private int clientModel = 0;
		private Map<String,List<String>> headerField = null;
		SecureMMSClientHandler.PollingResponseCallback myCallback = null;
		private HostnameVerifier hv = null;
		private boolean interrupted=false;
		
    	SecurePollingHandler (String clientMRN, String dstMRN, String svcMRN, int interval, int clientPort, int clientModel, Map<String,List<String>> headerField){
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
    		try{
	    		while (!Thread.currentThread().isInterrupted() && !interrupted){
	    			Thread.sleep(interval);
		    		Poll();
	    		}
	    		if (interrupted){
	    			System.out.println("[ERROR]Thread is dead");
	    		}
    		} catch (InterruptedException e){
    			System.out.println("[ERROR]Thread is dead");
    		} catch (Exception e){
    			System.out.print(TAG);
				//e.printStackTrace();
    			
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
				con = addCustomHeaderField(con, headerField);
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
				System.out.println("\n"+TAG+"Sending 'POST' request to URL : " + url);
				System.out.println(TAG+"Polling...");
				System.out.println(TAG+"Response Code : " + responseCode);
			}
			
			Map<String,List<String>> inH = con.getHeaderFields();
			inH = getModifiableMap(inH);
			inH.put("Response-code",responseCodes);
			BufferedReader inB = new BufferedReader(
			        new InputStreamReader(con.getInputStream(),Charset.forName("UTF-8")));
			String inputLine;

			StringBuffer response = new StringBuffer();
			List<String> resList = new ArrayList<String>();
			
			while ((inputLine = inB.readLine()) != null) {
				response.append(inputLine.trim()+"\n");
			}
			
			if (response.length() != 0){
				JSONArray jsonArr = new JSONArray();
				JSONParser jsonPars = new JSONParser();
				jsonArr = (JSONArray) jsonPars.parse(response.toString());
				for (int i = 0 ; i < jsonArr.size() ; i++) {
					resList.add(URLDecoder.decode(jsonArr.get(i).toString(), "UTF-8"));
				}
			}
			
			inB.close();
			
			processResponse(inH, resList);
		}
		
		private void processResponse(Map<String,List<String>> headerField, List<String> message) {
    		this.myCallback.callbackMethod(headerField, message);
    	}
		
    	void markInterrupted(){
    		interrupted=true;
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
	        } 
	        catch (KeyManagementException e) {
	        	System.out.println(TAG);
	        	//e.printStackTrace();
	        } catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
	        	System.out.println(TAG);
	        	//e.printStackTrace();
			}
	        
	        HostnameVerifier hv = new HostnameVerifier() {
	            public boolean verify(String urlHostName, SSLSession session) {
	            	if(MMSConfiguration.LOGGING)System.out.println(TAG+"Warning: URL Host: " + urlHostName + " vs. "
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
		private HttpsURLConnection addCustomHeaderField (HttpsURLConnection con, Map<String,List<String>> headerField) {
			HttpsURLConnection retCon = con;
			if(MMSConfiguration.LOGGING)System.out.println(TAG+"set headerfield[");
			for (Iterator keys = headerField.keySet().iterator() ; keys.hasNext() ;) {
				String key = (String) keys.next();
				List<String> valueList = (List<String>) headerField.get(key);
				for (String value : valueList) {
					if(MMSConfiguration.LOGGING)System.out.println(key+":"+value);
					retCon.addRequestProperty(key, value);
				}
			}
			if(MMSConfiguration.LOGGING)System.out.println("]");
			return retCon;
		}
	}
    //HJH end
}
