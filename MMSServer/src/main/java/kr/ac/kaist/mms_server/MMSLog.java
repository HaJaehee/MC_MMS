package kr.ac.kaist.mms_server;
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

Rev. history : 2017-10-24
Version : 0.6.0
	The log level of the log about MNS Dummy entries is lowered from debug level to trace level.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-10-25
Version : 0.6.0
	Added MMSLogsForDebug features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-15
Version : 0.7.0
	Added realtime log functions
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	Jaehyun Park (jae519@kaist.ac.kr)
	
Rev. history : 2017-11-18
Version : 0.7.0
	Fixed bugs due to null pointer execptions.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-18
Version : 0.7.0
	Divided each section.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-18
Version : 0.7.0
	Replaced this class from static class to singleton class.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-04-23
Version : 0.7.1
	Removed RESOURCE_LEAK, EXPOSURE_OF_SYSTEM_DATA hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	
*/
/* -------------------------------------------------------- */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.message_relaying.MessageRelayingHandler;
import kr.ac.kaist.message_relaying.SessionManager;
import kr.ac.kaist.seamless_roaming.PollingMethodRegDummy;


public class MMSLog {
	
	private static final Logger logger = LoggerFactory.getLogger(MMSLog.class);
	//public static String MNSLog = "";
	//public static StringBuffer queueLogForClient = new StringBuffer();
	
	private ArrayList<String> briefLogForStatus = new ArrayList<String>();
	private Map<String,List<String>> briefRealtimeLogEachIDs = new HashMap<String,List<String>>();
	private MMSLogForDebug logForDebug = null;
	
	private MMSLog() {
		logForDebug = MMSLogForDebug.getInstance();
	}
	
	public static MMSLog getInstance() { //double check synchronization.
		return LazyHolder.INSTANCE;
	}
	
	private static class LazyHolder {
		private static final MMSLog INSTANCE = new MMSLog();
	}
	
	
	public String getStatus (String mrn)  throws UnknownHostException, IOException{
		  	
		
		StringBuffer status = new StringBuffer();
		
		if (mrn.equals("")) {
			
			status.append("<strong>Maritime Name System Dummy:</strong><br/>");
			status.append("<div style=\"max-height: 200px; overflow-y: scroll;\">");
			status.append(dumpMNS());
			status.append("</div>");
			status.append("<br/>");
			
			
			status.append("<strong>Polling method:</strong><br/>");
			status.append("<div style=\"max-height: 200px; overflow-y: scroll;\">");
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
			status.append("</div>");
			status.append("<br/>");
			
			
			status.append("<strong>Sessions waiting for a message:</strong><br/>");
			status.append("<div style=\"max-height: 200px; overflow-y: scroll;\">");
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
			status.append("</div>");
			status.append("<br/>");
			
			
			status.append("<strong>MRNs being debugged:</strong><br/>");
			status.append("<div style=\"max-height: 200px; overflow-y: scroll;\">");
			if (!logForDebug.getMrnSet().isEmpty()) {
				SortedSet<String> keys = new TreeSet<String>(logForDebug.getMrnSet());
				for (String key : keys) {
					status.append(key+"<br/>");
				}
			}
			else {
				status.append("None<br/>");
			}
			status.append("</div>");
			status.append("<br/>");
			
			
			status.append("<strong>Realtime log service consumer IDs:</strong><br/>");
			status.append("<div style=\"max-height: 200px; overflow-y: scroll;\">");
			if (!briefRealtimeLogEachIDs.isEmpty()) {
				SortedSet<String> keys = new TreeSet<String>(briefRealtimeLogEachIDs.keySet());
				for (String key : keys) {
					status.append(key+"<br/>");
				}
			}
			else {
				status.append("None<br/>");
			}
			status.append("</div>");
			status.append("<br/>");
			
			status.append("<strong>MMS Brief Log(Maximum list size:"+MMSConfiguration.MAX_BRIEF_LOG_LIST_SIZE+"):</strong><br/>");
			for (String log : briefLogForStatus) {
				status.append(log+"<br/>");
			}
		} 
		else {
			
			status.append("<strong>MMS Brief Log for MRN="+mrn+"<br/>(Maximum session count:"+logForDebug.getMaxSessionCount()+"):</strong><br/>");
			String log = logForDebug.getLog(mrn);
			if (log != null) {
				status.append(log);
			}
			else {
				status.append("Invalid MRN being debugged.<br/>");
			}
		}
  	
  	return status.toString();
  }
	public String getRealtimeLog (String id) {
		StringBuffer realtimeLog = new StringBuffer();

		realtimeLog.append("{\"message\":[");
		if (briefRealtimeLogEachIDs.get(id)!=null) {
			ArrayList<String> logs = (ArrayList<String>) briefRealtimeLogEachIDs.get(id);
			while (!logs.isEmpty()) {
				try {
					realtimeLog.append("\""+URLEncoder.encode(logs.get(0),"UTF-8")+"\",");
				} catch (UnsupportedEncodingException e) {
					logger.info(e.getMessage());
				}
				logs.remove(0);
			}
		}
		else {
			realtimeLog.append("\"The ID does not exist in Realtime log service consumer IDs\"");
		}
	
		realtimeLog.append("]}");
		return realtimeLog.toString();
	}
	
//  When LOGGING MNS
	private String dumpMNS() throws UnknownHostException, IOException{ //
  	
  	//String modifiedSentence;
  	String dumpedMNS = "";
  	
  	Socket MNSSocket = new Socket("localhost", 1004);
  	OutputStreamWriter osw = new OutputStreamWriter(MNSSocket.getOutputStream(),Charset.forName("UTF-8"));
  	
  	BufferedWriter outToMNS = new BufferedWriter(osw);
  	
  	logger.debug("Dump-MNS.");
  	ServerSocket Sock = new ServerSocket(0);
  	int rplPort = Sock.getLocalPort();
  	logger.debug("Reply port : "+rplPort+".");
  	outToMNS.write("Dump-MNS:"+","+rplPort);
  	outToMNS.flush();
  	if (osw != null) {
  		osw.close();
  	}
  	outToMNS.close();
  	MNSSocket.close();
  	
  	Socket ReplySocket = Sock.accept();
  	InputStreamReader isr = new InputStreamReader(ReplySocket.getInputStream(),Charset.forName("UTF-8"));
  	
  	
  	BufferedReader inFromMNS = new BufferedReader(isr);
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = inFromMNS.readLine()) != null) {
			response.append(inputLine.trim());
		}
		
  	dumpedMNS = response.toString();
  	logger.trace("Dumped MNS: " + dumpedMNS+".");
  	if (isr != null) {
  		isr.close();
  	}
  	inFromMNS.close();
  	if (dumpedMNS.equals("No"))
  		return "No MRN to IP mapping.<br/>";
  	dumpedMNS = dumpedMNS.substring(15);
  	return dumpedMNS;
  }
	
	
	
	
	public void addBriefLogForStatus (String arg) {
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd HH:mm");
		arg = sdf.format(new Date()) + " " + arg;

		if (briefLogForStatus.size() > MMSConfiguration.MAX_BRIEF_LOG_LIST_SIZE) {
			briefLogForStatus.remove(0);
		}
		briefLogForStatus.add(arg);
		
		if (!briefRealtimeLogEachIDs.isEmpty()) {
			Set<String> keys = briefRealtimeLogEachIDs.keySet();
			for (String key : keys) {
				if (briefRealtimeLogEachIDs.get(key).size() > MMSConfiguration.MAX_BRIEF_LOG_LIST_SIZE) {
					briefRealtimeLogEachIDs.get(key).remove(0);
				}
				briefRealtimeLogEachIDs.get(key).add(arg);
			}
		}
	}

	public void addIdToBriefRealtimeLogEachIDs (String id) {
		if (briefRealtimeLogEachIDs.get(id)!=null) {
			
		}
		else {
			List<String> logs = new ArrayList<String>();
			briefRealtimeLogEachIDs.put(id, logs);
		}
		
	}
	
	public void removeIdFromBriefRealtimeLogEachIDs (String id) {
		if (briefRealtimeLogEachIDs.get(id)==null) {
			
		}
		else {
			if (!briefRealtimeLogEachIDs.get(id).isEmpty())
			{
				briefRealtimeLogEachIDs.clear();
			}
			briefRealtimeLogEachIDs.remove(id);
		}
	}

}
