package kr.ac.kaist.seamless_roaming;

import kr.ac.kaist.message_queue.MMSQueue;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;

public class PollingMessageHandling {
	void pushLocationInfo(MNSInteractionHandler mih, String dstMRN, String locator) {
		mih.updateDstInfo(dstMRN, locator);
	}
	
	byte[] getSCMessage(String sourceMRN) {
		byte[] message = null;
		
		try{
			message = MMSQueue.getMessage(sourceMRN);
		} catch (Exception e) {
			message = "EMPTY".getBytes();
		}
		
		return message;
	}
}
