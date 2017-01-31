package kr.ac.kaist.mns_interaction;

public class MNSInteractionHandler {
	private static final String TAG = "MNSInteractionHandler";

	private LocatorUpdater locatorUpdater;
	private LocatorQuerier locatorQuerier;
	private MessageOutput messageOutput;
	
	public MNSInteractionHandler() {
		locatorQuerier = new LocatorQuerier();
		locatorUpdater = new LocatorUpdater();
		messageOutput = new MessageOutput();
	}
	
	public String requestDstInfo(String dstMRN) {
		String msg = locatorQuerier.buildQuery(dstMRN);
		String dstInfo;
		dstInfo = messageOutput.sendToMNS(msg);

		return dstInfo;
	}
	
	public void updateClientInfo(String srcMRN, String srcIP, int srcPort, int srcModel){
		String msg = locatorUpdater.buildUpdate(srcMRN, srcIP, srcPort, srcModel);
		messageOutput.sendToMNS(msg);
	}

	public void registerClientInfo (String srcMRN, String srcIP, int srcPort, int srcModel){
		updateClientInfo(srcMRN, srcIP, srcPort, srcModel);
	}
}
