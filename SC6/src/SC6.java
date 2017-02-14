import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : SC5.java
	Service Consumer can be HTTP server and listen to port 'port'.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-02-01
Version : 0.3.01
*/
/* -------------------------------------------------------- */

public class SC6 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:0654321";
		//myMRN = args[0];
		
		//Service Consumer can be HTTP server and listen to port 'port'. 
		//port = Integer.parseInt(args[1]);
		int port = 8907;
		
		MMSConfiguration.MMS_URL="127.0.0.1:8088";

		MMSClientHandler ch = new MMSClientHandler(myMRN);
		ch.setPort(port);
		//Request Callback from the request message
		ch.setCallback(new MMSClientHandler.Callback() {
			
			//it is called when client receives a message
			@Override
			public String callbackMethod(Map<String,List<String>>  headerField, String message) {
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
