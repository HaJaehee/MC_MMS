import java.io.IOException;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;
/** 
File name : TS5_server.java
	Validation of MMS polling client
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-10-16
*/
public class TS5_server {
	String myMRN = "urn:mrn:imo:imo-no:ts-mms-05-server";
	MMSClientHandler sender = new MMSClientHandler(myMRN);

	public TS5_server() throws Exception {

		MMSConfiguration.MMS_URL = "143.248.57.144:8088";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.

		sender.setSender(new MMSClientHandler.ResponseCallback() {
			// Response Callback from the request message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				//System.out.println(message);
			}
		});

	}

	public void RightsrcMRN() throws Exception {
		
			String dstMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
			String message = "test";
			sender.sendPostMsg(dstMRN, message);
			Thread.sleep(100);
		
	}
	
	public void WrongsrcMRN() throws Exception{
		
			String dstMRN = "urn:mrn:imo:imo-no:ts-mms-05-client";
			String message = "test";
			sender.sendPostMsg(dstMRN, message);
			Thread.sleep(100);
		
	}
}
