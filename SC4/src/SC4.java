import java.util.Scanner;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : SC4.java
	Service Consumer requests a file from MMS server.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03
Version : 0.3.01
*/
/* -------------------------------------------------------- */

public class SC4 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:0141414";
		//myMRN = args[0];
		
		MMSConfiguration.MMS_URL="127.0.0.1:8088";

		//Service Consumer which request a file from server 
		MMSClientHandler ch = new MMSClientHandler(myMRN);

		//file transferring
		String response = ch.requestFile("urn:mrn:imo:imo-no:0654321", "get/test.xml");
	    System.out.println("Response from SC :" + response);
	}
}
