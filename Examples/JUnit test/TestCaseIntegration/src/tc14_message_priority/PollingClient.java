package tc14_message_priority;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
//import kr.ac.kaist.mms_client.PollHandler;
import net.etri.pkilib.client.ClientPKILibrary;
import net.etri.pkilib.tool.ByteConverter;

/**
File name : PollingClient.java
Author : Jin Jeong (jungst0001@kaist.ac.kr) 
Creation Date : 2019-09-17


Rev. history : 2019-09-17
Version : 0.9.5
	Add Constructor parameter for MRN.
	
	Modifier : Yunho Choi (choiking10@kaist.ac.kr)


 */

public class PollingClient {
	private String payload = null;
	private String priority = null;
	private int content_length = 0;

	private String myMRN;
	private String dstMRN;
	private String svcMRN;
	private MMSClientHandler myHandler = null;
	private MMSClientHandler.PollingResponseCallback callback = null;
	private static int length = -1;
	
	public PollingClient(String myMRN, String dstMRN, String svcMRN){
		this.myMRN = myMRN;
		this.dstMRN = dstMRN;
		this.svcMRN = svcMRN;
		
		try {
			myHandler = new MMSClientHandler(myMRN);
			callback = new MMSClientHandler.PollingResponseCallback() {
				
				@Override
				public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
					// TODO Auto-generated method stub					
//					List<String> list = headerField.get("priority");
					//System.out.println("list" +list.get(0));		
					
					
					List<String> list = headerField.get("content-length");
					//System.out.println("list" +list.get(0));		
															
				
					if(list != null){						
						content_length = Integer.parseInt(list.get(0));
						length = content_length;
						payload = messages.toString();
					}
				}
			};
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		pollingReqeust();//empty queue
	}
	
	public int pollingReqeust(){
		int retLength = -1; 
		try {
			myHandler.startPolling(dstMRN, svcMRN, getSignedData(true) ,1000, 10000, callback);

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
		myHandler.stopPolling();
		return retLength;
	}
	
	public String getSignedData(boolean isActive) {
		ClientPKILibrary clientPKILib = ClientPKILibrary.getInstance();
		ByteConverter byteConverter = ByteConverter.getInstance();
		
		//===== dummy content =====
		byte[] content = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
		
		String signedData = null;
		
		if (isActive) {
			//===== active certificate =====
			String privateKeyPath_active = "certs/PrivateKey_POUL_LOWENORN_active.pem";
			String certPath_active = "certs/Certificate_POUL_LOWENORN_active.pem";
			
			byte[] signedData_active = clientPKILib.generateSignedData(content, privateKeyPath_active, certPath_active);
			String hexSignedData_active = byteConverter.byteArrToHexString(signedData_active);
			signedData = hexSignedData_active;
		}
		else {
			//===== revoked certificate =====
			String privateKeyPath_revoked = "certs/PrivateKey_POUL_LOWENORN_revoked.pem";
			String certPath_revoked = "certs/Certificate_POUL_LOWENORN_revoked.pem";
			
			byte[] signedData_revoked = clientPKILib.generateSignedData(content, privateKeyPath_revoked, certPath_revoked);
			String hexSignedData_revoked = byteConverter.byteArrToHexString(signedData_revoked);
			signedData = hexSignedData_revoked;
		}
		
		return signedData;
	}
	
	public String getPriority() { return priority; }
	public String getPayload() { return payload; }
}
