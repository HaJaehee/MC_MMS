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

Rev. history : 2017-11-21
Version : 0.7.0
	Compatible with MMS Client beta-0.7.0.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class ServiceProvider {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:smart-navi:device:tm-server";
		int port = 8902;

		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.
		
		MMSClientHandler server = new MMSClientHandler(myMRN);
		MMSClientHandler sender = new MMSClientHandler(myMRN);
		sender.setSender(new MMSClientHandler.ResponseCallback() {
			//Response Callback from the request message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				System.out.println(message);
			}
		});
		String context = "/forwarding";
		server.setServerPort(port, context, new MMSClientHandler.RequestCallback() {
			//Request Callback from the request message
			//it is called when client receives a message
			
			@Override
			public int setResponseCode() {
				// TODO Auto-generated method stub
				return 200;
			}
			
			@Override
			public String respondToClient(Map<String,List<String>> headerField, String message) {
				try {
					Iterator<String> iter = headerField.keySet().iterator();
					while (iter.hasNext()){
						String key = iter.next();
						System.out.println(key+":"+headerField.get(key).toString());
					}
					System.out.println(message);

					//it only forwards messages to sc having urn:mrn:imo:imo-no:1000001
					String dstMRN = "urn:mrn:imo:imo-no:1000001";
					sender.sendPostMsg(dstMRN, message);
				} catch (Exception e) {
					e.printStackTrace();
				}
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

	}
}
