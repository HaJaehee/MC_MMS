package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : MMSData.java
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.2.00
*/
/* -------------------------------------------------------- */

public class MMSData {
	private int sequence = 0;
	private String srcMRN = null;
	private String data = null;
	
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
