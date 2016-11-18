package com.kaist.MMSClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class ServiceRegistry{
	
	public static void main(String args[]) throws Exception{
		String myMRN;
		int port;
		//myMRN = args[0];
		myMRN = "mrn:kor:123126";
		//port = Integer.parseInt(args[1]);
		port = 8904;
		MMSClientHandler mh = new MMSClientHandler(myMRN, port);
		//Request Callback from the request message
		mh.setReqCallBack(new MMSClientHandler.reqCallBack() {
			@Override
			public String callbackMethod(String message) {
				System.out.println(message);
				return "OK";
			}
		});
		//String response = mh.sendMSG("mrn:kor:123124", "hello");
		//System.out.println("response:" + response);
	}
	
		
		
}
