package tc08_long_polling_duplicate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import net.etri.pkilib.client.ClientPKILibrary;
import net.etri.pkilib.tool.ByteConverter;

/**
 * File name : LongPollingDuplicateClient.java Polling request message function for the purpose
 * of testing MMS Author : Youngjin Kim (jcdad3000@kaist.ac.kr) Creation Date :
 * 2019-05-02
 * 
 * 
 * Rev. history : 2019-05-17
 * Version : 0.9.1
 *		Added assert statements.
 * Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
 * 
Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS8_Client to LongPollingDuplicateClient
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-06-20
Version : 0.9.2
	Revised test cases and fixed bugs.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
 */
public class LongPollingDuplicateClient {

	private static int length = -1;
	
	public static int checker =0;

	public static String hexSignedData_active = null;
	
	public int getChecker() {
		return checker;
	}

	public LongPollingDuplicateClient() {
		ClientPKILibrary clientPKILib = ClientPKILibrary.getInstance();
		ByteConverter byteConverter = ByteConverter.getInstance();

		// ===== dummy content =====
		byte[] content = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };

		// ===== active certificate =====
		String privateKeyPath_active = "certs/PrivateKey_POUL_LOWENORN_active.pem";
		String certPath_active = "certs/Certificate_POUL_LOWENORN_active.pem";

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
		threadex.interval = 0;
		Thread thread1 = new Thread(threadex, "A");
		thread1.start();	
	}
	
	public static void emptyTheQueue() {
		ThreadEX threadex = new ThreadEX();	
		threadex.interval = 100;
		Thread thread1 = new Thread(threadex, "A");
		thread1.start();	
	}
	
	public static void multipleThreadStart() {
		ThreadEX threadex = new ThreadEX();
		threadex.interval = 0;
		ThreadEX threadex2 = new ThreadEX();
		ThreadEX threadex3 = new ThreadEX();
		ThreadEX threadex4 = new ThreadEX();

		Thread thread1 = new Thread(threadex, "A");
		Thread thread2 = new Thread(threadex2, "B");
		Thread thread3 = new Thread(threadex3, "C");
		Thread thread4 = new Thread(threadex4, "D");

		try {
			thread1.start();
			Thread.sleep(100);
			thread2.start();
			Thread.sleep(100);
			thread3.start();
			Thread.sleep(100);
			thread4.start();	
		} 
		catch (InterruptedException e) {
			
		}
		
	}

}

class ThreadEX implements Runnable {
	private int content_length = 0;


	private static String myMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	private static String dstMRN = "urn:mrn:smart-navi:device:mms1";
	private static String svcMRN = "urn:mrn:imo:imo-no:ts-mms-08-server";
	public static int interval = 0;
	private MMSClientHandler myHandler = null;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int retLength = -1;
		

		try {
			myHandler = new MMSClientHandler(myMRN);

			myHandler.startPolling(dstMRN, svcMRN, LongPollingDuplicateClient.hexSignedData_active, interval,
					new MMSClientHandler.PollingResponseCallback() {

						@Override
						public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
							// TODO Auto-generated method stub

							for (String s : messages) {
								LongPollingDuplicateTest.response.add(s);
								//System.out.println("message : " + s);
							}
							myHandler.stopPolling();
						}
					});
			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
	}

	
}