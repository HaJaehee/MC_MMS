package kr.ac.kaist.seamless_roaming;

/* -------------------------------------------------------- */
/** 
File name : SeamlessRoamingHandler.java
	It takes polling messages and forwards messages if there are any messages in MMSQueue.
	It forwards locator of MSC to MIM.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events.
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

Rev. history : 2019-05-07
Version : 0.9.0
	Duplicated polling requests are not allowed.
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)

Rev. history: 2019-05-29
Version : 0.9.1
	Added long polling session count api.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-14
Version : 0.9.2
	Refactoring.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.message_relaying.MessageParser;

import kr.ac.kaist.message_relaying.SessionManager;
import kr.ac.kaist.message_relaying.polling_auth.ClientVerifier;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeamlessRoamingHandler {

	private static final Logger logger = LoggerFactory.getLogger(SeamlessRoamingHandler.class);
	private String SESSION_ID = "";

	private PollingMessageHandler pmh = null;
	private SCMessageHandler scmh = null;
	private MNSInteractionHandler mih = null;
	private ClientVerifier cltVerifier = null;
	
	private MMSLog mmsLog = null;
	private MMSLogForDebug mmsLogForDebug = null;

	private static HashMap<String, String> duplicateInfo = new HashMap<>();
	

	public SeamlessRoamingHandler(String sessionId) {
		this.SESSION_ID = sessionId;

		initializeModule();
		initializeSubModule();
	}
	
	
	private void initializeModule() {
		mih = new MNSInteractionHandler(this.SESSION_ID);
		cltVerifier = new ClientVerifier();

	}

	private void initializeSubModule() {
		pmh = new PollingMessageHandler(this.SESSION_ID);
		scmh = new SCMessageHandler(this.SESSION_ID);
		mmsLog = MMSLog.getInstance();
		mmsLogForDebug = MMSLogForDebug.getInstance();
	}

	public byte[] initializeAndGetError (MessageParser parser, MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String method) {
		
		byte[] message = null;
		String srcMRN = parser.getSrcMRN();
		String dstMRN = parser.getDstMRN();
		String srcIP = parser.getSrcIP();
		
		boolean isClientVerified = false;
		
		if (parser.isJSONOfPollingMsg() == false){
			message = ErrorCode.JSON_FORMAT_ERROR.getJSONFormattedUTF8Bytes();
			return message;
		}
		

		if(parser.getSvcMRN() == null) {
			message = ErrorCode.NULL_SVC_MRN.getJSONFormattedUTF8Bytes();
			return message;
		}
		
		mmsLog.debug(logger, this.SESSION_ID, "This is a polling request and the service MRN is " + parser.getSvcMRN());

		//TODO: THIS VERIFICATION FUNCION SHOULD BE NECESSERY.
		if (parser.getHexSignedData() != null) { //In this version 0.8.0, polling client verification is optional. 
			
			mmsLog.debug(logger, this.SESSION_ID, " Client verification using MRN="+srcMRN+" and signed data.");

			isClientVerified = cltVerifier.verifyClient(srcMRN, parser.getHexSignedData());
			
//				if (cltVerifier instanceof ClientVerifierTest) {
//					byte[] verificationTime = ((ClientVerifierTest) cltVerifier).verificationTimeJSONString();
//					outputChannel.replyToSender(ctx, verificationTime, isRealtimeLog);
//				}
			
			if (isClientVerified) {
				//Success verifying the client.
				mmsLog.debug(logger, this.SESSION_ID, "Client verification is succeeded.");

			} else {
				//Fail to verify the client.
				mmsLog.debug(logger, this.SESSION_ID, "Client verification is failed.");
				
				if (cltVerifier.isMatching() == false) {
					// message = ErrorCode.AUTHENTICATION_FAIL_NOTMATCHING.getJSONFormattedUTF8Bytes();
					message = ErrorCode.AUTHENTICATE_FAIL.getJSONFormattedUTF8Bytes();
					return message;
				}
				else if (cltVerifier.isVerified() == false) {
					//message = ErrorCode.AUTHENTICATION_FAIL_REVOKED.getJSONFormattedUTF8Bytes();
					message = ErrorCode.AUTHENTICATE_FAIL.getJSONFormattedUTF8Bytes();
					return message;
				}
			}
		}
		else {
			mmsLog.debug(logger, this.SESSION_ID, "Client's certificate is not included.");
			
			message = ErrorCode.NULL_CERTIFICATE.getJSONFormattedUTF8Bytes();		
//				String msg = "The certificate is not inlcuded.";
//				try {
//					msg = "[\""+URLEncoder.encode(msg,"UTF-8")+"\"]";
//				} catch (UnsupportedEncodingException e) {
//					logger.warn("SessionID="+SESSION_ID+" "+e.getClass().getName()+" "+e.getMessage()+" "+e.getStackTrace()[0]+".");
//					for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
//						logger.warn("SessionID="+SESSION_ID+" "+e.getStackTrace()[i]+".");
//					}
//				}
			return message;
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
		

		processPollingMessage(outputChannel, ctx, srcMRN, srcIP, method, svcMRN);

		
		return message;
	}
	
	
	// TODO: Youngjin Kim must inspect this following code.
	// Poll SC message in queue.
	public void processPollingMessage(MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN,
			String srcIP, String pollingMethod, String svcMRN) {


		if (pollingMethod.equals("normal"))	{
			SessionManager.getSessionInfo().put(SESSION_ID, "p");
		}
		else if (pollingMethod.equals("long")) {
			SessionManager.getSessionInfo().put(SESSION_ID, "lp");
		}
		SessionManager.getSessionCountList().get(0).incPollingSessionCount();

		
		//Removed at version 0.8.2.
		/*if (MMSConfiguration.getMnsHost().equals("localhost")||MMSConfiguration.getMnsHost().equals("127.0.0.1")) {
			pmh.updateClientInfo(mih, srcMRN, srcIP);
		}*/

		// Youngjin code
		// Duplicated polling request is not allowed.
		
		String DUPLICATE_ID = srcMRN + svcMRN;
		//System.out.println("Duplicate ID : "+DUPLICATE_ID);

		if (duplicateInfo.containsKey(DUPLICATE_ID)) {
			
//			System.out.println("duplicate long polling request");
			
			// TODO: To define error message.
			byte[] message = ErrorCode.DUPLICATE_POLLING.getJSONFormattedUTF8Bytes();
			
			outputChannel.replyToSender(ctx, message);
			
		} else {
			duplicateInfo.put(DUPLICATE_ID, "y");
			pmh.dequeueSCMessage(outputChannel, ctx, srcMRN, svcMRN, pollingMethod);
		}


	}

//	save SC message into queue
	public void putSCMessage(String srcMRN, String dstMRN, String message) {
		scmh.enqueueSCMessage(srcMRN, dstMRN, message);
	}
	
	public static HashMap<String, String> getDuplicateInfo() {
		return duplicateInfo;
	}
	
	public static long getDuplicateInfoSize() {
		return duplicateInfo.size();
	}
}
