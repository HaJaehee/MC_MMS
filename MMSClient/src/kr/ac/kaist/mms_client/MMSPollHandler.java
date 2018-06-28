package kr.ac.kaist.mms_client;
/* -------------------------------------------------------- */
/** 
File name : MMSPollHandler.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-25
Version : 0.5.0

Rev. history : 2017-06-18
Version : 0.5.6
	Changed the variable Map<String,String> headerField to Map<String,List<String>>
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-07-28
Version : 0.5.9
	MMS replies message array into JSONArray form. And messages are encoded by URLEncoder, UTF-8.
	MMSPollHandler parses JSONArray and decodes messages by URLDecoder, UTF-8.
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
	Removed EXPOSURE_OF_SYSTEM_DATA hazard.
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
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class MMSPollHandler {
	PollHandler ph = null;
	//HJH
	private static final String USER_AGENT = "MMSClient/0.7.0";
	private String TAG = "[MMSPollHandler] ";
	private String clientMRN = null;
	
	MMSPollHandler(String clientMRN, String dstMRN, String svcMRN, int interval, int clientPort, int msgType, Map<String,List<String>> headerField) throws IOException{
		ph = new PollHandler(clientMRN, dstMRN, svcMRN, interval, clientPort, msgType, headerField);
		if(MMSConfiguration.DEBUG) {System.out.println(TAG+"Polling handler is created");}
	}
	
	//HJH
    class PollHandler extends Thread{
		private int interval = 0;
		private String clientMRN = null;
		private String dstMRN = null;
		private String svcMRN = null;
		private int clientPort = 0;
		private int clientModel = 0;
		private boolean interrupted=false;
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
    		interrupted=false;
    	}
    	
    	void setPollingResponseCallback(MMSClientHandler.PollingResponseCallback callback){
    		this.myCallback = callback;
    	}
    	
    	void markInterrupted(){
    		interrupted=true;
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
    			System.out.print(TAG+" Exception: "+ e.getLocalizedMessage());
				if(MMSConfiguration.DEBUG){e.printStackTrace();}
    			
    		}
    	}
    	
		void Poll(){
			try {
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
				if(MMSConfiguration.DEBUG){
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
				
				receiveResponse(inH, resList);
			}
			catch (IOException e) {
				System.out.print(TAG);
				if(MMSConfiguration.DEBUG){e.printStackTrace();}
			}
			catch (ParseException e) {
				System.out.print(TAG);
				if(MMSConfiguration.DEBUG){e.printStackTrace();}
			}
		}
		
		private void receiveResponse(Map<String,List<String>> headerField, List<String> messages) {
    		this.myCallback.callbackMethod(headerField, messages);
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
			if(MMSConfiguration.DEBUG) {System.out.println(TAG+"set headerfield[");}
			for (Iterator keys = headerField.keySet().iterator() ; keys.hasNext() ;) {
				String key = (String) keys.next();
				List<String> valueList = (List<String>) headerField.get(key);
				for (String value : valueList) {
					if(MMSConfiguration.DEBUG) {System.out.println(key+":"+value);}
					retCon.addRequestProperty(key, value);
				}
			}
			if(MMSConfiguration.DEBUG) {System.out.println("]");}
			return retCon;
		}
	}
    //HJH end
}
