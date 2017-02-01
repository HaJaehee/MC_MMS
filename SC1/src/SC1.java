import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

public class SC1 {
	public static void main(String args[]) throws Exception{
		String myMRN;
		int port;
		//myMRN = args[0];
		myMRN = "urn:mrn:imo:imo-no:0100006";

		MMSConfiguration.MMSURL="127.0.0.1:8088";
		MMSConfiguration.CMURL="127.0.0.1";
		
		//Service Consumer cannot be HTTP server and should poll from MMS. 
		MMSClientHandler ph = new MMSClientHandler(myMRN);
		int pollInterval = 1000;
		ph.startPolling("urn:mrn:smart-navi:device:mms1", pollInterval);
		
		//Request Callback from the request message
		ph.setReqCallBack(new MMSClientHandler.ReqCallBack() {
			
			//it is called when client receives a message
			@Override
			public String callbackMethod(Map<String,List<String>>  header, String message) {
				System.out.println(message);
				return "OK";
			}
		});
		
	}
}
