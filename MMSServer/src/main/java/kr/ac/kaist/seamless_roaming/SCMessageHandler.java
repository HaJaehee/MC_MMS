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
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import kr.ac.kaist.message_queue.MessageQueueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SCMessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(SCMessageHandler.class);
	private String SESSION_ID = "";
	
	SCMessageHandler (String sessionId) {
		this.SESSION_ID = sessionId;
	}
	
	void enqueueSCMessage(String srcMRN, String dstMRN, String message){
		MessageQueueManager mqm = new MessageQueueManager(this.SESSION_ID);
		mqm.enqueueMessage(srcMRN, dstMRN, message);
	}
}
