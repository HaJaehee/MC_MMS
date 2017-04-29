package kr.ac.kaist.mns_interaction;

/* -------------------------------------------------------- */
/** 
File name : LocatorQuerier.java
	It builds a locator query.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01
*/
/* -------------------------------------------------------- */

public class LocatorQuerier {
	private String TAG = "[LocatorQuerier] ";
	
	String buildQuery(String dstMRN){
		
		String msg = "MRN-Request:" + dstMRN;
		
		return msg;
	}
}
