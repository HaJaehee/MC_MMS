package tc07_general_errorcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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


import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;
/** 
File name : MMSGeneralErrorCodeClient.java
	This test client is for testing whether MMS give a error message properly or not.
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-02

Rev.history :2019-06-13
Version : 0.9.2
	Change the class name TS7_Client -> MMSGeneralErrorCodeClient
	
	** And this test is succeeded
Modifier : Yunho Choi (choiking10@kaist.ac.kr)
*/

public class MMSGeneralErrorCodeClient {
	//public static void main(String[] args) throws Exception{	
	private String TAG = "[MMSSndHandler] ";
	private final String USER_AGENT = MMSConfiguration.USER_AGENT;
	private String clientMRN = null;
	private boolean isRgstLoc = false;
	private MMSClientHandler.ResponseCallback myCallback;
	
	public MMSGeneralErrorCodeClient(String myMRN) throws Exception {		
		clientMRN = myMRN;
	}
	public void sendMessage(String dstMRN, String loc, String data, MMSClientHandler.ResponseCallback callback) {
		myCallback = callback;
		try {
			sendPostMsg(dstMRN, loc, data, null, -1, -1);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void sendPostMsg(String dstMRN, String loc, String data, Map<String,List<String>> headerField, int seqNum, int timeout) throws IOException  {
		String url = "http://"+MMSConfiguration.MMS_URL; // MMS Server
		if (!loc.startsWith("/")) {
			loc = "/" + loc;
		}
		url += loc;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		
		if (clientMRN != null) {
			con.setRequestProperty("srcMRN", clientMRN);
		}
		
		if (dstMRN != null) {
			con.setRequestProperty("dstMRN", dstMRN);
		}
		if (seqNum != -1) {
			con.setRequestProperty("seqNum", ""+seqNum);
		}
		
		if (timeout > 0) {
			con.setConnectTimeout(timeout);
			con.setReadTimeout(timeout);
		}
		//con.addRequestProperty("Connection","keep-alive");
		
		if (headerField != null) {
			con = addCustomHeaderField(con, headerField);
		} 
		
		//load contents
		String urlParameters = data;
		

		if(MMSConfiguration.DEBUG) {System.out.println(TAG+"urlParameters: "+urlParameters);}
		
		// Send post request
		con.setDoOutput(true);
		BufferedWriter wr = new BufferedWriter(
				new OutputStreamWriter(con.getOutputStream(),Charset.forName("UTF-8")));
		
		if(MMSConfiguration.DEBUG) {System.out.println(TAG+"Trying to send message");}
		wr.write(urlParameters);
		wr.flush();
		wr.close();

		Map<String,List<String>> inH = con.getHeaderFields();
		
		
		inH = getModifiableMap(inH);
		int responseCode = 0;
		InputStream inStream = null;
		if (con.getResponseCode() != HttpURLConnection.HTTP_ENTITY_TOO_LARGE) {
			responseCode = con.getResponseCode();
			inStream = con.getInputStream();
		} else {
		     /* error from server */
			responseCode = con.getResponseCode();
			inStream = new ByteArrayInputStream("HTTP 413 Error: HTTP Entity Too Large".getBytes());
		}
		List<String> responseCodes = new ArrayList<String>();
		responseCodes.add(responseCode+"");
		inH.put("Response-code", responseCodes);
		
		if(MMSConfiguration.DEBUG){
			System.out.println("\n"+TAG+"Sending 'POST' request to URL : " + url);
			System.out.println(TAG+"Post parameters : " + urlParameters);
			System.out.println(TAG+"Response Code : " + responseCode);
		}
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(inStream,Charset.forName("UTF-8")));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		in.close();
		if(MMSConfiguration.DEBUG) {System.out.println(TAG+"Response: " + response.toString() + "\n");}
		myCallback.callbackMethod(inH, response.toString());
		
		return;
	}

	private HttpURLConnection addCustomHeaderField (HttpURLConnection con, Map<String,List<String>> headerField) {
		HttpURLConnection retCon = con;
		if(MMSConfiguration.DEBUG) {System.out.println(TAG+"set headerfield[");}
		for (Iterator<String> keys = headerField.keySet().iterator() ; keys.hasNext() ;) {
			String key = (String) keys.next();
			List<String> valueList = (List<String>) headerField.get(key);
			if (valueList != null) {
				if (valueList.size() == 1) {
					if(MMSConfiguration.DEBUG) {System.out.println(key+":"+valueList.get(0));}
					retCon.addRequestProperty(key, valueList.get(0));
				}
				else if (valueList.size() > 1) { 
					
					StringBuilder valueBuf = new StringBuilder();
					if(MMSConfiguration.DEBUG) {System.out.print(key+":[");}
					valueBuf.append("[");
					int i = 0;
					for (i = 0 ; i < valueList.size() ; i++) {
						String value = valueList.get(i);
						if(MMSConfiguration.DEBUG) {System.out.print(value);}
						//valueBuf.append("\""+value+"\"");
						valueBuf.append(value);
						if (i != valueList.size()-1) {
							if(MMSConfiguration.DEBUG) {System.out.print(",");}
							valueBuf.append(",");
						}
					}
					if(MMSConfiguration.DEBUG) {System.out.println("]");}
					valueBuf.append("]");
					retCon.addRequestProperty(key, valueBuf.toString());
				}
				else {
					if(MMSConfiguration.DEBUG) {System.out.println(key+":");}
					retCon.addRequestProperty(key, "");
				}
			}
			else if (valueList == null) {
				if(MMSConfiguration.DEBUG) {System.out.println(key+":null");}
				retCon.addRequestProperty(key, null);
			}
		}
		if(MMSConfiguration.DEBUG) {System.out.println("]");}
		return retCon;
	}
	//HJH end
	
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
