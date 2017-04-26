import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;
import kr.ac.kaist.mms_client.SecureMMSClientHandler;

/* -------------------------------------------------------- */
/** 
File name : SC9.java
	Service Consumer cannot be HTTPS server and should poll from MMS by HTTPS. 
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-21
Version : 0.4.0

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class SC9 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:1000009";
		//myMRN = args[0];
		

		//MMSConfiguration.MMS_URL="winsgkwogml.iptime.org:444";
		
		//Service Consumer cannot be HTTPs server and should poll from MMS by HTTPS. 
		SecureMMSClientHandler sph = new SecureMMSClientHandler(myMRN);
		
		int pollInterval = 1;
		String dstMRN = "urn:mrn:smart-navi:device:mms1";
		String svcMRN = "urn:mrn:smart-navi:device:secure-tm-server";
		sph.startPolling(dstMRN, svcMRN, pollInterval, new SecureMMSClientHandler.PollingResponseCallback() {
			//Response Callback from the polling message
			//it is called when client receives a message
			@Override
			public void callbackMethod(Map<String,List<String>>  headerField, String message) {
				System.out.print(message);
			}
		});
	}
}
