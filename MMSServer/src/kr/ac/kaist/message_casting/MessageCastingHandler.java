package kr.ac.kaist.message_casting;

import kr.ac.kaist.mns_interaction.MNSInteractionHandler;

public class MessageCastingHandler {
	
	private MNSInteractionHandler mih;
	
	public MessageCastingHandler() {
		initializeModule();
	}
	
	private void initializeModule() {
		mih = new MNSInteractionHandler();
	}
	
	public String requestDstInfo(String dstMRN){
		String dstInfo = mih.requestDstInfo(dstMRN);
		
		return dstInfo;
	}
}
