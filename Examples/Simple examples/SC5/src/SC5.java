import java.util.ArrayList;
import java.util.HashMap;
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

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-05-02
Version : 0.5.4
	Added setting response header
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-21
Version : 0.7.0
	Compatible with MMS Client beta-0.7.0.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	
*/
/* -------------------------------------------------------- */

public class SC5 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:1000005";
		//myMRN = args[0];
		
		//Service Consumer can be HTTP server and listen to port 'port'. 
		//port = Integer.parseInt(args[1]);
		int port = 8906;
		
		MMSConfiguration.MMS_URL = "127.0.0.1:8088";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.

		//Server example.
		MMSClientHandler server = new MMSClientHandler(myMRN);
		server.setServerPort(port, new MMSClientHandler.RequestCallback() {
			//Request Callback from the request message
			//it is called when client receives a message
			
			@Override
			public int setResponseCode() {
				// TODO Auto-generated method stub
				return 200; //response HTTP OK code
			}
			
			@Override
			public String respondToClient(Map<String,List<String>>  headerField, String message) {
				Iterator<String> iter = headerField.keySet().iterator();
				while (iter.hasNext()){
					String key = iter.next();
					System.out.println(key+":"+headerField.get(key).toString());
				}
				System.out.println(message);
				
				return "OK";
			}

			@Override
			public Map<String, List<String>> setResponseHeader() {
				// TODO Auto-generated method stub
				
				Map<String, List<String>> myHdr = new HashMap<String, List<String>>();
				ArrayList<String> hi = new ArrayList<String>();
				hi.add("hello");
				myHdr.put("hi", hi);
				return myHdr;
			}
		});
		

	}
}
