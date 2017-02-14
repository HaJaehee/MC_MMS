package kr.ac.kaist.message_casting;

/* -------------------------------------------------------- */
/** 
File name : MessageCastingHandler.java
	If dstMRN in header field means multiple destinations such as geocast or multicast, 
	it relays messages to multiple destinations.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01
Rev. history : 2017-02-01
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import kr.ac.kaist.mns_interaction.MNSInteractionHandler;

public class MessageCastingHandler {
	
	private MNSInteractionHandler mih = null;
	
	public MessageCastingHandler() {
		initializeModule();
	}
	
	private void initializeModule() {
		mih = new MNSInteractionHandler();
	}
	
	public String requestDstInfo(String dstMRN){
		String dstInfo = mih.requestDstInfo(dstMRN);
		
		return dstInfo;
	}
	
	public String registerClientInfo (String srcMRN, String srcIP, int srcPort, int srcModel){
		return mih.registerClientInfo (srcMRN, srcIP, srcPort, srcModel);
	}
}
