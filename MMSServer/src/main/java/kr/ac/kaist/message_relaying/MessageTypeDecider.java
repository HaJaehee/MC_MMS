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

Rev. history : 2017-09-26
Version : 0.6.0
	Added adding mrn entry case.
	Removed empty queue logs case.
	Added enum msgType and removed public integers.
	Replaced from random int sessionId to String sessionId as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-29
Version : 0.6.0
	MMS filters out the messages which have srcMRN or dstMRN as this MMS's MRN .
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-10-25
Version : 0.6.0
	Added MMSLogsForDebug features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-15
Version : 0.7.0
	Added realtime log functions
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	Jaehyun Park (jae519@kaist.ac.kr)
	
Rev. history : 2018-07-10
Version : 0.7.2
	Fixed insecure codes.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-18
Version : 0.7.2
	Added handling input messages by reordering policy.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-27
Version : 0.7.2
	Added geocasting features which cast message to circle or polygon area.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-10-15
Version : 0.8.0
	Resolved MAVEN dependency problems with library "net.etri.pkilib".
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-10-16
Version : 0.8.0
	Modified in order to interact with MNS server.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-04-12
Version : 0.8.2
	Modified for coding rule conformity.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-05
Version : 0.9.0
	Added rest API functions.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-03
Version : 0.9.3
	Added multi-thread safety.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.handler.codec.http.HttpMethod;
import kr.ac.kaist.message_casting.GeolocationCircleInfo;
import kr.ac.kaist.message_casting.GeolocationPolygonInfo;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.mms_server.MMSConfiguration;

public class MessageTypeDecider {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageTypeDecider.class);
	private String sessionId = "";
	
	
	public static enum msgType {
			POLLING,
			LONG_POLLING,
			RELAYING_TO_SC,
			RELAYING_TO_SC_SEQUENTIALLY,
			RELAYING_TO_SERVER,
			RELAYING_TO_SERVER_SEQUENTIALLY,
			//REGISTER_CLIENT,
			UNKNOWN_MRN,
			STATUS,
			//EMPTY_MNSDummy,
			REMOVE_MNS_ENTRY,
			ADD_MNS_ENTRY,
			//POLLING_METHOD,
			RELAYING_TO_MULTIPLE_SC,
			NULL_SRC_MRN,
			NULL_DST_MRN,
			NULL_MRN,
			PRIORITY_ERROR,
			INVALID_SRC_MRN,
			INVALID_DST_MRN,
			INVALID_HTTP_METHOD,
			DST_MRN_IS_THIS_MMS_MRN,
			SRC_MRN_IS_THIS_MMS_MRN,
			ADD_MRN_BEING_DEBUGGED,
			REMOVE_MRN_BEING_DEBUGGED,
			REALTIME_LOG,
			ADD_ID_IN_REALTIME_LOG_IDS,
			REMOVE_ID_IN_REALTIME_LOG_IDS,
			GEOCASTING_CIRCLE,
			GEOCASTING_POLYGON,
			REST_API
	}

	
	MessageTypeDecider(String sessionId) {
		this.sessionId = sessionId;
	}
	
	msgType decideType(MessageParser parser, MessageCastingHandler mch) throws ParseException{
		String srcMRN = parser.getSrcMRN();
		String dstMRN = parser.getDstMRN();
		HttpMethod httpMethod = parser.getHttpMethod();
		String uri = parser.getUri();
		double seqNum = parser.getSeqNum();
		
//		When MRN(s) is(are) null
	   	if (srcMRN == null && dstMRN == null) {
	   		
//			when WEB_LOG_PROVIDING
			if (MMSConfiguration.isWebLogProviding() && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/status", 0, 7)){
				return msgType.STATUS;
			}
			else if (MMSConfiguration.isWebLogProviding() && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/realtime-log?id", 0, 16)){
				return msgType.REALTIME_LOG;
			}
			
//			when WEB_MANAGING
		   	else if (MMSConfiguration.isWebManaging() && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/add-mns-entry?mrn", 0, 18)){ 
		   		return msgType.ADD_MNS_ENTRY;
		   	} 
		   	else if (MMSConfiguration.isWebManaging() && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/remove-mns-entry?mrn", 0, 21)){ 
		   		return msgType.REMOVE_MNS_ENTRY;
		   	} 
			
		   	/* Removed at version 0.8.2.
		   	 * else if (MMSConfiguration.isWebManaging() && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/polling?method", 0, 15)){
		   		return msgType.POLLING_METHOD;
		   	}*/
			
			// MMS Restful API
		   	else if (httpMethod == HttpMethod.GET && uri.regionMatches(0, "/api?", 0, 5)) {
		   		
		   		return msgType.REST_API;
		   	}
			
			
		   	else if (MMSConfiguration.isWebManaging() && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/add-mrn-being-debugged?mrn", 0, 27)) {
		   		return msgType.ADD_MRN_BEING_DEBUGGED;
		   	}
		   	else if (MMSConfiguration.isWebManaging() && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/remove-mrn-being-debugged?mrn", 0, 30)) {
		   		return msgType.REMOVE_MRN_BEING_DEBUGGED;
		   	}
		   	else if (MMSConfiguration.isWebLogProviding() && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/add-id-realtime-log-ids?id", 0, 27)){
				return msgType.ADD_ID_IN_REALTIME_LOG_IDS;
			}
		   	else if (MMSConfiguration.isWebLogProviding() && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/remove-id-realtime-log-ids?id", 0, 30)){
				return msgType.REMOVE_ID_IN_REALTIME_LOG_IDS;
			}
			
			return msgType.NULL_MRN;
		}
		else if (srcMRN == null) {
			return msgType.NULL_SRC_MRN;
		}
		else if (dstMRN == null) {
			
			return msgType.NULL_DST_MRN;
		}
	   	
		else if (parser.getPriority() < 0) {
			return msgType.PRIORITY_ERROR;
		}
	   	
		else if (srcMRN.equals(MMSConfiguration.getMmsMrn())) {
			return msgType.SRC_MRN_IS_THIS_MMS_MRN;
		}
		
		else if (dstMRN.equals(MMSConfiguration.getMmsMrn())) {
			

			// TODO: Youngjin Kim must inspect this following code.
			//When polling
			if (httpMethod == HttpMethod.POST && uri.equals("/polling")) {
				SessionManager.incSessionCount();
	    		return msgType.POLLING; 
	    	}
			
			//When long polling
			if (httpMethod == HttpMethod.POST && uri.equals("/long-polling")) {
				SessionManager.incSessionCount();
	    		return msgType.LONG_POLLING; 
	    	}
	    	
	    	else {
	    		return msgType.DST_MRN_IS_THIS_MMS_MRN;
	    	}
		}
		
		// When geocasting
		else if (parser.isGeocastingMsg()) {
			
			
			if (httpMethod == HttpMethod.POST) {
				SessionManager.incSessionCount();
				
				if (parser.getGeoCircleInfo() != null) {
					GeolocationCircleInfo geo = parser.getGeoCircleInfo();
					String geocastInfo = mch.queryMNSForDstInfo(srcMRN, dstMRN, geo.getGeoLat(), geo.getGeoLong(), geo.getGeoRadius());
					parser.parseGeocastInfo(geocastInfo);
					
					return msgType.GEOCASTING_CIRCLE;
				}
				
				
				else if (parser.getGeoPolygonInfo() != null) {
					GeolocationPolygonInfo geo = parser.getGeoPolygonInfo();
					float[] geoLatList = geo.getGeoLatList();
					float[] geoLongList = geo.getGeoLongList();
					String geocastInfo = null;
					if (geoLatList != null && geoLongList != null) {
						geocastInfo = mch.queryMNSForDstInfo(srcMRN, dstMRN, geoLatList, geoLongList);
						if (geocastInfo != null) {
							parser.parseGeocastInfo(geocastInfo);
							return msgType.GEOCASTING_POLYGON;
						} else {
							//TODO: MUST define specific error code.
							return msgType.UNKNOWN_MRN;
						}
					}
					else {
						//TODO: MUST define specific error code.
						return msgType.UNKNOWN_MRN;
					}
				}
			}
			else { //httpMethod != HttpMethod.POST
				return msgType.INVALID_HTTP_METHOD;
			}
		 	return msgType.UNKNOWN_MRN;
		}
    	
//    	When relaying
    	else {
    		String dstInfo = mch.queryMNSForDstInfo(srcMRN, dstMRN, parser.getSrcIP());
    		
    		
    		if (dstInfo != null) {

    			//TODO: Exceptions from MNS must be handled.
	        	if (dstInfo.equals("No")) {
	        		return msgType.UNKNOWN_MRN;
	        	}  
	        	/*//TODO: This function must be defined.
	        	else if (dstInfo.regionMatches(0, "MULTIPLE_MRN,", 0, 9)){
	        		SessionManager.incSessionCount();
	        		parser.parseMultiDstInfo(dstInfo);
	        		return msgType.RELAYING_TO_MULTIPLE_SC;
	        	}*/
	
	        	parser.parseDstInfo(dstInfo);
	        	String model = parser.getDstModel();
	        	
				
				
	        	if (model.equals("push")) {//model B (destination MSR, MIR, or MSP as servers)
	        		SessionManager.incSessionCount();
	        		if (seqNum == -1) {
	        			return msgType.RELAYING_TO_SERVER;
	        		}
	        		else {
	        			return msgType.RELAYING_TO_SERVER_SEQUENTIALLY;
	        		}
	        	} 
	        	else if (model.equals("polling")){//when model A, it puts the message into the queue
	        		if (httpMethod == HttpMethod.POST) {
	        			SessionManager.incSessionCount();
		        		if (seqNum == -1) {
		        			return msgType.RELAYING_TO_SC;
		        		}
		        		else {
		        			return msgType.RELAYING_TO_SC_SEQUENTIALLY;
		        		}
	        		}
	        		else { //httpMethod != HttpMethod.POST
	        			return msgType.INVALID_HTTP_METHOD;
	        		}
	        	}
    		}
        	
        	return msgType.UNKNOWN_MRN;
        	
    	} 
		/*else {
    		return UNKNOWN_HTTP_TYPE;
    	}*/
	  
	}
	

}
