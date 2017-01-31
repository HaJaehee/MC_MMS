package kr.ac.kaist.seamless_roaming;

import kr.ac.kaist.message_queue.MMSQueue;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;

public class PollingMessageHandling {
	void updateClientInfo(MNSInteractionHandler mih, String srcMRN, String srcIP, int srcPort, int srcModel) {
		mih.updateClientInfo(srcMRN, srcIP, srcPort, srcModel);
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
