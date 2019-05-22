
/**
 * File name : TS11_client.java 
 * 		For testing MMS restful API.
 * Author : Jaehee Ha (jaehee.ha@kaist.ac.kr) 
 * Creation Date : 2019-05-22
 */

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSClientHandler.ResponseCallback;
import kr.ac.kaist.mms_client.MMSConfiguration;


public class TS11_client {

	public static int checker = 0;

	public static String hexSignedData_active = null;


	private static MMSClientHandler myHandler = null;
	
	public TS11_client() {

		MMSConfiguration.MMS_URL = "mms-kaist.com:8088";
		MMSConfiguration.DEBUG = true;


		// threadStart();
	}

	public static void apiTest() throws NullPointerException, IOException {

		myHandler = new MMSClientHandler(null);

		myHandler.setSender(new ResponseCallback() {
			
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				System.out.println(message);
			}
		});
		
		try {
			myHandler.sendApiReq("api", "mms-running=y&client-session-ids=y&polling-req-count-for=5&realtime-log-users=y&mrns-being-debugged=y&msg-queue-count=y&relay-req-count-for=5");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
