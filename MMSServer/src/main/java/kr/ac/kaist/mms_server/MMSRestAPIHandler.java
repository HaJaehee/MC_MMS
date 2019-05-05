package kr.ac.kaist.mms_server;
/* -------------------------------------------------------- */
/** 
File name : MMSRestAPIHandler.java
	It handles MMS restful API and generates response.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2019-05-05
Version : 0.9.0
/* -------------------------------------------------------- */



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.message_relaying.SessionList;
import kr.ac.kaist.message_relaying.SessionManager;

public class MMSRestAPIHandler {
	String SESSION_ID = "";
	private static Logger logger = null;
	Map<String,List<String>> params = null;
	Set<String> clientSessionIds = null;
	Set<String> mrnsBeingDebugged = null;
	Set<String> realtimeLogUsers = null; 
	double msgQueueCount = -1;
	double clientSessionCount = -1;
	boolean isMmsRunning = false;
	int relayReqCount = -1;
	int relayReqMinutes = -1;
	int pollingReqCount = -1;
	int pollingReqMinutes = -1;

	
	public MMSRestAPIHandler (String sessionId){
		this.SESSION_ID = sessionId;
		logger = LoggerFactory.getLogger(MMSRestAPIHandler.class);
		
	}
	
	//TODO: To define error messages.
	public void setParams (Map<String, List<String>> params) {
		this.params = params;
		
		if (this.params.get("client-session-ids") != null) {
			clientSessionIds = new TreeSet<String>();
		}
		if (this.params.get("mrns-being-debugged") != null) {
			mrnsBeingDebugged = new TreeSet<String>();
		}
		if (this.params.get("realtime-log-users") != null) {
			realtimeLogUsers = new TreeSet<String>();
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
	
	public String getResponse () {
		//TODO: To define error messages.
		if (params == null) {
			return "";
		}
		else {
			if (clientSessionIds != null) {
				clientSessionIds = (TreeSet<String>) SessionManager.sessionInfo.keySet();
				//TODO
			}
			if (mrnsBeingDebugged != null) {
				mrnsBeingDebugged = MMSLogForDebug.getInstance().getMrnSet();
				//TODO
			}
			if (realtimeLogUsers != null) {
				realtimeLogUsers = MMSLog.getInstance().getRealtimeLogUsersSet();
				//TODO
			}
			if (msgQueueCount != -1) {
				//TODO
			}
			if (clientSessionCount != -1) {
				clientSessionCount = SessionManager.sessionInfo.size();
				//TODO
			}
			if (isMmsRunning != false) {
				isMmsRunning = true;
				//TODO
			}
			if (relayReqCount != -1) {
				//TODO
			}
			if (pollingReqCount != -1) {
				//TODO
			}
			return "";
		}
	}
	
}
