import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;
import kr.ac.kaist.mms_client.SecureMMSClientHandler;

/* -------------------------------------------------------- */
/** 
File name : SecureSC1.java
	Service Consumer cannot be HTTPS server and should poll from MMS by HTTPS. 
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-21
Version : 0.4.0

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-07-28
Version : 0.5.9
	Changed from PollingResponseCallback.callbackMethod(Map<String,List<String>> headerField, message) 
	     to PollingResponseCallback.callbackMethod(Map<String,List<String>> headerField, List<String> messages) 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-21
Version : 0.7.0
	Compatible with MMS Client beta-0.7.0.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	
*/
/* -------------------------------------------------------- */

public class SecureSC1 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:secure-1000001";
		//myMRN = args[0];
		
		MMSConfiguration.MMS_URL="mms.smartnav.org:444";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.
		
		
		//Service Consumer cannot be HTTPs server and should poll from MMS by HTTPS. 
		SecureMMSClientHandler securePolling = new SecureMMSClientHandler(myMRN);
		
		int pollInterval = 1000; // Unit is millisecond. 
		String dstMRN = "urn:mrn:smart-navi:device:mms1";
		String svcMRN = "urn:mrn:smart-navi:device:secure-tm-server";
		securePolling.startPolling(dstMRN, svcMRN, pollInterval, new SecureMMSClientHandler.PollingResponseCallback() {
			//Response Callback from the polling message
			//it is called when client receives a message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
				// TODO Auto-generated method stub
				for (String s : messages) {
					System.out.print(s);
				}
			}
		});
		
		
		// Stopping polling example.
		Thread.sleep(10000); // After 10 seconds,
		securePolling.stopPolling(); // stop polling.
	}
}
