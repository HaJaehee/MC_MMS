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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.message_relaying.MessageRelayingHandler;
import kr.ac.kaist.message_relaying.SessionManager;
import kr.ac.kaist.seamless_roaming.PollingMethodRegDummy;

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

Rev. history : 2017-07-28
Version : 0.5.9
	Added polling method each service.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Added brief log for status case.
	Revised variable from nMsgWaitingPollClnt to msgWaitingPollClientCount.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-29
Version : 0.6.0
	Polling methods are printed into sorted by key(MRN) form .
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-10-12
Version : 0.6.0
	Removed msgWaitingPollClientCount.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class MMSLog {
	
	private static final Logger logger = LoggerFactory.getLogger(MMSLog.class);
	//public static String MNSLog = "";
	//public static StringBuffer queueLogForClient = new StringBuffer();
	
	private static ArrayList<String> briefLogForStatus = new ArrayList<String>();
	
	public static String getStatus ()  throws UnknownHostException, IOException{
		  	
		StringBuffer status = new StringBuffer();
		
		status.append("<strong>Maritime Name System Dummy:</strong><br/>");
		status.append(dumpMNS());
		status.append("<br/>");
		
		status.append("<strong>Polling method:</strong><br/>");
		if (!PollingMethodRegDummy.pollingMethodReg.isEmpty()){
			SortedSet<String> keys = new TreeSet<String>(PollingMethodRegDummy.pollingMethodReg.keySet());
			for (String key : keys){
				int value = PollingMethodRegDummy.pollingMethodReg.get(key);
				status.append(key+", "+((value==PollingMethodRegDummy.NORMAL_POLLING)?"normal":"long")+" polling<br/>");
			}
			status.append("Other services, normal polling<br/>");
		} else {
			status.append("All services, normal polling<br/>");
		}
		status.append("<br/>");
	
		status.append("<strong>Sessions waiting for a message:</strong><br/>");
		int nPollingSessions = 0;
		if (!SessionManager.sessionInfo.isEmpty()){
			SortedSet<String> keys = new TreeSet<String>(SessionManager.sessionInfo.keySet());
			for (String key : keys){
				if (SessionManager.sessionInfo.get(key).equals("p")) {
					status.append("SessionID="+key+"<br/>");
					nPollingSessions++;
				}
			}
		} 
		if (nPollingSessions == 0){
			status.append("None<br/>");
		}
		status.append("<br/>");
		
		status.append("<strong>MMS Brief Log(Maximum list size:"+MMSConfiguration.MAX_BRIEF_LOG_LIST_SIZE+"):</strong><br/>");
		for (String log : briefLogForStatus) {
			status.append(log+"<br/>");
		}
		
  	
  	return status.toString();
  }
	
//  When LOGGING MNS
	public static String dumpMNS() throws UnknownHostException, IOException{ //
  	
  	//String modifiedSentence;
  	String dumpedMNS = "";
  	
  	Socket MNSSocket = new Socket("localhost", 1004);
  	
  	BufferedWriter outToMNS = new BufferedWriter(
					new OutputStreamWriter(MNSSocket.getOutputStream(),Charset.forName("UTF-8")));
  	
  	logger.debug("Dump-MNS.");
  	ServerSocket Sock = new ServerSocket(0);
  	int rplPort = Sock.getLocalPort();
  	logger.debug("Reply port : "+rplPort+".");
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
  	logger.debug("Dumped MNS: " + dumpedMNS+".");
  	inFromMNS.close();
  	if (dumpedMNS.equals("No"))
  		return "No MRN to IP mapping.<br/>";
  	dumpedMNS = dumpedMNS.substring(15);
  	return dumpedMNS;
  }
	public static void addBriefLogForStatus (String arg) {
		if (briefLogForStatus.size() > MMSConfiguration.MAX_BRIEF_LOG_LIST_SIZE) {
			briefLogForStatus.remove(0);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd HH:mm");
		arg = sdf.format(new Date()) + " " + arg;
		briefLogForStatus.add(arg);
	}
	
}
