package TS3;
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
import kr.ac.kaist.mms_client.MMSConfiguration;

/**
 * File name : TS3_server.java Polling request message function for the purpose
 * of testing MMS This class acts as signal generator Author : Jin Jeong
 * (jungst0001@kaist.ac.kr) Creation Date : 2017-11-06

Rev.history :2018-10-13
Version : 0.8.0
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)

 */

public class TS3_server {
	private int response = 0;
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-03-server";
//	private String dstMRN = "urn:mrn:imo:imo-no:ts-mms-03-client";
	private String dstMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	private MMSClientHandler myHandler = null;

	public TS3_server() {
		MMSConfiguration.MMS_URL = "mms-kaist.com:8088";

		MMSConfiguration.DEBUG = false;

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

	public void sendContent(String FileName, int content) throws IOException {
		File file = new File(FileName);

		System.out.println(FileName);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufReader = new BufferedReader(fileReader);

		String data = ""; // createDataSize(actual);
		data = bufReader.readLine();
		if(data==null)
			data="";
		try {
			myHandler.sendPostMsg(dstMRN, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*String data = createDataSize(content);
		try {
			myHandler.sendPostMsg(dstMRN, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	public void terminateServer() {
		myHandler.terminateServer();
	}
}
