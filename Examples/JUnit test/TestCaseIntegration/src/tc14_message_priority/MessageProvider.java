package tc14_message_priority;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;

/**
 * File name : MessageProvider.java
 * Author : Jin Jeong (jungst0001@kaist.ac.kr) 
 * Creation Date : 2019-09-17
 */

public class MessageProvider {
	private int response = 0;
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-15-server";
//	private String dstMRN = "urn:mrn:imo:imo-no:ts-mms-03-client";
	private String dstMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	private MMSClientHandler myHandler = null;

	public MessageProvider() {

		try {
			myHandler = new MMSClientHandler(myMRN);

			myHandler.setSender(new MMSClientHandler.ResponseCallback() {

				@Override
				public void callbackMethod(Map<String, List<String>> headerField, String message) {
					// TODO Auto-generated method stub
					if (headerField.get("Response-code") != null) {
						int code = Integer.parseInt(headerField.get("Response-code").get(0));
						response = code;
						System.out.println("Response : " +response);
					}
				}

			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getResponse() {
		return response;
	}
	
	public void sendContent(String priority, String payload) throws IOException {
		Map<String, List<String>> headerfield = new HashMap<String, List<String>>();
		
		List<String> valueList = new ArrayList<String>();
		valueList.add(priority);
		headerfield.put("priority", valueList);

		try {
			myHandler.setMsgHeader(headerfield);
			myHandler.sendPostMsg(dstMRN, payload, 10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void terminateServer() {
		myHandler.terminateServer();
	}
}
