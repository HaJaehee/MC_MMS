package kr.ac.kaist.seamless_roaming;

/* -------------------------------------------------------- */
/** 
File name : SeamlessRoamingHandler.java
	It takes polling messages and forwards messages if there are any messages in MMSQueue.
	It forwards locator of MSC to MIM.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;

public class SeamlessRoamingHandler {
	private String TAG = "[SeamlessRoamingHandler:";
	private int SESSION_ID = 0;
	
	private PollingMessageHandler pmh = null;
	private SCMessageHandler scmh = null;
	private MNSInteractionHandler mih = null;

	
	public SeamlessRoamingHandler(int sessionId) {
		this.SESSION_ID = sessionId;
		this.TAG += SESSION_ID + "] ";
		
		initializeModule();
		initializeSubModule();
	}
	
	private void initializeModule() {
		mih = new MNSInteractionHandler(this.SESSION_ID);

	}
	
	private void initializeSubModule() {
		pmh = new PollingMessageHandler(this.SESSION_ID);
		scmh = new SCMessageHandler(this.SESSION_ID);
	}
	
//	poll SC message in queue
	@Deprecated
	public byte[] processPollingMessage(String srcMRN, String srcIP, int srcPort, int srcModel) {
		byte[] message;
		
		pmh.updateClientInfo(mih, srcMRN, srcIP, srcPort, srcModel);
		message = pmh.getSCMessage(srcMRN);
	
		return message;
	}
	
//	poll SC message in queue
	public void processPollingMessage(MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN, String srcIP, int srcPort, int srcModel, String svcMRN) {
		
		pmh.updateClientInfo(mih, srcMRN, srcIP, srcPort, srcModel);
		pmh.dequeueSCMessage(outputChannel, ctx, srcMRN, svcMRN);
		
	}
	
	
//	save SC message into queue
	@Deprecated
	public void putSCMessage(String dstMRN, FullHttpRequest req) {
		scmh.putSCMessage(dstMRN, req);
	}
	
//	save SC message into queue
	public void putSCMessage(String srcMRN, String dstMRN, String message) {
		scmh.enqueueSCMessage(srcMRN, dstMRN, message);
	}
}
