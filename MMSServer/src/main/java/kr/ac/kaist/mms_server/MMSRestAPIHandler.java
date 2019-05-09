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
/* -------------------------------------------------------- */



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.message_queue.MessageQueueManager;
import kr.ac.kaist.message_relaying.SessionList;
import kr.ac.kaist.message_relaying.SessionManager;

public class MMSRestAPIHandler {
	String SESSION_ID = "";
	private static final Logger logger = LoggerFactory.getLogger(MMSRestAPIHandler.class);
	Map<String,List<String>> params = null;
	List<String> clientSessionIds = null;
	List<String> mrnsBeingDebugged = null;
	List<String> realtimeLogUsers = null; 
	double msgQueueCount = -1;
	double clientSessionCount = -1;
	boolean isMmsRunning = false;
	int relayReqCount = -1;
	int relayReqMinutes = -1;
	int pollingReqCount = -1;
	int pollingReqMinutes = -1;
	
	MessageQueueManager mqm = null;

	
	public MMSRestAPIHandler (String sessionId){
		this.SESSION_ID = sessionId;
		initializeModule();
	}
	
	private void initializeModule () {
		mqm = new MessageQueueManager(SESSION_ID);
	}
	
	//TODO: To define error messages.
	public void setParams (Map<String, List<String>> params) {
		this.params = params;
		
		if (this.params.get("client-session-ids") != null) {
			clientSessionIds = new ArrayList<String>();
		}
		if (this.params.get("mrns-being-debugged") != null) {
			mrnsBeingDebugged = new ArrayList<String>();
		}
		if (this.params.get("realtime-log-users") != null) {
			realtimeLogUsers = new ArrayList<String>();
		}
		if (this.params.get("msg-queue-count") != null) {
			msgQueueCount = 0;
		}
		if (this.params.get("client-session-count") != null) {
			clientSessionCount = 0;
		}
		if (this.params.get("mms-running") != null) {
			isMmsRunning = true;
		}
		if (this.params.get("relay-req-count-for") != null) {
			relayReqCount = 0;
			relayReqMinutes = Integer.parseInt(this.params.get("relay-req-count-for").get(0));
		}
		if (this.params.get("polling-req-count-for") != null) {
			pollingReqCount = 0;
			pollingReqMinutes = Integer.parseInt(this.params.get("polling-req-count-for").get(0));
		}
	}
	
	@SuppressWarnings("unchecked")
	public String getResponse () {
		//TODO: To define error messages.
		if (params == null) {
			return "";
		}
		else {
			JSONObject jobj = new JSONObject();
			
			if (clientSessionIds != null) {
				clientSessionIds.addAll(SessionManager.getSessionInfo().keySet());
				JSONArray jary = new JSONArray();
				jary.addAll(clientSessionIds);
				jobj.put("client-session-ids", jary);
			}
			if (mrnsBeingDebugged != null) {
				mrnsBeingDebugged.addAll(MMSLogForDebug.getInstance().getMrnSet());
				JSONArray jary = new JSONArray();
				jary.addAll(mrnsBeingDebugged);
				jobj.put("mrns-being-debugged", jary);
			}
			if (realtimeLogUsers != null) {
				realtimeLogUsers.addAll(MMSLog.getInstance().getRealtimeLogUsersSet());
				JSONArray jary = new JSONArray();
				jary.addAll(realtimeLogUsers);
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
				int countListSize = SessionManager.getSessionCountList().size();
				for (int i = 0 ; i < countListSize && i < relayReqMinutes*12 ; i++) { // Adding count up for x minutes.
					relayReqCount += SessionManager.getSessionCountList().get(i).getSessionCount()
							- SessionManager.getSessionCountList().get(i).getPollingSessionCount();
				}
				JSONObject jobj2 = new JSONObject();
				jobj2.put("min", relayReqMinutes);
				jobj2.put("count", relayReqCount);
				jobj.put("relay-req-count-for", jobj2);
				
			}
			if (pollingReqCount != -1) {
				int countListSize = SessionManager.getSessionCountList().size();
				for (int i = 0 ; i < countListSize && i < pollingReqMinutes*12 ; i++) { // Adding count up for x minutes.
					pollingReqCount += SessionManager.getSessionCountList().get(i).getPollingSessionCount();
				}
				JSONObject jobj2 = new JSONObject();
				jobj2.put("min", pollingReqMinutes);
				jobj2.put("count", pollingReqCount);
				jobj.put("polling-req-count-for", jobj2);
				
			}
			return jobj.toJSONString();
		}
	}
	
}
