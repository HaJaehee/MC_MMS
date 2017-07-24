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
	public static void main(String[] args) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:ts-mms-01-client";

		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		
		String dstMRN = "urn:mrn:smart-navi:device:mms1";
		String svcMRN = "urn:mrn:imo:imo-no:ts-mms-01-server";
		
		MMSClientHandler sender = new MMSClientHandler(myMRN);
		sender.setSender(new MMSClientHandler.ResponseCallback() {
			
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
//				System.out.println("Client Side");
				Iterator<String> iter = headerField.keySet().iterator();
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
				System.out.println();
			}
		});
		
		String data = null;
//		
//		data = createDataSize(0);
//		sender.sendPostMsg(svcMRN, data);
//		
//		data = createDataSize(170);
//		sender.sendPostMsg(svcMRN, data);
//
		data = createDataSize(20000);
//		sender.sendPostMsg(svcMRN, data);
//		
//		data = createDataSize(500 * 1024);
		sender.sendPostMsg(svcMRN, data);
//		
//		data = testData.createDataSize(2 * 1024 * 1024);
//		sender.sendPostMsg(svcMRN, data);
//		
//		data = testData.createDataSize(7 * 1024 * 1024);
//		sender.sendPostMsg(svcMRN, data);
//		
//		data = testData.createDataSize(10 * 1024 * 1024);
//		sender.sendPostMsg(svcMRN, data);
//		
//		data = testData.createDataSize(20 * 1024 * 1024);
//		sender.sendPostMsg(svcMRN, data);
//		
//		data = testData.createDataSize(30 * 1024 * 1024);
//		sender.sendPostMsg(svcMRN, data);
//		
//		data = testData.createDataSize(40 * 1024 * 1024);
//		sender.sendPostMsg(svcMRN, data);
	}
	
	public static String createDataSize(int size){
		StringBuilder data = new StringBuilder(size);
		
		for(int i = 0 ; i < size; i ++){
			data.append('a');
		}
		
		return data.toString();
	}
}
