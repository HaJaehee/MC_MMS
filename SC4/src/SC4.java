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
Version : 0.2.00
*/
/* -------------------------------------------------------- */

public class SC4 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:0141414";
		//myMRN = args[0];
		
		MMSConfiguration.MMS_URL="127.0.0.1:8088";

		//Service Consumer which can only send message
		MMSClientHandler ch = new MMSClientHandler(myMRN);

		//file transferring
		/*
		String response = ch.requestFile("urn:mrn:smart-navi:device:tm-server", "test.xml");
	    System.out.println("response from SC :" + response);
	    response = ch.sendPostMsg("urn:mrn:smart-navi:device:tm-server", "hello, SC");
		System.out.println("response from MSR :" + response);
		*/
	}
}
