import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : ServiceProvider.java
	Service Provider only forwards messages to SC having urn:mrn:mcl:vessel:dma:paul-lowenorn
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-10-05
*/
/* -------------------------------------------------------- */

public class ServiceProvider {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:smart-navi:device:service-provider";

//		MMSConfiguration.MMS_URL="mms-kaist.com:8088";
		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.
		
		MMSClientHandler sender = new MMSClientHandler(myMRN);
		sender.setSender(new MMSClientHandler.ResponseCallback() {
			//Response Callback from the request message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				System.out.println(message);
			}
		});
		
		for (int i = 0; i < 1;i++){
			String dstMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
			String message = "¾È³ç hi \"hello\" " + i;
			sender.sendPostMsg(dstMRN, message, 3000);
			Thread.sleep(1000);
		}
	}
}
