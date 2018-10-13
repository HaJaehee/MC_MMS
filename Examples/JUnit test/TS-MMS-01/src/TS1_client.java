import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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

Rev.history :2018-10-13
Version : 0.8.0
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)

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

		MMSConfiguration.MMS_URL="143.248.55.83:8088";			
		
		sender.setSender(new MMSClientHandler.ResponseCallback() {
			
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				
				if(headerField.get("Response-code")!=null){
					int code = Integer.parseInt(headerField.get("Response-code").get(0));
					response = code;				
				}				

			}
		});
	}
	public void sendContentLength(String FileName ,int actual) throws IOException {
		
		File file = new File(FileName);
		
		
		System.out.println(FileName);		
		FileReader fileReader = new FileReader(file);
		BufferedReader bufReader = new BufferedReader(fileReader);
		
		String data =""; //createDataSize(actual);
		data=bufReader.readLine();				
		try {
			sender.sendPostMsg(svcMRN, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	public int getResponse() {
		return response;
	}
}
