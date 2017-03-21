import java.util.HashMap;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSConfiguration;
import kr.ac.kaist.mms_client.SecureMMSClientHandler;

/* -------------------------------------------------------- */
/** 
File name : SC8.java
	Service Consumer which can only send message
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-21
Version : 0.4.0
*/
/* -------------------------------------------------------- */

public class SC8 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:1000008";
		//myMRN = args[0];

		//MMSConfiguration.MMS_URL="winsgkwogml.iptime.org:444";

		//Service Consumer which can only send message
		SecureMMSClientHandler sch = new SecureMMSClientHandler(myMRN);
		Map<String, String> headerfield = new HashMap<String, String>();
		headerfield.put("AccessToken", "1234567890");
		sch.setMsgHeader(headerfield);
		
		
		/*
		for (int i = 0; i < 10;i++){
			String a = sch.sendPostMsg("urn:mrn:smart-navi:device:tm-server", "/forwarding", "¾È³ç hi hello " + i);
			//Thread.sleep(100);
		}*/

		
		for (int i = 0; i < 10;i++){
			String a = sch.sendPostMsg("urn:mrn:imo:imo-no:0000117", "¾È³ç hi hello " + i);
			//Thread.sleep(100);
		}

	}
}
