package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : SessionIdAndThr.java
	This is used in SessionManager.
	This is used for handling input messages by FIFO scheduling.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-07-03
Version : 0.7.2

Rev. history : 2018-07-18
Version : 0.7.2
	Added handling input messages by reordering policy.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class SessionIdAndThr {
	private String sessionId = "";
	private Thread sessionBlocker = null;
	private boolean isWaitingRes = false;
	private long seqNum = -1;
	private long preSeqNum = -1;
	private long nextSeqNum = -1;
	private int waitingCount = -1;
	private boolean exceptionFlag = false;
	
	public SessionIdAndThr (String aSessionId, Thread aSessionBlocker, long aSeqNum) {
		sessionId = aSessionId;
		sessionBlocker = aSessionBlocker;
		seqNum = aSeqNum;
		preSeqNum = aSeqNum-1;
		nextSeqNum = aSeqNum+1;
		waitingCount = 0;
		isWaitingRes = false;
		exceptionFlag = false;
	}

	public String getSessionId() {
		return sessionId;
	}

	public Thread getSessionBlocker() {
		return sessionBlocker;
	}

	public int getWaitingCount() {
		return waitingCount;
	}

	public void setWaitingCount(int waitingCount) {
		this.waitingCount = waitingCount;
	}
	
	public void incWaitingCount() {
		this.waitingCount++;
	}

	public void setWaitingRes(boolean isWaitingRes) {
		this.isWaitingRes = isWaitingRes;
		return;
	}
	
	public boolean isWaitingRes() {
		return isWaitingRes;
	}

	public double getSeqNum() {
		return seqNum;
	}

	public double getPreSeqNum() {
		return preSeqNum;
	}

	public double getNextSeqNum() {
		return nextSeqNum;
	}

	public boolean isExceptionOccured() {
		return exceptionFlag;
	}

	public void setExceptionFlag(boolean exceptionFlag) {
		this.exceptionFlag = exceptionFlag;
	}
	
}
