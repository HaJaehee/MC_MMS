package kr.ac.kaist.seamless_roaming;


/* -------------------------------------------------------- */
/** 
File name : SCMassageHandling.java
	It puts messages to MMSQueue.
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
	Replaced from random int sessionId to String sessionId as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import kr.ac.kaist.message_queue.MessageQueueManager;
import kr.ac.kaist.message_queue.v2.PriorityMessageQueueManager;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SCMessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(SCMessageHandler.class);
	private String sessionId = "";
	
	SCMessageHandler (String sessionId) {
		this.sessionId = sessionId;
	}
	
	byte[] enqueueSCMessage(MRH_MessageInputChannel.ChannelBean bean){
		MessageQueueManager mqm = null;
		
		if (bean.getParser().getPriority() != PriorityMessageQueueManager.DEFAULT_PRIORITY) {
			mqm = new PriorityMessageQueueManager(this.sessionId, bean.getParser().getPriority());
		}
		else {
			mqm = new MessageQueueManager(this.sessionId);
		}
		
		return mqm.enqueueMessage(bean);
	}
}
