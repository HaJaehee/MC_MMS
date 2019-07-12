package kr.ac.kaist.message_relaying.polling_auth;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/** 
File name : PollingSessionManager.java
	This class is used for polling message authentication to be more faster.
	When if a polling message is come and the message was already authenticated before,
	This class makes the message not be checked by trusted third party such as MIR and 
	the message is considered as a authenticated message.
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-05-21
Version : 0.9.1

Rev. history : 2019-07-09
Version : 0.9.3
	Revised for coding rule conformity.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/

@Deprecated
public class PollingSessionManager {
	private static PollingSessionList sessionList = new PollingSessionList();
	public final long TIMEOUT = 30000;
	
	private PollingSessionManagerCode setTimerOn(PollingSession session) {
		Timer s_timer = new Timer();
		TimerTask s_task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				System.out.println("[PollingSessionManager] timeout and delete session");
				delete(session);
			}
		};
		s_timer.schedule(s_task, TIMEOUT);
		
		session.setTimer(s_timer);
		session.setTask(s_task);
		
		return PollingSessionManagerCode.OK;
	}
	
	public synchronized PollingSessionManagerCode contains (String srcMRN, String hexSignedData){
		if (sessionList.contains(srcMRN, hexSignedData)){
			return PollingSessionManagerCode.CONTAINED;
		}
		
		return PollingSessionManagerCode.FAIL;
	}
	
	public synchronized PollingSessionManagerCode add (String srcMRN, String hexSignedData){
//		System.out.println("[PollingSessionManager] Add this session");
		PollingSession session = new PollingSession(srcMRN, hexSignedData);
		sessionList.add(session);
		setTimerOn(session);
		
		return PollingSessionManagerCode.ADDED;
	}
	
	public synchronized PollingSessionManagerCode delete(PollingSession session) {
		sessionList.remove(session);
		
		return PollingSessionManagerCode.DELETED;
	}
	
	public synchronized PollingSessionManagerCode refresh(String srcMRN, String hexSignedData) {
//		System.out.println("[PollingSessionManager] Update this session");
		PollingSession session = sessionList.get(srcMRN, hexSignedData);
		
		if (session != null) {
			TimerTask task = session.getTask();
			if (task != null) {
				task.cancel();
			}
			setTimerOn(session);
		}
		
		return PollingSessionManagerCode.REFLESHED;
	}
	
	public synchronized PollingSessionManagerCode refresh(PollingSession session) {
		TimerTask task = session.getTask();
		if (task != null) {
			task.cancel();
		}
		setTimerOn(session);
		
		return PollingSessionManagerCode.REFLESHED;
	}
}
