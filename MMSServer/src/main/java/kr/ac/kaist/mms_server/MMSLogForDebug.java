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

Rev. history : 2017-11-18
Version : 0.6.1
	Replaced this class from static class to singleton class.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-22
Version : 0.6.1
	Resolved critical problem caused by duplicated items in the list of mrnSessionIdMapper and sessionIdMrnMapper.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
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
	private HashMap<String,ArrayList<String>> sessionIdLogMapper = null;
	private HashMap<String,LinkedHashSet<String>> mrnSessionIdMapper = null;
	private HashMap<String,LinkedHashSet<String>> sessionIdMrnMapper = null;
	private int maxSessionCount = 0;
	
	private MMSLogForDebug () {
		sessionIdLogMapper = new HashMap<String,ArrayList<String>>();
		mrnSessionIdMapper = new HashMap<String,LinkedHashSet<String>>();
		sessionIdMrnMapper = new HashMap<String,LinkedHashSet<String>>();
		maxSessionCount = 50;
		
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
	}
	
	public static MMSLogForDebug getInstance() { //double check synchronization.
		return LazyHolder.INSTANCE;
	}
	
	private static class LazyHolder {
		private static final MMSLogForDebug INSTANCE = new MMSLogForDebug();
	}
	
	public String getLog (String mrn){
		if (mrn!=null&&mrnSessionIdMapper!=null&&sessionIdLogMapper!=null) {
			LinkedHashSet<String> sessionIdList = mrnSessionIdMapper.get(mrn);
			if (sessionIdList!=null) {
				StringBuilder logs = new StringBuilder();
				for (String sessionId : sessionIdList) {
					ArrayList<String> logList = sessionIdLogMapper.get(sessionId);
					if (logList!=null) {
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
		else {
			return null;
		}
	}
	
	public boolean containsSessionId (String sessionId){
		if (sessionId!=null&&
				sessionIdLogMapper!=null&&sessionIdMrnMapper!=null&&
				sessionIdLogMapper.containsKey(sessionId)&&sessionIdMrnMapper.containsKey(sessionId)) {
			return true;
		} 
		else {
			return false;
		}
	}
	
	public void addMrn (String mrn) {
		if (mrnSessionIdMapper==null) {
			mrnSessionIdMapper = new HashMap<String,LinkedHashSet<String>>();
		}
			
		if (mrn==null||mrnSessionIdMapper.containsKey(mrn)) {
			return;
		}
		
		LinkedHashSet<String> sessionIdList = new LinkedHashSet<String>();
		mrnSessionIdMapper.put(mrn, sessionIdList);
	}
	
	
	public void removeMrn (String mrn){
		if (mrnSessionIdMapper!=null) {
			Set<String> sessionIdList = mrnSessionIdMapper.get(mrn);
			if (sessionIdList!=null) {
				for (String sessionId : sessionIdList) {
					if (sessionIdMrnMapper!=null) {
						Set<String> mrnList = sessionIdMrnMapper.get(sessionId);
						if (mrnList!=null) {
							mrnList.remove(mrn);
							if (mrnList.isEmpty()) {
								if (sessionIdLogMapper!=null) {
									List<String> logList = sessionIdLogMapper.get(sessionId);
									if (logList!=null) {
										logList.clear();
										logList = null;
										sessionIdLogMapper.remove(sessionId);
									}
								}
								else { //sessionIdLogMapper==null
									sessionIdLogMapper = new HashMap<String,ArrayList<String>>();
								}
								mrnList = null;
								sessionIdMrnMapper.remove(sessionId);
							}
						}
					}
					else { //sessionIdMrnMapper==null
						sessionIdMrnMapper = new HashMap<String,LinkedHashSet<String>>();
					}
				}
				sessionIdList.clear();
				sessionIdList = null;
			}
			mrnSessionIdMapper.remove(mrn);
		}
		else { // mrnSessionIdMapper==null
			mrnSessionIdMapper = new HashMap<String,LinkedHashSet<String>>();
		}
	}
	
	public Set<String> getMrnSet () {
		if (mrnSessionIdMapper==null){
			mrnSessionIdMapper = new HashMap<String,LinkedHashSet<String>>();
		}
		return mrnSessionIdMapper.keySet();
	}
	
	public void addSessionId (String mrn, String sessionId){
		if(mrn!=null&&sessionId!=null) {
			
			if (sessionIdLogMapper==null){
				sessionIdLogMapper = new HashMap<String,ArrayList<String>>();
			}
			if (mrnSessionIdMapper==null){
				mrnSessionIdMapper = new HashMap<String,LinkedHashSet<String>>();
			}
			if (sessionIdMrnMapper==null){
				sessionIdMrnMapper = new HashMap<String,LinkedHashSet<String>>();
			}
			
			LinkedHashSet<String> sessionIdList = mrnSessionIdMapper.get(mrn);
			if (sessionIdList!=null) {
				while (sessionIdList.size() > maxSessionCount) {
					String lruSession = sessionIdList.iterator().next();
					LinkedHashSet<String> mrnList = sessionIdMrnMapper.get(lruSession);
					if (mrnList!=null) {
						mrnList.remove(mrn);
						if (mrnList.isEmpty()) {
							List<String> logList = sessionIdLogMapper.get(lruSession);
							if (logList!=null) {
								logList.clear();
								logList = null;
								sessionIdLogMapper.remove(lruSession);
							}
							mrnList = null;
							sessionIdMrnMapper.remove(lruSession);
						}
					}
					sessionIdList.remove(lruSession);
				}
				sessionIdList.add(sessionId);
				mrnSessionIdMapper.put(mrn, sessionIdList);
				
				LinkedHashSet<String> mrnList = sessionIdMrnMapper.get(sessionId);
				if (mrnList==null) {
					mrnList = new LinkedHashSet<String>();
					mrnList.add(mrn);
					sessionIdMrnMapper.put(sessionId, mrnList);
				} 
				else { //sessionIdMrnMapper.get(sessionId)!=null
					mrnList.add(mrn);
					sessionIdMrnMapper.put(sessionId, mrnList);
				}
				ArrayList<String> logList = sessionIdLogMapper.get(sessionId);
				if (logList==null)
				{
					logList = new ArrayList<String>();
					sessionIdLogMapper.put(sessionId, logList);
				}
			} 
		}
	}
	
	public void addLog (String sessionId, String log) {

		if (sessionId!=null&&sessionIdLogMapper!=null&&sessionIdLogMapper.get(sessionId)!=null)	{
			SimpleDateFormat sdf = new SimpleDateFormat("M/dd HH:mm");
			log = sdf.format(new Date()) + " " + log;
			
			sessionIdLogMapper.get(sessionId).add(log);
		}
	}

	public boolean isItsLogListNull (String sessionId) {
		if (sessionId!=null&&sessionIdLogMapper!=null&&sessionIdLogMapper.get(sessionId)==null) {
			return true;
		}
		else {
			return false;
		}
	}
	public int getMaxSessionCount ()	{
		return maxSessionCount;
	}
}
