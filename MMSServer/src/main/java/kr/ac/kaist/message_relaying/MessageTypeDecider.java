package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : MessageTypeDecision.java
	It decides type of a message.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-02-01
	Added log providing features.
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25 
Version : 0.5.0
	Revised class name from MessageTypeDecision to MessageTypeDecider.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-29
Version : 0.5.3
	Added system log features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-17
Version : 0.5.6
	Added polling method switching features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr) 

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-27
Version : 0.5.8
	Added RELAYING_TO_MULTIPLE_SC.
	Added EMTPY_QUEUE_LOGS.
Modifier : Jaehyun Park (jae519@kaist.ac.kr)
		   Jaehee Ha (jaehee.ha@kaist.ac.kr)
		   
Rev. history : 2017-07-28
Version : 0.5.9
	Added null MRN and invalid MRN cases. 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.handler.codec.http.HttpMethod;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.mms_server.MMSConfiguration;

class MessageTypeDecider {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageTypeDecider.class);
	private int SESSION_ID = 0;
	
	static final int POLLING = 1; // it means polling message 
	static final int RELAYING_TO_SC = 2; // it means relaying to SC
	static final int RELAYING_TO_SERVER = 3; // it means relaying to SR, IR or SP
	static final int REGISTER_CLIENT = 4; // it means registering MMS client 
	static final int UNKNOWN_MRN = 5; // it means unknown MRN
	static final int UNKNOWN_HTTP_TYPE = 6; // it means unknown http type
	static final int STATUS = 7;
	static final int EMPTY_MNSDummy = 8;
	static final int REMOVE_MNS_ENTRY = 9;
	static final int EMPTY_QUEUE_LOGS = 10;
	static final int POLLING_METHOD = 11;
	static final int RELAYING_TO_MULTIPLE_SC = 12; // it means multicase
	static final int NULL_SRC_MRN = 13;
	static final int NULL_DST_MRN = 14;
	static final int NULL_MRN = 15;
	static final int INVALID_SRC_MRN = 16;
	static final int INVALID_DST_MRN = 17;
	MessageTypeDecider(int sessionId) {
		this.SESSION_ID = sessionId;
	}
	
	int decideType(MessageParser parser, MessageCastingHandler mch) {
		String srcMRN = parser.getSrcMRN();
		String dstMRN = parser.getDstMRN();
		HttpMethod httpMethod = parser.getHttpMethod();
		String uri = parser.getUri();
		
//		when WEB_LOG_PROVIDING
		if (MMSConfiguration.WEB_LOG_PROVIDING && httpMethod == HttpMethod.GET && uri.equals("/status")){
			return STATUS;
		}
   	
   	
//		when WEB_MANAGING
	   	/* else if (MMSConfiguration.WEB_MANAGING && httpMethod == HttpMethod.GET && uri.equals("/emptyqueue")){ 
	   		return EMPTY_QUEUE;
	   	}*/ 
	   	else if (MMSConfiguration.WEB_MANAGING && httpMethod == HttpMethod.GET && uri.equals("/emptymnsdummy")){ 
	   		return EMPTY_MNSDummy;
	   	} 
	   	else if (MMSConfiguration.WEB_MANAGING && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/removemnsentry", 0, 15)){ 
	   		return REMOVE_MNS_ENTRY;
	   	} 
	   	else if (MMSConfiguration.WEB_MANAGING && httpMethod == HttpMethod.GET && uri.regionMatches(0,"/polling?method", 0, 15)){
	   		return POLLING_METHOD;
	   	} 
	   	else if (MMSConfiguration.WEB_MANAGING && httpMethod == HttpMethod.GET && uri.equals("/emptyqueuelogs")){
	   		return EMPTY_QUEUE_LOGS;
	   	} 
		
		
//		When MRN(s) is(are) null
	   	else if (srcMRN == null && dstMRN == null) {
			return NULL_MRN;
		}
		else if (srcMRN == null) {
			return NULL_SRC_MRN;
		}
		else if (dstMRN == null) {
			return NULL_DST_MRN;
		}
		
		
//    	When polling
		else if (httpMethod == HttpMethod.POST && uri.equals("/polling") && dstMRN.equals(MMSConfiguration.MMS_MRN)) {
    		return POLLING; 
    	}
    	
//		when registering
    	else if (httpMethod == HttpMethod.POST && uri.equals("/registering") && dstMRN.equals(MMSConfiguration.MMS_MRN)) {
    		return REGISTER_CLIENT;
    	}
    	

    	
//    	When relaying
    	else {
    		String dstInfo = mch.requestDstInfo(dstMRN);
    		
        	if (dstInfo.equals("No")) {
        		return UNKNOWN_MRN;
        	}
        	if (dstInfo.regionMatches(0, "MULTIPLE_MRN,", 0, 9)){
        		parser.parseMultiDstInfo(dstInfo);
        		return RELAYING_TO_MULTIPLE_SC;
        	}

        	parser.parseDstInfo(dstInfo);
        	int model = parser.getDstModel();
        	
        	if (model == 2) {//model B (destination MSR, MIR, or MSP as servers)
        		return RELAYING_TO_SERVER;
        	} 
        	else {//when model A, it puts the message into the queue
        		return RELAYING_TO_SC;
        	}
    	} /*else {
    		return UNKNOWN_HTTP_TYPE;
    	}*/
	}
	

}
