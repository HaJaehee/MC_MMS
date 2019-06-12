package TS9;


/** 
File name : TS9_server.java
 * Client Type decide junit test 
 * of testing MMS Author : Youngjin Kim (jcdad3000@kaist.ac.kr) Creation Date :
 * 2019-05-20

*/

import java.io.IOException;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;




public class TS9_server {	
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-09-server";
	private String dstMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	private MMSClientHandler myHandler = null;

	public TS9_server() {

		MMSConfiguration.MMS_URL="mms-kaist.com:8088";
		MMSConfiguration.DEBUG = false;

		try {
			myHandler = new MMSClientHandler(myMRN);

			myHandler.setSender(new MMSClientHandler.ResponseCallback() {

				@Override
				public void callbackMethod(Map<String, List<String>> headerField, String message) {
					// TODO Auto-generated method stub					
				}

			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void sendContent(int content) throws IOException {
		

		String data = "aa"; // createDataSize(actual);				
		try {
			myHandler.sendPostMsg(dstMRN, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}


}

