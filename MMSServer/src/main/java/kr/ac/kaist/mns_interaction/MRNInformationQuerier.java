package kr.ac.kaist.mns_interaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* -------------------------------------------------------- */
/** 
File name : MRNInformationQuerior.java
	It builds a locator query.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-06-06
Version : 0.7.1
	Changed class name from LocatorQuerior to MRNInformationQuerior
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class MRNInformationQuerier {

	private static final Logger logger = LoggerFactory.getLogger(MRNInformationQuerier.class);
	
	String buildQuery(String castType, String srcMRN, float geoLat, float geoLong, float geoRadius) {
		return "{\""+castType+"\":"+
				"{\"srcMRN\":\""+srcMRN+"\","+
				"\"lat\":\""+geoLat+"\","+
				"\"long\":\""+geoLong+"\","+
				"\"radius\":\""+geoRadius+"\"}}";
	}
	
	//TODO
	String buildQuery(String castType, String srcMRN, float geoLat[], float[] geoLong) {
		String ret = "";
		
		return ret;
	}
	String buildQuery(String castType, String srcMRN, String dstMRN, String srcIP){
		return "{\""+castType+"\":"+
				"{\"srcMRN\":\""+srcMRN+"\","+
				"\"dstMRN\":\""+dstMRN+"\","+
				"\"IPAddr\":\""+srcIP+"\"}}";
	}
}
