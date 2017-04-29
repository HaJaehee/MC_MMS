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
*/
/* -------------------------------------------------------- */

import java.io.UnsupportedEncodingException;

import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.message_queue.MMSQueue;
import kr.ac.kaist.message_queue.MessageQueueManager;
import kr.ac.kaist.mms_server.MMSConfiguration;

class SCMessageHandler {
	private String TAG = "[SCMessageHandler:";
	private int SESSION_ID = 0;
	
	SCMessageHandler (int sessionId) {
		this.SESSION_ID = sessionId;
		this.TAG += SESSION_ID + "] ";
	}
	
	@Deprecated
	void putSCMessage(String dstMRN, FullHttpRequest req){
		try {
			MMSQueue.putMessage(dstMRN, req);
		} catch (UnsupportedEncodingException e) {
			if(MMSConfiguration.CONSOLE_LOGGING){
				System.out.print(TAG);
				e.printStackTrace();
			}
		}
	}
	
	void enqueueSCMessage(String srcMRN, String dstMRN, String message){
		MessageQueueManager mqm = new MessageQueueManager(this.SESSION_ID);
		mqm.enqueueMessage(srcMRN, dstMRN, message);
	}
}
