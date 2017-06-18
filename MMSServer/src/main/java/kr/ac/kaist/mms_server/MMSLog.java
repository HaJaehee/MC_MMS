package kr.ac.kaist.mms_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.message_relaying.MessageRelayingHandler;

/* -------------------------------------------------------- */
/** 
File name : MMSLog.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-04-27
Version : 0.5.2
	Added MNSLog, queueLog, msgWaitingPollClnt
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr) 

Rev. history : 2017-04-29
Version : 0.5.3
	Added queueLogForSAS, systemLog, revised from queueLog to queueLogForClient
	Added getStatusForSAS()
	Changed data type of queueLogForClient, queueLogForSAS, systemLog, status from String to StringBuffer
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr) 

Rev. history : 2017-06-17
Version : 0.5.6
	Added polling method switching features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr) 

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class MMSLog {
	
	private static final Logger logger = LoggerFactory.getLogger(MMSLog.class);
	public static String MNSLog = "";
	public static StringBuffer queueLogForClient = new StringBuffer();

	public static int nMsgWaitingPollClnt = 0;
	
	public static String getStatus ()  throws UnknownHostException, IOException{
		  	
		StringBuffer status = new StringBuffer();
		
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
		
		status.append("Polling method: ");
		if (MMSConfiguration.POLLING_METHOD == MMSConfiguration.NORMAL_POLLING) {
			status.append("normal polling<br/>");
		} else if (MMSConfiguration.POLLING_METHOD == MMSConfiguration.LONG_POLLING) {
			status.append("long polling<br/>");
		}
	
		status.append("Waiting polling clients: "+MMSLog.nMsgWaitingPollClnt+"<br/><br/>");
		
		status.append("MMS Queue log:<br/>");
		status.append(MMSLog.queueLogForClient + "<br/>");

		status.append("MNS Dummy:<br/>");
		status.append(dumpMNS() + "<br/>");
  	
  	return status.toString();
  }
	
//  When LOGGING MNS
	public static String dumpMNS() throws UnknownHostException, IOException{ //
  	
  	//String modifiedSentence;
  	String dumpedMNS = "";
  	
  	Socket MNSSocket = new Socket("localhost", 1004);
  	
  	BufferedWriter outToMNS = new BufferedWriter(
					new OutputStreamWriter(MNSSocket.getOutputStream(),Charset.forName("UTF-8")));
  	
  	logger.debug("Dump-MNS");
  	ServerSocket Sock = new ServerSocket(0);
  	int rplPort = Sock.getLocalPort();
  	logger.debug("Reply port : "+rplPort);
  	outToMNS.write("Dump-MNS:"+","+rplPort);
  	outToMNS.flush();
  	outToMNS.close();
  	MNSSocket.close();
  	
  	
  	Socket ReplySocket = Sock.accept();
  	BufferedReader inFromMNS = new BufferedReader(
  			new InputStreamReader(ReplySocket.getInputStream(),Charset.forName("UTF-8")));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = inFromMNS.readLine()) != null) {
			response.append(inputLine.trim());
		}
		
  	dumpedMNS = response.toString();
  	logger.debug("Dumped MNS: " + dumpedMNS);
  	inFromMNS.close();
  	if (dumpedMNS.equals("No"))
  		return "No MRN to IP mapping";
  	dumpedMNS = dumpedMNS.substring(15);
  	return dumpedMNS;
  }
}
