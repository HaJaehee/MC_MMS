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
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
/* -------------------------------------------------------- */



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.message_queue.MessageQueueManager;
import kr.ac.kaist.message_relaying.SessionManager;

public class MMSRestAPIHandler {
	String SESSION_ID = "";
	private static final Logger logger = LoggerFactory.getLogger(MMSRestAPIHandler.class);
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
	
	MessageQueueManager mqm = null;

	
	public MMSRestAPIHandler (String sessionId){
		this.SESSION_ID = sessionId;
		initializeModule();
		setApiList();
	}
	
	private void initializeModule () {
		mqm = new MessageQueueManager(SESSION_ID);
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
		}
	}
	
	
	public void setParams (Map<String, List<String>> params) {
		
		correctParams = checkParamsInApiList(params);

		clientSessionIds = checkParamsAndSetLongZero(params, "client-session-ids");
		mrnsBeingDebugged = checkParamsAndSetLongZero(params, "mrns-being-debugged");	
		realtimeLogUsers = checkParamsAndSetLongZero(params, "realtime-log-users");
		msgQueueCount = checkParamsAndSetLongZero(params, "msg-queue-count");
		clientSessionCount = checkParamsAndSetLongZero(params, "client-session-count");

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
			return "{\"error\":\"wrong parameter.\"}";
		}
		else {
			JSONObject jobj = new JSONObject();
			
			if (clientSessionIds != -1) {
				JSONArray jary = new JSONArray();
				jary.addAll(SessionManager.getSessionInfo().keySet());
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
				clientSessionCount = SessionManager.getSessionInfo().size();
				jobj.put("client-session-count", clientSessionCount+"");
			}
			if (isMmsRunning != false) {
				isMmsRunning = true;
				jobj.put("mms-running", isMmsRunning+"");
				
			}
			if (relayReqCount != -1) {
				int countListSize = SessionManager.getSessionCountList().size(); // Session counting list saves the number of sessions for every 5 seconds.
				for (int i = 0 ; i < countListSize && i < relayReqMinutes*12 ; i++) { // Adding count up for x minutes.
					relayReqCount += SessionManager.getSessionCountList().get(i).getSessionCount() // Total session counts.
							- SessionManager.getSessionCountList().get(i).getPollingSessionCount();// Subtract polling session counts from total session counts.
				}
				JSONObject jobj2 = new JSONObject();
				
				if (countListSize <= 12) {
					jobj2.put("min", "1");
				}
				else {
					jobj2.put("min", Math.min(countListSize/12, relayReqMinutes));
				}
				jobj2.put("count", relayReqCount);
				jobj.put("relay-req-count-for", jobj2);
				
			}
			if (pollingReqCount != -1) {
				int countListSize = SessionManager.getSessionCountList().size(); // Session counting list saves the number of sessions for every 5 seconds.
				for (int i = 0 ; i < countListSize && i < pollingReqMinutes*12 ; i++) { // Adding count up for x minutes.
					pollingReqCount += SessionManager.getSessionCountList().get(i).getPollingSessionCount(); // Polling session counts.
				}
				JSONObject jobj2 = new JSONObject();
				if (countListSize <= 12) {
					jobj2.put("min", "1");
				}
				else {
					jobj2.put("min", Math.min(countListSize/12, pollingReqMinutes));
				}
				jobj2.put("count", pollingReqCount);
				jobj.put("polling-req-count-for", jobj2);
				
			}
			return jobj.toJSONString();
		}
	}
	
}
