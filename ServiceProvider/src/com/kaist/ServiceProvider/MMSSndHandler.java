package com.kaist.ServiceProvider;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
//srcMRNL: "mrn:kor:123123"
//dstMRN:  "mrn:kor:123124"
public class MMSSndHandler {
	private final String USER_AGENT = "MMSClient/0.1";
	private String myMRN;
	public  MMSSndHandler (String myMRN){
		this.myMRN = myMRN;
	}
	static void callbacktype(String data){}
	public String sendPost(String dstMRN, String data) throws Exception {

		String url = "http://localhost:8088/"; // MMS Server
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("srcMRN", myMRN);
		con.setRequestProperty("dstMRN", dstMRN);
		//con.addRequestProperty("Connection","keep-alive");
		String urlParameters = data;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return new String(response.toString().getBytes(), "utf-8");
		//print result
		//System.out.println("response: " + response.toString());
		//callback(response.toString());

	}
}
