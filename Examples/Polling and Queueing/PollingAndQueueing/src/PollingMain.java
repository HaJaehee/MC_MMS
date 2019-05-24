/* -------------------------------------------------------- */
/** 
File name : PollingAndQueueing.java
	Polling and Queuing
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-10-05

Rev. history : 2018-10-11
Version : 0.8.0
	Modified polling client verification.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

import net.etri.pkilib.client.ClientPKILibrary;
import net.etri.pkilib.tool.ByteConverter;

public class PollingMain {
	public static int POLLING_INTERVAL = 0 * 1000;
	public static int TEST_TIME = 5 * 60 * 1000 + 10000;

	public static final String srcMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	public static final String dstMRN = "urn:mrn:smart-navi:device:mms1";
	public static final String svcMRN = "urn:mrn:smart-navi:device:service-provider";
	public static long START_TIME;
	
	public static void printStampMessage(String message) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println(String.format("[%s] %s.", timestamp.toString(), message));
	}
	
	public static void main(String args[]) throws Exception{

		Scanner readyToStart = new Scanner(System.in);
		readyToStart.nextLine();
		START_TIME = System.currentTimeMillis();
		
		MMSConfiguration.MMS_URL="mms-kaist.com:8088";
//		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.
		
		//Service Consumer cannot be HTTP server and should poll from MMS. 
		MMSClientHandler polling = new MMSClientHandler(srcMRN);
		
		ClientPKILibrary clientPKILib = ClientPKILibrary.getInstance();
		ByteConverter byteConverter = ByteConverter.getInstance();
		
		//===== dummy content =====
		byte[] content = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
		
		//===== active certificate =====
		String privateKeyPath_active = "PrivateKey_POUL_LOWENORN_active.pem";
		String certPath_active = "Certificate_POUL_LOWENORN_active.pem";
		
		byte[] signedData_active = clientPKILib.generateSignedData(content, privateKeyPath_active, certPath_active);
		String hexSignedData_active = byteConverter.byteArrToHexString(signedData_active);
		
		printStampMessage("test started");
		
		polling.startPolling(dstMRN, svcMRN, hexSignedData_active, POLLING_INTERVAL, 
				new MMSClientHandler.PollingResponseCallback() {
			//Response Callback from the polling message
			//it is called when client receives a message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
				// TODO Auto-generated method stub
				for (String s : messages) {
					printStampMessage(String.format("get polling message: [%s].", s));
				}
			}
		});
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				printStampMessage("time : " + (System.currentTimeMillis() - START_TIME)/1000 + "seconds");
			}
		}, 30000, 30000);
		
		Thread.sleep(TEST_TIME);
		printStampMessage("test ended");
		System.exit(0);
	}
}
