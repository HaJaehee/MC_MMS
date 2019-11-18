package kr.ac.kaist.mns_interaction;

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
	Replaced from random int sessionId to String sessionId as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-27
Version : 0.7.2
	Added geocasting features which cast message to circle or polygon area.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-10-15
Version : 0.8.0
	Resolved MAVEN dependency problems with library "net.etri.pkilib".
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

*/
/* -------------------------------------------------------- */


import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;


public class MNSInteractionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MNSInteractionHandler.class);
	private String sessionId = "";
	private MRNInformationQuerier MRNInfoQuerier = null;
	private MIH_MessageOutputChannel messageOutput = null;
	private MMSLog mmsLog = null;
	private MMSLogForDebug mmsLogForDebug = null;
	
	public MNSInteractionHandler(String sessionId) {
		this.sessionId = sessionId;
		
		initializeModule();
	}
	
	private void initializeModule(){
		MRNInfoQuerier = new MRNInformationQuerier();
		messageOutput = new MIH_MessageOutputChannel(this.sessionId);
		mmsLog = MMSLog.getInstance();
		mmsLogForDebug = MMSLogForDebug.getInstance();
	}
	
	public String requestIPtoMRN(String ipAddress){
		String msg = "IP-Request:" + ipAddress;
		String mrn = messageOutput.sendToMNS(msg);
		
		return mrn;
	}
	

	public String requestDstInfo(String srcMRN, String dstMRN, float geoLat, float geoLong, float geoRadius) {
		String msg = MRNInfoQuerier.buildQuery("geocasting_circle", srcMRN, dstMRN, geoLat, geoLong, geoRadius);
		return messageOutput.sendToMNS(msg);
	}
	
	public String requestDstInfo(String srcMRN, String dstMRN, float[] geoLat, float[] geoLong) {
		String msg = MRNInfoQuerier.buildQuery("geocasting_polygon", srcMRN, dstMRN, geoLat, geoLong);
		return messageOutput.sendToMNS(msg);
	}
	
	public String requestDstInfo(String srcMRN, String dstMRN, String srcIP){
		String msg = MRNInfoQuerier.buildQuery("unicasting", srcMRN, dstMRN, srcIP);
		return messageOutput.sendToMNS(msg);
	}
}
