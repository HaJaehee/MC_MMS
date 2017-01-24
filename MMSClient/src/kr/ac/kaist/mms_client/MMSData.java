package kr.ac.kaist.mms_client;

public class MMSData {
	private int sequence;
	private String srcMRN;
	private String data;
	
	MMSData (int seq, String srcMRN, String data) {
		this.sequence = seq;
		this.srcMRN = srcMRN;
		this.data = data;
	}
	
	int getSequence() {
		return this.sequence;
	}
	
	String getSrcMRN() { 
		return this.srcMRN;
	}
	
	String getData() {
		return this.data;
	}
}
