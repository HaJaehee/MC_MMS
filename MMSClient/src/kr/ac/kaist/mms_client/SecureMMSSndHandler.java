package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : SecureMMSSndHandler.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-21
Version : 0.4.0
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
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class SecureMMSSndHandler {
	private static final String TAG = "MMSSndHandler";
	private final String USER_AGENT = "MMSClient/0.4.0";
	private String clientMRN = null;
	private HostnameVerifier hv = null;
	
	SecureMMSSndHandler (String clientMRN){
		this.clientMRN = clientMRN;
		hv = getHV();
	}

	String registerLocator(int port) throws Exception {
		return sendHttpsPost("urn:mrn:smart-navi:device:mms1", "/registering", port+":2", null);
		
	}
	
	String sendHttpsPost(String dstMRN, String loc, String data, Map<String,String> headerField) throws Exception{
        
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
		String urlParameters = "";
		if (!loc.equals("/registering")){
			//		change the string data to json format
			JSONObject jsonFrame = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("seq", "1");
			jsonObject.put("srcMRN", clientMRN);
			jsonObject.put("data", data);
			jsonArray.add(jsonObject);
			jsonFrame.put("payload", jsonArray);
			
			String jsonPayload = jsonFrame.toJSONString();
			
			urlParameters = jsonPayload;
		} else {
			urlParameters = data;
		}

		if(MMSConfiguration.LOGGING)System.out.println("urlParameters: "+urlParameters);
		
		// Send post request
		con.setDoOutput(true);
		BufferedWriter wr = new BufferedWriter(
				new OutputStreamWriter(con.getOutputStream(),Charset.forName("UTF-8")));
		wr.write(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		if(MMSConfiguration.LOGGING)System.out.println("\nSending 'POST' request to URL : " + url);
		if(MMSConfiguration.LOGGING)System.out.println("Post parameters : " + urlParameters);
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
		
		
		
		return new String(response.toString().getBytes(), "utf-8");
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
	String sendHttpsGet(String dstMRN, String loc, String params, Map<String,String> headerField) throws Exception {

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

		int responseCode = con.getResponseCode();
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
		return new String(response.toString().getBytes(), "utf-8");
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
	
	//HJH end
	
}
