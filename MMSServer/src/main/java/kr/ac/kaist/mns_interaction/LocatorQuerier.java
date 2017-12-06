package kr.ac.kaist.mns_interaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* -------------------------------------------------------- */
/** 
File name : LocatorQuerier.java
	It builds a locator query.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class LocatorQuerier {

	private static final Logger logger = LoggerFactory.getLogger(LocatorQuerier.class);
	String buildQuery(String dstMRN){
		
		String msg = "MRN-Request:" + dstMRN;
		
		return msg;
	}
}
