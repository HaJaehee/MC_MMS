import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;
import kr.ac.kaist.mms_client.SecureMMSClientHandler;

/* -------------------------------------------------------- */
/** 
File name : Secure SC5.java
	Service Consumer can be HTTPS server and listen to port 'port'.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-02-01
Version : 0.4.0

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-21
Version : 0.7.0
	Compatible with MMS Client beta-0.7.0.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	
*/
/* -------------------------------------------------------- */

public class SecureSC5 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:secure-1000005";
		//myMRN = args[0];
		
		MMSConfiguration.MMS_URL="127.0.0.1:444";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.
		
		//Service Consumer can be HTTP server and listen to port 'port'. 
		//port = Integer.parseInt(args[1]);
		int port = 8906;
		
		// Java Key Store example.
		// In order to create HTTS server, JKS is necessary. testkey.jks is a self-signed key.
		String jksDirectory = System.getProperty("user.dir")+"/testkey.jks";
		String jksPassword = "mmsclient";
		
		SecureMMSClientHandler sch = new SecureMMSClientHandler(myMRN);	
		sch.setServerPort(port, jksDirectory, jksPassword, new SecureMMSClientHandler.RequestCallback() {
			//Request Callback from the request message
			//it is called when client receives a message
			
			@Override
			public int setResponseCode() {
				// TODO Auto-generated method stub
				return 200;
			}
			
			@Override
			public String respondToClient(Map<String,List<String>>  headerField, String message) {
				Iterator<String> iter = headerField.keySet().iterator();
				while (iter.hasNext()){
					String key = iter.next();
					System.out.println(key+":"+headerField.get(key).toString());// Print the matched header field and the header contents.
				}
				System.out.println(message);
				
				return "OK";
			}

			@Override
			public Map<String, List<String>> setResponseHeader() {
				// TODO Auto-generated method stub
				return null;
			}
		});


	}
}
