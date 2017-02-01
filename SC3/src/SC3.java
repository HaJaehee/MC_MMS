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
Version : 0.2.00
*/
/* -------------------------------------------------------- */

public class SC3 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:0010005";
		//myMRN = args[0];

		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		
		//Service Consumer which can only send message
		MMSClientHandler ch = new MMSClientHandler(myMRN);
		
		String a = ch.sendGetMsg("urn:mrn:simple:simple:server", "HelloWorldServer/", "¾È³ç hi hello");
		System.out.println(a);
	}
}
