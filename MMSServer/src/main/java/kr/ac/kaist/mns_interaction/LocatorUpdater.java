package kr.ac.kaist.mns_interaction;
/* -------------------------------------------------------- */
/** 
File name : LocatorUpdater.java
	It builds a locator update query.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-02-01
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-06-06
Version : 0.6.1
	Deprecated this class.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import kr.ac.kaist.mms_server.MMSConfiguration;

@Deprecated
public class LocatorUpdater {
	
	private static final Logger logger = LoggerFactory.getLogger(LocatorUpdater.class);
	private String sessionId = "";
	
	LocatorUpdater (String sessionId){
		this.sessionId = sessionId;
	}
	String buildUpdate(String MRN, String IP) {
		String msg = "Location-Update:" + IP + "," + MRN + ",0,1";

		logger.debug("SessionID="+this.sessionId+" "+msg+".");
		return msg;
	}
}
