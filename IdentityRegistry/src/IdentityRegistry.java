import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.*;

public class IdentityRegistry{
	
	public static void main(String args[]) throws Exception{
		String myMRN;
		int port;
		//myMRN = args[0];
		myMRN = "urn:mrn:smart-navi:device:mir1";
		//port = Integer.parseInt(args[1]);
		port = 8904;
		
		MMSConfiguration.MMSURL="127.0.0.1:8088";
		MMSConfiguration.CMURL="127.0.0.1";
		
		MMSClientHandler ch = new MMSClientHandler(myMRN);
		ch.setMIR(port);
		
		//Request Callback from the request message
		ch.setReqCallBack(new MMSClientHandler.ReqCallBack() {
			
			//it is called when client receives a message
			@Override
			public String callbackMethod(Map<String,List<String>> header, String message) {
				Iterator<String> iter = header.keySet().iterator();
				while (iter.hasNext()){
					String key = iter.next();
					System.out.println(key+":"+header.get(key).toString());
				}
				System.out.println(message);
				return "OK";
			}
		});

	}
	
		
		
}
