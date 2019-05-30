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
import kr.ac.kaist.mms_client.MMSConfiguration;

/** 
File name : TS4_client.java
	message sequence test
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-10-13
*/
public class TS4_client {
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-04-client";
	//private String svcMRN = "urn:mrn:imo:imo-no:ts-mms-04-server";
	//private String dstMRN = "urn:mrn:smart-navi:device:mms1";
	private String dstMRN = "urn:mrn:imo:imo-no:ts-mms-04-server";
	private MMSClientHandler sender = new MMSClientHandler(myMRN);
	private int response = 0;
	String message = " SC2 �ȳ� hi \"hello\"";
	Map<String, List<String>> headerfield = new HashMap<String, List<String>>();

	public TS4_client() throws Exception {
		MMSConfiguration.MMS_URL = "mms-kaist.com:8088";

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
