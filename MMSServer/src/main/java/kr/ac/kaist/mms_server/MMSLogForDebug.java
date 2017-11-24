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
	private Map<String,List<String>> sessionIdLogMapper = new HashMap<String,List<String>>();
	private Map<String,Set<String>> mrnSessionIdMapper = new HashMap<String,Set<String>>();
	private Map<String,Set<String>> sessionIdMrnMapper = new HashMap<String,Set<String>>();
	private int maxSessionCount = 50;
	
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
		addMrn("urn:mrn:smart:vessel:imo-no:mof:tmp520fors55");
	}
	
	public static MMSLogForDebug getInstance() { //double check synchronization.
		return LazyHolder.INSTANCE;
	}
	
	private static class LazyHolder {
		private static final MMSLogForDebug INSTANCE = new MMSLogForDebug();
	}
	
	public String getLog (String mrn){
		if (mrn!=null&&mrnSessionIdMapper.get(mrn)!=null) {
			StringBuilder logs = new StringBuilder();
			for (String sessionId : mrnSessionIdMapper.get(mrn)) {
				if (sessionIdLogMapper.get(sessionId)!=null) {
					for (String log : sessionIdLogMapper.get(sessionId)) {
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
	
	public boolean containsSessionId (String sessionId){
		if (sessionIdLogMapper.containsKey(sessionId)&&sessionIdMrnMapper.containsKey(sessionId)) {
			return true;
		} 
		else {
			return false;
		}
	}
	
	public void addMrn (String mrn) {
		if (mrn==null||mrnSessionIdMapper.containsKey(mrn)) {
			return;
		}
		Set<String> sessionIdList = new LinkedHashSet<String>();
		mrnSessionIdMapper.put(mrn, sessionIdList);
	}
	
	
	public void removeMrn (String mrn){
		if (mrnSessionIdMapper.get(mrn)!=null) {
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
		}
		mrnSessionIdMapper.remove(mrn);
	}
	
	public Set<String> getMrnSet () {
		return mrnSessionIdMapper.keySet();
	}
	
	public void addSessionId (String mrn, String sessionId){
	
		
		if(mrn!=null&&sessionId!=null) {
			
			if (sessionIdLogMapper==null){
				sessionIdLogMapper = new HashMap<String,List<String>>();
			}
			if (mrnSessionIdMapper==null){
				mrnSessionIdMapper = new HashMap<String,Set<String>>();
			}
			if (sessionIdMrnMapper==null){
				sessionIdMrnMapper = new HashMap<String,Set<String>>();
			}
			
			if (mrnSessionIdMapper.get(mrn)!=null) {
				while (mrnSessionIdMapper.get(mrn).size() > maxSessionCount) {
					Iterator<String> it = mrnSessionIdMapper.get(mrn).iterator();
					String lruSession = it.next();
					if (sessionIdMrnMapper.get(lruSession)!=null) {
						sessionIdMrnMapper.get(lruSession).remove(mrn);
						if (sessionIdMrnMapper.get(lruSession).isEmpty()) {
							if (sessionIdLogMapper.get(lruSession)!=null) {
								sessionIdLogMapper.get(lruSession).clear();
								sessionIdLogMapper.remove(lruSession);
							}
							sessionIdMrnMapper.remove(lruSession);
						}	
					}
					mrnSessionIdMapper.get(mrn).remove(lruSession);
				}
				
				mrnSessionIdMapper.get(mrn).add(sessionId);
				
				if (sessionIdMrnMapper.get(sessionId)==null) {
					Set<String> mrnList = new LinkedHashSet<String>();
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
	}
	
	public void addLog (String sessionId, String log) {

		if (sessionId!=null&&sessionIdLogMapper.get(sessionId)!=null)	{
			SimpleDateFormat sdf = new SimpleDateFormat("M/dd HH:mm");
			log = sdf.format(new Date()) + " " + log;
			
			sessionIdLogMapper.get(sessionId).add(log);
		}
	}

	public boolean isItsLogListNull (String sessionId) {
		if (sessionId!=null&&sessionIdLogMapper.get(sessionId)==null) {
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
