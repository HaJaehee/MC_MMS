package tc01_relaying_variable_payload;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;

/** 
File name : TS1_server.java
	Relaying message function for the purpose of testing MMS
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2017-07-23

Rev.history :2018-10-13
Version : 0.8.0
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)

Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS1_Test to MMSMessagewithVariablePayloadServer
	Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

public class MMSMessagewithVariablePayloadServer {
	private int response = 0;
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-01-server";
	private String dstMRN = "urn:mrn:imo:imo-no:ts-mms-01-client";
	private MMSClientHandler myHandler = null;
	private static int content_length = 0;
	private static int length = -1;
	
	
	public MMSMessagewithVariablePayloadServer() throws Exception {
		myHandler = new MMSClientHandler(myMRN);
		int port = 8907;
		
		myHandler.setServerPort(port, new MMSClientHandler.RequestCallback() {
			
			@Override
			public Map<String, List<String>> setResponseHeader() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int setResponseCode() {
				// TODO Auto-generated method stub
				return 200;
			}
			
			@Override
			public String respondToClient(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				List<String> list = headerField.get("content-length");															
			
				if(list != null){
//					System.out.println("list" +list.get(0));
				
//					System.out.println("message : "+messages.get(0));
					
					content_length = Integer.parseInt(list.get(0));
					
					length = content_length;
					}
				
				
				return "OK";
			}
		});
	}
	
	public static int getContentLength() {
		return content_length;
	}

	public void terminateServer() {
		myHandler.terminateServer();
	}
}
