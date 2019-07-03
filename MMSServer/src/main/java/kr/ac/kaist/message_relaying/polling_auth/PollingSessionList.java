package kr.ac.kaist.message_relaying.polling_auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kr.ac.kaist.mms_server.MMSLog;

/** 
File name : PollingSessionList.java
	This class is used for polling message authentication to be more faster.
	When if a polling message is come and the message was already authenticated before,
	This class makes the message not be checked by trusted third party such as MIR and 
	the message is considered as a authenticated message.
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-05-21
Version : 0.9.1

*/

@Deprecated
public class PollingSessionList extends ArrayList<PollingSession>{
	private final MMSLog mmsLog = MMSLog.getInstance();
	private final int END_INDEX = 128;
	
	@Override
	public PollingSession get(int index) {
		// TODO Auto-generated method stub
		return super.get(index);
	}
	
	public PollingSession get(String srcMRN, String hexSignedData) {
		Iterator<PollingSession> itr = this.iterator();
		
		while(itr.hasNext()){
			PollingSession session = (PollingSession) itr.next();
//			mmsLog.addBriefLogForStatus("[PollingSessionList] my srcMRN: " + session.getSrcMRN() );
//			mmsLog.addBriefLogForStatus("[PollingSessionList] in srcMRN: " + srcMRN );
//			mmsLog.addBriefLogForStatus("[PollingSessionList] my certificate: " + session.getCertificate() );
//			mmsLog.addBriefLogForStatus("[PollingSessionList] in certificate: " + hexSignedData );
			if (session.getSrcMRN().equals(srcMRN) && session.getCertificate().contains(hexSignedData.substring(0, END_INDEX))) {
				return session;
			}
		}
		
		return null;
	}
	
	public boolean contains(String srcMRN, String hexSignedData) {
		PollingSession session = this.get(srcMRN, hexSignedData);
		
		if (session == null) {
//			mmsLog.addBriefLogForStatus("[PollingSessionList] Failed to get the session");
			return false;
		} else {
//			mmsLog.addBriefLogForStatus("[PollingSessionList] Succeeded to get the session");
			return true;
		}
	}
}
