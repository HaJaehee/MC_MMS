import java.io.IOException;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/** 
File name : TS6_Client.java
	Polling messsage authentication tests
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-04-16
*/

public class TS6_Client {
	private String myMRN;
	private String dstMRN;
	private String svcMRN;
	private MMSClientHandler handler = null;
	private MMSClientHandler.PollingResponseCallback callback = null;
	public static String sentMessage = null;
	
	public TS6_Client() {
		this.myMRN = TS6_Test.clientMRN;
		this.dstMRN = "urn:mrn:smart-navi:device:mms1";
		this.svcMRN = TS6_Test.serverMRN;
		MMSConfiguration.MMS_URL = TS6_Test.MMS_URL;
		
		try {
			this.handler = new MMSClientHandler(this.myMRN);
			this.handler.setSender(new MMSClientHandler.ResponseCallback() {
				
				@Override
				public void callbackMethod(Map<String, List<String>> headerField, String message) {
					// TODO Auto-generated method stub
					sentMessage = message;
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public TS6_Client(String myMRN) {
		this.myMRN = myMRN;
		this.dstMRN = "urn:mrn:smart-navi:device:mms1";
		this.svcMRN = TS6_Test.serverMRN;
		MMSConfiguration.MMS_URL = TS6_Test.MMS_URL;
		
		try {
			this.handler = new MMSClientHandler(this.myMRN);
			this.handler.setSender(new MMSClientHandler.ResponseCallback() {
				
				@Override
				public void callbackMethod(Map<String, List<String>> headerField, String message) {
					// TODO Auto-generated method stub
					sentMessage = message;
				}
			});
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
}



