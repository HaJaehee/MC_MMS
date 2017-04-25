import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSConfiguration;
import kr.ac.kaist.mms_client.SecureMMSClientHandler;

/* -------------------------------------------------------- */
/** 
File name : SC8.java 
	Service Consumer which can only send messages by HTTPS.
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

public class SC8 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:1000008";
		//myMRN = args[0];

		//MMSConfiguration.MMS_URL="winsgkwogml.iptime.org:444";

		//Service Consumer which can only send message
		SecureMMSClientHandler sender = new SecureMMSClientHandler(myMRN);
		Map<String, String> headerfield = new HashMap<String, String>();
		headerfield.put("AccessToken", "1234567890");
		sender.setMsgHeader(headerfield);
		
		sender.setSender(new SecureMMSClientHandler.ResponseCallback() {
			//Response Callback from the request message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				System.out.println(message);
			}
		});
		
		for (int i = 0; i < 10;i++){
			sender.sendPostMsg("urn:mrn:smart-navi:device:secure-tm-server", "/forwarding", "¾È³ç hi hello " + i);
			//Thread.sleep(100);
		}

		/*
		for (int i = 0; i < 10;i++){
			sender.sendPostMsg("urn:mrn:imo:imo-no:1000007", "¾È³ç hi hello " + i);
			//Thread.sleep(100);
		}*/

		/*
		for (int i = 0; i < 10;i++){
			sender.sendPostMsg("urn:mrn:imo:imo-no:1000009", "¾È³ç hi hello " + i);
			//Thread.sleep(100);
		}*/
	}
}
