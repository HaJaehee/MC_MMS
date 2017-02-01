package kr.ac.kaist.mns_interaction;

import kr.ac.kaist.mms_server.MMSConfiguration;

public class LocatorUpdater {
	
	String buildUpdate(String MRN, String IP, int port, int model) {
		String msg = null;
		msg = "Location-Update:" + IP + "," + MRN + "," + Integer.toString(port) + "," + Integer.toString(model);
		if(MMSConfiguration.logging)System.out.println(msg);
		return msg;
	}
}
