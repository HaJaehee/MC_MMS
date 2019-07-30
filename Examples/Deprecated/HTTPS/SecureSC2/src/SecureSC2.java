import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSConfiguration;
import kr.ac.kaist.mms_client.SecureMMSClientHandler;

/* -------------------------------------------------------- */
/** 
File name : SecureSC2.java 
	Service Consumer which can only send messages by HTTPS.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-21
Version : 0.4.0

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-18
Version : 0.5.6
	Changed the variable Map<String,String> headerField to Map<String,List<String>>
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-21
Version : 0.7.0
	Compatible with MMS Client beta-0.7.0.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	
*/
/* -------------------------------------------------------- */

public class SecureSC2 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:secure-1000002";
		//myMRN = args[0];

		MMSConfiguration.MMS_URL="mms-kaist.com:444";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.
		
		
		//Service Consumer which can only send message
		SecureMMSClientHandler sender = new SecureMMSClientHandler(myMRN);
		
		//Service Consumer is able to set its HTTP header field
		Map<String, List<String>> headerfield = new HashMap<String, List<String>>(); // Header field example. You are able to remove this code.
		List<String> valueList = new ArrayList<String>();
		valueList.add("1234567890");
		headerfield.put("AccessToken",valueList);
		sender.setMsgHeader(headerfield);
		// Header field example ends.
		
		// Sender example.
		sender.setSender(new SecureMMSClientHandler.ResponseCallback() {
			// callbackMethod is called when the response message arrives which is related to request message.
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) { // headerField and message of the response message.
				// TODO Auto-generated method stub
				Iterator<String> iter = headerField.keySet().iterator();
				while (iter.hasNext()){
					String key = iter.next();
					System.out.println(key+":"+headerField.get(key).toString());// Print the matched header field and the header contents.
				}
				System.out.println(message);
			}
		});
		
		for (int i = 0; i < 10;i++){
			String dstMRN = "urn:mrn:smart-navi:device:secure-tm-server";
			String location = "/forwarding";
			String message = "¾È³ç hi hello " + i;
			sender.sendPostMsg(dstMRN, location, message, 10000);
			//Thread.sleep(100);
		}

		/*
		for (int i = 0; i < 10;i++){
			String dstMRN = "urn:mrn:imo:imo-no:1000007";
			String message = "¾È³ç hi hello " + i;
			sender.sendPostMsg(dstMRN, message);
			//Thread.sleep(100);
		}*/

		/*
		for (int i = 0; i < 10;i++){
			String dstMRN = "urn:mrn:imo:imo-no:1000009"
			String message = "¾È³ç hi hello " + i;
			sender.sendPostMsg(dstMRN, message);
			//Thread.sleep(100);
		}*/
	}
}
