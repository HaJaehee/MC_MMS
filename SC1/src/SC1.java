import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/* -------------------------------------------------------- */
/** 
File name : SC1.java
	Service Consumer cannot be HTTP server and should poll from MMS. 
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03
Version : 0.3.01
Rev. history : 2017-02-01
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class SC1 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:0100006";
		//myMRN = args[0];
		

		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		
		//Service Consumer cannot be HTTP server and should poll from MMS. 
		MMSClientHandler ph = new MMSClientHandler(myMRN);
		
		int pollInterval = 1000;
		ph.startPolling("urn:mrn:smart-navi:device:mms1", pollInterval);
		
		//Request Callback from the request message
		ph.setCallback(new MMSClientHandler.Callback() {
			
			//it is called when client receives a message
			@Override
			public String callbackMethod(Map<String,List<String>>  headerField, String message) {
				System.out.println(message);
				return "OK";
			}
		});

	}
}
