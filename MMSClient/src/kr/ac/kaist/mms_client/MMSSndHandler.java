package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : MMSSndHandler.java
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03
Version : 0.3.01
Rev. history : 2017-02-01
	Added setting header field features. 
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MMSSndHandler {
	private static final String TAG = "MMSSndHandler";
	private final String USER_AGENT = "MMSClient/0.3.01";
	private String clientMRN = null;
	private MMSClientHandler.ResponseCallback myCallback;
	MMSSndHandler (String clientMRN){
		this.clientMRN = clientMRN;
	}

	void registerLocator(int port) throws Exception {
		sendHttpPost("urn:mrn:smart-navi:device:mms1", "/registering", port+":2", null);
		
	}
	
	void setResponseCallback (MMSClientHandler.ResponseCallback callback){
		this.myCallback = callback;
	}
	
	void sendHttpPost(String dstMRN, String loc, String data, Map<String,String> headerField) throws Exception{
		
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
		con.setRequestProperty("srcMRN", clientMRN);
		con.setRequestProperty("dstMRN", dstMRN);
		//con.addRequestProperty("Connection","keep-alive");
		
		if (headerField != null) {
			if(MMSConfiguration.LOGGING)System.out.println("set headerfield[");
			for (Iterator keys = headerField.keySet().iterator() ; keys.hasNext() ;) {
				String key = (String) keys.next();
				String value = (String) headerField.get(key);
				if(MMSConfiguration.LOGGING)System.out.println(key+":"+value);
				con.setRequestProperty(key, value);
			}
			if(MMSConfiguration.LOGGING)System.out.println("]");
		} 
		
		//load contents
		String urlParameters = data;
		

		if(MMSConfiguration.LOGGING)System.out.println("urlParameters: "+urlParameters);
		
		// Send post request
		con.setDoOutput(true);
		BufferedWriter wr = new BufferedWriter(
				new OutputStreamWriter(con.getOutputStream(),Charset.forName("UTF-8")));
		wr.write(urlParameters);
		wr.flush();
		wr.close();

		Map<String,List<String>> inH = con.getHeaderFields();
		inH = getModifiableMap(inH);
		int responseCode = con.getResponseCode();
		List<String> responseCodes = new ArrayList<String>();
		responseCodes.add(responseCode+"");
		inH.put("Response-code", responseCodes);
		
		if(MMSConfiguration.LOGGING){
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);
		}
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream(),Charset.forName("UTF-8")));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		in.close();
		if(MMSConfiguration.LOGGING)System.out.println("Response: " + response.toString());
		receiveResponse(inH, new String(response.toString().getBytes(), "utf-8"));
		
		return;
	}
	
	//OONI
	String sendHttpGetFile(String dstMRN, String fileName, Map<String,String> headerField) throws Exception {

		String url = "http://"+MMSConfiguration.MMS_URL; // MMS Server
		if (!fileName.startsWith("/")) {
			fileName = "/" + fileName;
		}
		url += fileName;
		URL obj = new URL(url);
		
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		//add request header
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("srcMRN", clientMRN);
		con.setRequestProperty("dstMRN", dstMRN);
		if (headerField != null) {
			if(MMSConfiguration.LOGGING)System.out.println("set headerfield[");
			for (Iterator keys = headerField.keySet().iterator() ; keys.hasNext() ;) {
				String key = (String) keys.next();
				String value = (String) headerField.get(key);
				con.setRequestProperty(key, value);
			}
			if(MMSConfiguration.LOGGING)System.out.println("]");
		}
		//con.addRequestProperty("Connection","keep-alive");

		int responseCode = con.getResponseCode();
		if(MMSConfiguration.LOGGING)System.out.println("\nSending 'GET' request to URL : " + url);
		if(MMSConfiguration.LOGGING)System.out.println("Response Code : " + responseCode);
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream(),Charset.forName("UTF-8")));
		String inputLine;
		BufferedWriter out = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+fileName));
		
		while ((inputLine = in.readLine()) != null) {
			out.append(inputLine); out.newLine();
		}
		
		out.flush();
		out.close();
		in.close();
		return fileName + " is saved";
	}
	//OONI end
	
	//HJH
	void sendHttpGet(String dstMRN, String loc, String params, Map<String,String> headerField) throws Exception {

		String url = "http://"+MMSConfiguration.MMS_URL; // MMS Server
		if (!loc.startsWith("/")) {
			loc = "/" + loc;
		}
		url += loc;
		if (params != null) {
			if (params.equals("")) {
				
			}
			else if (params.startsWith("?")) {
				url += params;
			} else {
				url += "?" + params;
			}
		}
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		//add request header
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("srcMRN", clientMRN);
		con.setRequestProperty("dstMRN", dstMRN);
		if (headerField != null) {
			if(MMSConfiguration.LOGGING)System.out.println("set headerfield[");
			for (Iterator keys = headerField.keySet().iterator() ; keys.hasNext() ;) {
				String key = (String) keys.next();
				String value = (String) headerField.get(key);
				con.setRequestProperty(key, value);
			}
			if(MMSConfiguration.LOGGING)System.out.println("]");
		}
		//con.addRequestProperty("Connection","keep-alive");

		Map<String,List<String>> inH = con.getHeaderFields();
		inH = getModifiableMap(inH);
		int responseCode = con.getResponseCode();
		List<String> responseCodes = new ArrayList<String>();
		responseCodes.add(responseCode+"");
		inH.put("Response-code", responseCodes);
		
		if(MMSConfiguration.LOGGING)System.out.println("\nSending 'GET' request to URL : " + url);
		if(MMSConfiguration.LOGGING)System.out.println("Response Code : " + responseCode);
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream(),Charset.forName("UTF-8")));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		
		
		in.close();
		if(MMSConfiguration.LOGGING)System.out.println("Response: " + response.toString());
		
		receiveResponse(inH, new String(response.toString().getBytes(), "utf-8"));
		return;
	}
	
	void receiveResponse (Map<String,List<String>> headerField, String message) {
		
		try {
			myCallback.callbackMethod(headerField, message);
		} catch (NullPointerException e) {
			System.out.println("NullPointerException : Have to set response callback interface! MMSClientHandler.setResponseCallback()");
		}
		
		return;
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
	//HJH end
}
