package kr.ac.kaist.message_queue;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;

/* -------------------------------------------------------- */
/** 
File name : MessageQueueManager.java
	Manager of the Message Queue.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-19
Version : 0.5.0 
*/
/* -------------------------------------------------------- */

public class MessageQueueManager {
	public void dequeueMessage (MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN, String svcMRN ) {
		MessageQueueDequeuer mqd = new MessageQueueDequeuer();
		mqd.dequeueMessage(outputChannel, ctx, srcMRN, svcMRN);
	}
	
	public void enqueueMessage (String srcMRN, String dstMRN, String message) {
		MessageQueueEnqueuer mqe = new MessageQueueEnqueuer();
		mqe.enqueueMessage(srcMRN, dstMRN, message);
	}
}
