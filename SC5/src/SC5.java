import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import kr.ac.kaist.mms_client.*;

public class SC5 {
	public static void main(String args[]) throws Exception{
		String myMRN;
		//myMRN = args[0];
		myMRN = "urn:mrn:imo:imo-no:0000112";
		//Service Consumer can be HTTP server and listen to port 'port'. 
		//port = Integer.parseInt(args[1]);
		int port = 8906;
		
		MMSConfiguration.MMSURL="127.0.0.1:8088";
		MMSConfiguration.CMURL="127.0.0.1";

		MMSClientHandler ch = new MMSClientHandler(myMRN);
		ch.setPort(port);
		//Request Callback from the request message
		ch.setReqCallBack(new MMSClientHandler.ReqCallBack() {
			
			//it is called when client receives a message
			@Override
			public String callbackMethod(Map<String,List<String>>  header, String message) {
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
