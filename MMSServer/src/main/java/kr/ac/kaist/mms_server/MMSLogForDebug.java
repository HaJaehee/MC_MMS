package kr.ac.kaist.mms_server;
/* -------------------------------------------------------- */
/** 
File name : MMSLogForDebug.java
	It contains filtered logs by related MRN and session IDs.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-10-25
Version : 0.6.0

Rev. history : 2017-11-18
Version : 0.7.0
	Fixed bugs due to null pointer execptions.
	Replaced class name from MMSLogsForDebug to MMSLogForDebug. 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-20
Version : 0.7.0
	Arguments must be checked if it is null.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-18
Version : 0.7.0
	Replaced this class from static class to singleton class.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-22
Version : 0.7.0
	Resolved critical problem caused by duplicated items in the list of mapMrnAndSessionId and mapSessionIdAndMrn.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)


Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-08
Version : 0.9.3
	Improved performance.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-09
Version : 0.9.3
	Revised for coding rule conformity.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ch.qos.logback.classic.jul.JULHelper;


public class MMSLogForDebug {
	private HashMap<String,ArrayList<String>> mapSessionIdAndLog = null;
	private HashMap<String,LinkedHashSet<String>> mapMrnAndSessionId = null;
	private HashMap<String,LinkedHashSet<String>> mapSessionIdAndMrn = null;
	private int maxSessionCount = 0;
	
	private MMSLogForDebug () {
		mapSessionIdAndLog = new HashMap<String,ArrayList<String>>();
		mapMrnAndSessionId = new HashMap<String,LinkedHashSet<String>>();
		mapSessionIdAndMrn = new HashMap<String,LinkedHashSet<String>>();
		maxSessionCount = 50;
		
		//addMrn("urn:mrn:mcl:vessel:dma:poul-lowenorn"); //For testing.
		/*
		addMrn("urn:mrn:smart:service:instance:mof:S10");
		addMrn("urn:mrn:smart:service:instance:mof:S11");
		addMrn("urn:mrn:smart:service:instance:mof:S20");
		addMrn("urn:mrn:smart:service:instance:mof:S30");
		addMrn("urn:mrn:smart:service:instance:mof:S40");
		addMrn("urn:mrn:smart:service:instance:mof:S51");
		addMrn("urn:mrn:smart:service:instance:mof:S52");
		addMrn("urn:mrn:smart:vessel:imo-no:mof:tmp100fors10");
		addMrn("urn:mrn:smart:vessel:imo-no:mof:tmp100fors11");
		addMrn("urn:mrn:smart:vessel:imo-no:mof:tmp101fors10");
		addMrn("urn:mrn:smart:vessel:imo-no:mof:tmp200fors20");
		addMrn("urn:mrn:smart:vessel:imo-no:mof:tmp300fors30");
		addMrn("urn:mrn:smart:vessel:imo-no:mof:tmp400fors40");
		addMrn("urn:mrn:smart:vessel:imo-no:mof:tmp400fors41");
		addMrn("urn:mrn:smart:vessel:imo-no:mof:tmp510fors51");
		addMrn("urn:mrn:smart:vessel:imo-no:mof:tmp520fors52");
		addMrn("urn:mrn:smart:vessel:imo-no:mof:tmp520fors55");
		*/
	}
	
	public static MMSLogForDebug getInstance() { //double check synchronization.
		return LazyHolder.INSTANCE;
	}
	
	private static class LazyHolder {
		private static final MMSLogForDebug INSTANCE = new MMSLogForDebug();
	}
	
	public synchronized String getLog (String mrn){
		if (mrn!=null&&mapMrnAndSessionId!=null&&mapSessionIdAndLog!=null) {
			LinkedHashSet<String> sessionIdList = mapMrnAndSessionId.get(mrn);
			if (sessionIdList!=null) {
				StringBuilder logs = new StringBuilder();
				for (String sessionId : sessionIdList) {
					ArrayList<String> logList = mapSessionIdAndLog.get(sessionId);
					if (logList!=null) {
						for (String log : logList) {
							logs.append(log);
						}
					} 
					else { // mapSessionIdAndLog.get(sessionId)==null
						return "";
					}
				}
				return logs.toString();
			} 
			else { // mapMrnAndSessionId.get(mrn)==null
				if (mapMrnAndSessionId.containsKey(mrn)) {
					return "";
				}
				else {
					return null;
				}
			}
		}
		else {
			return null;
		}
	}
	
	public boolean containsSessionId (String sessionId){
		synchronized (mapSessionIdAndLog) {
			synchronized (mapSessionIdAndMrn) {
				if (sessionId!=null&&
						mapSessionIdAndLog!=null&&mapSessionIdAndMrn!=null&&
						mapSessionIdAndLog.containsKey(sessionId)&&mapSessionIdAndMrn.containsKey(sessionId)) {
					return true;
				} 
				else {
					return false;
				}
			}
		}
		
	}
	
	public void addMrn (String mrn) {
		synchronized (mapMrnAndSessionId) {
			if (mapMrnAndSessionId==null) {
				mapMrnAndSessionId = new HashMap<String,LinkedHashSet<String>>();
			}
				
			if (mrn==null||mapMrnAndSessionId.containsKey(mrn)) {
				return;
			}
			
			LinkedHashSet<String> sessionIdList = new LinkedHashSet<String>();
			mapMrnAndSessionId.put(mrn, sessionIdList);
		}
	}
	
	
	public synchronized void removeMrn (String mrn){
		if (mapMrnAndSessionId!=null) {
			LinkedHashSet<String> sessionIdList = mapMrnAndSessionId.get(mrn);
			if (sessionIdList!=null) {
				for (String sessionId : sessionIdList) {
					if (mapSessionIdAndMrn!=null) {
						LinkedHashSet<String> mrnList = mapSessionIdAndMrn.get(sessionId);
						if (mrnList!=null) {
							mrnList.remove(mrn);
							if (mrnList.isEmpty()) {
								if (mapSessionIdAndLog!=null) {
									ArrayList<String> logList = mapSessionIdAndLog.get(sessionId);
									if (logList!=null) {
										logList.clear();
										logList = null;
										mapSessionIdAndLog.remove(sessionId);
									}
								}
								else { //mapSessionIdAndLog==null
									mapSessionIdAndLog = new HashMap<String,ArrayList<String>>();
								}
								mrnList = null;
								mapSessionIdAndMrn.remove(sessionId);
							}
						}
					}
					else { //mapSessionIdAndMrn==null
						mapSessionIdAndMrn = new HashMap<String,LinkedHashSet<String>>();
					}
				}
				sessionIdList.clear();
				sessionIdList = null;
			}
			mapMrnAndSessionId.remove(mrn);
		}
		else { // mapMrnAndSessionId==null
			mapMrnAndSessionId = new HashMap<String,LinkedHashSet<String>>();
		}
	}
	
	public Set<String> getMrnSet () {
		synchronized (mapMrnAndSessionId) {
			if (mapMrnAndSessionId==null){
				mapMrnAndSessionId = new HashMap<String,LinkedHashSet<String>>();
			}
			return mapMrnAndSessionId.keySet();
		}
	}
	
	public synchronized void addSessionId (String mrn, String sessionId){
		if(mrn!=null&&sessionId!=null) {
			
			if (mapSessionIdAndLog==null){
				mapSessionIdAndLog = new HashMap<String,ArrayList<String>>();
			}
			if (mapMrnAndSessionId==null){
				mapMrnAndSessionId = new HashMap<String,LinkedHashSet<String>>();
			}
			if (mapSessionIdAndMrn==null){
				mapSessionIdAndMrn = new HashMap<String,LinkedHashSet<String>>();
			}
			
			LinkedHashSet<String> sessionIdList = mapMrnAndSessionId.get(mrn);
			if (sessionIdList!=null) {
				while (sessionIdList.size() > maxSessionCount) {
					String lruSession = sessionIdList.iterator().next();
					LinkedHashSet<String> mrnList = mapSessionIdAndMrn.get(lruSession);
					if (mrnList!=null) {
						mrnList.remove(mrn);
						if (mrnList.isEmpty()) {
							ArrayList<String> logList = mapSessionIdAndLog.get(lruSession);
							if (logList!=null) {
								logList.clear();
								logList = null;
								mapSessionIdAndLog.remove(lruSession);
							}
							mrnList = null;
							mapSessionIdAndMrn.remove(lruSession);
						}
					}
					sessionIdList.remove(lruSession);
				}
				sessionIdList.add(sessionId);
				mapMrnAndSessionId.put(mrn, sessionIdList);
				
				LinkedHashSet<String> mrnList = mapSessionIdAndMrn.get(sessionId);
				if (mrnList==null) {
					mrnList = new LinkedHashSet<String>();
					mrnList.add(mrn);
					mapSessionIdAndMrn.put(sessionId, mrnList);
				} 
				else { //mapSessionIdAndMrn.get(sessionId)!=null
					mrnList.add(mrn);
					mapSessionIdAndMrn.put(sessionId, mrnList);
				}
				ArrayList<String> logList = mapSessionIdAndLog.get(sessionId);
				if (logList==null)
				{
					logList = new ArrayList<String>();
					mapSessionIdAndLog.put(sessionId, logList);
				}
			} 
		}
	}
	
	void addLog (String sessionId, String log) {
		synchronized (mapSessionIdAndLog) {
			if (sessionId!=null&&mapSessionIdAndLog!=null&&mapSessionIdAndLog.get(sessionId)!=null)	{
				mapSessionIdAndLog.get(sessionId).add(log);
			}
		}
	}

	public boolean isItsLogListEmtpy (String sessionId) {
		synchronized (mapSessionIdAndLog) {
			if (sessionId!=null&&mapSessionIdAndLog!=null&&mapSessionIdAndLog.get(sessionId)!=null&&mapSessionIdAndLog.get(sessionId).isEmpty()) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	public int getMaxSessionCount ()	{
		return maxSessionCount;
	}
}
