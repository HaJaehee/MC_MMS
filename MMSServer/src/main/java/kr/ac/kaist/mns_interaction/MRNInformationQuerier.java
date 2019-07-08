package kr.ac.kaist.mns_interaction;
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

Rev. history : 2018-07-27
Version : 0.7.2
	Added geocasting features which cast message to circle or polygon area.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MRNInformationQuerier {

	private static final Logger logger = LoggerFactory.getLogger(MRNInformationQuerier.class);
	
	String buildQuery(String castType, String srcMRN, String dstMRN, float geoLat, float geoLong, float geoRadius) {
		return "{\""+castType+"\":"+
				"{\"srcMRN\":\""+srcMRN+"\","+
				"\"dstMRN\":\""+dstMRN+"\","+
				"\"lat\":\""+geoLat+"\","+
				"\"long\":\""+geoLong+"\","+
				"\"radius\":\""+geoRadius+"\"}}";
	}
	
	//TODO
	String buildQuery(String castType, String srcMRN, String dstMRN, float geoLat[], float[] geoLong) {
		
		StringBuffer buf = new StringBuffer();
		StringBuffer longituteBuf = new StringBuffer();
		buf.append("{\""+castType+"\":" +
				"{\"srcMRN\":\""+srcMRN+"\","+
				"\"dstMRN\":\""+dstMRN+"\","+
				"\"lat\":[\"");
		longituteBuf.append("\"long\":[\"");
		int i = 0 ;
		for (i = 0 ; i < geoLat.length-1 ; i++) {
			buf.append(geoLat[i]+"\",\"");
			longituteBuf.append(geoLong[i]+"\",\"");
		}
		buf.append(geoLat[i]+"\"],");
		longituteBuf.append(geoLong[i]+"\"]}}"); 
		//System.out.println(buf.toString() + longituteBuf.toString());
		return buf.toString() + longituteBuf.toString();
	}
	String buildQuery(String castType, String srcMRN, String dstMRN, String srcIP){
		return "{\""+castType+"\":"+
				"{\"srcMRN\":\""+srcMRN+"\","+
				"\"dstMRN\":\""+dstMRN+"\","+
				"\"IPAddr\":\""+srcIP+"\"}}";
	}
}
