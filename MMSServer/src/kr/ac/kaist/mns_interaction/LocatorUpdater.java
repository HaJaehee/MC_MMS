package kr.ac.kaist.mns_interaction;

public class LocatorUpdater {
	
	public String buildUpdate(String dstMRN, String locator) {
		String msg = null;
//		msg = "Location-Update:" + iPAddress + "," + msg.content().toString(CharsetUtil.UTF_8).substring(16);
		msg = "Location-Update:" + dstMRN + "," + locator;
		
		return msg;
	}
}
