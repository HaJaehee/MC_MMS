import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;
//import kr.ac.kaist.mms_client.PollHandler;

/** 
File name : TS3_client.java
	Polling request message function for the purpose of testing MMS
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2017-11-06
*/

public class TS3_client {
	private int content_length = 0;
	
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-03-client";
	private String dstMRN = "urn:mrn:smart-navi:device:mms1";
	private String svcMRN = "urn:mrn:imo:imo-no:ts-mms-03-server";
	private MMSClientHandler myHandler = null;
	private MMSClientHandler.PollingResponseCallback callback = null;
	private static int length = -1;
	
	public TS3_client(){
		MMSConfiguration.MMS_URL="143.248.55.83:8088";
//		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		MMSConfiguration.DEBUG = false;
		
		try {
			myHandler = new MMSClientHandler(myMRN);
			callback = new MMSClientHandler.PollingResponseCallback() {
				
				@Override
				public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
					// TODO Auto-generated method stub
					List<String> list = headerField.get("content-length");
					if(list != null){
						content_length = Integer.parseInt(list.get(0));
						length = content_length;
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
			myHandler.startPolling(dstMRN, svcMRN, 1, callback);

			while(length==-1){ //busy waiting the content length
				Thread.sleep(1);
				if(length != -1){
					retLength = length;
					length = -1;
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (InterruptedException e){
			
		}
		
		return retLength;
	}

}
