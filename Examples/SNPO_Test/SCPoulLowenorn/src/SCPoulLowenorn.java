/* -------------------------------------------------------- */
/** 
File name : SCPoulLowenorn.java
	Service Consumer cannot be HTTP server and should poll from MMS. 
	Service Consumer has a certificate.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-10-05

Rev. history : 2018-10-11
Version : 0.8.0
	Modified polling client verification.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-10-21
Version : 0.8.0
	Created for SNPO test.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

import net.etri.pkilib.client.ClientPKILibrary;
import net.etri.pkilib.tool.ByteConverter;

public class SCPoulLowenorn {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
		//myMRN = args[0];
		
		MMSConfiguration.MMS_URL="127.0.0.1:8088";
//		MMSConfiguration.MMS_URL="143.248.55.83:8088";
		//MMSConfiguration.MMS_URL = "211.43.202.193:8088";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.
		
		//Service Consumer cannot be HTTP server and should poll from MMS. 
		MMSClientHandler polling = new MMSClientHandler(myMRN);
		MMSClientHandler sender = new MMSClientHandler(myMRN);
		
		sender.setSender(new MMSClientHandler.ResponseCallback() {
			
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				System.out.println(message);
			}
		});
		
		String dstMRN = "urn:mrn:mcp:service:instance:sp-uni";
		String data = "";
		sender.sendPostMsg(dstMRN, data);
		
		
		ClientPKILibrary clientPKILib = ClientPKILibrary.getInstance();
		ByteConverter byteConverter = ByteConverter.getInstance();
		
		//===== dummy content =====
		byte[] content = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
		
		//===== active certificate =====
		String privateKeyPath_active = "PrivateKey_POUL_LOWENORN_active.pem";
		String certPath_active = "Certificate_POUL_LOWENORN_active.pem";
		
		byte[] signedData_active = clientPKILib.generateSignedData(content, privateKeyPath_active, certPath_active);
		String hexSignedData_active = byteConverter.byteArrToHexString(signedData_active);
		
		//===== revoked certificate =====
		//String privateKeyPath_revoked = "PrivateKey_POUL_LOWENORN_revoked.pem";
		//String certPath_revoked = "Certificate_POUL_LOWENORN_revoked.pem";
		
		//byte[] signedData_revoked = clientPKILib.generateSignedData(content, privateKeyPath_revoked, certPath_revoked);
		//String hexSignedData_revoked = byteConverter.byteArrToHexString(signedData_revoked);

		
		int pollInterval = 3000; // The unit is millisecond. 
		String dstMRNMMS = "urn:mrn:smart-navi:device:mms1";
		String svcMRN = "urn:mrn:mcp:service:instance:sp-uni";

		polling.startPolling(dstMRNMMS, svcMRN, hexSignedData_active, pollInterval, 
				new MMSClientHandler.PollingResponseCallback() {
			//Response Callback from the polling message
			//it is called when client receives a message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
				// TODO Auto-generated method stub
				for (String s : messages) {
					System.out.print(s);
				}
			}
		});
		
		// Stopping polling example.
		//Thread.sleep(10000); // After 10 seconds,
		//polling.stopPolling(); // stop polling.
	}
}
