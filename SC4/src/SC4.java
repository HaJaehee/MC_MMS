import java.util.Scanner;

import kr.ac.kaist.mms_client.*;

public class SC4 {
	public static void main(String args[]) throws Exception{
		String myMRN;
		//myMRN = args[0];
		myMRN = "urn:mrn:imo:imo-no:0141414";
		
		MMSConfiguration.MMSURL="127.0.0.1:8088";
		MMSConfiguration.CMURL="127.0.0.1";

		//Service Consumer which can only send message
		MMSClientHandler ch = new MMSClientHandler(myMRN);

		//file transferring
		/*
		String response = ch.requestFile("urn:mrn:smart-navi:device:tm-server", "test.xml");
	    System.out.println("response from SC :" + response);
	    response = ch.sendMSG("urn:mrn:smart-navi:device:tm-server", "hello, SC");
		System.out.println("response from MSR :" + response);
		*/
	}
}
