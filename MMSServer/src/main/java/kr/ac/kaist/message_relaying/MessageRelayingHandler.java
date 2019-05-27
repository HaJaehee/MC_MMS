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
*/
/* -------------------------------------------------------- */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel.ConnectionThread;
import kr.ac.kaist.message_relaying.polling_auth.ClientVerifier;
import kr.ac.kaist.message_relaying.polling_auth.ClientVerifierTest;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.ErrorResponseException;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;
import kr.ac.kaist.mms_server.MMSRestAPIHandler;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;


public class MessageRelayingHandler  {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageRelayingHandler.class);

	private String SESSION_ID = "";
	private Thread sessionBlocker = null;

	private MessageParser parser = null;
	private MessageTypeDecider typeDecider = null;
	private MRH_MessageOutputChannel outputChannel = null;
	private ClientVerifier cltVerifier = null;
	
	private SeamlessRoamingHandler srh = null;
	private MessageCastingHandler mch = null;
	
	private MMSLog mmsLog = null;
	private MMSLogForDebug mmsLogForDebug = null;
	private MMSRestAPIHandler mmsRestApiHandler = null;

	private String protocol = "";
	
	private boolean isClientVerified = false;
    private ConnectionThread thread;

	
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
			logger.warn("SessionID="+SESSION_ID+" "+e.getClass().getName()+" "+e.getStackTrace()[0]+".");
			for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
				logger.warn("SessionID="+SESSION_ID+" "+e.getStackTrace()[i]+".");
			}
		}
		try {
			processRelaying(type, ctx, req);
		} catch(ErrorResponseException e) {
			e.replyToSender(outputChannel, ctx);
		}
	}
	
	private void initializeModule() {
		srh = new SeamlessRoamingHandler(this.SESSION_ID);
		mch = new MessageCastingHandler(this.SESSION_ID);
		mmsLog = MMSLog.getInstance();
		mmsLogForDebug = MMSLogForDebug.getInstance();
		mmsRestApiHandler = new MMSRestAPIHandler(this.SESSION_ID);
	}
	
	private void initializeSubModule(ChannelHandlerContext ctx) {
		sessionBlocker = new Thread();
		typeDecider = new MessageTypeDecider(this.SESSION_ID);
		outputChannel = new MRH_MessageOutputChannel(this.SESSION_ID, ctx);
		
		cltVerifier = new ClientVerifier();
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
		
		try {
			String srcMRN = parser.getSrcMRN();
			String dstMRN = parser.getDstMRN();
			HttpMethod httpMethod = parser.getHttpMethod();
			String uri = parser.getUri();
			String dstIP = parser.getDstIP();
			String srcIP = parser.getSrcIP();
			int dstPort = parser.getDstPort();
			double seqNum = parser.getSeqNum();
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
			
			if (type == MessageTypeDecider.msgType.REST_API) {
				
				QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		mmsRestApiHandler.setParams(params);
	    		message = mmsRestApiHandler.getResponse().getBytes(Charset.forName("UTF-8"));
			}
			
			
			else if (type == MessageTypeDecider.msgType.RELAYING_TO_SERVER_SEQUENTIALLY || type == MessageTypeDecider.msgType.RELAYING_TO_SC_SEQUENTIALLY) {
				
				//System.out.println("SessionID="+this.SESSION_ID+" RELAYING_TO_SERVER_SEQUENTIALLY INIT");
				
				if (SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair) == null ) { //Initialization
					SessionManager.getMapSrcDstPairAndSessionInfo().put(srcDstPair, new SessionList<SessionIdAndThr>());	
					while (SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair) == null) {}
				}
				
				if (SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) == null ) { //Initialization
					SessionManager.getMapSrcDstPairAndLastSeqNum().put(srcDstPair, (double) -1);
					while (SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) == null) {
					}
				}
				List <SessionIdAndThr> itemList = SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair);
				//System.out.println("SessionID="+this.SESSION_ID+" RELAYING_TO_SERVER_SEQUENTIALLY START");
				//printSessionsInSessionMng (srcDstPair);
				if (seqNum == 0) {
					
					if (SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) != -1) { //Reset sessions in SessionManager related to srcMRN and dstMRN pair.
						//System.out.println("Reset sessions in SessionManager related to srcMRN and dstMRN pair.");
						int itemListSize = itemList.size();
						while (itemListSize > 0) {
							//System.out.println("Reset sessions");
							itemList.get(0).setExceptionFlag(true);
							if (!itemList.get(0).isWaitingRes()) {
								itemList.get(0).getSessionBlocker().interrupt();
							}
							itemListSize = itemList.size(); //MUST be updated in every iteration because of multi-thread safety.
						}
						itemList.clear();
						
						SessionManager.getMapSrcDstPairAndLastSeqNum().put(srcDstPair, (double) -1);
						itemList.add(new SessionIdAndThr(this.SESSION_ID, this.sessionBlocker, seqNum));
					}
					else { //SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) == -1
						itemList.add(0, new SessionIdAndThr(this.SESSION_ID, this.sessionBlocker, seqNum));
					}

					//System.out.println("SessionID="+itemList.get(0).getSessionId()+" seqNum="+itemList.get(0).getSeqNum());
					
				}
				else if (seqNum != 0) {
					//TODO MUST be implemented. THIS may cause deadlock because even "if (seqNum == 0)" statement is not completed, THIS statement may be incurred.
					//TODO Sort messages based on seqNums of messages.
					//System.out.println("seqNum="+seqNum+"!=0");
					
					int index = 0;
					int itemListSize = itemList.size();
					if (seqNum > SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair)) {
						while (index < itemListSize) {
							try {
								if (seqNum > itemList.get(index).getSeqNum()) {
									index++;
									itemListSize = itemList.size(); //MUST be updated in every iteration because of multi-thread safety.
									continue;
								}
								else if (seqNum < itemList.get(index).getSeqNum()) {
									itemList.add(index, new SessionIdAndThr(this.SESSION_ID, this.sessionBlocker, seqNum));
									break;
								}
								else { //seqNum == itemList.get(index).getSeqNum()
									//System.out.println("index="+index+", seqNum="+seqNum+", seqNum in List="+itemList.get(0).getSeqNum());
									//System.out.println("Sequence number of message is duplicated.");
									throw new MessageOrderException("Sequence number of message is duplicated.");
								}
							}
							catch (NullPointerException e) {
								index = 0;
								itemListSize = itemList.size();
							}
						}
						if (index == itemListSize) { //This condition contains conditions "index == 0" and "itemListSize == 0".
							itemList.add(index, new SessionIdAndThr(this.SESSION_ID, this.sessionBlocker, seqNum));
						}
					}
					else { //Drop message.
						throw new MessageOrderException("Sequence number of message is out of ordered.");
					}
					//System.out.println("index="+index+", seqNum="+seqNum+", seqNum in List="+itemList.get(0).getSeqNum());
				}
				//System.out.println("SessionID="+this.SESSION_ID+" RELAYING_TO_SERVER_SEQUENTIALLY END");
				//printSessionsInSessionMng (srcDstPair);
			}
			

			else if (type == MessageTypeDecider.msgType.NULL_MRN) {
				message = ErrorCode.NULL_MRN.getUTF8Bytes();
			}
			else if (type == MessageTypeDecider.msgType.NULL_SRC_MRN) {
				message = ErrorCode.NULL_SRC_MRN.getUTF8Bytes();
			}
			else if (type == MessageTypeDecider.msgType.NULL_DST_MRN) {
				message = ErrorCode.NULL_DST_MRN.getUTF8Bytes();
			}
			// TODO: Youngjin Kim must inspect this following code.
			else if (type == MessageTypeDecider.msgType.POLLING || type == MessageTypeDecider.msgType.LONG_POLLING) {
				//parser.parseSvcMRNAndHexSign(req);
				
				if (parser.isJSONOfPollingMsg() == false){
					message = ErrorCode.JSON_FORMAT_ERR.getJSONFormattedUTF8Bytes();
					outputChannel.replyToSender(ctx, message, isRealtimeLog);
				}
				
	
				if(parser.getSvcMRN() == null) {
					throw new IOException("Service MRN is not included.");
				}
				
				mmsLog.debug(logger, this.SESSION_ID, "This is a polling request and the service MRN is " + parser.getSvcMRN());

				//TODO: THIS VERIFICATION FUNCION SHOULD BE NECESSERY.
				if (parser.getHexSignedData() != null) { //In this version 0.8.0, polling client verification is optional. 
					
					mmsLog.debug(logger, this.SESSION_ID, " Client verification using MRN="+srcMRN+" and signed data.");

					isClientVerified = cltVerifier.verifyClient(srcMRN, parser.getHexSignedData());
					
//					if (cltVerifier instanceof ClientVerifierTest) {
//						byte[] verificationTime = ((ClientVerifierTest) cltVerifier).verificationTimeJSONString();
//						outputChannel.replyToSender(ctx, verificationTime, isRealtimeLog);
//					}
					
					if (isClientVerified) {
						//Success verifying the client.
						mmsLog.debug(logger, this.SESSION_ID, "Client verification is succeeded.");

					} else {
						//Fail to verify the client.
						mmsLog.debug(logger, this.SESSION_ID, "Client verification is failed.");
						
						if (cltVerifier.isMatching() == false) {
							// message = ErrorCode.AUTHENTICATION_FAIL_NOTMATCHING.getJSONFormattedUTF8Bytes();
							message = ErrorCode.AUTHENTICATE_FAIL.getJSONFormattedUTF8Bytes();
						}
						else if (cltVerifier.isVerified() == false) {
							//message = ErrorCode.AUTHENTICATION_FAIL_REVOKED.getJSONFormattedUTF8Bytes();
							message = ErrorCode.AUTHENTICATE_FAIL.getJSONFormattedUTF8Bytes();
						}
						
						outputChannel.replyToSender(ctx, message, isRealtimeLog);
					}
				}
				else {
					mmsLog.debug(logger, this.SESSION_ID, "Client's certificate is not included.");
					
					message = ErrorCode.NULL_CERTIFICATE.getJSONFormattedUTF8Bytes();		
//					String msg = "The certificate is not inlcuded.";
//					try {
//						msg = "[\""+URLEncoder.encode(msg,"UTF-8")+"\"]";
//					} catch (UnsupportedEncodingException e) {
//						logger.warn("SessionID="+SESSION_ID+" "+e.getClass().getName()+" "+e.getMessage()+" "+e.getStackTrace()[0]+".");
//						for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
//							logger.warn("SessionID="+SESSION_ID+" "+e.getStackTrace()[i]+".");
//						}
//					}
					outputChannel.replyToSender(ctx, message, isRealtimeLog);
				}

				String svcMRN = parser.getSvcMRN();
			
				try {
					mmsLogForDebug.addSessionId(svcMRN, this.SESSION_ID);
				}
				catch (NullPointerException e){
					mmsLog.info(logger, this.SESSION_ID, "Detected MMSLogForDebug problem with MRN="+svcMRN+".");
					mmsLogForDebug.removeMrn(svcMRN);
					mmsLogForDebug.addMrn(svcMRN);
					
				}
				finally {
					mmsLogForDebug.addSessionId(svcMRN, this.SESSION_ID);
				}
				
				if(mmsLogForDebug.isItsLogListEmtpy(this.SESSION_ID)) {
					mmsLog.debug(logger, this.SESSION_ID, "In header, srcMRN="+srcMRN+", dstMRN="+dstMRN+".");
				}
				
				if (type == MessageTypeDecider.msgType.POLLING) {
//					System.out.println("[RelayingHandler]-[InPolling] serviceMRN: " + svcMRN);
					srh.processPollingMessage(outputChannel, ctx, srcMRN, srcIP, "normal", svcMRN);
				} else if (type == MessageTypeDecider.msgType.LONG_POLLING) {
					srh.processPollingMessage(outputChannel, ctx, srcMRN, srcIP, "long", svcMRN);
				}
				
				return;
			} 
			else if (type == MessageTypeDecider.msgType.RELAYING_TO_SC) {
				srh.putSCMessage(srcMRN, dstMRN, req.content().toString(Charset.forName("UTF-8")).trim());
	    		message = "OK".getBytes(Charset.forName("UTF-8"));
			} 
			else if (type == MessageTypeDecider.msgType.RELAYING_TO_MULTIPLE_SC){
				String [] dstMRNs = parser.getMultiDstMRN();
				
				message = mch.castMsgsToMultipleCS(srcMRN, dstMRNs, req.content().toString(Charset.forName("UTF-8")).trim());
			} 
			
			else if (type == MessageTypeDecider.msgType.RELAYING_TO_SERVER_SEQUENTIALLY || type == MessageTypeDecider.msgType.RELAYING_TO_SC_SEQUENTIALLY) {
				List<SessionIdAndThr> itemList = SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair);

				while (true) { 
					if (itemList == null || 
							itemList.size() == 0 ||
							itemList.get(0) == null ||
							itemList.get(0).getSessionBlocker() == null) { //Check null pointer exception.
						// This condition is required for safe coding when using multi-threads.
						
						message = ErrorCode.SEQUENTIAL_RELAYING_INITIALIZATION_ERR.getUTF8Bytes();
						
						throw new NullPointerException();
					}
					try {
						//System.out.println("RELAYING_TO_SERVER_SEQUENTIALLY getSessionID="+itemList.get(0).getSessionId());
						if (itemList.get(0).getSessionId().equals(this.SESSION_ID)) { //MUST be THIS session.
							if (SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) == itemList.get(0).getPreSeqNum() || 
									itemList.get(0).getWaitingCount() > 0 ||
									itemList.get(0).isExceptionOccured()) {
								// TODO 이 코드에 대한 설명 필요
								throw new InterruptedException();	
							}
							else {
								//System.out.println("Block (by sleep) this relaying process if it's not this session's turn with seq num.");
								printSessionsInSessionMng(srcDstPair);
								//System.out.println("last seq number="+SessionManager.mapSrcDstPairAndLastSeqNum.get(srcDstPair));
								sessionBlocker.sleep(MMSConfiguration.getWaitingMessageTimeout()); //Block (by sleep) this relaying process if it's not this session's turn with sequence number.
								itemList.get(0).incWaitingCount();
							}
						}
						else {
							//System.out.println("Block (by sleep) this relaying process if it's not this session's turn.");
							sessionBlocker.sleep(MMSConfiguration.getWaitingMessageTimeout()); //Block (by sleep) this relaying process if it's not this session's turn.
						}
					} 
					catch (InterruptedException e) {
						//System.out.println("Interrupted! This session ID="+SESSION_ID+", Session ID in list="+itemList.get(0).getSessionId()+", isExceptionOccured="+itemList.get(0).isExceptionOccured()+", seq num="+seqNum+", last seq num="+SessionManager.mapSrcDstPairAndLastSeqNum.get(srcDstPair));
						if (itemList.get(0).getSessionId().equals(this.SESSION_ID)) { //MUST be THIS session.
							if ((itemList.get(0).getPreSeqNum() == SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) && 
									!itemList.get(0).isExceptionOccured()) || itemList.get(0).getWaitingCount() > 0){
								setThisSessionWaitingRes(srcDstPair);
								if (type == MessageTypeDecider.msgType.RELAYING_TO_SERVER_SEQUENTIALLY) {
									thread = mch.asynchronizedUnicast(outputChannel, req, dstIP, dstPort, protocol, httpMethod, srcMRN, dstMRN); // Execute this relaying process
								}
								else if (type == MessageTypeDecider.msgType.RELAYING_TO_SC_SEQUENTIALLY) {
									srh.putSCMessage(srcMRN, dstMRN, req.content().toString(Charset.forName("UTF-8")).trim());
						    		message = "OK".getBytes(Charset.forName("UTF-8"));
								}
								rmvCurRlyFromScheduleAndWakeUpNxtRlyBlked(srcDstPair);
								break;
							}
							else if (itemList.get(0).isExceptionOccured()) {
								message = ErrorCode.SEQUENTIAL_RELAYING_EXCEPTION_ERR.getUTF8Bytes();
								
								printSessionsInSessionMng(srcDstPair);
								mmsLog.warn(logger, this.SESSION_ID, "Message order exception is occured. Message sequence is reset 0.");
			
								itemList.remove(0);
								break;
							}
							
						}
					}
				}

				// TODO 이 위치에 진입하면 message가 null로 설정됩니다. 적당한 할당 필요 by using Error Code 
				// 위에 꺼를 방지하기 위해 이 위치에서 thread도 널이고, message도 널이면 message에 적당한 에러 코드를 삽입하면 좋을듯
			}
			else if (type == MessageTypeDecider.msgType.RELAYING_TO_SERVER) {
				thread = mch.asynchronizedUnicast(outputChannel, req, dstIP, dstPort, protocol, httpMethod, srcMRN, dstMRN);
			}
			else if (type == MessageTypeDecider.msgType.GEOCASTING_CIRCLE || type == MessageTypeDecider.msgType.GEOCASTING_POLYGON) {
				
				JSONArray geoDstInfo = parser.getGeoDstInfo();
				message = mch.geocast(outputChannel, req, srcMRN, geoDstInfo, protocol, httpMethod);
				
			}
			else if (type == MessageTypeDecider.msgType.STATUS){
	    		String status;
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		mmsLog.info(logger, this.SESSION_ID, "Get MMS status and logs.");

	    		if (params.get("mrn") == null) {
	    			try {
						status = mmsLog.getStatus("");
						message = status.getBytes(Charset.forName("UTF-8"));
					} 
					catch (UnknownHostException e) {
						message = ErrorCode.MONITORING_CONNECTION_ERR.getUTF8Bytes();
						
						mmsLog.warnException(logger, this.SESSION_ID, ErrorCode.MONITORING_CONNECTION_ERR.toString(), e, 5);
					} 
					catch (IOException e) {
						message = ErrorCode.DUMPMNS_LOGGING_ERR.getUTF8Bytes();
						
						mmsLog.warnException(logger, this.SESSION_ID, ErrorCode.DUMPMNS_LOGGING_ERR.toString(), e, 5);
					}
	    		}
	    		else {
	
					try {
	    				status = mmsLog.getStatus(params.get("mrn").get(0));
						message = status.getBytes(Charset.forName("UTF-8"));
					} 
					catch (UnknownHostException e) {
						message = ErrorCode.MONITORING_CONNECTION_ERR.getUTF8Bytes();
						
						mmsLog.warnException(logger, this.SESSION_ID, ErrorCode.MONITORING_CONNECTION_ERR.toString(), e, 5);
					} 
					catch (IOException e) {
						message = ErrorCode.DUMPMNS_LOGGING_ERR.getUTF8Bytes();
						
						mmsLog.warnException(logger, this.SESSION_ID, ErrorCode.DUMPMNS_LOGGING_ERR.toString(), e, 5);
					}
	    		}
			}
			else if (type == MessageTypeDecider.msgType.REALTIME_LOG){
	    		String realtimeLog = "";
	    		String callback = "";
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		if (params.get("id") != null & params.get("callback") != null) {
	    			callback = params.get("callback").get(0);
	    			realtimeLog = mmsLog.getRealtimeLog(params.get("id").get(0));
	    			isRealtimeLog = true;
	    		}
	    		else {
	    			message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
	    		}
				
	    		// TODO : 위에 wrong_parameter 에 관련된 코드가 있는데 message를 한번 덮어씌워버리네요?
				message = (callback+"("+realtimeLog+")").getBytes(Charset.forName("UTF-8"));
			}
			else if (type == MessageTypeDecider.msgType.ADD_ID_IN_REALTIME_LOG_IDS) {
				
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		if (params.get("id") != null) {
	    			mmsLog.addIdToBriefRealtimeLogEachIDs(params.get("id").get(0));
	    			mmsLog.warn(logger, this.SESSION_ID, "Added an ID using realtime log service="+params.get("id").get(0)+".");
	    			message = "OK".getBytes(Charset.forName("UTF-8"));
	    		}
	    		else {
	    			mmsLog.warn(logger, this.SESSION_ID, "Wrong parameter.");
					message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
	    		}
			}
			else if (type == MessageTypeDecider.msgType.REMOVE_ID_IN_REALTIME_LOG_IDS) {
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		if (params.get("id") != null) {
	    			mmsLog.removeIdFromBriefRealtimeLogEachIDs(params.get("id").get(0));
	    			mmsLog.warn(logger, this.SESSION_ID, "Removed an ID using realtime log service="+params.get("id").get(0)+".");
	    			message = "OK".getBytes(Charset.forName("UTF-8"));
	    		}
	    		else {
	    			mmsLog.warn(logger, this.SESSION_ID, "Wrong parameter.");
	    			message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
	    		}
			}
			else if (type == MessageTypeDecider.msgType.ADD_MRN_BEING_DEBUGGED) {
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		if (params.get("mrn")!=null) {
	    			String mrn = params.get("mrn").get(0);
	    			mmsLogForDebug.addMrn(mrn);
	    			mmsLog.warn(logger, this.SESSION_ID, "Added a MRN being debugged="+mrn+".");
	    			message = "OK".getBytes(Charset.forName("UTF-8"));
	    		}
	    		else {
	    			mmsLog.warn(logger, this.SESSION_ID, "Wrong parameter.");
	    			message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
	    		}
			}
			else if (type == MessageTypeDecider.msgType.REMOVE_MRN_BEING_DEBUGGED) {
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		if (params.get("mrn")!=null) {
	    			String mrn = params.get("mrn").get(0);
	    			mmsLogForDebug.removeMrn(mrn);
	    			mmsLog.warn(logger, this.SESSION_ID, "Removed debug MRN="+mrn+".");
	    			message = "OK".getBytes(Charset.forName("UTF-8"));
	    		}
	    		else {
	    			mmsLog.warn(logger, this.SESSION_ID, "Wrong parameter.");
	    			message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
	    		}
			}
			// TODO this condition has to be deprecated.
			else if (type == MessageTypeDecider.msgType.REMOVE_MNS_ENTRY) {
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		logger.warn("SessionID="+this.SESSION_ID+" Remove MRN=" + params.get("mrn").get(0)+".");
	    		if (params.get("mrn")!=null && !params.get("mrn").get(0).equals(MMSConfiguration.getMmsMrn())) {
	    			try {
						removeEntryMNS(params.get("mrn").get(0));
						message = "OK".getBytes(Charset.forName("UTF-8"));
					} 
		    		catch (UnknownHostException e) {
						// This code block will be deprecated, so there is no definition of error code.
		    			
		    			mmsLog.warnException(logger, this.SESSION_ID, "", e, 5);
					} 
		    		catch (IOException e) {
		    			message = ErrorCode.DUMPMNS_LOGGING_ERR.getUTF8Bytes();
		    			
		    			mmsLog.warnException(logger, this.SESSION_ID, "", e, 5);
					} 
	    		}
	    		else {
	    			mmsLog.warn(logger, this.SESSION_ID, "Wrong parameter.");
					message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
				}
			} 
			// TODO this condition has to be deprecated.
			else if (type == MessageTypeDecider.msgType.ADD_MNS_ENTRY) {
				QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
				Map<String,List<String>> params = qsd.parameters();
				mmsLog.warn(logger, this.SESSION_ID, "Add MRN=" + params.get("mrn").get(0) + " IP=" + params.get("ip").get(0) + " Port=" + params.get("port").get(0) + " Model=" + params.get("model").get(0)+".");
				if (params.get("mrn")!=null && !params.get("mrn").get(0).equals(MMSConfiguration.getMmsMrn())) {
					try {
						addEntryMNS(params.get("mrn").get(0), params.get("ip").get(0), params.get("port").get(0), params.get("model").get(0));
						message = "OK".getBytes(Charset.forName("UTF-8"));
					}
					catch (UnknownHostException e) {
						// This code block will be deprecated, so there is no definition of error code.
						
						mmsLog.warnException(logger, this.SESSION_ID, "", e, 5);
					} 
		    		catch (IOException e) {
		    			ErrorCode.DUMPMNS_LOGGING_ERR.getUTF8Bytes();
		    			
		    			mmsLog.warnException(logger, this.SESSION_ID, "", e, 5);
					} 
				}
				else {
					mmsLog.warn(logger, this.SESSION_ID, "Wrong parameter.");
					message = ErrorCode.WRONG_PARAM.getUTF8Bytes();
				}
			}

			
			else if (type == MessageTypeDecider.msgType.DST_MRN_IS_THIS_MMS_MRN) {
				message = "Hello, MMS!".getBytes();
			}
			else if (type == MessageTypeDecider.msgType.SRC_MRN_IS_THIS_MMS_MRN) {
				message = "You are not me.".getBytes();
			}
			else if (type == MessageTypeDecider.msgType.UNKNOWN_MRN) {
				message = ErrorCode.UNKNOWN_MRN.getBytes();
				//logger.info("test "+message);
			} 
			
		} 
		catch (NullPointerException | IOException e) {
			
			mmsLog.warnException(logger, this.SESSION_ID, "", e, 5);
		}
		finally {
			if (type != MessageTypeDecider.msgType.POLLING && type != MessageTypeDecider.msgType.LONG_POLLING && thread == null) {
				if (message == null) {
					message = ErrorCode.UNKNOWN_ERR.getBytes();
					mmsLog.info(logger, this.SESSION_ID, "INVALID MESSAGE.");
					outputChannel.replyToSender(ctx, message, isRealtimeLog); //TODO: MUST HAVE MORE DEFINED EXCEPTION MESSAGES.
				}
				else {
					outputChannel.replyToSender(ctx, message, isRealtimeLog);
				}
			}
			
			if (type == MessageTypeDecider.msgType.RELAYING_TO_SERVER_SEQUENTIALLY || type == MessageTypeDecider.msgType.RELAYING_TO_SC_SEQUENTIALLY) {
				String srcMRN = parser.getSrcMRN();
				String dstMRN = parser.getDstMRN();
				String srcDstPair = srcMRN+"::"+dstMRN;
				List<SessionIdAndThr> itemList = SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair);

				// TODO 이 위치 진입시에 응답 없이 종료 시켜버릴 가능성이 있지 않나요?
				if (itemList.get(0).getSessionId().equals(this.SESSION_ID)) { //MUST be THIS session.
					if ((itemList.get(0).getPreSeqNum() == SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) && 
							!itemList.get(0).isExceptionOccured()) || itemList.get(0).getWaitingCount() > 0){
						try {
							rmvCurRlyFromScheduleAndWakeUpNxtRlyBlked(srcDstPair);
						} catch (NullPointerException | IOException e) {
							mmsLog.warnException(logger, this.SESSION_ID, "", e, 5);
						}
					}
				}
			}
			
			//TODO: THIS VERIFICATION FUNCION SHOULD BE NECESSERY.
			//In this version 0.8.0, polling client verification is optional. 
			if ((type == MessageTypeDecider.msgType.POLLING || type == MessageTypeDecider.msgType.LONG_POLLING) && parser.getHexSignedData() != null && !isClientVerified) {
				byte[] msg = null;
				if (parser.getSvcMRN() == null) {
					msg = ErrorCode.NULL_SVC_MRN.getJSONFormattedUTF8Bytes();
				} 
				else {
					//msg = ErrorCode.AUTHENTICATION_FAIL_REVOKED.getJSONFormattedUTF8Bytes();
					msg = ErrorCode.AUTHENTICATE_FAIL.getJSONFormattedUTF8Bytes();
				}
				outputChannel.replyToSender(ctx, msg, isRealtimeLog);
			}
		}
	}
	

	private void printSessionsInSessionMng (String srcDstPair) {
		List<SessionIdAndThr> itemList = SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair);
		if (itemList.size() > 0) {
			SessionIdAndThr item = itemList.get(0);
			//System.out.println("index="+0+", SessionID="+item.getSessionId()+", seqNum="+item.getSeqNum()+", waitingCount="+item.getWaitingCount()+", isExceptionOccured="+item.isExceptionOccured());
		}
		/*for (int i = 0 ; i < itemList.size() ; i++) {
			SessionIdAndThr item = itemList.get(i);
			//System.out.println("index="+i+", SessionID="+item.getSessionId()+", seqNum="+item.getSeqNum()+", waitingCount="+item.getWaitingCount()+", isExceptionOccured="+item.isExceptionOccured());
		}*/
	}
	
	private void rmvCurRlyFromScheduleAndWakeUpNxtRlyBlked (String srcDstPair) throws IOException, NullPointerException{
		
		List <SessionIdAndThr> listItem = SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair);
		if (listItem == null || 
				listItem.size() == 0 ||
				listItem.get(0) == null ||
				listItem.get(0).getSessionBlocker() == null ||
				SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) == null) { //Check null pointer exception.
			throw new NullPointerException();
		}
		//System.out.println("Seq number="+listItem.get(0).getSeqNum());
		//System.out.println("Last seq number="+SessionManager.mapSrcDstPairAndLastSeqNum.get(srcDstPair));
		//TODO MUST be implemented. MUST awake waitingDiscardingSessionThr if it is not null.
		if (listItem.get(0).getSessionId().equals(this.SESSION_ID)) { 
			//TODO Next message having successive seqNum will be relayed.
			boolean checkNextSeq = false; 
			if (listItem != null && 
					listItem.size() > 1 && 
					listItem.get(1) != null && 
					listItem.get(1).getSessionBlocker() != null &&
					listItem.get(1).getPreSeqNum() == listItem.get(0).getSeqNum() &&
					!listItem.get(1).isWaitingRes()) { // Check next sequence of message.
				checkNextSeq = true;
				//System.out.println("index="+1+", SessionID="+listItem.get(1).getSessionId()+", seqNum="+listItem.get(1).getSeqNum()+", waitingCount="+listItem.get(1).getWaitingCount()+", isExceptionOccured="+listItem.get(1).isExceptionOccured());

			}
			SessionManager.getMapSrcDstPairAndLastSeqNum().put(srcDstPair, (double) listItem.get(0).getSeqNum());
			//System.out.println("Updated last seq number="+SessionManager.mapSrcDstPairAndLastSeqNum.get(srcDstPair));
			listItem.remove(0); //Remove current relaying process from the schedule. 
			if (checkNextSeq) { //Wake up next relaying process blocked if exist.
				//System.out.println("index="+0+", SessionID="+listItem.get(0).getSessionId()+", seqNum="+listItem.get(0).getSeqNum()+", waitingCount="+listItem.get(0).getWaitingCount()+", isExceptionOccured="+listItem.get(0).isExceptionOccured());
				listItem.get(0).getSessionBlocker().interrupt();
			}
		}
		else {
			throw new IOException();
		}
		return;
	}
	
	private void setThisSessionWaitingRes (String srcDstPair) throws IOException, NullPointerException{
		
		List <SessionIdAndThr> listItem = SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair);
		if (listItem == null || 
				listItem.size() == 0 ||
				listItem.get(0) == null ||
				listItem.get(0).getSessionBlocker() == null ||
				SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) == null) { //Check null pointer exception.
			throw new NullPointerException();
		}
		
		if (listItem.get(0).getSessionId().equals(this.SESSION_ID)) { //This session is waiting a respond.
			listItem.get(0).setWaitingRes(true);
		}
		else {
			throw new IOException();
		}
		return;
	}

  
//This method will be
  @Deprecated
  private void removeEntryMNS(String mrn) throws UnknownHostException, IOException{ //


	  Socket MNSSocket = null;
	  PrintWriter pw = null;	
	  InputStreamReader isr = null;
	  BufferedReader br = null;
	  String queryReply = null;
	  try{
		  //String modifiedSentence;

		  MNSSocket = new Socket(MMSConfiguration.getMnsHost(), MMSConfiguration.getMnsPort());
		  MNSSocket.setSoTimeout(5000);
		  pw = new PrintWriter(MNSSocket.getOutputStream());
		  isr = new InputStreamReader(MNSSocket.getInputStream());
		  br = new BufferedReader(isr);
		  String inputLine = null;
		  StringBuffer response = new StringBuffer();

		  mmsLog.warn(logger, this.SESSION_ID, "Remove Entry="+mrn+".");

		  pw.println("Remove-Entry:"+mrn);
		  pw.flush();
		  if (!MNSSocket.isOutputShutdown()) {
			  MNSSocket.shutdownOutput();
		  }


		  while ((inputLine = br.readLine()) != null) {
			  response.append(inputLine);
		  }


		  queryReply = response.toString();
		  mmsLog.trace(logger, this.SESSION_ID, "From server="+queryReply+".");


	  } catch (UnknownHostException e) {
		  mmsLog.errorException(logger, this.SESSION_ID, "", e, 5);
		  
	  } catch (IOException e) {
		  mmsLog.errorException(logger, this.SESSION_ID, "", e, 5);
		  
	  } finally {
		  if (pw != null) {
			  pw.close();
		  }
		  if (isr != null) {
			  try {
				  isr.close();
			  } catch (IOException e) {
				  mmsLog.errorException(logger, this.SESSION_ID, "", e, 5);
			  }
		  }
		  if (br != null) {
			  try {
				  br.close();
			  } catch (IOException e) {
				  mmsLog.errorException(logger, this.SESSION_ID, "", e, 5);
			  }
		  }
		  if (MNSSocket != null) {
			  try {
				  MNSSocket.close();
			  } catch (IOException e) {
				  mmsLog.errorException(logger, this.SESSION_ID, "", e, 5);
			  }
		  }
	  }
	  return;
  }
  
//This method will be
  @Deprecated
  private void addEntryMNS(String mrn, String ip, String port, String model) throws UnknownHostException, IOException {


	  Socket MNSSocket = null;
	  PrintWriter pw = null;	
	  InputStreamReader isr = null;
	  BufferedReader br = null;
	  String queryReply = null;
	  try{
		  //String modifiedSentence;

		  MNSSocket = new Socket(MMSConfiguration.getMnsHost(), MMSConfiguration.getMnsPort());
		  MNSSocket.setSoTimeout(5000);
		  pw = new PrintWriter(MNSSocket.getOutputStream());
		  isr = new InputStreamReader(MNSSocket.getInputStream());
		  br = new BufferedReader(isr);
		  String inputLine = null;
		  StringBuffer response = new StringBuffer();

		  mmsLog.warn(logger, this.SESSION_ID, "Add Entry="+mrn+".");

		  pw.println("Add-Entry:"+mrn+","+ip+","+port+","+model);
		  pw.flush();
		  if (!MNSSocket.isOutputShutdown()) {
			  MNSSocket.shutdownOutput();
		  }


		  while ((inputLine = br.readLine()) != null) {
			  response.append(inputLine);
		  }


		  queryReply = response.toString();
		  mmsLog.trace(logger, this.SESSION_ID, "From server=" + queryReply+".");

	  } catch (UnknownHostException e) {
		  logger.error("SessionID="+SESSION_ID+" "+e.getClass().getName()+" "+e.getStackTrace()[0]+".");
			for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
				logger.warn("SessionID="+SESSION_ID+" "+e.getStackTrace()[i]+".");
			}
		 
	  } catch (IOException e) {
		  logger.error("SessionID="+SESSION_ID+" "+e.getClass().getName()+" "+e.getStackTrace()[0]+".");
			for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
				logger.warn("SessionID="+SESSION_ID+" "+e.getStackTrace()[i]+".");
			}
		  
	  } finally {
		  if (pw != null) {
			  pw.close();
		  }
		  if (isr != null) {
			  try {
				  isr.close();
			  } catch (IOException e) {
				  logger.warn("SessionID="+SESSION_ID+" "+e.getClass().getName()+" "+e.getStackTrace()[0]+".");
	    			for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
	    				logger.warn("SessionID="+SESSION_ID+" "+e.getStackTrace()[i]+".");
	    			}
			  }
		  }
		  if (br != null) {
			  try {
				  br.close();
			  } catch (IOException e) {
				  logger.warn("SessionID="+SESSION_ID+" "+e.getClass().getName()+" "+e.getStackTrace()[0]+".");
	    			for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
	    				logger.warn("SessionID="+SESSION_ID+" "+e.getStackTrace()[i]+".");
	    			}
			  }
		  }
		  if (MNSSocket != null) {
			  try {
				  MNSSocket.close();
			  } catch (IOException e) {
				  logger.warn("SessionID="+SESSION_ID+" "+e.getClass().getName()+" "+e.getStackTrace()[0]+".");
	    			for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
	    				logger.warn("SessionID="+SESSION_ID+" "+e.getStackTrace()[i]+".");
	    			}
			  }
		  }
	  }
	  return;
  }
  
  
}
