package kr.ac.kaist.message_relaying.polling_auth;

import java.util.Timer;
import java.util.TimerTask;

/** 
File name : PollingSession.java
	This class is used for polling message authentication to be more faster.
	When if a polling message is come and the message was already authenticated before,
	This class makes the message not be checked by trusted third party such as MIR and 
	the message is considered as a authenticated message.
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-05-21
Version : 0.9.1

*/

@Deprecated
class PollingSession {
	private String srcMRN;
	private String hexSignedData;
	private Timer s_timer;
	private TimerTask s_task;
	
	PollingSession(String srcMRN, String hexSignedData) {
		// TODO Auto-generated constructor stub
		this.srcMRN = srcMRN;
		this.hexSignedData = hexSignedData;
		s_timer = null;
		s_task = null;
	}
	
	protected String getSrcMRN() {
		return srcMRN;
	}
	
	protected String getCertificate() {
		return hexSignedData;
	}
	
	protected void setTimer(Timer timer) {
		s_timer = timer;
	}
	
	protected void setTask(TimerTask task) {
		s_task = task;
	}
	
	protected Timer getTimer() {
		return s_timer;
	}
	
	protected TimerTask getTask() {
		return s_task;
	}
}
