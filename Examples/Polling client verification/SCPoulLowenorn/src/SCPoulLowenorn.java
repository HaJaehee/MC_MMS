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
		
		MMSConfiguration.MMS_URL="mms-kaist.com:8088";
//		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		MMSConfiguration.DEBUG = true; // If you are debugging client, set this variable true.
		
		//Service Consumer cannot be HTTP server and should poll from MMS. 
		MMSClientHandler polling = new MMSClientHandler(myMRN);
		
		int pollInterval = 1000; // The unit is millisecond. 
		String dstMRN = "urn:mrn:smart-navi:device:mms1";
		String svcMRN = "urn:mrn:smart-navi:device:service-provider";
		
		
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
//		String privateKeyPath_revoked = "PrivateKey_POUL_LOWENORN_revoked.pem";
//		String certPath_revoked = "Certificate_POUL_LOWENORN_revoked.pem";
//		
//		byte[] signedData_revoked = clientPKILib.generateSignedData(content, privateKeyPath_revoked, certPath_revoked);
//		String hexSignedData_revoked = byteConverter.byteArrToHexString(signedData_revoked);

		polling.startPolling(dstMRN, svcMRN, hexSignedData_active, pollInterval, 
				new MMSClientHandler.PollingResponseCallback() {
			//Response Callback from the polling message
			//it is called when client receives a message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
				// TODO Auto-generated method stub
				for (String s : messages) {
					System.out.println(s);
				}
			}
		});
		
		// Stopping polling example.
		//Thread.sleep(10000); // After 10 seconds,
		//polling.stopPolling(); // stop polling.
	}
}
