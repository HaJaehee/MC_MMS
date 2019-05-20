import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

import net.etri.pkilib.client.ClientPKILibrary;
import net.etri.pkilib.tool.ByteConverter;

/**
 * File name : TS9_client.java 
 * Client Type decide junit test 
 * of testing MMS Author : Youngjin Kim (jcdad3000@kaist.ac.kr) Creation Date :
 * 2019-05-20
 
 */
public class TS9_client {

	private static int length = -1;

	public static int checker = 0;

	public static String hexSignedData_active = null;

	private int content_length = 0;

	private static String myMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	private static String dstMRN = "urn:mrn:smart-navi:device:mms1";
	private static String svcMRN = "urn:mrn:imo:imo-no:ts-mms-09-server";
	private static MMSClientHandler myHandler = null;
	
	private static boolean clientNormalChecker= false;
	private static boolean clientLongChecker =false;

	public TS9_client() {

//		MMSConfiguration.MMS_URL="mms.smartnav.org:8088";
//		MMSConfiguration.MMS_URL="143.248.55.83:8088";
		MMSConfiguration.MMS_URL = "127.0.0.1:8088";
		MMSConfiguration.DEBUG = true;
		ClientPKILibrary clientPKILib = ClientPKILibrary.getInstance();
		ByteConverter byteConverter = ByteConverter.getInstance();

		// ===== dummy content =====
		byte[] content = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };

		// ===== active certificate =====
		String privateKeyPath_active = "PrivateKey_POUL_LOWENORN_active.pem";
		String certPath_active = "Certificate_POUL_LOWENORN_active.pem";

		byte[] signedData_active = clientPKILib.generateSignedData(content, privateKeyPath_active, certPath_active);
		hexSignedData_active = byteConverter.byteArrToHexString(signedData_active);

		/*
		 * while (i < 4) { i++; System.out.println("case "+i); pollingReqeust();// empty
		 * queue }
		 */

		// threadStart();
	}
	
	public boolean getNormalchecker () {
		return clientNormalChecker;
	}
	
	public boolean getLongchecker() {
		return clientLongChecker;
	}

	public static void normalPollingTest() throws NullPointerException, IOException {

		myHandler = new MMSClientHandler(myMRN);

		try {
			myHandler.startPolling(dstMRN, svcMRN, TS9_client.hexSignedData_active, 1000,
					new MMSClientHandler.PollingResponseCallback() {

						@Override
						public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
							// TODO Auto-generated method stub

							for (String s : messages) {
								TS9_test.response.add(s);
								// System.out.println("message : " + s);
							}
							clientNormalChecker = myHandler.getNormalchecker();
							clientLongChecker=myHandler.getLongChecker();
							myHandler.stopPolling();
						}
					});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void longPollingTest() throws NullPointerException, IOException {
		myHandler = new MMSClientHandler(myMRN);

		try {
			myHandler.startPolling(dstMRN, svcMRN, TS9_client.hexSignedData_active, 0,
					new MMSClientHandler.PollingResponseCallback() {

						@Override
						public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
							// TODO Auto-generated method stub

							for (String s : messages) {
								TS9_test.response.add(s);
								// System.out.println("message : " + s);
							}
							clientNormalChecker = myHandler.getNormalchecker();
							clientLongChecker=myHandler.getLongChecker();
							myHandler.stopPolling();
						}
					});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
