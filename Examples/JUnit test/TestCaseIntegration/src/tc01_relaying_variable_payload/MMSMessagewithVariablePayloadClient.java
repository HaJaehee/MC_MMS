package tc01_relaying_variable_payload;
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

/** 
File name : TS1_client.java
	Relaying message function for the purpose of testing MMS
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2017-07-23

Rev.history :2018-10-13
Version : 0.8.0
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)

Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS1_Test to MMSMessagewithVariablePayloadClient
	Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

public class MMSMessagewithVariablePayloadClient {
	//public static void main(String[] args) throws Exception{	
	
	private int response = 0;
	private int content_length = 0;
	private static int length = -1;
	private String dstMRN = "urn:mrn:smart-navi:device:mms1";
	private String svcMRN = "urn:mrn:imo:imo-no:ts-mms-01-server";
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-01-client";
	private MMSClientHandler sender = new MMSClientHandler(myMRN);
	
	public MMSMessagewithVariablePayloadClient() throws Exception {		
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
	public void sendContentLength(String FileName ,int actual, int timeout) throws IOException {
		
		File file = new File(FileName);
		
		
		System.out.println(FileName);		
		FileReader fileReader = new FileReader(file);
		BufferedReader bufReader = new BufferedReader(fileReader);
		
		String data =""; //createDataSize(actual);
		data=bufReader.readLine();		
		if (data == null) {
			data = "";
		}
		try {
			sender.sendPostMsg(svcMRN, data, timeout);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	public int getResponse() {
		return response;
	}
}
