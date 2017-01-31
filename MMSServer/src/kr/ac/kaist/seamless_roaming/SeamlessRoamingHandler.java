package kr.ac.kaist.seamless_roaming;

import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;

public class SeamlessRoamingHandler {
	private static final String TAG = "SeamlessRoamingHandler";
	
	private PollingMessageHandling pmh;
	private SCMessageHandling scmh;
	private MNSInteractionHandler mih;
	
	public SeamlessRoamingHandler() {
		initializeModule();
		initializeSubModule();
	}
	
	private void initializeModule() {
		mih = new MNSInteractionHandler();
	}
	
	private void initializeSubModule() {
		pmh = new PollingMessageHandling();
		scmh = new SCMessageHandling();
	}
	
//	poll SC message in queue
	public byte[] processPollingMessage(String srcMRN, String srcIP, int srcPort, int srcModel) {
		byte[] message;
		
		pmh.updateClientInfo(mih, srcMRN, srcIP, srcPort, srcModel);
		message = pmh.getSCMessage(srcMRN);
		
//		System.out.println(TAG + ":" + srcMRN + "/" + srcIP + "/" + srcPort + "/" + srcModel);
		
		return message;
	}
	
	
//	save SC message into queue
	public void putSCMessage(String dstMRN, FullHttpRequest req) {
		scmh.putSCMessage(dstMRN, req);
	}
}
