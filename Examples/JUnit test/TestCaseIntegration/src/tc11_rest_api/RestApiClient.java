package tc11_rest_api;

/**
 * File name : RestApiClient.java 
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

public class RestApiClient {

	private static MMSClientHandler myHandler = null;
	public static JSONObject jobj = null;
	
	public RestApiClient() {
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
			myHandler.sendApiReq("api", params, 10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
