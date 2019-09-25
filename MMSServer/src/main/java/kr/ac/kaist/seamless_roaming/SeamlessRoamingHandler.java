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

Rev. history : 2019-06-20
Version : 0.9.2
	HOTFIX: polling authentication bug.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-02
Version : 0.9.2
	HOTFIX: duplicated long polling is not accepted.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-03
Version : 0.9.3
	Added multi-thread safety.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-07
Version : 0.9.3
	Added resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-10
Version : 0.9.3
	Updated resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-14
Version : 0.9.4
	Introduced MRH_MessageInputChannel.ChannelBean.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-14
Version : 0.9.4
	Updated MRH_MessageInputChannel.ChannelBean.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

 Rev. history : 2019-07-16
 Version : 0.9.4
 	Revised bugs related to MessageOrderingHandler and SeamlessRoamingHandler.
 Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

 Rev. history : 2019-09-25
 Version : 0.9.5
 	Revised bugs related to not allowing duplicated long polling request 
 	    when a MMS Client loses connection with MMS because of unexpected network disconnection.
 Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel.ChannelBean;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.message_relaying.MessageParser;
import kr.ac.kaist.message_relaying.MessageTypeDecider;
import kr.ac.kaist.message_relaying.SessionManager;
import kr.ac.kaist.message_relaying.polling_auth.ClientVerifier;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeamlessRoamingHandler {

	private static final Logger logger = LoggerFactory.getLogger(SeamlessRoamingHandler.class);
	private String sessionId = "";

	private PollingMessageHandler pmh = null;
	private SCMessageHandler scmh = null;
	private MNSInteractionHandler mih = null;
	private ClientVerifier cltVerifier = null;
	
	private MMSLog mmsLog = null;
	private MMSLogForDebug mmsLogForDebug = null;

	private static HashMap<String, DupInfoRefCntAndChannelBean> duplicationInfo = new HashMap<>();

	

	public SeamlessRoamingHandler(String sessionId) {
		this.sessionId = sessionId;

		initializeModule();
		initializeSubModule();
	}
	
	
	private void initializeModule() {
		mih = new MNSInteractionHandler(this.sessionId);
		cltVerifier = new ClientVerifier(this.sessionId);

	}

	private void initializeSubModule() {
		pmh = new PollingMessageHandler(this.sessionId);
		scmh = new SCMessageHandler(this.sessionId);
		mmsLog = MMSLog.getInstance();
		mmsLogForDebug = MMSLogForDebug.getInstance();
	}

	public byte[] initializeAndGetError (MRH_MessageInputChannel.ChannelBean bean) {
		
		byte[] message = null;
		String srcMRN = bean.getParser().getSrcMRN();
		String dstMRN = bean.getParser().getDstMRN();
		String srcIP = bean.getParser().getSrcIP();
		
		boolean isClientVerified = false;
		
		if (bean.getParser().isJSONOfPollingMsg() == false){
			message = ErrorCode.JSON_FORMAT_ERROR.getJSONFormattedUTF8Bytes();
			return message;
		}
		

		if(bean.getParser().getSvcMRN() == null) {
			message = ErrorCode.NULL_SVC_MRN.getJSONFormattedUTF8Bytes();
			return message;
		}
		
		mmsLog.debug(logger, this.sessionId, "This is a polling request and the service MRN is " + bean.getParser().getSvcMRN());

		//TODO: THIS VERIFICATION FUNCION SHOULD BE NECESSERY.
		if (bean.getParser().getHexSignedData() != null) { //In this version 0.8.0, polling client verification is optional. 
			
			mmsLog.debug(logger, this.sessionId, " Client verification using MRN="+srcMRN+" and signed data.");

			isClientVerified = cltVerifier.verifyClient(srcMRN, bean.getParser().getHexSignedData());
			
//				if (cltVerifier instanceof ClientVerifierTest) {
//					byte[] verificationTime = ((ClientVerifierTest) cltVerifier).verificationTimeJSONString();
//					outputChannel.replyToSender(ctx, verificationTime, isRealtimeLog);
//				}
			
			if (isClientVerified) {
				//Success verifying the client.
				mmsLog.debug(logger, this.sessionId, "Client verification is succeeded.");

			} else {
				//Fail to verify the client.
				mmsLog.debug(logger, this.sessionId, ErrorCode.AUTHENTICATE_FAIL.toString());
				
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
			mmsLog.debug(logger, this.sessionId, ErrorCode.NULL_CERTIFICATE.toString());
			
			message = ErrorCode.NULL_CERTIFICATE.getJSONFormattedUTF8Bytes();		
//				String msg = "The certificate is not inlcuded.";
//				try {
//					msg = "[\""+URLEncoder.encode(msg,"UTF-8")+"\"]";
//				} catch (UnsupportedEncodingException e) {
//					logger.warn("SessionID="+sessionId+" "+e.getClass().getName()+" "+e.getMessage()+" "+e.getStackTrace()[0]+".");
//					for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
//						logger.warn("SessionID="+sessionId+" "+e.getStackTrace()[i]+".");
//					}
//				}
			return message;
		}

		String svcMRN = bean.getParser().getSvcMRN();
	
		try {
			mmsLogForDebug.addSessionId(svcMRN, this.sessionId);
		}
		catch (NullPointerException e){
			mmsLog.info(logger, this.sessionId, "Detected MMSLogForDebug problem with MRN="+svcMRN+".");
			mmsLogForDebug.removeMrn(svcMRN);
			mmsLogForDebug.addMrn(svcMRN);
			
		}
		finally {
			mmsLogForDebug.addSessionId(svcMRN, this.sessionId);
		}
		
		if(mmsLogForDebug.isItsLogListEmtpy(this.sessionId)) {
			mmsLog.debug(logger, this.sessionId, "In header, srcMRN="+srcMRN+", dstMRN="+dstMRN+".");
		}
		

		message = processPollingMessage(bean);

		
		return message;
	}
	
	
	// TODO: Youngjin Kim must inspect this following code.
	// Poll SC message in queue.
	public byte[] processPollingMessage(MRH_MessageInputChannel.ChannelBean bean) {

		byte[] message = null;
		if (bean.getType() == MessageTypeDecider.msgType.POLLING)	{
			bean.retain();
			SessionManager.putSessionInfo(sessionId, "p");
			SessionManager.incPollingSessionCount();
			pmh.dequeueSCMessage(bean);
		}
		else if (bean.getType() == MessageTypeDecider.msgType.LONG_POLLING) {
			SessionManager.putSessionInfo(sessionId, "lp");
			SessionManager.incPollingSessionCount();
			
			// Youngjin code
			// Duplicated polling request is not allowed.
			String duplicationId = bean.getParser().getSrcMRN() + bean.getParser().getSvcMRN();
		
			retainDuplicationInfo(duplicationId, bean);
			
			if (getDuplicationInfoCnt(duplicationId) > 1) {

				synchronized (duplicationInfo) {
					DupInfoRefCntAndChannelBean obj = duplicationInfo.get(duplicationId);
					ChannelBean beanInDupInfo = obj.getBean();
					
				
					mmsLog.debug(logger, bean.getSessionId(), ErrorCode.DUPLICATED_POLLING.toString());
					try {
						beanInDupInfo.getOutputChannel().replyToSender(bean, ErrorCode.DUPLICATED_POLLING.getJSONFormattedUTF8Bytes());
						beanInDupInfo.getCtx().fireChannelInactive();
					} catch (IOException e) {
						mmsLog.infoException(logger, beanInDupInfo.getSessionId(), ErrorCode.LONG_POLLING_CLIENT_DISCONNECTED.toString(), new IOException(), 5);
					}
						
					int refCnt = obj.getRefCnt();
					DupInfoRefCntAndChannelBean obj2 = new DupInfoRefCntAndChannelBean(bean);
					obj2.setRefCnt(refCnt);
					duplicationInfo.put(duplicationId,obj2);
				}
				releaseDuplicationInfo(duplicationId);
			}
			bean.retain();
			pmh.dequeueSCMessage(bean);
			
		}
		
		return message;
		
		
	}

//	save SC message into queue
	public byte[] putSCMessage(MRH_MessageInputChannel.ChannelBean bean) {
		return scmh.enqueueSCMessage(bean);
	}

	
	public static long getDuplicationInfoSize() {
		synchronized(duplicationInfo) {
			return duplicationInfo.size();
		}
	}
	
	public static int getDuplicationInfoCnt(String duplicationId) {
		synchronized(duplicationInfo) {
			DupInfoRefCntAndChannelBean obj = duplicationInfo.get(duplicationId);
			if (obj != null) {
				return obj.getRefCnt();
			}
			else {
				return 0;
			}
		}
	}
	
	public static void retainDuplicationInfo(String duplicationId, ChannelBean bean) {
		synchronized(duplicationInfo) {

			//System.out.println("Retain Dup");
			DupInfoRefCntAndChannelBean obj = duplicationInfo.get(duplicationId);
			if (obj == null) {
				obj = new DupInfoRefCntAndChannelBean(bean);
				obj.setRefCnt(1);
				duplicationInfo.put(duplicationId,obj);
			}
			else {
				int refCnt = obj.getRefCnt();
				obj.setRefCnt(refCnt+1);
				duplicationInfo.put(duplicationId,obj);
			}
		}
	}
	
	public static void releaseDuplicationInfo(String duplicationId) {
		synchronized(duplicationInfo) {

			//System.out.println("Release Dup");
			DupInfoRefCntAndChannelBean obj = duplicationInfo.get(duplicationId);
			if (obj != null) {
				int refCnt = obj.getRefCnt();
				if (refCnt == 1) {
					//System.out.println(obj.getRefCnt());
					duplicationInfo.remove(duplicationId);
				}
				else {
					obj.setRefCnt(refCnt - 1);
					//System.out.println(obj.getRefCnt());
				}
			}
		}
	}
	

}

