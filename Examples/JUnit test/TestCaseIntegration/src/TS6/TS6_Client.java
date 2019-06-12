package TS6;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/** 
File name : TS6_Client.java
	Polling messsage authentication tests
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-04-16

Rev. history : 2019-04-23
	Add server which is MMSClientHandler.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

public class TS6_Client {
	private String myMRN;
	private String dstMRN;
	private String svcMRN;
	private MMSClientHandler handler = null;
	private MMSClientHandler.PollingResponseCallback callback = null;
	public static String sentMessage = null;
	private static int length = -1;
	
	public TS6_Client() {
		this(false, TS6_Test.clientMRN);
	}
	
	public TS6_Client(String myMRN) {
		this(false, myMRN);
	}
	
	public TS6_Client(boolean isPolling, String myMRN) {
		this.myMRN = myMRN;
		this.dstMRN = "urn:mrn:smart-navi:device:mms1";
		this.svcMRN = TS6_Test.serverMRN;
		MMSConfiguration.MMS_URL = TS6_Test.MMS_URL;

		try {
			if (isPolling) {
				this.handler = new MMSClientHandler(this.myMRN);
				callback = new MMSClientHandler.PollingResponseCallback() {
					
					@Override
					public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
						// TODO Auto-generated method stub
						
					}
				};
			}
			else {
				this.handler = new MMSClientHandler(this.myMRN);
				this.handler.setSender(new MMSClientHandler.ResponseCallback() {
					
					@Override
					public void callbackMethod(Map<String, List<String>> headerField, String message) {
						// TODO Auto-generated method stub
						if (MMSConfiguration.DEBUG) {
							Iterator<String> iter = headerField.keySet().iterator();
							while (iter.hasNext()){
								String key = iter.next();
								System.out.println(key+":"+headerField.get(key).toString());// Print the matched header field and the header contents.
							}
						}
						
						List<String> messages = new ArrayList<String>();
						try {
							if (message.length() != 0){
								JSONArray jsonArr = new JSONArray();
								JSONParser jsonPars = new JSONParser();

								jsonArr = (JSONArray) jsonPars.parse(message);
								for (int i = 0 ; i < jsonArr.size() ; i++) {
									messages.add(URLDecoder.decode(jsonArr.get(i).toString(), "UTF-8"));
								}
								sentMessage = messages.get(0);
								return;
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
//							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
//							e.printStackTrace();
						}
						
//						sentMessage = messages.get(0);
						sentMessage = message;
					}
				});
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void sendPollingMessage(String data) {
		try {
			handler.sendPostMsg(dstMRN, "polling", data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendPollingMessage(String dstMRN, String data) {
		try {
			handler.sendPostMsg(dstMRN, "polling", data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getServiceMRN () { return this.svcMRN; }
	
	public int pollingReqeust(){
		if (callback != null) {
			int retLength = -1; 
			try {
				handler.startPolling(dstMRN, svcMRN, 1000, callback);
	
				while(length==-1){ //busy waiting the content length		
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(length != -1){					
						retLength = length;
						System.out.println("retLength : "+ retLength);
						length = -1;
						break;
					}
					
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
	//			e.printStackTrace();
			} 
			handler.stopPolling();
			return retLength;
		}
		
		return -1;
	}
}



