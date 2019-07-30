import java.io.IOException;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;
import net.etri.pkilib.client.ClientPKILibrary;
import net.etri.pkilib.tool.ByteConverter;
/** 
File name : TS5_client.java
	Validation of MMS polling client
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-10-16
*/
public class TS5_client {

	ClientPKILibrary clientPKILib = ClientPKILibrary.getInstance();
	ByteConverter byteConverter = ByteConverter.getInstance();

	String myMRN = new String();
	String checkMessage = new String();
	public TS5_client() throws NullPointerException, IOException {

		MMSConfiguration.MMS_URL = "143.248.57.144:8088";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.

	}

	public void RightMRN() {
		myMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	}

	public void WrongMRN() {
		myMRN = "urn:mrn:imo:imo-no:ts-mms-05-client";
	}
	
	public String getCheckMessage() {
		return checkMessage;
		}

	public void ValidCertificatedCase() throws NullPointerException, IOException {

		String dstMRN = "urn:mrn:smart-navi:device:mms1";
		String svcMRN = "urn:mrn:imo:imo-no:ts-mms-05-server";

		MMSClientHandler polling = new MMSClientHandler(myMRN);

		int pollInterval = 1000;
		byte[] content = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };

		String privateKeyPath_active = "PrivateKey_POUL_LOWENORN_active.pem";
		String certPath_active = "Certificate_POUL_LOWENORN_active.pem";

		byte[] signedData_active = clientPKILib.generateSignedData(content, privateKeyPath_active, certPath_active);
		String hexSignedData_active = byteConverter.byteArrToHexString(signedData_active);

		polling.startPolling(dstMRN, svcMRN, hexSignedData_active, pollInterval,
				new MMSClientHandler.PollingResponseCallback() {
					// Response Callback from the polling message
					// it is called when client receives a message
					@Override
					public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
						// TODO Auto-generated method stub
						for (String s : messages) {
							System.out.print(s);
							checkMessage =s;
						}
						polling.stopPolling();
					}
				});
		
	}
	

	public void InValidCertificatedCase() throws NullPointerException, IOException {

		String dstMRN = "urn:mrn:smart-navi:device:mms1";
		String svcMRN = "urn:mrn:imo:imo-no:ts-mms-05-server";

		MMSClientHandler polling = new MMSClientHandler(myMRN);

		int pollInterval = 100;
		byte[] content = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };

		// ===== revoked certificate =====
		String privateKeyPath_revoked = "PrivateKey_POUL_LOWENORN_revoked.pem";
		String certPath_revoked = "Certificate_POUL_LOWENORN_revoked.pem";

		byte[] signedData_revoked = clientPKILib.generateSignedData(content, privateKeyPath_revoked, certPath_revoked);
		String hexSignedData_revoked = byteConverter.byteArrToHexString(signedData_revoked);

		polling.startPolling(dstMRN, svcMRN, hexSignedData_revoked, pollInterval,
				new MMSClientHandler.PollingResponseCallback() {
					// Response Callback from the polling message
					// it is called when client receives a message
					@Override
					public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
						// TODO Auto-generated method stub
						for (String s : messages) {
							System.out.print(s);
							checkMessage =s;
						}
						polling.stopPolling();
					}
				});

	}
}
