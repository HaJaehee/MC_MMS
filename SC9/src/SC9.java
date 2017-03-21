import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;
import kr.ac.kaist.mms_client.SecureMMSClientHandler;

/* -------------------------------------------------------- */
/** 
File name : SC9.java
	Service Consumer cannot be HTTPS server and should poll from MMS by https. 
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-21
Version : 0.4.0
*/
/* -------------------------------------------------------- */

public class SC9 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:1000009";
		//myMRN = args[0];
		

		//MMSConfiguration.MMS_URL="winsgkwogml.iptime.org:444";
		
		//Service Consumer cannot be HTTPs server and should poll from MMS by https. 
		SecureMMSClientHandler sph = new SecureMMSClientHandler(myMRN);
		
		int pollInterval = 1000;
		sph.startPolling("urn:mrn:smart-navi:device:mms1", pollInterval);
		
		//Request Callback from the request message
		sph.setCallback(new SecureMMSClientHandler.Callback() {
			
			//it is called when client receives a message
			@Override
			public String callbackMethod(Map<String,List<String>>  headerField, String message) {
				System.out.println(message);
				return "OK";
			}
		});

	}
}
