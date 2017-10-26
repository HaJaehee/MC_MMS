package kr.ac.kaist.mns_interaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* -------------------------------------------------------- */
/** 
File name : MNSInteractionHandler.java
	It interacts with Maritime Naming System (MNS) which registers MRN, locator, and so on, and replies to a query with MRN.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-02-01
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class MNSInteractionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MNSInteractionHandler.class);
	private String SESSION_ID = "";
	private LocatorUpdater locatorUpdater = null;
	private LocatorQuerier locatorQuerier = null;
	private MIH_MessageOutputChannel messageOutput = null;
	
	public MNSInteractionHandler(String sessionId) {
		this.SESSION_ID = sessionId;
		
		initializeModule();
	}
	
	private void initializeModule(){
		locatorQuerier = new LocatorQuerier();
		locatorUpdater = new LocatorUpdater(this.SESSION_ID);
		messageOutput = new MIH_MessageOutputChannel(this.SESSION_ID);
	}
	
	public String requestIPtoMRN(String ipAddress){
		String msg = "IP-Request:" + ipAddress;
		String mrn = messageOutput.sendToMNS(msg);
		
		return mrn;
	}
	
	public String requestDstInfo(String dstMRN) {
		String msg = locatorQuerier.buildQuery(dstMRN);
		String dstInfo;
		dstInfo = messageOutput.sendToMNS(msg);

		return dstInfo;
	}
	
	public String updateClientInfo(String srcMRN, String srcIP, int srcPort, int srcModel){
		String msg = locatorUpdater.buildUpdate(srcMRN, srcIP, srcPort, srcModel);
		return messageOutput.sendToMNS(msg);
	}

	public String registerClientInfo (String srcMRN, String srcIP, int srcPort, int srcModel){
		return updateClientInfo(srcMRN, srcIP, srcPort, srcModel);
	}
}
