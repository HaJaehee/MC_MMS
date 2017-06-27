package kr.ac.kaist.seamless_roaming;

/* -------------------------------------------------------- */
/** 
File name : PollingMessageHandling.java
	It interacts with MMSQueue and polls messages from MMSQueue.
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
import kr.ac.kaist.message_queue.MMSQueue;
import kr.ac.kaist.message_queue.MessageQueueManager;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;


class PollingMessageHandler {
	private String TAG = "[PollingMessageHandler:";
	private int SESSION_ID = 0;
	private MessageQueueManager mqm = null;
	
	public PollingMessageHandler(int sessionId) {
		this.SESSION_ID = sessionId;
		this.TAG += SESSION_ID + "] ";
		
		initializeModule();
	}
	
	private void initializeModule() {
		mqm = new MessageQueueManager(this.SESSION_ID);
	}
	
	void updateClientInfo(MNSInteractionHandler mih, String srcMRN, String srcIP, int srcPort, int srcModel) {
		mih.updateClientInfo(srcMRN, srcIP, srcPort, srcModel);
	}
	
	@Deprecated
	byte[] getSCMessage(String sourceMRN) {
		byte[] message = null;
		
		try{
			message = MMSQueue.getMessage(sourceMRN);
		} catch (Exception e) {
			message = "EMPTY".getBytes();
		}
		
		return message;
	}
	
	void dequeueSCMessage(MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN, String svcMRN){
		mqm.dequeueMessage(outputChannel, ctx, srcMRN, svcMRN);
	}
	
}
