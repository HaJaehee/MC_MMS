import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/** 
File name : TS1_server.java
	Relaying message function for the purpose of testing MMS
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2017-07-23
*/

public class TS1_server {
	private int response = 0;
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-01-server";
	private String dstMRN = "urn:mrn:imo:imo-no:ts-mms-01-client";
	private MMSClientHandler myHandler = null;
	private static int content_length = 0;
	private static int length = -1;
	
	
	public TS1_server() throws Exception {
		MMSConfiguration.MMS_URL="143.248.57.144:8088";
		MMSClientHandler server = new MMSClientHandler(myMRN);
		int port = 8907;
		
		server.setServerPort(port, new MMSClientHandler.RequestCallback() {
			
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
					//System.out.println("list" +list.get(0));
					
					//System.out.println("message : "+messages.get(0));
					
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
	/*public static void main(String[] args) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:ts-mms-01-server";

		MMSConfiguration.MMS_URL="143.248.57.144:8088";
		
		MMSClientHandler server = new MMSClientHandler(myMRN);
		int port = 8907;
		server.setServerPort(port, new MMSClientHandler.RequestCallback() {
			
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
				try {
					Iterator<String> iter = headerField.keySet().iterator();
					while (iter.hasNext()){
						String key = iter.next();
						System.out.println(key+":"+headerField.get(key).toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println();
				
				return "OK";
			}
		});


		
	}*/
}
