import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : ServiceProvider.java
	Service Provider only forwards messages to SC having urn:mrn:imo:imo-no:1000001
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03

Rev. history : 2017-02-01
Version : 0.3.01
	Added header field features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-05-02
Version : 0.5.4
	Added setting response header
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

*/
/* -------------------------------------------------------- */

public class SP_OnBoardTest {
	static int start = 0;
	public static void main(String args[]) throws Exception{
		//String myMRN = "urn:mrn:smart:service:instance:mof:onKAIST";
		String myMRN = "urn:mrn:smart:service:instance:mof:onEarthServer";
		
		int port = 8902;

		MMSConfiguration.MMS_URL="mms-kaist.com:8088";
		MMSConfiguration.DEBUG = false;
		MMSClientHandler server = new MMSClientHandler(myMRN);
		
			
		server.setServerPort(port, "/forwarding", new MMSClientHandler.RequestCallback() {
			//Request Callback from the request message
			//it is called when client receives a message			
			@Override
			public int setResponseCode() {
				// TODO Auto-generated method stub
				return 200;
			}
			
			@Override
			public String respondToClient(Map<String,List<String>> headerField, String message) {
				start = 1;
				return "OK";
			}
			@Override
			public Map<String, List<String>> setResponseHeader() {
				// TODO Auto-generated method stub
				return null;
			}
		}); //server has a context '/forwarding'
		/* It is not same with:
		 * server.setPort(port); //It sets default context as '/'
		 * server.addContext("/forwarding"); //Finally server has two context '/' and '/forwarding'
		 */
		
		
		int count = 0;
		MMSClientHandler sender = new MMSClientHandler(myMRN);
		sender.setSender(new MMSClientHandler.ResponseCallback() {
			//Response Callback from the request message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				System.out.println(message);
			}
		});
		while (true){
			Thread.sleep(1000);
			count = count+1;
			sender.sendPostMsg("urn:mrn:smart:vessel:imo-no:mof:onBoardVessel", ""+count+"th message");
			
		}
		

	}
}
