package tc04_message_sequence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import kr.ac.kaist.mms_client.MMSClientHandler;

/** 
File name : MessageSequenceClient.java
	message sequence test
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-10-13

Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS4_Test to MessageSequenceClient
	Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/
public class MessageSequenceClient {
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-04-client";
	//private String svcMRN = "urn:mrn:imo:imo-no:ts-mms-04-server";
	//private String dstMRN = "urn:mrn:smart-navi:device:mms1";
	private String dstMRN = "urn:mrn:imo:imo-no:ts-mms-04-server";
	private MMSClientHandler sender = new MMSClientHandler(myMRN);
	private int response = 0;
	String message = " SC2 ¾È³ç hi \"hello\"";
	Map<String, List<String>> headerfield = new HashMap<String, List<String>>();

	public MessageSequenceClient() throws Exception {

		sender.setSender(new MMSClientHandler.ResponseCallback() {

			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				if (headerField.get("Response-code") != null) {
					int code = Integer.parseInt(headerField.get("Response-code").get(0));
					response = code;
				}

			}
		});

	}

	public void SendRandomSequence(int delay) throws Exception {
		List<MsgSenderThread> thrList = new ArrayList<MsgSenderThread>();
		for (int i = 0; i < 10; i++) {
			thrList.add(new MsgSenderThread(myMRN, headerfield, dstMRN, i + message, i));
		}
		thrList.get(0).start();
		Thread.sleep(delay);
		thrList.get(2).start();
		Thread.sleep(delay);
		thrList.get(1).start();
		Thread.sleep(delay);
		thrList.get(3).start();
		Thread.sleep(delay);
		thrList.get(5).start();
		Thread.sleep(delay);
		thrList.get(4).start();
		Thread.sleep(delay);
		thrList.get(8).start();
		Thread.sleep(delay);
		thrList.get(6).start();
		Thread.sleep(delay);
		thrList.get(7).start();
		Thread.sleep(delay);
		thrList.get(9).start();
		thrList.get(9).join();

	}
	public void SendSortedSequence(int delay) throws Exception {
		List<MsgSenderThread> thrList = new ArrayList<MsgSenderThread>();
		for (int i = 0; i < 10; i++) {
			thrList.add(new MsgSenderThread(myMRN, headerfield, dstMRN, i + message, i));
		}
		thrList.get(0).start();
		Thread.sleep(delay);
		thrList.get(1).start();
		Thread.sleep(delay);
		thrList.get(2).start();
		Thread.sleep(delay);
		thrList.get(3).start();
		Thread.sleep(delay);
		thrList.get(4).start();
		Thread.sleep(delay);
		thrList.get(5).start();
		Thread.sleep(delay);
		thrList.get(6).start();
		Thread.sleep(delay);
		thrList.get(7).start();
		Thread.sleep(delay);
		thrList.get(8).start();
		Thread.sleep(delay);
		thrList.get(9).start();
		thrList.get(9).join();

	}
}
