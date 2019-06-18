package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : MessageRelayingHandler.java
	It relays messages from external components to destination in header field of the messages.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.4.0

Rev. history : 2017-02-01
	Added log providing features.
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-03-22
	Added member variable protocol in order to handle HTTPS.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-20 
Version : 0.5.0
	Long polling is enabled and Message Queue is implemented.
	Deprecates some methods would not be used any more.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-29
Version : 0.5.3
	Added session id and system log features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-05-06
Version : 0.5.5
	Added SessionManager features
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
	The case which type is RELAYING_TO_MULTIPLE_SC is added. 
Modifier : Jaehyun Park (jae519@kaist.ac.kr)

Rev. history : 2017-07-28
Version : 0.5.9
	Added null MRN and invalid MRN cases. 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Added adding mrn entry case.
	Added removing polling method of mrn case.
	Added enum msgType and removed public integers.
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-29
Version : 0.6.0
	MMS filters out the messages which have srcMRN or dstMRN as this MMS's MRN .
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-10-24
Version : 0.6.0
	MMS logs msg payloads at trace level.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-10-25
Version : 0.6.0
	Added MMSLogForDebug features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-15
Version : 0.7.0
	Added realtime log functions.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	Jaehyun Park (jae519@kaist.ac.kr)
	
Rev. history : 2017-11-20
Version : 0.7.0
	Revised logs slightly.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-21
Version : 0.7.0
	Revised logs slightly.
	Added try/catch statements in processRelaying().
	Placed replyToSender() method into the finally statement.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-04-23
Version : 0.7.1
	Removed RESOURCE_LEAK, IMPROPER_CHECK_FOR_UNUSUAL_OR_EXCEPTIONAL_CONDITION hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	

Rev. history : 2018-06-06
Version : 0.7.1
	Added geocasting features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	

Rev. history : 2018-06-26
Version : 0.7.1
	Moved jobs, related to the casting feature, from MessageRelayingHandler to MessageCastingHandler.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-06-29
Version : 0.7.2
	Fixed a bug of realtime log service related to removing an ID.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-03
Version : 0.7.2
	Added handling input messages by FIFO scheduling.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-10
Version : 0.7.2
	Fixed insecure codes.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-18
Version : 0.7.2
	Added handling input messages by reordering policy for relaying to server scenario.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-23
Version : 0.7.2
	Added handling input messages by reordering policy for enqueueing to message queue scenario.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-27
Version : 0.7.2
	Added geocasting features which cast message to circle or polygon area.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-10-05
Version : 0.8.0
	Added polling client verification optionally.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-10-11
Version : 0.8.0
	Modified polling client verification.
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

Rev. history : 2019-04-18
Version : 0.8.2
	Applying Asynchronous.
Modifier : Yunho Choi (choiking10@kaist.ac.kr)

Rev. history: 2019-05-05
Version : 0.9.0
	Added rest API functions.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-17
Version : 0.9.1
	Add error codes related to polling authentication message.
	MMS does not accept the polling request message not formatted by JSON.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-05-17
Version : 0.9.1
	From now, MessageParser is initialized in MRH_MessageInputChannel class.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-21
Version : 0.9.1
	Added session management of polling message authentication.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-05-26
Version : 0.9.1
	Session management of polling message authentication is deprecated.
	Make error code to be general.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-01
Version : 0.9.2
	Revised log levels.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-10
Version : 0.9.2
	Made logs neat (cont'd).
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-13
Version : 0.9.2
	HOTFIX: Resolved a bug related to message ordering.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr),
		Yunho Choi (choiking10@kaist.ac.kr)
		
Rev. history : 2019-06-14
Version : 0.9.2
	Refactoring.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.nio.charset.Charset;
import java.text.ParseException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel.ConnectionThread;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.ErrorResponseException;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;
import kr.ac.kaist.mms_server.MMSRestAPIHandler;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;


public class MessageRelayingHandler  {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageRelayingHandler.class);

	private String SESSION_ID = "";
	private MessageParser parser = null;
	private MessageTypeDecider typeDecider = null;
	private MRH_MessageOutputChannel outputChannel = null;
	
	private MessageOrderingHandler moh = null;
	private SeamlessRoamingHandler srh = null;
	private MessageCastingHandler mch = null;
	
	private MMSLog mmsLog = null;
	private MMSLogForDebug mmsLogForDebug = null;
	private MMSRestAPIHandler mmsRestApiHandler = null;

	private String protocol = "";
	
	private boolean isClientVerified = false;
    private ConnectionThread thread = null;
    
    private boolean isErrorOccured = false;

	
	public MessageRelayingHandler(ChannelHandlerContext ctx, FullHttpRequest req, String protocol, MessageParser parser, String sessionId) {		
		this.protocol = protocol;
		this.SESSION_ID = sessionId;
		
		initializeModule();
		initializeSubModule(ctx);
		
		this.parser = parser;
	
		
		MessageTypeDecider.msgType type = null;
		try {
			type = typeDecider.decideType(parser, mch);
		} 
		catch (ParseException e) {
			mmsLog.info(logger, SESSION_ID, ErrorCode.MESSAGE_PARSING_ERROR.toString());
		}
		try {
			processRelaying(type, ctx, req);
		} catch(ErrorResponseException e) {
			e.replyToSender(outputChannel, ctx);
		}
	}
	
	private void initializeModule() {
		mch = new MessageCastingHandler(this.SESSION_ID);
		mmsLog = MMSLog.getInstance();
		mmsLogForDebug = MMSLogForDebug.getInstance();
	}
	
	private void initializeSubModule(ChannelHandlerContext ctx) {
		typeDecider = new MessageTypeDecider(this.SESSION_ID);
		outputChannel = new MRH_MessageOutputChannel(this.SESSION_ID, ctx);
		
//		if (MMSConfiguration.isPollingTest()) {
//			cltVerifier = new ClientVerifierTest();
//		} else {
//			cltVerifier = new ClientVerifier();
//		}
	}

    public ConnectionThread getConnectionThread() {
        return thread;
    }

	private void processRelaying(MessageTypeDecider.msgType type, ChannelHandlerContext ctx, FullHttpRequest req)
			throws ErrorResponseException {

		byte[] message = null;
		boolean isRealtimeLog = false;
		
		
		String srcMRN = parser.getSrcMRN();
		String dstMRN = parser.getDstMRN();
		HttpMethod httpMethod = parser.getHttpMethod();
		String dstIP = parser.getDstIP();
		int dstPort = parser.getDstPort();
		long seqNum = parser.getSeqNum();
		String srcDstPair = srcMRN+"::"+dstMRN;
		
		try {
			mmsLogForDebug.addSessionId(srcMRN, this.SESSION_ID);
		}
		catch (NullPointerException e) {
			mmsLog.info(logger, this.SESSION_ID, "Detected MMSLogForDebug problem with MRN="+srcMRN+".");
			mmsLogForDebug.removeMrn(srcMRN);
			mmsLogForDebug.addMrn(srcMRN);
			mmsLogForDebug.addSessionId(srcMRN, this.SESSION_ID);
		}

		try {
			mmsLogForDebug.addSessionId(dstMRN, this.SESSION_ID);
		}
		catch (NullPointerException e) {
			mmsLog.info(logger, this.SESSION_ID, "Detected MMSLogForDebug problem with MRN="+srcMRN+".");
			mmsLogForDebug.removeMrn(dstMRN);
			mmsLogForDebug.addMrn(dstMRN);
			mmsLogForDebug.addSessionId(dstMRN, this.SESSION_ID);
		}
		
		
		//This code MUST be 'if' statement not 'else if'. 
		if (type != MessageTypeDecider.msgType.REALTIME_LOG) {
			if (seqNum != -1) {
				String log = "In header, srcMRN="+srcMRN+", dstMRN="+dstMRN+", seqNum="+seqNum+".";
				mmsLog.info(logger, this.SESSION_ID, log);
			}
			else {
				String log = "In header, srcMRN="+srcMRN+", dstMRN="+dstMRN+".";
				mmsLog.info(logger, this.SESSION_ID, log);
			}
			
			if(logger.isTraceEnabled()) {
				mmsLog.trace(logger, this.SESSION_ID, "Payload="+StringEscapeUtils.escapeXml(req.content().toString(Charset.forName("UTF-8")).trim()));
			}
		}
		
		//This code MUST be 'if' statement not 'else if'. 
		if (type == MessageTypeDecider.msgType.REST_API) {
    		mmsRestApiHandler = new MMSRestAPIHandler(this.SESSION_ID);
    		mmsRestApiHandler.setParams(req);
    		message = mmsRestApiHandler.getResponse().getBytes(Charset.forName("UTF-8"));
    		mmsLog.info(logger, this.SESSION_ID, "Respond to a REST API request.");
		}
		
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.RELAYING_TO_SERVER_SEQUENTIALLY || type == MessageTypeDecider.msgType.RELAYING_TO_SC_SEQUENTIALLY) {
			moh = new MessageOrderingHandler();
			message = moh.initializeAndGetError(parser, this.SESSION_ID);
			if (message != null) {
				isErrorOccured = true;
			}
		}
		
		//Below code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.NULL_MRN) {
			isErrorOccured = true;
			message = ErrorCode.NULL_MRN.getUTF8Bytes();
		}
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.NULL_SRC_MRN) {
			isErrorOccured = true;
			message = ErrorCode.NULL_SRC_MRN.getUTF8Bytes();
		}
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.NULL_DST_MRN) {
			isErrorOccured = true;
			message = ErrorCode.NULL_DST_MRN.getUTF8Bytes();
		}
		// TODO: Youngjin Kim must inspect this following code.
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.POLLING || type == MessageTypeDecider.msgType.LONG_POLLING) {
			srh = new SeamlessRoamingHandler(this.SESSION_ID);
			if (type == MessageTypeDecider.msgType.POLLING) {
				message = srh.initializeAndGetError(parser, outputChannel, ctx, "normal");
			}
			else if (type == MessageTypeDecider.msgType.LONG_POLLING) {
				message = srh.initializeAndGetError(parser, outputChannel, ctx, "long");
			}
			if (message != null) {
				outputChannel.replyToSender(ctx, message, isRealtimeLog);
			}
			return;
		} 
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.RELAYING_TO_SC) {
			srh = new SeamlessRoamingHandler(this.SESSION_ID);
			srh.putSCMessage(srcMRN, dstMRN, req.content().toString(Charset.forName("UTF-8")).trim());
    		message = "OK".getBytes(Charset.forName("UTF-8"));
    		outputChannel.replyToSender(ctx, message, isRealtimeLog);
    		return;
		} 
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.RELAYING_TO_MULTIPLE_SC){
			String [] dstMRNs = parser.getMultiDstMRN();
			message = mch.castMsgsToMultipleCS(srcMRN, dstMRNs, req.content().toString(Charset.forName("UTF-8")).trim());
		} 
		
		
		//Below code MUST be 'if' statement not 'else if'. 
		if (type == MessageTypeDecider.msgType.RELAYING_TO_SERVER_SEQUENTIALLY || type == MessageTypeDecider.msgType.RELAYING_TO_SC_SEQUENTIALLY) {
			message = moh.processMessage(outputChannel, req, protocol, mch, type);
			if (message != null) {
				isErrorOccured = true;
			}
			else {
				thread = moh.getConnectionThread();
			}
		}
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.RELAYING_TO_SERVER) {
			thread = mch.asynchronizedUnicast(outputChannel, req, dstIP, dstPort, protocol, httpMethod, srcMRN, dstMRN);
		}
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.GEOCASTING_CIRCLE || type == MessageTypeDecider.msgType.GEOCASTING_POLYGON) {
			JSONArray geoDstInfo = parser.getGeoDstInfo();
			message = mch.geocast(outputChannel, req, srcMRN, geoDstInfo, protocol, httpMethod);
		}
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.STATUS){
			mmsRestApiHandler = new MMSRestAPIHandler(this.SESSION_ID);
    		message = mmsRestApiHandler.getStatus(req);
		}
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.REALTIME_LOG){
			isRealtimeLog = true;
			mmsRestApiHandler = new MMSRestAPIHandler(this.SESSION_ID);
			message = mmsRestApiHandler.getRealtimeLog(req);
			
		}
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.ADD_ID_IN_REALTIME_LOG_IDS) {
			mmsRestApiHandler = new MMSRestAPIHandler(this.SESSION_ID);
			message = mmsRestApiHandler.addIdInRealtimeLogIds(req);
		}
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.REMOVE_ID_IN_REALTIME_LOG_IDS) {
			mmsRestApiHandler = new MMSRestAPIHandler(this.SESSION_ID);
			message = mmsRestApiHandler.removeIdInRealtimeLogIds(req);
		}
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.ADD_MRN_BEING_DEBUGGED) {
			mmsRestApiHandler = new MMSRestAPIHandler(this.SESSION_ID);
			message = mmsRestApiHandler.addMrnBeingDebugged(req);
		}
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.REMOVE_MRN_BEING_DEBUGGED) {
			mmsRestApiHandler = new MMSRestAPIHandler(this.SESSION_ID);
			message = mmsRestApiHandler.removeMrnBeingDebugged(req);
		}
		// TODO this condition has to be deprecated.
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.ADD_MNS_ENTRY) {
			mmsRestApiHandler = new MMSRestAPIHandler(this.SESSION_ID);
			message = mmsRestApiHandler.addMnsEntry(req);
		}
		// TODO this condition has to be deprecated.
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.REMOVE_MNS_ENTRY) {
			mmsRestApiHandler = new MMSRestAPIHandler(this.SESSION_ID);
			message = mmsRestApiHandler.removeMnsEntry(req);
		} 

		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.DST_MRN_IS_THIS_MMS_MRN) {
			isErrorOccured = true;
			mmsLog.debug(logger, this.SESSION_ID, "Hello, MMS!");
			message = "Hello, MMS!".getBytes();
		}
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.SRC_MRN_IS_THIS_MMS_MRN) {
			isErrorOccured = true;
			mmsLog.debug(logger, this.SESSION_ID, "You are not me.");
			message = "You are not me.".getBytes();
		}
		//This code MUST be 'else if' statement not 'if'. 
		else if (type == MessageTypeDecider.msgType.UNKNOWN_MRN) {
			isErrorOccured = true;
			mmsLog.info(logger, this.SESSION_ID, ErrorCode.UNKNOWN_MRN.toString());
			message = ErrorCode.UNKNOWN_MRN.getUTF8Bytes();
			//logger.info("test "+message);
		} 

		//This code MUST be 'if' statement not 'else if'.
		if (type != MessageTypeDecider.msgType.POLLING && type != MessageTypeDecider.msgType.LONG_POLLING) {

			if ((type == MessageTypeDecider.msgType.RELAYING_TO_SERVER_SEQUENTIALLY || type == MessageTypeDecider.msgType.RELAYING_TO_SERVER) && thread == null) {
				if (message == null) {
					message = ErrorCode.UNKNOWN_ERR.getBytes();
					mmsLog.info(logger, this.SESSION_ID, "INVALID MESSAGE.");
					outputChannel.replyToSender(ctx, message, isRealtimeLog); //TODO: MUST HAVE MORE DEFINED EXCEPTION MESSAGES.
					return;
				}
				else {
					outputChannel.replyToSender(ctx, message, isRealtimeLog);
					return;
				}
			}
			else if (isErrorOccured || message != null) {
				outputChannel.replyToSender(ctx, message, isRealtimeLog);
				return;
			}
			else if (!isErrorOccured && message == null && !(type == MessageTypeDecider.msgType.RELAYING_TO_SERVER_SEQUENTIALLY || type == MessageTypeDecider.msgType.RELAYING_TO_SERVER)) {
				message = ErrorCode.UNKNOWN_ERR.getBytes();
				mmsLog.info(logger, this.SESSION_ID, "INVALID MESSAGE.");
				outputChannel.replyToSender(ctx, message, isRealtimeLog); //TODO: MUST HAVE MORE DEFINED EXCEPTION MESSAGES.
				return;
			}
		}

		
		//TODO: THIS VERIFICATION FUNCION SHOULD BE NECESSERY.
		//In version 0.8.0, polling client verification is optional. 
		//This code MUST be 'if' statement not 'else if'. 
		if ((type == MessageTypeDecider.msgType.POLLING || type == MessageTypeDecider.msgType.LONG_POLLING) && parser.getHexSignedData() != null && !isClientVerified) {
			byte[] msg = null;
			
			System.out.println(parser.getSvcMRN());
			if (parser.getSvcMRN() == null) {
				msg = ErrorCode.NULL_SVC_MRN.getJSONFormattedUTF8Bytes();
			} 
			else {
				//msg = ErrorCode.AUTHENTICATION_FAIL_REVOKED.getJSONFormattedUTF8Bytes();
				msg = ErrorCode.AUTHENTICATE_FAIL.getJSONFormattedUTF8Bytes();
			}
			outputChannel.replyToSender(ctx, msg, isRealtimeLog);
			return;
		}
	}
}
