import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : ServiceProvider.java
	Service Provider only forwards messages to SC having urn:mrn:imo:imo-no:0100006
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03
Version : 0.3.01
Rev. history : 2017-02-01
	Added header field features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class ServiceProvider {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:smart-navi:device:tm-server";
		int port = 8902;

		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		
		MMSClientHandler ch = new MMSClientHandler(myMRN);
		ch.setPort(port, "/forwarding"); //ch has a context '/forwarding'
		/* It is not same with:
		 * ch.setPort(port); //It sets default context as '/'
		 * ch.addContext("/forwarding"); //Finally ch has two context '/' and '/forwarding'
		 */
		
		ch.setCallback(new MMSClientHandler.Callback() {
			
			//it is called when client receives a message
			@Override
			public String callbackMethod(Map<String,List<String>> headerField, String message) {
				try {
					Iterator<String> iter = headerField.keySet().iterator();
					while (iter.hasNext()){
						String key = iter.next();
						System.out.println(key+":"+headerField.get(key).toString());
					}
					System.out.println(message);
					JSONParser Jpar = new JSONParser();
					String httpBody = (String)((JSONObject) Jpar.parse(message)).get("HTTP Body");
					//it only forwards messages to sc having urn:mrn:imo:imo-no:0100006
					String res = ch.sendPostMsg("urn:mrn:imo:imo-no:0100006", httpBody);
					System.out.println(res);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "OK";
			}
		});
	}
}
