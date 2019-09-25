package kr.ac.kaist.mms_server;
/* -------------------------------------------------------- */
/** 
File name : MMSRestAPIHandler.java
	It handles MMS restful API and generates response.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2019-05-05
Version : 0.9.0

Rev. history: 2019-05-07
Version : 0.9.0
	Added session counter functions.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-09
Version : 0.9.0
	Added getting total queue number function.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-10
Version : 0.9.0
	Fixed bugs related to session count list.
	Added checking wrong cases in restful api functions.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-22
Version : 0.9.1
	Fixed bugs related to relay-req-count-for and polling-req-count-for.
	Added protocol parameter. e.g., http or https. 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-29
Version : 0.9.1
	Added MMS configuration querying api.
	Added long polling session count api.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-14
Version : 0.9.2
	Refactoring.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-03
Version : 0.9.3
	Added multi-thread safety.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-09
Version : 0.9.3
	Revised for coding rule conformity.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-10
Version : 0.9.3
	Added resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-09-25
Version : 0.9.5
 	Revised bugs related to not allowing duplicated long polling request 
 	    when a MMS Client loses connection with MMS because of unexpected network disconnection.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
**/
/* -------------------------------------------------------- */



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import kr.ac.kaist.message_queue.MessageQueueManager;
import kr.ac.kaist.message_relaying.SessionManager;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

public class MMSRestAPIHandler {
	String sessionId = "";
	private static final Logger logger = LoggerFactory.getLogger(MMSRestAPIHandler.class);
	
	private MMSLog mmsLog = null;
	private MMSLogForDebug mmsLogForDebug = null;
	
	private static final List<String> apiList = new ArrayList<String>();
	private long clientSessionIds = -1;
	private long mrnsBeingDebugged = -1;
	private long realtimeLogUsers = -1; 
	private long msgQueueCount = -1;
	private long clientSessionCount = -1;
	private boolean isMmsRunning = false;
	private long relayReqCount = -1;
	private int relayReqMinutes = -1;
	private long pollingReqCount = -1;
	private int pollingReqMinutes = -1;
	private boolean correctParams = true;
	private boolean mmsConfiguration = false;
	private long longPollingSessionCount = -1;

	
	MessageQueueManager mqm = null;

	
	public MMSRestAPIHandler (String sessionId){
		this.sessionId = sessionId;
		initializeModule();
		setApiList();
	}
	
	private void initializeModule () {
		mqm = new MessageQueueManager(sessionId);
		mmsLog = MMSLog.getInstance();
		mmsLogForDebug = MMSLogForDebug.getInstance();
	}
	
	private void setApiList () {
		if (apiList.size() == 0) {
			apiList.add("client-session-ids");
			apiList.add("mrns-being-debugged");
			apiList.add("realtime-log-users");
			apiList.add("msg-queue-count");
			apiList.add("client-session-count");
			apiList.add("mms-running");
			apiList.add("relay-req-count-for");
			apiList.add("polling-req-count-for");
			apiList.add("mms-configuration");
			apiList.add("long-polling-session-count");
		}
	}
	
	
	public void setParams (FullHttpRequest req) {
		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
		Map<String,List<String>> params = qsd.parameters();
		
		correctParams = checkParamsInApiList(params);

		clientSessionIds = checkParamsAndSetLongZero(params, "client-session-ids");
		mrnsBeingDebugged = checkParamsAndSetLongZero(params, "mrns-being-debugged");	
		realtimeLogUsers = checkParamsAndSetLongZero(params, "realtime-log-users");
		msgQueueCount = checkParamsAndSetLongZero(params, "msg-queue-count");
		clientSessionCount = checkParamsAndSetLongZero(params, "client-session-count");
		mmsConfiguration = checkParamsAndSetTrue(params, "mms-configuration");
		longPollingSessionCount = checkParamsAndSetLongZero(params, "long-polling-session-count");

		if (correctParams && params.get("mms-running") != null && params.get("mms-running").get(0).equals("y")) {
			isMmsRunning = true;
		}
		
		if (correctParams && params.get("relay-req-count-for") != null) {
			if (StringUtils.isNumeric(params.get("relay-req-count-for").get(0))) {
				relayReqCount = 0;
				relayReqMinutes = Integer.parseInt(params.get("relay-req-count-for").get(0));
				if (relayReqMinutes > 60*24) {  // Session counts are saved for 24 hours.
					relayReqMinutes = 60*24;
				}
				else if (relayReqMinutes < 0) {
					relayReqMinutes = 0;
				}
			}
			else {
				correctParams = false;
			}
		}
		if (correctParams && params.get("polling-req-count-for") != null) {
			if (StringUtils.isNumeric(params.get("polling-req-count-for").get(0))) {
				pollingReqCount = 0;
				pollingReqMinutes = Integer.parseInt(params.get("polling-req-count-for").get(0));
				if (pollingReqMinutes > 60*24) { // Session counts are saved for 24 hours.
					pollingReqMinutes = 60*24;
				}
				else if (pollingReqMinutes < 0) {
					pollingReqMinutes = 0;
				}
			}
			else{
				correctParams = false;
			}
		}
	}
	
	private List<String> checkParamsAndSetNewList (Map<String,List<String>> params, String api) {
		List<String> ret = null;
		if (correctParams) {
			if (params.get(api) != null) {
				if (params.get(api).get(0).equals("y")) {
					ret = new ArrayList<String>();
				}
				else if (!params.get(api).get(0).equals("y")) {
					correctParams = false;
				}
			}
		}
		return ret;
	}
	
	private long checkParamsAndSetLongZero (Map<String,List<String>> params, String api) {
		long ret = -1;
		if (correctParams) {
			if (params.get(api) != null) {
				if (params.get(api).get(0).equals("y")) {
					ret = 0;
				}
				else if (!params.get(api).get(0).equals("y")) {
					correctParams = false;
				}
			}
		}
		return ret;
	}
	
	private boolean checkParamsAndSetTrue (Map<String,List<String>> params, String api) {
		boolean ret = false;
		if (correctParams) {
			if (params.get(api) != null) {
				if (params.get(api).get(0).equals("y")) {
					ret = true;
				}
				else if (!params.get(api).get(0).equals("y")) {
					correctParams = false;
				}
			}
		}
		return ret;
	}
	
	private boolean checkParamsInApiList (Map<String,List<String>> params) {
		Iterator<String> keys = params.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			if (!apiList.contains(key)) {
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public String getResponse () {
		//TODO: To define error messages.
		if (!correctParams) {
			
			return "{\"error\":\""+ErrorCode.WRONG_PARAM.toString()+"\"}";
			
		}
		else {
			JSONObject jobj = new JSONObject();
			
			if (clientSessionIds != -1) {
				JSONArray jary = new JSONArray();
				if (SessionManager.getSessionInfoSize()<0) {
					SessionManager.resetSessionInfo();
				}
				jary.addAll(SessionManager.getSessionIDs());
				jobj.put("client-session-ids", jary);
			}
			if (mrnsBeingDebugged != -1) {
				JSONArray jary = new JSONArray();
				jary.addAll(MMSLogForDebug.getInstance().getMrnSet());
				jobj.put("mrns-being-debugged", jary);
			}
			if (realtimeLogUsers != -1) {
				JSONArray jary = new JSONArray();
				jary.addAll(MMSLog.getInstance().getRealtimeLogUsersSet());
				jobj.put("realtime-log-users", jary);
			}
			if (msgQueueCount != -1) {
				jobj.put("msg-queue-count", mqm.getTotalQueueNumber());
			}
			if (clientSessionCount != -1) {
				if (SessionManager.getSessionInfoSize()<0) {
					SessionManager.resetSessionInfo();
				}
				clientSessionCount = SessionManager.getSessionInfoSize();
				jobj.put("client-session-count", clientSessionCount);
			}
			if (isMmsRunning != false) {
				isMmsRunning = true;
				jobj.put("mms-running", isMmsRunning);
				
			}
			if (relayReqCount != -1) {
				int countListSize = SessionManager.getSessionCountListSize();
				relayReqCount = SessionManager.getSessionCount(relayReqMinutes);
				JSONObject jobj2 = new JSONObject();
				
				if (countListSize <= 12) {
					jobj2.put("min", 1);
				}
				else {
					jobj2.put("min", Math.min(countListSize/12, relayReqMinutes));
				}
				jobj2.put("count", relayReqCount);
				jobj.put("relay-req-count-for", jobj2);
				
			}
			if (pollingReqCount != -1) {
				int countListSize = SessionManager.getSessionCountListSize();
				pollingReqCount = SessionManager.getPollingSessionCount(pollingReqMinutes);
				JSONObject jobj2 = new JSONObject();
				if (countListSize <= 12) {
					jobj2.put("min", 1);
				}
				else {
					jobj2.put("min", Math.min(countListSize/12, pollingReqMinutes));
				}
				jobj2.put("count", pollingReqCount);
				jobj.put("polling-req-count-for", jobj2);
				
			}
			if (mmsConfiguration) {
				
				JSONObject jobj2 = new JSONObject();
				if (!MMSConfiguration.getMmsConfiguration().isEmpty()) {
					SortedSet<String> keys = new TreeSet<String>(MMSConfiguration.getMmsConfiguration().keySet());
					for (String key : keys) {
						String val = MMSConfiguration.getMmsConfiguration().get(key);
						if (isNumeric(val)) {
							jobj2.put(key,Integer.parseInt(val));
						}
						else if (isTrue(val)) {
							jobj2.put(key,true);
						}
						else if (isFalse(val)) {
							jobj2.put(key,false);
						}
						else {
							jobj2.put(key, val);
						}
					}
				}
				
				jobj.put("mms-configuration", jobj2);
			}
			if (longPollingSessionCount != -1) {
				jobj.put("long-polling-session-count", SeamlessRoamingHandler.getDuplicationInfoSize());
			}
			
			return jobj.toJSONString();
		}
	}
	private boolean isNumeric(String strNum) {
	    return strNum.matches("-?\\d+(\\.\\d+)?");
	}
	private boolean isTrue(String str) {
		if (str.equals("true")) {
			return true;
		}
		else {
			return false;
		}
	}
	private boolean isFalse(String str) {
		if (str.equals("false")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public byte[] getRealtimeLog (FullHttpRequest req) {
		byte[] message;
		
		String realtimeLog = "";
		String callback = "";
		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
		Map<String,List<String>> params = qsd.parameters();
		if (params.get("id") != null & params.get("callback") != null) {
			callback = params.get("callback").get(0);
			realtimeLog = mmsLog.getRealtimeLog(params.get("id").get(0), this.sessionId);
			message = (callback+"("+realtimeLog+")").getBytes(Charset.forName("UTF-8"));
		}
		else {
			message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
		}
		
		return message;
	}
	
	public byte[] getStatus (FullHttpRequest req) {
		
		byte[] message = null;
		
		String status;
		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
		Map<String,List<String>> params = qsd.parameters();
		mmsLog.info(logger, this.sessionId, "Get MMS status and logs.");

		if (params.get("mrn") == null) {
			try {
				status = mmsLog.getStatus("");
				message = status.getBytes(Charset.forName("UTF-8"));
			} 
			catch (UnknownHostException e) {
				message = ErrorCode.DUMPMNS_LOGGING_ERROR.getUTF8Bytes();
				mmsLog.warnException(logger, this.sessionId, ErrorCode.DUMPMNS_LOGGING_ERROR.toString(), e, 5);
			} 
			catch (IOException e) {
				message = ErrorCode.DUMPMNS_LOGGING_ERROR.getUTF8Bytes();
				mmsLog.warnException(logger, this.sessionId, ErrorCode.DUMPMNS_LOGGING_ERROR.toString(), e, 5);
			}
		}
		else {

			try {
				status = mmsLog.getStatus(params.get("mrn").get(0));
				message = status.getBytes(Charset.forName("UTF-8"));
			} 
			catch (UnknownHostException e) {
				message = ErrorCode.DUMPMNS_LOGGING_ERROR.getUTF8Bytes();
				mmsLog.warnException(logger, this.sessionId, ErrorCode.DUMPMNS_LOGGING_ERROR.toString(), e, 5);
			} 
			catch (IOException e) {
				message = ErrorCode.DUMPMNS_LOGGING_ERROR.getUTF8Bytes();
				mmsLog.warnException(logger, this.sessionId, ErrorCode.DUMPMNS_LOGGING_ERROR.toString(), e, 5);
			}
		}
		
		return message;
	}
	
	public byte[] addIdInRealtimeLogIds (FullHttpRequest req) {
		byte[] message = null;

		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
		Map<String,List<String>> params = qsd.parameters();
		if (params.get("id") != null) {
			mmsLog.addIdToBriefRealtimeLogEachIDs(params.get("id").get(0));
			mmsLog.warn(logger, this.sessionId, "Added an ID using realtime log service="+params.get("id").get(0)+".");
			message = "OK".getBytes(Charset.forName("UTF-8"));
		}
		else {
			mmsLog.warn(logger, this.sessionId, ErrorCode.WRONG_PARAM.toString());
			message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
		}
		
		return message;
	}
	
	public byte[] removeIdInRealtimeLogIds (FullHttpRequest req) {
		byte[] message = null;

		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
		Map<String,List<String>> params = qsd.parameters();
		if (params.get("id") != null) {
			mmsLog.removeIdFromBriefRealtimeLogEachIDs(params.get("id").get(0));
			mmsLog.warn(logger, this.sessionId, "Removed an ID using realtime log service="+params.get("id").get(0)+".");
			message = "OK".getBytes(Charset.forName("UTF-8"));
		}
		else {
			mmsLog.warn(logger, this.sessionId, ErrorCode.WRONG_PARAM.toString());
			message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
		}
		
		return message;
	}
	
	public byte[] addMrnBeingDebugged (FullHttpRequest req) {
		byte[] message = null;
		
		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
		Map<String,List<String>> params = qsd.parameters();
		if (params.get("mrn")!=null) {
			String mrn = params.get("mrn").get(0);
			if (mrn != null) {
				mmsLogForDebug.addMrn(mrn);
				mmsLog.warn(logger, this.sessionId, "Added a MRN being debugged="+mrn+".");
				message = "OK".getBytes(Charset.forName("UTF-8"));
			}
			else {
				mmsLog.warn(logger, this.sessionId, ErrorCode.WRONG_PARAM.toString());
				message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
			}
		}
		else {
			mmsLog.warn(logger, this.sessionId, ErrorCode.WRONG_PARAM.toString());
			message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
		}
		
		return message;
	}
	
	public byte[] removeMrnBeingDebugged (FullHttpRequest req) {
		byte[] message = null;
		
		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
		Map<String,List<String>> params = qsd.parameters();
		if (params.get("mrn")!=null) {
			String mrn = params.get("mrn").get(0);
			mmsLogForDebug.removeMrn(mrn);
			mmsLog.warn(logger, this.sessionId, "Removed debug MRN="+mrn+".");
			message = "OK".getBytes(Charset.forName("UTF-8"));
		}
		else {
			mmsLog.warn(logger, this.sessionId, ErrorCode.WRONG_PARAM.toString());
			message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
		}
		
		return message;
	}
	
	//This method will be
	  @Deprecated
	public byte[] addMnsEntry (FullHttpRequest req) {
		byte[] message = null;
		
		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
		Map<String,List<String>> params = qsd.parameters();
		mmsLog.warn(logger, this.sessionId, "Add MRN=" + params.get("mrn").get(0) + " IP=" + params.get("ip").get(0) + " Port=" + params.get("port").get(0) + " Model=" + params.get("model").get(0)+".");
		if (params.get("mrn")!=null && !params.get("mrn").get(0).equals(MMSConfiguration.getMmsMrn())) {
			try {
				addEntryMNS(params.get("mrn").get(0), params.get("ip").get(0), params.get("port").get(0), params.get("model").get(0));
				message = "OK".getBytes(Charset.forName("UTF-8"));
			}
			catch (UnknownHostException e) {
				// This code block will be deprecated, so there is no definition of error code.
				
				mmsLog.errorException(logger, this.sessionId, "", e, 5);
			} 
    		catch (IOException e) {
    			ErrorCode.DUMPMNS_LOGGING_ERROR.getUTF8Bytes();
    			
    			mmsLog.errorException(logger, this.sessionId, "", e, 5);
			} 
		}
		else {
			mmsLog.warn(logger, this.sessionId, ErrorCode.WRONG_PARAM.toString());
			message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
		}
		return message;
	}
	//This method will be
	  @Deprecated
	public byte[] removeMnsEntry (FullHttpRequest req) {
		byte[] message = null;
		
		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
		Map<String,List<String>> params = qsd.parameters();
		mmsLog.warn(logger, this.sessionId, "Remove MRN=" + params.get("mrn").get(0)+".");
		if (params.get("mrn")!=null && !params.get("mrn").get(0).equals(MMSConfiguration.getMmsMrn())) {
			try {
				removeEntryMNS(params.get("mrn").get(0));
				message = "OK".getBytes(Charset.forName("UTF-8"));
			} 
    		catch (UnknownHostException e) {
				// This code block will be deprecated, so there is no definition of error code.
    			
    			mmsLog.errorException(logger, this.sessionId, "", e, 5);
			} 
    		catch (IOException e) {
    			message = ErrorCode.DUMPMNS_LOGGING_ERROR.getUTF8Bytes();
    			
    			mmsLog.errorException(logger, this.sessionId, "", e, 5);
			} 
		}
		else {
			mmsLog.warn(logger, this.sessionId, ErrorCode.WRONG_PARAM.toString());
			message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
		}
		
		return message;
	}

	//This method will be
	  @Deprecated
	private void removeEntryMNS(String mrn) throws UnknownHostException, IOException{ //
	
	
		Socket MNSSocket = null;
		PrintWriter pw = null;	
		InputStreamReader isr = null;
		BufferedReader br = null;
		String queryReply = null;
		try{
			//String modifiedSentence;
	
			MNSSocket = new Socket(MMSConfiguration.getMnsHost(), MMSConfiguration.getMnsPort());
			MNSSocket.setSoTimeout(5000);
			pw = new PrintWriter(MNSSocket.getOutputStream());
			isr = new InputStreamReader(MNSSocket.getInputStream());
			br = new BufferedReader(isr);
			String inputLine = null;
			StringBuffer response = new StringBuffer();
	
			mmsLog.warn(logger, this.sessionId, "Remove Entry="+mrn+".");
	
			pw.println("Remove-Entry:"+mrn);
			pw.flush();
			if (!MNSSocket.isOutputShutdown()) {
				MNSSocket.shutdownOutput();
			}
	
	
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
	
	
			queryReply = response.toString();
			mmsLog.trace(logger, this.sessionId, "From server="+queryReply+".");
	
	
		} catch (UnknownHostException e) {
			mmsLog.errorException(logger, this.sessionId, "", e, 5);
			
		} catch (IOException e) {
			mmsLog.errorException(logger, this.sessionId, "", e, 5);
			
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					mmsLog.errorException(logger, this.sessionId, "", e, 5);
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					mmsLog.errorException(logger, this.sessionId, "", e, 5);
				}
			}
			if (MNSSocket != null) {
				try {
					MNSSocket.close();
				} catch (IOException e) {
					mmsLog.errorException(logger, this.sessionId, "", e, 5);
				}
			}
		}
		return;
	}
	
	//This method will be
	@Deprecated
	private void addEntryMNS(String mrn, String ip, String port, String model) throws UnknownHostException, IOException {


		Socket MNSSocket = null;
		PrintWriter pw = null;	
		InputStreamReader isr = null;
		BufferedReader br = null;
		String queryReply = null;
		try{
			//String modifiedSentence;

			MNSSocket = new Socket(MMSConfiguration.getMnsHost(), MMSConfiguration.getMnsPort());
			MNSSocket.setSoTimeout(5000);
			pw = new PrintWriter(MNSSocket.getOutputStream());
			isr = new InputStreamReader(MNSSocket.getInputStream());
			br = new BufferedReader(isr);
			String inputLine = null;
			StringBuffer response = new StringBuffer();

			mmsLog.warn(logger, this.sessionId, "Add Entry="+mrn+".");

			pw.println("Add-Entry:"+mrn+","+ip+","+port+","+model);
			pw.flush();
			if (!MNSSocket.isOutputShutdown()) {
				MNSSocket.shutdownOutput();
			}


			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}


			queryReply = response.toString();
			mmsLog.trace(logger, this.sessionId, "From server=" + queryReply+".");

		} catch (UnknownHostException e) {
			mmsLog.errorException(logger, this.sessionId, "", e, 5);
			 
		} catch (IOException e) {
			mmsLog.errorException(logger, this.sessionId, "", e, 5);
			
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					mmsLog.errorException(logger, this.sessionId, "", e, 5);
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					mmsLog.errorException(logger, this.sessionId, "", e, 5);
				}
			}
			if (MNSSocket != null) {
				try {
					MNSSocket.close();
				} catch (IOException e) {
					mmsLog.errorException(logger, this.sessionId, "", e, 5);
				}
			}
		}
		return;
	}
	
}
