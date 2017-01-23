package kr.ac.kaist.mms_client;

public class MMSData {
	private int sequence;
	private String srcMRN;
	private String data;
	
	public MMSData (int seq, String srcMRN, String data) {
		this.sequence = seq;
		this.srcMRN = srcMRN;
		this.data = data;
	}
	
	public int getSequence() {
		return this.sequence;
	}
	
	public String getSrcMRN() { 
		return this.srcMRN;
	}
	
	public String getData() {
		return this.data;
	}
}
