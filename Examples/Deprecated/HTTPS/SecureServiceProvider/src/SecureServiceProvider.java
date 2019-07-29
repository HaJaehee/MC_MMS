import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : SecureServiceProvider.java
	HTTPS Service Provider only forwards messages to SC having urn:mrn:imo:imo-no:1000009 by HTTPS
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-21
Version : 0.4.0

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

public class SecureServiceProvider {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:smart-navi:device:secure-tm-server";
		int port = 8907;
		
		MMSConfiguration.MMS_URL="mms-kaist.com:444";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.
		
		
		// Java Key Store example.
		// In order to create HTTS server, JKS is necessary. testkey.jks is a self-signed key.
		String jksDirectory = System.getProperty("user.dir")+"/testkey.jks";
		String jksPassword = "mmsclient";

		
		
		SecureMMSClientHandler server = new SecureMMSClientHandler(myMRN);
		SecureMMSClientHandler sender = new SecureMMSClientHandler(myMRN);
		sender.setSender(new SecureMMSClientHandler.ResponseCallback() {
			//Response Callback from the request message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				System.out.println(message);
			}
		});
		
		String context = "/forwarding";
		server.setServerPort(port, context, jksDirectory, jksPassword, new SecureMMSClientHandler.RequestCallback() {
			
			@Override
			public int setResponseCode() {
				// TODO Auto-generated method stub
				return 200;
			}
			
			//it is called when client receives a message
			@Override
			public String respondToClient(Map<String,List<String>> headerField, String message) {
				try {
					Iterator<String> iter = headerField.keySet().iterator();
					while (iter.hasNext()){
						String key = iter.next();
						System.out.println(key+":"+headerField.get(key).get(0));
					}
					System.out.println(message);

					//it only forwards messages to sc having urn:mrn:imo:imo-no:secure-1000001
					String dstMRN = "urn:mrn:imo:imo-no:secure-1000001";
					sender.sendPostMsg(dstMRN, message, 10000);

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
		}); //sch has a context '/forwarding'
		/* It is not same with:
		 * sch.setPort(port); //It sets default context as '/'
		 * sch.addContext("/forwarding"); //Finally sch has two context '/' and '/forwarding'
		 */
	}
}
