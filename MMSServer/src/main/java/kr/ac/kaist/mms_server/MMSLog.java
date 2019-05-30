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


Rev. history : 2018-06-29
Version : 0.7.2
	Fixed a bug of realtime log service related to removing an ID.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-10
Version : 0.7.2
	Fixed insecure codes.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-05
Version : 0.9.0
	Added rest API functions.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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


public class MMSLog {
	
	private static final Logger logger = LoggerFactory.getLogger(MMSLog.class);
	
	private ArrayList<String> briefLogForStatus = new ArrayList<String>();
	private Map<String,List<String>> briefRealtimeLogEachIDs = new HashMap<String,List<String>>();
	private MMSLogForDebug mmsLogForDebug = null;


	
	private MMSLog() {
		mmsLogForDebug = MMSLogForDebug.getInstance();
		//addIdToBriefRealtimeLogEachIDs("JaeheeHa"); // For testing.
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
			
			
			if (MMSConfiguration.getMnsHost().equals("localhost") || MMSConfiguration.getMnsHost().equals("127.0.0.1")) {
				status.append("<strong>Maritime Name System Dummy:</strong><br/>");
				status.append("<div style=\"max-height: 200px; overflow-y: scroll;\">");
				status.append(dumpMNS());
			}
			else {
				status.append("<strong>Maritime Name System:</strong><br/>");
				status.append("MNS host="+MMSConfiguration.getMnsHost()+":"+MMSConfiguration.getMnsPort()+"<br/>");
			}
			status.append("</div>");
			status.append("<br/>");

			
			status.append("<strong>Sessions waiting for a message:</strong><br/>");
			status.append("<div style=\"max-height: 200px; overflow-y: scroll;\">");
			int nPollingSessions = 0;
			if (!SessionManager.getSessionInfo().isEmpty()){
				SortedSet<String> keys = new TreeSet<String>(SessionManager.getSessionInfo().keySet());
				for (String key : keys){
					if (SessionManager.getSessionInfo().get(key).equals("p")) {
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
			if (!mmsLogForDebug.getMrnSet().isEmpty()) {
				SortedSet<String> keys = new TreeSet<String>(mmsLogForDebug.getMrnSet());
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
			
			status.append("<strong>MMS Brief Log(Maximum list size:"+MMSConfiguration.getMaxBriefLogListSize()+"):</strong><br/>");
			for (String log : briefLogForStatus) {
				status.append(log+"<br/>");
			}
		} 
		else {
			
			status.append("<strong>MMS Brief Log for MRN="+mrn+"<br/>(Maximum session count:"+mmsLogForDebug.getMaxSessionCount()+"):</strong><br/>");
			String log = mmsLogForDebug.getLog(mrn);
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

					logger.info(e.getClass().getName()+" "+e.getStackTrace()[0]+".");
	    			for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
	    				logger.info(e.getStackTrace()[i]+".");
	    			}
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
	@Deprecated
	private String dumpMNS() throws UnknownHostException, IOException{ //
  	
	  	//String modifiedSentence;
	  	String dumpedMNS = "";
	  	

	  	Socket MNSSocket = new Socket(MMSConfiguration.getMnsHost(), MMSConfiguration.getMnsPort());
	  	PrintWriter pw = new PrintWriter(MNSSocket.getOutputStream());
	  	InputStreamReader isr = new InputStreamReader(MNSSocket.getInputStream());
	  	BufferedReader br = new BufferedReader(isr);
	  	
	  	logger.debug("Dump-MNS.");
	  	pw.println("Dump-MNS:");
	  	pw.flush();
	
	  	if (!MNSSocket.isOutputShutdown()) {
	  		MNSSocket.shutdownOutput();
	  	}

		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = br.readLine()) != null) {
			response.append(inputLine.trim());
		}
			
	  	dumpedMNS = response.toString();
	  	logger.trace("Dumped MNS: " + dumpedMNS+".");
	  	

	  	if (pw != null) {
	  		pw.close();
	  	}
	  	if (isr != null) {
	  		isr.close();
	  	}
	  	if (br != null) {
	  		br.close();
	  	}
	  	if (MNSSocket != null) {
	  		MNSSocket.close();
	  	}

	  	
	  	if (dumpedMNS.equals("No")) {
	  		String ret = "No MRN to IP mapping.<br/>";
	  		return ret;
	  	}
	  	
	  	dumpedMNS = dumpedMNS.substring(15);

	  	return dumpedMNS;
  }
	
	
	
	

	private void addBriefLogForStatus (String arg) {
		

		if (briefLogForStatus.size() > MMSConfiguration.getMaxBriefLogListSize()) {
			briefLogForStatus.remove(0);
		}
		briefLogForStatus.add(arg);
		
		if (!briefRealtimeLogEachIDs.isEmpty()) {
			Set<String> keys = briefRealtimeLogEachIDs.keySet();
			for (String key : keys) {
				if (briefRealtimeLogEachIDs.get(key).size() > MMSConfiguration.getMaxBriefLogListSize()) {
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
				briefRealtimeLogEachIDs.get(id).clear();
			}
			briefRealtimeLogEachIDs.remove(id);
		}
	}
	
	public Set<String> getRealtimeLogUsersSet (){
		return briefRealtimeLogEachIDs.keySet();
	}
	
	private String makeLog (String SessionId, String log) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("SessionID=");
		sb.append(SessionId);
		if (!Character.isWhitespace(log.charAt(0))) {
			sb.append(" ");
		}
		sb.append(log);
		if (!log.endsWith(".")) {
			sb.append(".");
		}
		
		return sb.toString(); 
	}

	
	private void addWebLog (String SessionId, String log, String logLevel) {
		
		if (MMSConfiguration.isWebLogProviding()) {
			StringBuilder sb = new StringBuilder();
			SimpleDateFormat sdf = new SimpleDateFormat("M/dd HH:mm:ss");
			sb.append(sdf.format(new Date()));
			sb.append(" ");
			sb.append(logLevel);
			sb.append(" ");
			sb.append(log);
			String newLog = sb.toString();
			
			addBriefLogForStatus(newLog);
			mmsLogForDebug.addLog(SessionId, newLog);
		}
	}
	
	public void trace (Logger logger, String SessionId, String log) {
		if (logger.isTraceEnabled()) {
			String newLog = makeLog(SessionId, log);
			addWebLog(SessionId, newLog, "TRACE");
			logger.trace(newLog);
		}
	}
	public void debug (Logger logger, String SessionId, String log) {
		if (logger.isDebugEnabled()) {
			String newLog = makeLog(SessionId, log);
			addWebLog(SessionId, newLog, "DEBUG");
			logger.debug(newLog);
		}
	}
	public void info (Logger logger, String SessionId, String log) {
		if (logger.isInfoEnabled()) {
			String newLog = makeLog(SessionId, log);
			addWebLog(SessionId, newLog, "INFO");
			logger.info(newLog);
		}
	}
	public void warn (Logger logger, String SessionId, String log) {
		if (logger.isWarnEnabled()) {
			String newLog = makeLog(SessionId, log);
			addWebLog(SessionId, newLog, "WARN");
			logger.warn(newLog);
		}
	}
	public void error (Logger logger, String SessionId, String log) {
		if (logger.isErrorEnabled()) {
			String newLog = makeLog(SessionId, log);
			addWebLog(SessionId, newLog, "ERROR");
			logger.error(newLog);
		}
	}
	
	public void warnException (Logger logger, String SessionId, String log, Exception e, int traceDepth) {
		if (logger.isWarnEnabled()) {
			warn(logger, SessionId, makeExceptionLog(log, e));
			for (int i = 1 ; i < e.getStackTrace().length && i < traceDepth ; i++) {
				logger.warn(makeLog(SessionId, e.getStackTrace()[i]+"."));
			}
		}
	}
	
	public void errorException (Logger logger, String SessionId, String log, Exception e, int traceDepth) {
		if (logger.isErrorEnabled()) {
			error(logger, SessionId, makeExceptionLog(log, e));
			for (int i = 1 ; i < e.getStackTrace().length && i < traceDepth ; i++) {
				logger.error(makeLog(SessionId, e.getStackTrace()[i]+"."));
			}
		}
	}

	private String makeExceptionLog (String log, Exception e){
		StringBuilder sb = new StringBuilder();
		sb.append(log);
		sb.append(" ");
		sb.append(e.getMessage());
		sb.append(" ");
		sb.append(e.getClass().getName());
		sb.append(" ");
		sb.append(e.getStackTrace()[0]);
		sb.append(".");
		return sb.toString();
	}
}
