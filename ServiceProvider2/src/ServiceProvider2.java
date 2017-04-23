import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.ac.kaist.mms_client.*;
import kr.ac.kaist.mms_client.MMSClientHandler.RequestCallback;

/* -------------------------------------------------------- */
/** 
File name : ServiceProvider2.java
	Service Provider only forwards messages to SC having urn:mrn:imo:imo-no:1000001
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-20
Version : 0.5.0
*/
/* -------------------------------------------------------- */

public class ServiceProvider2 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:smart-navi:device:tm-server2";
		int port = 8903;

		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		
		MMSClientHandler ch = new MMSClientHandler(myMRN);
		ch.setPort(port, "/forwarding"); //ch has a context '/forwarding'
		/* It is not same with:
		 * ch.setPort(port); //It sets default context as '/'
		 * ch.addContext("/forwarding"); //Finally ch has two context '/' and '/forwarding'
		 */
		
		ch.setRequestCallback(new MMSClientHandler.RequestCallback() {
			
			//it is called when client receives a message
			@Override
			public String callbackMethod(Map<String,List<String>> headerField, String message) {
				try {
					Iterator<String> iter = headerField.keySet().iterator();
					while (iter.hasNext()){
						String key = iter.next();
						System.out.println(key+":"+headerField.get(key).toString());
					}
					ch.setResponseCallback(new MMSClientHandler.ResponseCallback() {
						
						@Override
						public void callbackMethod(Map<String, List<String>> headerField, String message) {
							// TODO Auto-generated method stub
							System.out.println(message);
						}
					});
					System.out.println(message);
					//it only forwards messages to sc having urn:mrn:imo:imo-no:1000001
					ch.sendPostMsg("urn:mrn:imo:imo-no:1000010", message);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "OK";
			}

			@Override
			public int setResponseCode() {
				// TODO Auto-generated method stub
				return 200;
			}
		});
	}
}
