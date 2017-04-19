package kr.ac.kaist.seamless_roaming;

/* -------------------------------------------------------- */
/** 
File name : SCMassageHandling.java
	It puts messages to MMSQueue.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01
*/
/* -------------------------------------------------------- */

import java.io.UnsupportedEncodingException;

import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.message_queue.MMSQueue;
import kr.ac.kaist.message_queue.MessageQueueManager;
import kr.ac.kaist.mms_server.MMSConfiguration;

public class SCMessageHandling {

	void putSCMessage(String dstMRN, FullHttpRequest req){
		try {
			MMSQueue.putMessage(dstMRN, req);
		} catch (UnsupportedEncodingException e) {
			if(MMSConfiguration.LOGGING)e.printStackTrace();
		}
	}
	
	void putSCMessage(String srcMRN, String dstMRN, String message){
		MessageQueueManager mqm = new MessageQueueManager();
		mqm.enqueueMessage(srcMRN, dstMRN, message);
	}
}
