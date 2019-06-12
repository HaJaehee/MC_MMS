package TS11;

/**
 * File name : TS11_client.java 
 * 		For testing MMS restful API.
 * Author : Jaehee Ha (jaehee.ha@kaist.ac.kr) 
 * Creation Date : 2019-05-22
 */

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSClientHandler.ResponseCallback;
import kr.ac.kaist.mms_client.MMSConfiguration;


public class TS11_client {

	private static MMSClientHandler myHandler = null;
	public static JSONObject jobj = null;
	
	public TS11_client() {

		MMSConfiguration.MMS_URL = "mms-kaist.com:8088";
		MMSConfiguration.DEBUG = true;


		// threadStart();
	}

	public static void apiTest(String params) throws NullPointerException, IOException {

		myHandler = new MMSClientHandler(null);

		myHandler.setSender(new ResponseCallback() {
			
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				System.out.println(message);
				JSONParser parser = new JSONParser();
				jobj = new JSONObject();
				try {
					jobj = (JSONObject) parser.parse(message);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		try {
			myHandler.sendApiReq("api", params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
