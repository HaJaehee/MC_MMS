package kr.ac.kaist.seamless_roaming;

/* -------------------------------------------------------- */
/** 
File name : SeamlessRoamingHandler.java
	It takes polling messages and forwards messages if there are any messages in MMSQueue.
	It forwards locator of MSC to MIM.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.2.00
*/
/* -------------------------------------------------------- */

import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;

public class SeamlessRoamingHandler {
	private static final String TAG = "SeamlessRoamingHandler";
	
	private PollingMessageHandling pmh = null;
	private SCMessageHandling scmh = null;
	private MNSInteractionHandler mih = null;
	
	public SeamlessRoamingHandler() {
		initializeModule();
		initializeSubModule();
	}
	
	private void initializeModule() {
		mih = new MNSInteractionHandler();
	}
	
	private void initializeSubModule() {
		pmh = new PollingMessageHandling();
		scmh = new SCMessageHandling();
	}
	
//	poll SC message in queue
	public byte[] processPollingMessage(String srcMRN, String srcIP, int srcPort, int srcModel) {
		byte[] message;
		
		pmh.updateClientInfo(mih, srcMRN, srcIP, srcPort, srcModel);
		message = pmh.getSCMessage(srcMRN);
	
		return message;
	}
	
	
//	save SC message into queue
	public void putSCMessage(String dstMRN, FullHttpRequest req) {
		scmh.putSCMessage(dstMRN, req);
	}
}
