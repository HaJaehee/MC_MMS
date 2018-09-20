import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/** 
File name : TS1_client.java
	Relaying message function for the purpose of testing MMS
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2017-07-23
*/

public class TS1_client {
	//public static void main(String[] args) throws Exception{
	private int response = 0;
	private int content_length = 0;
	private static int length = -1;
	private String dstMRN = "urn:mrn:smart-navi:device:mms1";
	private String svcMRN = "urn:mrn:imo:imo-no:ts-mms-01-server";
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-01-client";
	private MMSClientHandler sender = new MMSClientHandler(myMRN);
	
	public TS1_client() throws Exception {		

		MMSConfiguration.MMS_URL="143.248.57.144:8088";			
		
		sender.setSender(new MMSClientHandler.ResponseCallback() {
			
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				
				if(headerField.get("Response-code")!=null){
					int code = Integer.parseInt(headerField.get("Response-code").get(0));
					response = code;				
				}
				
//				System.out.println("Client Side");
				/*Iterator<String> iter = headerField.keySet().iterator();
				while (iter.hasNext()){
					String key = iter.next();
					List<String> contents = headerField.get(key);
					Iterator<String> citer = contents.iterator();
					if(key != null)
						System.out.print(key + ": ");
					while(citer.hasNext()){
						String content = citer.next();
						System.out.print(content + " ");
					}
					System.out.println();
				}
				System.out.println();*/
			}
		});
	}
	public void sendContentLength(int actual) {
		String data =createDataSize(actual);
		try {
			sender.sendPostMsg(svcMRN, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String createDataSize(int size){
		StringBuilder data = new StringBuilder(size);
		
		for(int i = 0 ; i < size; i ++){
			data.append('a');
		}
		
		return data.toString();
	}
	public int getResponse() {
		return response;
	}
}
