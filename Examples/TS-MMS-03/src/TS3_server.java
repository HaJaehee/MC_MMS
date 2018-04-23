import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/** 
File name : TS3_server.java
	Polling request message function for the purpose of testing MMS
	This class acts as signal generator
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2017-11-06
*/

public class TS3_server {
	private int response = 0;
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-03-server";
	private String dstMRN = "urn:mrn:imo:imo-no:ts-mms-03-client";
	private MMSClientHandler myHandler = null;
	
	public TS3_server(){
		MMSConfiguration.MMS_URL="143.248.55.83:8088";
//		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		MMSConfiguration.LOGGING = false;
		
		try {
			myHandler = new MMSClientHandler(myMRN);
			
			myHandler.setSender(new MMSClientHandler.ResponseCallback() {
				
				@Override
				public void callbackMethod(Map<String, List<String>> headerField, String message) {
					// TODO Auto-generated method stub
					if(headerField.get("Response-code")!=null){
						int code = Integer.parseInt(headerField.get("Response-code").get(0));
						response = code;				
					}
				}	
				
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int getResponse(){
		return response;
	}
	
	public void sendContent(int content){
		String data = createDataSize(content);
		try {
			myHandler.sendPostMsg(dstMRN, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String createDataSize(int size){
		StringBuilder data = new StringBuilder(size);
		
		for(int i = 0 ; i < size; i ++){
			data.append("a");
		}
		
		return data.toString();
	}
}
