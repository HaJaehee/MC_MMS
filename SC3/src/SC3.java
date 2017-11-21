import java.util.List;
import java.util.Map;
import java.util.Scanner;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : SC3.java
	Service Consumer which can only send message
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03
Version : 0.3.01

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-21
Version : 0.6.1
	Compatible with MMS Client beta-0.6.1.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	
*/
/* -------------------------------------------------------- */

public class SC3 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:1000003";
		//myMRN = args[0];

		MMSConfiguration.MMS_URL = "127.0.0.1:8088";
		MMSConfiguration.LOGGING = false; // If you are debugging client, set this variable true.
		
		
		//Service Consumer which can only send message
		//Sender example.
		MMSClientHandler sender = new MMSClientHandler(myMRN);
		sender.setSender(new MMSClientHandler.ResponseCallback (){
			// callbackMethod is called when the response message arrives which is related to request message.
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {// headerField and message of the response message.
				// TODO Auto-generated method stub
				System.out.println(message);
			}
			
		});
		
		String dstMRN = "urn:mrn:simple:simple:server";
		String location = "HelloWorldServer/";
		String message = ""; //Empty message
		sender.sendGetMsg(dstMRN, location, message);
	}
}
