package tc02_relaying_variable_header;
/** 
File name : TS2_client.java
	Relaying message function for the purpose of testing MMS.
Author : YoungJin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-09-13

Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS2_Test to MMSMessagewithVariableHeaderClient
	Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;

public class MMSMessagewithVariableHeaderClient {
	String response = null;
	private int content_length = 0;
	private static int length = -1;
	private String dstMRN = "urn:mrn:smart-navi:device:mms1";
	private String svcMRN = "urn:mrn:imo:imo-no:ts-mms-02-server";
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-02-client";

	private MMSClientHandler sender = new MMSClientHandler(myMRN);
	private MMSClientHandler empty_sender = new MMSClientHandler("");
	public MMSMessagewithVariableHeaderClient() throws Exception {

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
			sender.sendPostMsg(svcMRN, data, 10000);
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
			sender.sendPostMsg(svcMRN, data, 10000);
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
			empty_sender.sendPostMsg(null, data, 10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}

}
