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

public class QueuingMain {
	public static int QUEUEING_INTERVAL = 0 * 1000;
	public static int TEST_TIME = 5 * 60 * 1000+10000;

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
		printStampMessage("test start");
		START_TIME = System.currentTimeMillis();
		
		MMSConfiguration.MMS_URL="mms-kaist.com:8088";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.
				
		if (QUEUEING_INTERVAL != 0) {
			SendClient client = new SendClient(svcMRN, srcMRN);
			client.Send(QUEUEING_INTERVAL);
		}
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
