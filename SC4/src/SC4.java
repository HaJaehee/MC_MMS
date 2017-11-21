import java.util.List;
import java.util.Map;
import java.util.Scanner;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : SC4.java
	Service Consumer requests a file from a Service Provider.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03

Rev. history : 2017-02-14
Version : 0.3.01
	fixed http get file request bugs
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

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

public class SC4 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:1000004";
		//myMRN = args[0];
		
		MMSConfiguration.MMS_URL = "127.0.0.1:8088";
		MMSConfiguration.LOGGING = false; // If you are debugging client, set this variable true.

		//Service Consumer which request a file from server 
		MMSClientHandler sender = new MMSClientHandler(myMRN);
		sender.setSender(new MMSClientHandler.ResponseCallback (){
			//Response Callback from the request message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				System.out.println(message);
			}
			
		});
		
		
		// Requests file example
		String dstMRN = "urn:mrn:imo:imo-no:1000006";
		String fileLocation = "get/test.xml";
		String response = sender.requestFile(dstMRN, fileLocation);
	    System.out.println("Response from SC :" + response);
	    
	    fileLocation = "get/mc.png";
	    response = sender.requestFile(dstMRN, fileLocation);
	    System.out.println("Response from SC :" + response);
	    
	    fileLocation = "get/pdf.pdf";
	    response = sender.requestFile(dstMRN, fileLocation);
	    System.out.println("Response from SC :" + response);
	    
	    fileLocation = "get/korean_pdf.pdf";
	    response = sender.requestFile(dstMRN, fileLocation);
	    System.out.println("Response from SC :" + response);
	}
}
