package kr.ac.kaist.mns_interaction;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* -------------------------------------------------------- */
/** 
File name : MNSInteractionHandler.java
	It interacts with Maritime Naming System (MNS) which registers MRN, locator, and so on, and replies to a query with MRN.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-02-01
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-27
Version : 0.7.2
	Added geocasting features which cast message to circle or polygon area.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class MNSInteractionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MNSInteractionHandler.class);
	private String SESSION_ID = "";
	private LocatorUpdater locatorUpdater = null;
	private MRNInformationQuerier MRNInfoQuerier = null;
	private MIH_MessageOutputChannel messageOutput = null;
	
	public MNSInteractionHandler(String sessionId) {
		this.SESSION_ID = sessionId;
		
		initializeModule();
	}
	
	private void initializeModule(){
		MRNInfoQuerier = new MRNInformationQuerier();
		locatorUpdater = new LocatorUpdater(this.SESSION_ID);
		messageOutput = new MIH_MessageOutputChannel(this.SESSION_ID);
	}
	
	public String requestIPtoMRN(String ipAddress){
		String msg = "IP-Request:" + ipAddress;
		String mrn = messageOutput.sendToMNS(msg);
		
		return mrn;
	}
	

	public String requestDstInfo(String srcMRN, float geoLat, float geoLong, float geoRadius) {
		String msg = MRNInfoQuerier.buildQuery("geocasting_circle", srcMRN, geoLat, geoLong, geoRadius);
		return messageOutput.sendToMNS(msg);
	}
	
	public String requestDstInfo(String srcMRN, float[] geoLat, float[] geoLong) {
		String msg = MRNInfoQuerier.buildQuery("geocasting_polygon", srcMRN, geoLat, geoLong);
		return messageOutput.sendToMNS(msg);
	}
	
	public String requestDstInfo(String srcMRN, String dstMRN, String srcIP){
		String msg = MRNInfoQuerier.buildQuery("unicasting", srcMRN, dstMRN, srcIP);
		return messageOutput.sendToMNS(msg);
	}
	
	@Deprecated
	public String updateClientInfo(String srcMRN, String srcIP, int srcPort, String srcModel){
		String msg = locatorUpdater.buildUpdate(srcMRN, srcIP, srcPort, srcModel);
		return messageOutput.sendToMNS(msg);
	}

	@Deprecated
	public String registerClientInfo (String srcMRN, String srcIP, int srcPort, String srcModel){
		return updateClientInfo(srcMRN, srcIP, srcPort, srcModel);
	}
}
