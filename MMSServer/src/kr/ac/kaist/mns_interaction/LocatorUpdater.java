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
*/
/* -------------------------------------------------------- */

import kr.ac.kaist.mms_server.MMSConfiguration;

public class LocatorUpdater {
	private static final String TAG = "[LocatorUpdater] ";
	String buildUpdate(String MRN, String IP, int port, int model) {
		String msg = "Location-Update:" + IP + "," + MRN + "," + Integer.toString(port) + "," + Integer.toString(model);
		if(MMSConfiguration.LOGGING)System.out.println(TAG+msg);
		return msg;
	}
}
