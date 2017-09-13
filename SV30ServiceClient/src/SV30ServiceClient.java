import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

public class SV30ServiceClient {
	
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:smart:vessel:imo-no:mof:5000030";
		//myMRN = args[0];
		
		MMSConfiguration.MMS_URL="143.248.55.83:8088";
		
		//Service Consumer cannot be HTTP server and should poll from MMS. 
		MMSClientHandler polling = new MMSClientHandler(myMRN);
		
		int pollInterval = 2000;
		String dstMRN = "urn:mrn:smart-navi:device:mms1";
		String svcMRN = "urn:mrn:smart:service:inst:mof:sv30";
		polling.startPolling(dstMRN, svcMRN, pollInterval, new MMSClientHandler.PollingResponseCallback() {
			//Response Callback from the polling message
			//it is called when client receives a message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
				// TODO Auto-generated method stub
				for (String s : messages) {
					System.out.println(s);
				}
			}
		});
	}
}
