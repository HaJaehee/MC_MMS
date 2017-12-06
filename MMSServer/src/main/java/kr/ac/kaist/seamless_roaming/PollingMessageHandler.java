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

import io.netty.channel.ChannelHandlerContext;
import kr.ac.kaist.message_queue.MessageQueueManager;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PollingMessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(PollingMessageHandler.class);
	private String SESSION_ID = "";
	private MessageQueueManager mqm = null;
	
	public PollingMessageHandler(String sessionId) {
		this.SESSION_ID = sessionId;
		
		initializeModule();
	}
	
	private void initializeModule() {
		mqm = new MessageQueueManager(this.SESSION_ID);
	}
	
	void updateClientInfo(MNSInteractionHandler mih, String srcMRN, String srcIP, int srcPort, int srcModel) {
		mih.updateClientInfo(srcMRN, srcIP, srcPort, srcModel);
	}
	
	void dequeueSCMessage(MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN, String svcMRN){
		mqm.dequeueMessage(outputChannel, ctx, srcMRN, svcMRN);
	}
	
}
