package kr.ac.kaist.mns_interaction;

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
*/
/* -------------------------------------------------------- */

public class MNSInteractionHandler {
	private String TAG = "[MNSInteractionHandler:";
	private int SESSION_ID = 0;
	private LocatorUpdater locatorUpdater = null;
	private LocatorQuerier locatorQuerier = null;
	private MIH_MessageOutputChannel messageOutput = null;
	
	public MNSInteractionHandler(int sessionId) {
		this.SESSION_ID = sessionId;
		this.TAG += SESSION_ID + "] ";
		
		initializeModule();
	}
	
	private void initializeModule(){
		locatorQuerier = new LocatorQuerier();
		locatorUpdater = new LocatorUpdater(this.SESSION_ID);
		messageOutput = new MIH_MessageOutputChannel(this.SESSION_ID);
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
