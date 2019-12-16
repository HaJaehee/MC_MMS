package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : MessageOrderingHandler.java
	It relays messages from external components to destination in header field of the messages with keeping sequence.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-06-13
Version : 0.9.2

Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-03
Version : 0.9.3
	Added multi-thread safety.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-09
Version : 0.9.3
	Revised for coding rule conformity.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-10
Version : 0.9.3
	Added resource managing codes.
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
 
Rev. history : 2019-10-25
Version : 0.9.6
	Removed unused codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
**/
/* -------------------------------------------------------- */

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel.ConnectionThread;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

class MessageOrderingHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageOrderingHandler.class);
	
	private String srcMRN = null;
	private String dstMRN = null;
	private HttpMethod httpMethod = null;
	private String uri = null;
	private String dstIP = null;
	private String srcIP = null;
	int dstPort = 0;
	long seqNum = -1;
	String srcDstPair = null;
	private Thread sessionBlocker = null;
	private MMSLog mmsLog = null;
	private ConnectionThread thread = null;
	
	public MessageOrderingHandler() {
		
		initializeModule();
	}
	private void initializeModule() {
		this.mmsLog = MMSLog.getInstance();
	}
	
	public byte[] initializeAndGetError (MRH_MessageInputChannel.ChannelBean bean) {
		
		byte[] message = null;
		this.srcMRN = bean.getParser().getSrcMRN();
		this.dstMRN = bean.getParser().getDstMRN();
		this.dstIP = bean.getParser().getDstIP();
		this.dstPort = bean.getParser().getDstPort();
		this.httpMethod = bean.getParser().getHttpMethod();
		this.uri = bean.getParser().getUri();
		this.seqNum = bean.getParser().getSeqNum();
		this.srcDstPair = srcMRN+"::"+dstMRN;
		this.sessionBlocker = new Thread();

		//System.out.println("SessionID="+bean.getSessionId()+" RELAYING_TO_SERVER_SEQUENTIALLY INIT");
		
		if (SessionManager.getItemFromMapSrcDstPairAndSessionInfo(srcDstPair) == null ) { //Initialization
			SessionManager.putItemToMapSrcDstPairAndSessionInfo(srcDstPair);	
			while (SessionManager.getItemFromMapSrcDstPairAndSessionInfo(srcDstPair) == null) {}
		}
		
		if (SessionManager.getNumFromMapSrcDstPairAndLastSeqNum(srcDstPair) == null ) { //Initialization
			SessionManager.resetNumInMapSrcDstPairAndLastSeqNum(srcDstPair);
			while (SessionManager.getNumFromMapSrcDstPairAndLastSeqNum(srcDstPair) == null) {}
		}
		
		List <SessionIdAndThr> itemList = SessionManager.getItemFromMapSrcDstPairAndSessionInfo(srcDstPair);
		//System.out.println("SessionID="+bean.getSessionId()+" RELAYING_TO_SERVER_SEQUENTIALLY START");
		//printSessionsInSessionMng (srcDstPair);
		if (seqNum == 0) {
			
			if (SessionManager.getNumFromMapSrcDstPairAndLastSeqNum(srcDstPair) != -1) { //Reset sessions in SessionManager related to srcMRN and dstMRN pair.
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
				
				SessionManager.resetNumInMapSrcDstPairAndLastSeqNum(srcDstPair);
				itemList.add(new SessionIdAndThr(bean.getSessionId(), this.sessionBlocker, seqNum));
			}
			else { //SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) == -1
				itemList.add(0, new SessionIdAndThr(bean.getSessionId(), this.sessionBlocker, seqNum));
			}

			//System.out.println("SessionID="+itemList.get(0).getSessionId()+" seqNum="+itemList.get(0).getSeqNum());
			
		}
		else if (seqNum != 0) {
			//THIS may cause deadlock because even "if (seqNum == 0)" statement is not completed, THIS statement may be incurred.
			//Sort messages based on seqNums of messages.
			//System.out.println("seqNum="+seqNum+"!=0");
			
			int index = 0;
			int itemListSize = itemList.size();
			if (seqNum > SessionManager.getNumFromMapSrcDstPairAndLastSeqNum(srcDstPair)) {
				while (index < itemListSize) {
					if (seqNum > itemList.get(index).getSeqNum()) {
						index++;
						itemListSize = itemList.size(); //MUST be updated in every iteration because of multi-thread safety.
						continue;
					}
					else if (seqNum < itemList.get(index).getSeqNum()) {
						itemList.add(index, new SessionIdAndThr(bean.getSessionId(), this.sessionBlocker, seqNum));
						break;
					}
					else { //seqNum == itemList.get(index).getSeqNum()
						//System.out.println("index="+index+", seqNum="+seqNum+", seqNum in List="+itemList.get(0).getSeqNum());
						//System.out.println("Sequence number of message is duplicated.");
						message = ErrorCode.SEQUENCE_NUMBER_IS_DUPLICATED.getUTF8Bytes();
						mmsLog.info(logger, bean.getSessionId(), ErrorCode.SEQUENCE_NUMBER_IS_DUPLICATED.toString());
						return message;
					}
				}
				if (index == itemListSize) { //This condition contains conditions "index == 0" and "itemListSize == 0".
					itemList.add(index, new SessionIdAndThr(bean.getSessionId(), this.sessionBlocker, seqNum));
				}
			}
			else { //Drop message.
				message = ErrorCode.SEQUENCE_NUMBER_IS_OUT_OF_ORDER.getUTF8Bytes();
				mmsLog.info(logger, bean.getSessionId(), ErrorCode.SEQUENCE_NUMBER_IS_OUT_OF_ORDER.toString());
				return message;
			}
			//System.out.println("index="+index+", seqNum="+seqNum+", seqNum in List="+itemList.get(0).getSeqNum());
		}
		//System.out.println("SessionID="+bean.getSessionId()+" RELAYING_TO_SERVER_SEQUENTIALLY END");
		//printSessionsInSessionMng (srcDstPair);
		
		return message;
	}
	
	
	public byte[] processMessage (MRH_MessageInputChannel.ChannelBean bean, MessageCastingHandler mch) {
		byte[] message = null;
		
		List<SessionIdAndThr> itemList = SessionManager.getItemFromMapSrcDstPairAndSessionInfo(srcDstPair);
		boolean escapeLoop = false;
		int numCheckingItemList = 0;
		while (!escapeLoop) {
			if (numCheckingItemList > 0 && (itemList == null || itemList.size() == 0 || itemList.get(0).getSessionId() == null))  { //Check null pointer exception.
				// This condition is required for safe coding when using multi-threads.
				
				return ErrorCode.SEQUENTIAL_RELAYING_INITIALIZATION_ERROR.getUTF8Bytes();
			}
			try {
				//System.out.println("RELAYING_TO_SERVER_SEQUENTIALLY getSessionID="+itemList.get(0).getSessionId());
				if (itemList != null && itemList.size()>0 && itemList.get(0).getSessionId().equals(bean.getSessionId())) { //MUST be THIS session.
					if (SessionManager.getNumFromMapSrcDstPairAndLastSeqNum(srcDstPair) == itemList.get(0).getPreSeqNum() || 
							itemList.get(0).getWaitingCount() > 0 ||
							itemList.get(0).isExceptionOccured()) {
						// If this session is interrupted, process its message.
						message = processThisThread(itemList, bean, mch);
						if (message != null) {
							return message;
						}
						else {
							return null;
						}
						
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
					numCheckingItemList++;
				}
			} 
			catch (InterruptedException e) {
				message = processThisThread(itemList, bean, mch);
				if (message != null) {
					return message;
				}
				else {
					return null;
				}
			}
		}
		
		itemList = SessionManager.getItemFromMapSrcDstPairAndSessionInfo(srcDstPair);

		// TODO Is there any risk for shutting down the process before replying to sender?
		// HOTFIX: Resolved a bug related to message ordering.
		if (itemList.size()>0 && itemList.get(0).getSessionId().equals(bean.getSessionId())) { //MUST be THIS session.
			if ((itemList.get(0).getPreSeqNum() == SessionManager.getNumFromMapSrcDstPairAndLastSeqNum(srcDstPair) && 
					!itemList.get(0).isExceptionOccured()) || itemList.get(0).getWaitingCount() > 0){
				
				message = rmvCurRlyFromScheduleAndWakeUpNxtRlyBlked(srcDstPair, bean.getSessionId());
				if (message != null) {
					mmsLog.info(logger, bean.getSessionId(), new String(message));
					return message;
				}
			}
		}

		// If message is null, no error occurred, otherwise an error occurred (excluding message equals "OK").
		// If thread is null, an error occurred, otherwise no error occurred (excluding message equals "OK").
		return message;
	}
	
	private byte[] processThisThread (List<SessionIdAndThr> itemList, MRH_MessageInputChannel.ChannelBean bean, MessageCastingHandler mch) {
		byte[] message = null;
		//System.out.println("Interrupted! This session ID="+sessionId+", Session ID in list="+itemList.get(0).getSessionId()+", isExceptionOccured="+itemList.get(0).isExceptionOccured()+", seq num="+seqNum+", last seq num="+SessionManager.mapSrcDstPairAndLastSeqNum.get(srcDstPair));
		if (itemList.size()>0 && itemList.get(0).getSessionId().equals(bean.getSessionId())) { //MUST be THIS session.
			if ((itemList.get(0).getPreSeqNum() == SessionManager.getNumFromMapSrcDstPairAndLastSeqNum(srcDstPair) && 
					!itemList.get(0).isExceptionOccured()) || itemList.get(0).getWaitingCount() > 0){
				message = setThisSessionWaitingRes(srcDstPair, bean.getSessionId());
				if (message != null) {
					mmsLog.info(logger, bean.getSessionId(), new String(message));
					itemList.remove(0);
					return message;
				}
				if (bean.getType() == MessageTypeDecider.msgType.RELAYING_TO_SERVER_SEQUENTIALLY) {
					thread = mch.asynchronizedUnicast(bean); // Execute this relaying process
					bean.retain();
				}
				
				message = rmvCurRlyFromScheduleAndWakeUpNxtRlyBlked(srcDstPair, bean.getSessionId());
				if (message != null) {
					mmsLog.info(logger, bean.getSessionId(), new String(message));
					return message;
				}
				return message;
			}
			else if (itemList.get(0).isExceptionOccured()) {
				message = ErrorCode.SEQUENTIAL_RELAYING_EXCEPTION_ERROR.getUTF8Bytes();
				
				printSessionsInSessionMng(srcDstPair);
				mmsLog.info(logger, bean.getSessionId(), ErrorCode.SEQUENTIAL_RELAYING_EXCEPTION_ERROR.toString());

				itemList.remove(0);
				return message;
			}
			
		}
		return message;
	}
	
	private void printSessionsInSessionMng (String srcDstPair) {
		List<SessionIdAndThr> itemList = SessionManager.getItemFromMapSrcDstPairAndSessionInfo(srcDstPair);
		if (itemList.size() > 0) {
			SessionIdAndThr item = itemList.get(0);
			//System.out.println("index="+0+", SessionID="+item.getSessionId()+", seqNum="+item.getSeqNum()+", waitingCount="+item.getWaitingCount()+", isExceptionOccured="+item.isExceptionOccured());
		}
		/*for (int i = 0 ; i < itemList.size() ; i++) {
			SessionIdAndThr item = itemList.get(i);
			//System.out.println("index="+i+", SessionID="+item.getSessionId()+", seqNum="+item.getSeqNum()+", waitingCount="+item.getWaitingCount()+", isExceptionOccured="+item.isExceptionOccured());
		}*/
	}
	
	private byte[] rmvCurRlyFromScheduleAndWakeUpNxtRlyBlked (String srcDstPair, String sessionId){
		
		byte[] message = null;
		List <SessionIdAndThr> listItem = SessionManager.getItemFromMapSrcDstPairAndSessionInfo(srcDstPair);
		if (listItem == null || 
				listItem.size() == 0 ||
				listItem.get(0) == null ||
				listItem.get(0).getSessionBlocker() == null ||
				SessionManager.getNumFromMapSrcDstPairAndLastSeqNum(srcDstPair) == null) { //Check null pointer exception.
			return ErrorCode.SEQUENTIAL_RELAYING_NULL_POINTER_EXCEPTION.getUTF8Bytes();
		}
		//System.out.println("Seq number="+listItem.get(0).getSeqNum());
		//System.out.println("Last seq number="+SessionManager.mapSrcDstPairAndLastSeqNum.get(srcDstPair));
		//TODO MUST be implemented. MUST awake waitingDiscardingSessionThr if it is not null.
		if (listItem.size() > 0 && listItem.get(0).getSessionId().equals(sessionId)) { 
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
			SessionManager.setNumInMapSrcDstPairAndLastSeqNum(srcDstPair, (double) listItem.get(0).getSeqNum());
			//System.out.println("Updated last seq number="+SessionManager.mapSrcDstPairAndLastSeqNum.get(srcDstPair));
			listItem.remove(0); //Remove current relaying process from the schedule. 
			if (checkNextSeq) { //Wake up next relaying process blocked if exist.
				//System.out.println("index="+0+", SessionID="+listItem.get(0).getSessionId()+", seqNum="+listItem.get(0).getSeqNum()+", waitingCount="+listItem.get(0).getWaitingCount()+", isExceptionOccured="+listItem.get(0).isExceptionOccured());
				listItem.get(0).getSessionBlocker().interrupt();
			}
		}
		else {
			return ErrorCode.SEQUENTIAL_RELAYING_LIST_EMTPY.getUTF8Bytes();
		}
		return message;
	}
	
	private byte[] setThisSessionWaitingRes (String srcDstPair, String sessionId) {
		
		byte[] message = null;
		
		List <SessionIdAndThr> listItem = SessionManager.getItemFromMapSrcDstPairAndSessionInfo(srcDstPair);
		if (listItem == null || 
				listItem.size() == 0 ||
				listItem.get(0) == null ||
				listItem.get(0).getSessionBlocker() == null ||
				SessionManager.getNumFromMapSrcDstPairAndLastSeqNum(srcDstPair) == null) { //Check null pointer exception.
			return ErrorCode.SEQUENTIAL_RELAYING_NULL_POINTER_EXCEPTION.getUTF8Bytes();
		}
		
		if (listItem.size()>0 && listItem.get(0).getSessionId().equals(sessionId)) { //This session is waiting a respond.
			listItem.get(0).setWaitingRes(true);
		}
		else {
			return ErrorCode.SEQUENTIAL_RELAYING_LIST_EMTPY.getUTF8Bytes();
		}
		return null;
	}
	
	public ConnectionThread getConnectionThread () {
		return thread;
	}
}
