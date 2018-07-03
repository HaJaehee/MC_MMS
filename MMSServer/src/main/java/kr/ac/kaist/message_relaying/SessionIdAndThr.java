package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : SessionIdAndThr.java
	This is used in SessionManager.
	This is used for handling input messages by FIFO scheduling.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-07-03
Version : 0.7.2
*/
/* -------------------------------------------------------- */

public class SessionIdAndThr {
	private String sessionId = "";
	private Thread sessionBlocker = null;
	
	public SessionIdAndThr (String aSessionId, Thread aSessionBlocker) {
		sessionId = aSessionId;
		sessionBlocker = aSessionBlocker;
	}

	public String getSessionId() {
		return sessionId;
	}

	public Thread getSessionBlocker() {
		return sessionBlocker;
	}
	
}
