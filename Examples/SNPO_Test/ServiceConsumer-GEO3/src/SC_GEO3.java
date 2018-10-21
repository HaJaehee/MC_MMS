import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/* -------------------------------------------------------- */
/** 
File name : SC_GEO3.java
	It is used to test the geocasting
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Version : 0.8.0
Creation Date : 2018-10-21
*/
/* -------------------------------------------------------- */

public class SC_GEO3 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:mcp:vessel:smart:imo-9775763";

		MMSConfiguration.MMS_URL="192.168.202.193:8088";
		
		//Service Consumer cannot be HTTP server and should poll from MMS. 
		MMSClientHandler polling = new MMSClientHandler(myMRN);
		
		int pollInterval = 1000;
		String dstMRN = "urn:mrn:smart-navi:device:mms1";
		String svcMRN = "urn:mrn:mcp:service:instance:sp-geo";
		polling.startPolling(dstMRN, svcMRN, pollInterval, new MMSClientHandler.PollingResponseCallback() {
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
	}
}
