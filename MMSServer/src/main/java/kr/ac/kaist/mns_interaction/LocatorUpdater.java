package kr.ac.kaist.mns_interaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
*/
/* -------------------------------------------------------- */

import kr.ac.kaist.mms_server.MMSConfiguration;

public class LocatorUpdater {
	
	private static final Logger logger = LoggerFactory.getLogger(LocatorUpdater.class);
	private int SESSION_ID = 0;
	
	LocatorUpdater (int sessionId){
		this.SESSION_ID = sessionId;
	}
	String buildUpdate(String MRN, String IP, int port, int model) {
		String msg = "Location-Update:" + IP + "," + MRN + "," + Integer.toString(port) + "," + Integer.toString(model);

		logger.debug("SessionID="+this.SESSION_ID+" "+msg);
		return msg;
	}
}
