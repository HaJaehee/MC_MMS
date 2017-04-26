package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : SecureMMSSndHandler.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-21
Version : 0.4.0

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;

import javax.net.ssl.*;

class SecureMMSSndHandler {
	private static final String TAG = "MMSSndHandler";
	private final String USER_AGENT = "MMSClient/0.5.0";
	private String clientMRN = null;
	private boolean isRgstLoc = false;
	private SecureMMSClientHandler.ResponseCallback myCallback;
	private HostnameVerifier hv = null;
	
	SecureMMSSndHandler (String clientMRN){
		this.clientMRN = clientMRN;
		hv = getHV();
	}
	
	void setResponseCallback (SecureMMSClientHandler.ResponseCallback callback){
		this.myCallback = callback;
	}
	
	@Deprecated
	void registerLocator(int port) throws Exception {
		isRgstLoc = true;
		sendHttpsPost("urn:mrn:smart-navi:device:mms1", "/registering", port+":2", null);
	}
	
	void sendHttpsPost(String dstMRN, String loc, String data, Map<String,String> headerField) throws Exception{
        
		String url = "https://"+MMSConfiguration.MMS_URL; // MMS Server
		if (!loc.startsWith("/")) {
			loc = "/" + loc;
		}
		url += loc;
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setHostnameVerifier(hv);
		
		
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
	String sendHttpsGetFile(String dstMRN, String fileName, Map<String,String> headerField) throws Exception {

		String url = "https://"+MMSConfiguration.MMS_URL; // MMS Server
		if (!fileName.startsWith("/")) {
			fileName = "/" + fileName;
		}
		url += fileName;
		URL obj = new URL(url);
		
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setHostnameVerifier(hv);
		
		
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
	void sendHttpsGet(String dstMRN, String loc, String params, Map<String,String> headerField) throws Exception {

		String url = "https://"+MMSConfiguration.MMS_URL; // MMS Server
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
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setHostnameVerifier(hv);
		
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
	
	void receiveResponse (Map<String,List<String>> headerField, String message) {
		
		if (!isRgstLoc){
			isRgstLoc = false;
			try{
				myCallback.callbackMethod(headerField, message);
			} catch (NullPointerException e) {
				System.out.println("NullPointerException : Have to set response callback interface! SecureMMSClientHandler.setSender()");
			}
			return;
		}
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
