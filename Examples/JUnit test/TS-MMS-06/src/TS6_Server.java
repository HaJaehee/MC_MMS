import java.io.IOException;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/** 
File name : Server.java
	Polling messsage authentication tests
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-04-16
*/

public class TS6_Server {
	private String myMRN = null;
	private String dstMRN = null;
	private MMSClientHandler handler = null;
	
	public TS6_Server() {
		this.myMRN = TS6_Test.serverMRN;
		this.dstMRN = TS6_Test.clientMRN;
		MMSConfiguration.MMS_URL = TS6_Test.MMS_URL;
		
		try {
			handler = new MMSClientHandler(myMRN);
			handler.setSender(new MMSClientHandler.ResponseCallback() {
				
				@Override
				public void callbackMethod(Map<String, List<String>> headerField, String message) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendMessage() {
		String data = "Hello, polling client!";
		try {
			handler.sendPostMsg(dstMRN, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getMyMRN() { return myMRN; }
}
