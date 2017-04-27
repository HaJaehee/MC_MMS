package kr.ac.kaist.mms_server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kr.ac.kaist.message_relaying.MessageRelayingHandler;

/* -------------------------------------------------------- */
/** 
File name : MMSLog.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Creation Date : 2017-01-24
Version : 0.5.2
	Added MNSLog, queueLog, msgWaitingPollClnt
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr) 
*/
/* -------------------------------------------------------- */

public class MMSLog {
	private static final String TAG = "[MMSLog] ";
	public static String MNSLog = "";
	public static String queueLog = "";
	public static String log = "";
	public static int nMsgWaitingPollClnt = 0;
	
	public static String getStatus ()  throws UnknownHostException, IOException{
		  	
			String status = "";
			
			//@Deprecated
			/*
			HashMap<String, String> queue = MMSQueue.queue;
			status = status + "Message Queue:<br/>";
			Set<String> queueKeys = queue.keySet();
			Iterator<String> queueKeysIter = queueKeys.iterator();
			while (queueKeysIter.hasNext() ){
				String key = queueKeysIter.next();
				if (key==null)
					continue;
				String value = queue.get(key);
				status = status + key + "," + value + "<br/>"; 
			}
			status = status + "<br/>";
			*/
			
			status += "Waiting polling clients: "+MMSLog.nMsgWaitingPollClnt+"<br/><br/>";
			
			status += "MMS Queue log:<br/>";
			status += MMSLog.queueLog + "<br/>";

			status += "MNS Dummy:<br/>";
			status += MessageRelayingHandler.dumpMNS() + "<br/>";
	  	
	  	return status;
	  }
}
