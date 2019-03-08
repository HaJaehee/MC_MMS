package kr.ac.kaist.message_queue;
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

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.\
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import io.netty.channel.ChannelHandlerContext;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
public class MessageQueueManager {
	
	private String SESSION_ID = "";
	
	public MessageQueueManager(String sessionId) {
		
		this.SESSION_ID = sessionId;
	}
	
	public void dequeueMessage (MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN, String svcMRN, String pollingMethod) {
		MessageQueueDequeuer mqd = new MessageQueueDequeuer(this.SESSION_ID);
		mqd.dequeueMessage(outputChannel, ctx, srcMRN, svcMRN, pollingMethod);
	}
	
	public void enqueueMessage (String srcMRN, String dstMRN, String message) {
		MessageQueueEnqueuer mqe = new MessageQueueEnqueuer(this.SESSION_ID);
		mqe.enqueueMessage(srcMRN, dstMRN, message);
	}
}
