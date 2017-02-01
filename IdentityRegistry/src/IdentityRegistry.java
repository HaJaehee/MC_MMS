import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : IdentityRegistry.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-02-01
Version : 0.2.00
*/
/* -------------------------------------------------------- */

public class IdentityRegistry{
	
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:smart-navi:device:mir1";
		int port = 8904;
		//myMRN = args[0];
		//port = Integer.parseInt(args[1]);
		
		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		
		MMSClientHandler ch = new MMSClientHandler(myMRN);
		ch.setMIR(port);
		
		//Request Callback from the request message
		ch.setReqCallBack(new MMSClientHandler.ReqCallBack() {
			
			//it is called when client receives a message
			@Override
			public String callbackMethod(Map<String,List<String>> headerField, String message) {
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
