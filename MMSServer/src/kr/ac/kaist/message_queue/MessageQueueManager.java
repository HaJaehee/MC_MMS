package kr.ac.kaist.message_queue;

import io.netty.channel.ChannelHandlerContext;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;

/* -------------------------------------------------------- */
/** 
File name : MessageQueueManager.java
	Manager of the Message Queue.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-19
Version : 0.5.0 

Rev. history : 2017-04-29
Version : 0.5.3
	Added system log features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class MessageQueueManager {
	
	private String TAG = "[MessageQueueManager:";
	private int SESSION_ID = 0;
	
	public MessageQueueManager(int sessionId) {
		// TODO Auto-generated constructor stub
		this.SESSION_ID = sessionId;
		this.TAG += SESSION_ID + "] ";
	}
	
	public void dequeueMessage (MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN, String svcMRN ) {
		MessageQueueDequeuer mqd = new MessageQueueDequeuer(this.SESSION_ID);
		mqd.dequeueMessage(outputChannel, ctx, srcMRN, svcMRN);
	}
	
	public void enqueueMessage (String srcMRN, String dstMRN, String message) {
		MessageQueueEnqueuer mqe = new MessageQueueEnqueuer(this.SESSION_ID);
		mqe.enqueueMessage(srcMRN, dstMRN, message);
	}
}
