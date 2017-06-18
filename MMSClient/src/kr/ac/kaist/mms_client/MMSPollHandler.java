package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : MMSPollHandler.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-25
Version : 0.5.0

Rev. history : 2017-06-17
Version : 0.5.6
	Removed UTF-8 decoding in receiving response message condition 
	Changed the variable Map<String,String> headerField to Map<String,List<String>>
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class MMSPollHandler {
	PollHandler ph = null;
	//HJH
	private static final String USER_AGENT = "MMSClient/0.5.0";
	private String TAG = "[MMSPollHandler] ";
	private String clientMRN = null;
	
	MMSPollHandler(String clientMRN, String dstMRN, String svcMRN, int interval, int clientPort, int msgType, Map<String,List<String>> headerField) throws IOException{
		ph = new PollHandler(clientMRN, dstMRN, svcMRN, interval, clientPort, msgType, headerField);
		if(MMSConfiguration.LOGGING)System.out.println(TAG+"Polling handler is created");
	}
	
	//HJH
    class PollHandler extends Thread{
		private int interval = 0;
		private String clientMRN = null;
		private String dstMRN = null;
		private String svcMRN = null;
		private int clientPort = 0;
		private int clientModel = 0;
		private Map<String,List<String>> headerField = null;
		MMSClientHandler.PollingResponseCallback myCallback = null;
		

    	PollHandler (String clientMRN, String dstMRN, String svcMRN, int interval, int clientPort, int clientModel, Map<String,List<String>> headerField){
    		this.interval = interval;
    		this.clientMRN = clientMRN;
    		this.dstMRN = dstMRN;
    		this.svcMRN = svcMRN;
    		this.clientPort = clientPort;
    		this.clientModel = clientModel;
    		this.headerField = headerField;
    	}
    	
    	void setPollingResponseCallback(MMSClientHandler.PollingResponseCallback callback){
    		this.myCallback = callback;
    	}
    	
    	public void run(){
    		while (true){
    			try{
	    			Thread.sleep(interval);
	    			Poll();
    			}catch (Exception e){
    				if(MMSConfiguration.LOGGING){
						System.out.print(TAG);
						e.printStackTrace();
					}
					
    			}
    		}
    	}
    	
		void Poll() throws Exception {
			
			String url = "http://"+MMSConfiguration.MMS_URL+"/polling"; // MMS Server
			URL obj = new URL(url);
			String data;
			if (svcMRN != null){
				data = (clientPort + ":" + clientModel + ":" + svcMRN); //To do: add geographical info, channel info, etc. 
			} else {
				data = (clientPort + ":" + clientModel + ":");
			}
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
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
			        new InputStreamReader(con.getInputStream()));//,Charset.forName("UTF-8")));
			String inputLine;
			
			StringBuffer response = new StringBuffer();
			while ((inputLine = inB.readLine()) != null) {
				response.append(inputLine.trim() + "\n");
			}
			
			inB.close();
			
			String res = response.toString();
			
				
			receiveResponse(inH, res );
				
			
		}
		
		private void receiveResponse(Map<String,List<String>> headerField, String message) {
    		this.myCallback.callbackMethod(headerField, message);
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
		private HttpURLConnection addCustomHeaderField (HttpURLConnection con, Map<String,List<String>> headerField) {
			HttpURLConnection retCon = con;
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
