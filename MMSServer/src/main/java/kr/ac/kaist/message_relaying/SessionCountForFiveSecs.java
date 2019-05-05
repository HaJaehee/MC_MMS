package kr.ac.kaist.message_relaying;


// TODO: Jaehee will implement this.
public class SessionCountForFiveSecs {
	private long currentTimeInMills = 0;
	private long sessionCount = 0;
	private long pollingSessionCount = 0;
	
	public long getSessionCount() {
		return sessionCount;
	}
	public void incSessionCount() {
		this.sessionCount++;
	}
	public void incPollingSessionCount() {
		this.pollingSessionCount++;
	}
	public long getPollingSessionCount() {
		return this.pollingSessionCount;
	}
}
