import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/* -------------------------------------------------------- */
/** 
File name : SC10.java
	Service Consumer cannot be HTTP server and should poll from MMS. 
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-20
Version : 0.5.0
*/
/* -------------------------------------------------------- */

public class SC10 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:1000010";
		//myMRN = args[0];
		

		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		
		//Service Consumer cannot be HTTP server and should poll from MMS. 
		MMSClientHandler ph = new MMSClientHandler(myMRN);
		
		
		String dstMRN = "urn:mrn:smart-navi:device:mms1";
		String svcMRN = "urn:mrn:smart-navi:device:tm-server2";
		int pollInterval = 1;
		ph.startPolling(dstMRN, svcMRN, pollInterval);
		
		//it is called when client receives a message
		ph.setPollingResponseCallback(new MMSClientHandler.PollingResponseCallback() {
			
			@Override
			public String callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				Iterator<String> iter = headerField.keySet().iterator();
				while (iter.hasNext()){
					String key = iter.next();
					System.out.println(key+":"+headerField.get(key).toString());
				}
				System.out.println(message);
				return "OK";
			}
		});

	}
}