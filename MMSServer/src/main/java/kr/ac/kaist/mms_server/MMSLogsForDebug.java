package kr.ac.kaist.mms_server;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/* -------------------------------------------------------- */
/** 
File name : MMSLogsForDebug.java
	It contains filtered logs by related MRN and session IDs.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-10-25
Version : 0.6.0
*/
/* -------------------------------------------------------- */
public class MMSLogsForDebug {
	private static Map<String,List<String>> sessionIdLogMapper = new HashMap<String,List<String>>();
	private static Map<String,List<String>> mrnSessionIdMapper = new HashMap<String,List<String>>();
	private static Map<String,List<String>> sessionIdMrnMapper = new HashMap<String,List<String>>();
	private static int maxSessionCount = 100;
	
	// TODO
	public static String getLog (String mrn){
		if (mrnSessionIdMapper.get(mrn)!=null) {
			List<String> sessionIdList = mrnSessionIdMapper.get(mrn);
			StringBuilder logs = new StringBuilder();
			for (String sessionId : sessionIdList) {
				if (sessionIdLogMapper.get(sessionId)!=null) {
					List<String> logList = sessionIdLogMapper.get(sessionId);
					for (String log : logList) {
						logs.append(log+"<br/>");
					}
				} 
				else {
					return "";
				}
			}
			return logs.toString();
		} 
		else {
			if (mrnSessionIdMapper.containsKey(mrn)) {
				return "";
			}
			else {
				return null;
			}
		}
	}
	
	public static boolean containsSessionId (String sessionId){
		if (sessionIdLogMapper.containsKey(sessionId)) {
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static void addMrn (String mrn) {
		if (mrn == null) {
			return;
		}
		else if (mrnSessionIdMapper.containsKey(mrn)) {
			return;
		}
		mrnSessionIdMapper.put(mrn, null);
	}
	
	
	public static void removeMrn (String mrn) {
		if (mrnSessionIdMapper.containsKey(mrn)&&mrnSessionIdMapper.get(mrn)!=null) {
			for (String sessionId : mrnSessionIdMapper.get(mrn)) {
				if (sessionIdMrnMapper.get(sessionId)!=null&&sessionIdMrnMapper.get(sessionId).contains(mrn)) {
					sessionIdMrnMapper.get(sessionId).remove(mrn);
					if (sessionIdMrnMapper.get(sessionId).isEmpty()) {
						if (sessionIdLogMapper.get(sessionId)!=null) {
							sessionIdLogMapper.get(sessionId).clear();
							sessionIdLogMapper.remove(sessionId);
						}
						sessionIdMrnMapper.remove(sessionId);
					}
				}
			}
			mrnSessionIdMapper.get(mrn).clear();
		}
		mrnSessionIdMapper.remove(mrn);
	}
	
	public static Set<String> getMrnSet () {
		return mrnSessionIdMapper.keySet();
	}
	
	public static void addSessionId (String mrn, String sessionId) {
		if (mrnSessionIdMapper.containsKey(mrn)) {
			if (mrnSessionIdMapper.get(mrn)!=null) {
				List<String> sessionIdList = mrnSessionIdMapper.get(mrn);
				while (sessionIdList.size() > maxSessionCount) {
					String lruSession = mrnSessionIdMapper.get(mrn).get(0);
					if (sessionIdLogMapper.get(lruSession)!=null) {
						sessionIdLogMapper.get(lruSession).clear();
						sessionIdLogMapper.remove(lruSession);
					}
					mrnSessionIdMapper.get(mrn).remove(0);
				}
				sessionIdList.add(sessionId);
				mrnSessionIdMapper.put(mrn, sessionIdList);
				sessionIdLogMapper.put(sessionId, null);
			} 
			else {
				List<String> sessionIdList = new ArrayList<String>();
				sessionIdList.add(sessionId);
				mrnSessionIdMapper.put(mrn, sessionIdList);
				sessionIdLogMapper.put(sessionId, null);

			}
			if (sessionIdMrnMapper.get(sessionId)==null) {
				List<String> mrnList = new ArrayList<String>();
				mrnList.add(mrn);
				sessionIdMrnMapper.put(sessionId, mrnList);
			} 
			else {
				sessionIdMrnMapper.get(sessionId).add(mrn);
			}
		}
	}
	
	public static void addLog (String sessionId, String log) {
		
		if (sessionIdLogMapper.containsKey(sessionId)) {
			if (sessionIdLogMapper.get(sessionId)!=null) {
				SimpleDateFormat sdf = new SimpleDateFormat("M/dd HH:mm");
				log = sdf.format(new Date()) + " " + log;
				sessionIdLogMapper.get(sessionId).add(log);
			} 
			else {
				List<String> logList = new ArrayList<String>();
				SimpleDateFormat sdf = new SimpleDateFormat("M/dd HH:mm");
				log = sdf.format(new Date()) + " " + log;
				logList.add(log);
				sessionIdLogMapper.put(sessionId, logList);
			}
		}
	}

	public static int getMaxSessionCount ()	{
		return maxSessionCount;
	}
}
