import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

import net.etri.pkilib.client.ClientPKILibrary;
import net.etri.pkilib.tool.ByteConverter;

//import kr.ac.kaist.mms_client.PollHandler;
/**
 * File name : TS6_client.java Polling request message function for the purpose
 * of testing MMS Author : Youngjin Kim (jcdad3000@kaist.ac.kr) Creation Date :
 * 2019-05-02
 * 
 */
public class TS8_client {

	private static int length = -1;
	
	public static int checker =0;

	public static String hexSignedData_active = null;
	
	public int getChecker() {
		return checker;
	}

	public TS8_client() {

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

	public static void singleThreadStart() {
		ThreadEX threadex = new ThreadEX();	
		Thread thread1 = new Thread(threadex, "A");
		thread1.start();	
	}
	
	public static void multipleThreadStart() {
		ThreadEX threadex = new ThreadEX();
		ThreadEX threadex2 = new ThreadEX();
		ThreadEX threadex3 = new ThreadEX();
		ThreadEX threadex4 = new ThreadEX();

		Thread thread1 = new Thread(threadex, "A");
		Thread thread2 = new Thread(threadex2, "B");
		Thread thread3 = new Thread(threadex3, "C");
		Thread thread4 = new Thread(threadex4, "D");

		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();	
		
	}

}

class ThreadEX implements Runnable {
	private int content_length = 0;


	private static String myMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	private static String dstMRN = "urn:mrn:smart-navi:device:mms1";
	private static String svcMRN = "urn:mrn:imo:imo-no:ts-mms-06-server";

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int retLength = -1;
		MMSClientHandler myHandler = null;

		try {
			myHandler = new MMSClientHandler(myMRN);

			myHandler.startPolling(dstMRN, svcMRN, TS8_client.hexSignedData_active, 0,
					new MMSClientHandler.PollingResponseCallback() {

						@Override
						public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
							// TODO Auto-generated method stub

							for (String s : messages) {
								System.out.println("message : " + s);
							}
						}
					});

		
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
	}

	
}