import java.util.Scanner;

import org.json.simple.JSONObject;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

public class SC2 {
	public static void main(String args[]) throws Exception{
		String myMRN;
		//myMRN = args[0];
		myMRN = "urn:mrn:imo:imo-no:1000007";
		
		MMSConfiguration.MMSURL="127.0.0.1:8088";
		MMSConfiguration.CMURL="127.0.0.1";

		//Service Consumer which can only send message
		MMSClientHandler ch = new MMSClientHandler(myMRN);
		JSONObject headerfield = new JSONObject();
		headerfield.put("AccessToken", "1234567890");
		ch.setMsgHeader(headerfield);
		for (int i = 0; i < 1;i++){
			String a = ch.sendPostMsg("urn:mrn:smart-navi:device:tm-server", "¾È³ç hi hello " + i);
			//Thread.sleep(100);
		}
		
		for (int i = 0; i < 1;i++){
			String a = ch.sendPostMsg("urn:mrn:imo:imo-no:0000112", "¾È³ç hi hello " + i);
			//Thread.sleep(100);
		}
	}
}
