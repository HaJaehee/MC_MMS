package kr.ac.kaist.message_relaying;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	String srcDstPair = srcMRN+"::"+dstMRN;
	private String SESSION_ID = "";
	private Thread sessionBlocker = null;
	private MMSLog mmsLog = null;
	private ConnectionThread thread = null;
	
	public MessageOrderingHandler() {
		
		initializeModule();
	}
	private void initializeModule() {
		this.mmsLog = MMSLog.getInstance();
	}
	
	public byte[] initializeAndGetError (MessageParser parser, String sessionId) {
		
		byte[] message = null;
		this.srcMRN = parser.getSrcMRN();
		this.dstMRN = parser.getDstMRN();
		this.dstIP = parser.getDstIP();
		this.dstPort = parser.getDstPort();
		this.httpMethod = parser.getHttpMethod();
		this.uri = parser.getUri();
		this.seqNum = parser.getSeqNum();
		this.srcDstPair = srcMRN+"::"+dstMRN;
		this.SESSION_ID = sessionId;
		this.sessionBlocker = new Thread();

		//System.out.println("SessionID="+this.SESSION_ID+" RELAYING_TO_SERVER_SEQUENTIALLY INIT");
		
		if (SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair) == null ) { //Initialization
			SessionManager.getMapSrcDstPairAndSessionInfo().put(srcDstPair, new SessionList<SessionIdAndThr>());	
			while (SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair) == null) {}
		}
		
		if (SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) == null ) { //Initialization
			SessionManager.getMapSrcDstPairAndLastSeqNum().put(srcDstPair, (double) -1);
			while (SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) == null) {}
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
			//THIS may cause deadlock because even "if (seqNum == 0)" statement is not completed, THIS statement may be incurred.
			//Sort messages based on seqNums of messages.
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
							message = ErrorCode.SEQUENCE_NUMBER_IS_DUPLICATED.getUTF8Bytes();
							mmsLog.info(logger, this.SESSION_ID, "Sequence number of message is duplicated.");
							return message;
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
				message = ErrorCode.SEQUENCE_NUMBER_IS_OUT_OF_ORDERED.getUTF8Bytes();
				mmsLog.info(logger, this.SESSION_ID, "Sequence number of message is out of ordered.");
				return message;
			}
			//System.out.println("index="+index+", seqNum="+seqNum+", seqNum in List="+itemList.get(0).getSeqNum());
		}
		//System.out.println("SessionID="+this.SESSION_ID+" RELAYING_TO_SERVER_SEQUENTIALLY END");
		//printSessionsInSessionMng (srcDstPair);
		
		return message;
	}
	
	
	public byte[] processMessage (MRH_MessageOutputChannel outputChannel, FullHttpRequest req, String protocol, MessageCastingHandler mch, MessageTypeDecider.msgType type) {
		byte[] message = null;
		
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
						// If this session is interrupted, process its message.
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
						message = setThisSessionWaitingRes(srcDstPair);
						if (message != null) {
							mmsLog.info(logger, this.SESSION_ID, new String(message));
							return message;
						}
						if (type == MessageTypeDecider.msgType.RELAYING_TO_SERVER_SEQUENTIALLY) {
							thread = mch.asynchronizedUnicast(outputChannel, req, dstIP, dstPort, protocol, httpMethod, srcMRN, dstMRN); // Execute this relaying process
						}
						else if (type == MessageTypeDecider.msgType.RELAYING_TO_SC_SEQUENTIALLY) {
							SeamlessRoamingHandler srh = new SeamlessRoamingHandler(this.SESSION_ID);
							srh.putSCMessage(srcMRN, dstMRN, req.content().toString(Charset.forName("UTF-8")).trim());
				    		message = "OK".getBytes(Charset.forName("UTF-8"));
						}
						message = rmvCurRlyFromScheduleAndWakeUpNxtRlyBlked(srcDstPair);
						if (message != null) {
							mmsLog.info(logger, this.SESSION_ID, new String(message));
							return message;
						}
						break;
					}
					else if (itemList.get(0).isExceptionOccured()) {
						message = ErrorCode.SEQUENTIAL_RELAYING_EXCEPTION_ERR.getUTF8Bytes();
						
						printSessionsInSessionMng(srcDstPair);
						mmsLog.info(logger, this.SESSION_ID, "Message order exception is occured. Message sequence is reset 0.");
	
						itemList.remove(0);
						break;
					}
					
				}
			}
		}
		
		itemList = SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair);

		// TODO Is there any risk for shutting down the process before replying to sender?
		// HOTFIX: Resolved a bug related to message ordering.
		if (itemList.size()>0 && itemList.get(0).getSessionId().equals(this.SESSION_ID)) { //MUST be THIS session.
			if ((itemList.get(0).getPreSeqNum() == SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) && 
					!itemList.get(0).isExceptionOccured()) || itemList.get(0).getWaitingCount() > 0){
				
				message = rmvCurRlyFromScheduleAndWakeUpNxtRlyBlked(srcDstPair);
				if (message != null) {
					mmsLog.info(logger, this.SESSION_ID, new String(message));
					return message;
				}
			}
		}

		// TODO 이 위치에 진입하면 message가 null로 설정됩니다. 적당한 할당 필요  by using Error Code 
		// 위에 꺼를 방지하기 위해 이 위치에서 thread도 널이고, message도 널이면 message에 적당한 에러 코드를 삽입하면 좋을듯
		return message;
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
	
	private byte[] rmvCurRlyFromScheduleAndWakeUpNxtRlyBlked (String srcDstPair){
		
		byte[] message = null;
		List <SessionIdAndThr> listItem = SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair);
		if (listItem == null || 
				listItem.size() == 0 ||
				listItem.get(0) == null ||
				listItem.get(0).getSessionBlocker() == null ||
				SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) == null) { //Check null pointer exception.
			return ErrorCode.SEQUENTIAL_RELAYING_NULL_POINTER_EXCEPTION.getUTF8Bytes();
		}
		//System.out.println("Seq number="+listItem.get(0).getSeqNum());
		//System.out.println("Last seq number="+SessionManager.mapSrcDstPairAndLastSeqNum.get(srcDstPair));
		//TODO MUST be implemented. MUST awake waitingDiscardingSessionThr if it is not null.
		if (listItem.size() > 0 && listItem.get(0).getSessionId().equals(this.SESSION_ID)) { 
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
			return ErrorCode.SEQUENTIAL_RELAYING_LIST_EMTPY.getUTF8Bytes();
		}
		return message;
	}
	
	private byte[] setThisSessionWaitingRes (String srcDstPair) {
		
		byte[] message = null;
		
		List <SessionIdAndThr> listItem = SessionManager.getMapSrcDstPairAndSessionInfo().get(srcDstPair);
		if (listItem == null || 
				listItem.size() == 0 ||
				listItem.get(0) == null ||
				listItem.get(0).getSessionBlocker() == null ||
				SessionManager.getMapSrcDstPairAndLastSeqNum().get(srcDstPair) == null) { //Check null pointer exception.
			return ErrorCode.SEQUENTIAL_RELAYING_NULL_POINTER_EXCEPTION.getUTF8Bytes();
		}
		
		if (listItem.size()>0 && listItem.get(0).getSessionId().equals(this.SESSION_ID)) { //This session is waiting a respond.
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
