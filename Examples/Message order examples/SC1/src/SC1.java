import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/* -------------------------------------------------------- */
/** 
File name : SC1.java
	Service Consumer cannot be HTTP server and should poll from MMS. 
	Added API; message sender guarantees message sequence.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-07-23

*/
/* -------------------------------------------------------- */

public class SC1 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:1000001";
		//myMRN = args[0];
		

		MMSConfiguration.MMS_URL = "127.0.0.1:8088";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.
		
		//Service Consumer cannot be HTTP server and should poll from MMS. 
		MMSClientHandler polling = new MMSClientHandler(myMRN);
		
		int pollInterval = 1000; // Unit is millisecond. 
		String dstMRN = "urn:mrn:smart-navi:device:mms1";
		String svcMRN = "urn:mrn:imo:imo-no:1000002";
		polling.startPolling(dstMRN, svcMRN, pollInterval, new MMSClientHandler.PollingResponseCallback() {
			//Response Callback from the polling message
			//it is called when client receives a message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
				// TODO Auto-generated method stub
				for (String s : messages) {
					System.out.print(s);
				}
				
				// Stopping polling example.
				polling.stopPolling(); // stop polling.
			}
		});
		
	
		//Thread.sleep(10000); // After 10 seconds,

	}
}
