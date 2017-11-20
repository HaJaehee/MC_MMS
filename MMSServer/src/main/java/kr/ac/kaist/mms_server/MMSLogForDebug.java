package kr.ac.kaist.mms_server;
/* -------------------------------------------------------- */
/** 
File name : MMSLogForDebug.java
	It contains filtered logs by related MRN and session IDs.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-10-25
Version : 0.6.0

Rev. history : 2017-11-18
Version : 0.6.1
	Fixed bugs due to null pointer execptions.
	Replaced class name from MMSLogsForDebug to MMSLogForDebug. 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-20
Version : 0.6.1
	Arguments must be checked if it is null.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ch.qos.logback.classic.jul.JULHelper;


public class MMSLogForDebug {
	private static Map<String,List<String>> sessionIdLogMapper = new HashMap<String,List<String>>();
	private static Map<String,List<String>> mrnSessionIdMapper = new HashMap<String,List<String>>();
	private static Map<String,List<String>> sessionIdMrnMapper = new HashMap<String,List<String>>();
	private static int maxSessionCount = 100;
	private static MMSLogForDebug inst = null;
	
	private MMSLogForDebug () {
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
	}
	
	public static MMSLogForDebug getInstance() {
		if (inst==null) {
			inst = new MMSLogForDebug();
		}
		return inst;
	}
	public static String getLog (String mrn){
		if (mrn!=null&&mrnSessionIdMapper.get(mrn)!=null) {
			List<String> sessionIdList = mrnSessionIdMapper.get(mrn);
			StringBuilder logs = new StringBuilder();
			for (String sessionId : sessionIdList) {
				if (sessionIdLogMapper.get(sessionId)!=null) {
					List<String> logList = sessionIdLogMapper.get(sessionId);
					for (String log : logList) {
						logs.append(log+"<br/>");
					}
				} 
				else { // sessionIdLogMapper.get(sessionId)==null
					return "";
				}
			}
			return logs.toString();
		} 
		else { // mrnSessionIdMapper.get(mrn)==null
			if (mrnSessionIdMapper.containsKey(mrn)) {
				return "";
			}
			else {
				return null;
			}
		}
	}
	
	public static boolean containsSessionId (String sessionId){
		if (sessionIdLogMapper.containsKey(sessionId)&&sessionIdMrnMapper.containsKey(sessionId)) {
			return true;
		} 
		else {
			return false;
		}
	}
	
	public static void addMrn (String mrn) {
		if (mrn==null||mrnSessionIdMapper.containsKey(mrn)) {
			return;
		}
		List<String> sessionIdList = new ArrayList<String>();
		mrnSessionIdMapper.put(mrn, sessionIdList);
	}
	
	
	public static void removeMrn (String mrn){
		if (mrn!=null&&mrnSessionIdMapper.get(mrn)!=null) {
			for (String sessionId : mrnSessionIdMapper.get(mrn)) {
				if (sessionIdMrnMapper.get(sessionId)!=null) {
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
			mrnSessionIdMapper.remove(mrn);
		}
	}
	
	public static Set<String> getMrnSet () {
		return mrnSessionIdMapper.keySet();
	}
	
	public static void addSessionId (String mrn, String sessionId){
	
		if(mrn!=null&&sessionId!=null) {
			if (mrnSessionIdMapper.get(mrn)!=null) {
				List<String> sessionIdList = mrnSessionIdMapper.get(mrn);
				
				while (sessionIdList.size() > maxSessionCount) {
					String lruSession = mrnSessionIdMapper.get(mrn).get(0);
					sessionIdMrnMapper.get(lruSession).remove(mrn);
					if (sessionIdMrnMapper.get(lruSession).isEmpty()) {
						if (sessionIdLogMapper.get(lruSession)!=null) {
							sessionIdLogMapper.get(lruSession).clear();
							sessionIdLogMapper.remove(lruSession);
						}
						sessionIdMrnMapper.remove(lruSession);
					}
					sessionIdList.remove(0);
				}
				
				sessionIdList.add(sessionId);
				mrnSessionIdMapper.put(mrn, sessionIdList);
			} 
			else { //mrnSessionIdMapper.get(mrn)==null
				List<String> sessionIdList = new ArrayList<String>();
				sessionIdList.add(sessionId);
				mrnSessionIdMapper.put(mrn, sessionIdList);
			}
			if (sessionIdMrnMapper.get(sessionId)==null) {
				List<String> mrnList = new ArrayList<String>();
				mrnList.add(mrn);
				sessionIdMrnMapper.put(sessionId, mrnList);
			} 
			else { //sessionIdMrnMapper.get(sessionId)!=null
				sessionIdMrnMapper.get(sessionId).add(mrn);
			}
			if (sessionIdLogMapper.get(sessionId)==null)
			{
				List<String> logList = new ArrayList<String>();
				sessionIdLogMapper.put(sessionId, logList);
			}
		}
	}
	
	public static void addLog (String sessionId, String log) {
		if(sessionId!=null)	{
			SimpleDateFormat sdf = new SimpleDateFormat("M/dd HH:mm");
			log = sdf.format(new Date()) + " " + log;
			
			if (sessionIdLogMapper.get(sessionId)!=null) {
				sessionIdLogMapper.get(sessionId).add(log);
			}
			else { //sessionIdLogMapper.get(sessionId)==null
				List<String> logList = new ArrayList<String>();
				logList.add(log);
				sessionIdLogMapper.put(sessionId, logList);
			}
		}
	}

	public static boolean isItsLogListNull (String sessionId) {
		if (sessionId!=null&&sessionIdLogMapper.get(sessionId)==null) {
			return true;
		}
		else {
			return false;
		}
	}
	public static int getMaxSessionCount ()	{
		return maxSessionCount;
	}
}
