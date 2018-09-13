import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

public class TS2_client {
	String response = null;
	private int content_length = 0;
	private static int length = -1;
	private String dstMRN = "urn:mrn:smart-navi:device:mms1";
	private String svcMRN = "urn:mrn:imo:imo-no:ts-mms-02-server";
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-02-client";

	private MMSClientHandler sender = new MMSClientHandler(myMRN);
	private MMSClientHandler empty_sender = new MMSClientHandler("");
	public TS2_client() throws Exception {
		MMSConfiguration.MMS_URL = "143.248.57.144:8088";

		sender.setSender(new MMSClientHandler.ResponseCallback() {

			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub


//		System.out.println("Client Side");
				/*
				 * Iterator<String> iter = headerField.keySet().iterator(); while
				 * (iter.hasNext()){ String key = iter.next(); List<String> contents =
				 * headerField.get(key); Iterator<String> citer = contents.iterator(); if(key !=
				 * null) System.out.print(key + ": "); while(citer.hasNext()){ String content =
				 * citer.next(); System.out.print(content + " "); } System.out.println(); }
				 * System.out.println();
				 */
			}
		});
	}
	
	public void insertHeader01(String dstMRN,String srcMRN ) {
		Map<String, List<String>> headerfield = new HashMap<String, List<String>>(); // Header field example. You are able to remove this code.
		List<String> valueList = new ArrayList<String>(); 
		List<String> valueList2 = new ArrayList<String>(); 
		valueList.add(dstMRN);
		valueList2.add(srcMRN);
		headerfield.put("dstMRN",valueList);
		headerfield.put("srcMRN",valueList2);
		try {
			sender.setMsgHeader(headerfield);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String data ="a";
		
		try {
			sender.sendPostMsg(svcMRN, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	
	public void insertHeader03(ArrayList headername,ArrayList value,int index) { 
		Map<String, List<String>> headerfield = new HashMap<String, List<String>>(); // Header field example. You are able to remove this code.
		
		
		for(int i=0;i<index;i++)
		{
			List<String> valueList = new ArrayList<String>();
			valueList.add((String) value.get(i));
			headerfield.put((String) headername.get(i),valueList);
		}
		try {
			sender.setMsgHeader(headerfield);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String data ="a";
		
		try {
			sender.sendPostMsg(svcMRN, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public String sendmsg() {
		empty_sender.setSender(new MMSClientHandler.ResponseCallback() {

			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				System.out.println("message " +message);
				response=message;
			}
			
		});
		String data ="a";
		
		try {
			empty_sender.sendPostMsg(null, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}

}
