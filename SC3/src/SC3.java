import java.util.Scanner;

import kr.ac.kaist.mms_client.MMSClientHandler;

public class SC3 {
	public static void main(String args[]) throws Exception{
		String myMRN;
		//myMRN = args[0];
		myMRN = "urn:mrn:imo:imo-no:0010005";
		
		//MMSConfiguration.MMSURL="127.0.0.1:8088";
		//MMSConfiguration.CMURL="127.0.0.1";

		//Service Consumer which can only send message
		MMSClientHandler ch = new MMSClientHandler(myMRN);
		
		String a = ch.sendGetMsg("urn:mrn:simple:simple:server", "HelloWorldServer/", "¾È³ç hi hello");
		System.out.println(a);
	}
}
