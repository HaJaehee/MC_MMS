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
*/
/* -------------------------------------------------------- */

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.seamless_roaming.PollingMethodRegDummy;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

public class MessageRelayingHandler  {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageRelayingHandler.class);

	private int SESSION_ID = 0;

	private MessageParser parser = null;
	private MessageTypeDecider typeDecider = null;
	private MRH_MessageOutputChannel outputChannel = null;
	
	private SeamlessRoamingHandler srh = null;
	private MessageCastingHandler mch = null;
	
	private String protocol = "";
	
	public MessageRelayingHandler(ChannelHandlerContext ctx, FullHttpRequest req, String protocol, int sessionId) {		
		this.protocol = protocol;
		this.SESSION_ID = sessionId;
		
		initializeModule();
		initializeSubModule();
		parser.parseMessage(ctx, req);
		
		int type = typeDecider.decideType(parser, mch);
		processRelaying(type, ctx, req);
	}
	
	private void initializeModule() {
		srh = new SeamlessRoamingHandler(this.SESSION_ID);
		mch = new MessageCastingHandler(this.SESSION_ID);
	}
	
	private void initializeSubModule() {
		parser = new MessageParser(this.SESSION_ID);
		typeDecider = new MessageTypeDecider(this.SESSION_ID);
		outputChannel = new MRH_MessageOutputChannel(this.SESSION_ID);
	}

	private void processRelaying(int type, ChannelHandlerContext ctx, FullHttpRequest req){
		String srcMRN = parser.getSrcMRN();
		String dstMRN = parser.getDstMRN();
		HttpMethod httpMethod = parser.getHttpMethod();
		String uri = parser.getUri();
		String dstIP = parser.getDstIP();
		int dstPort = parser.getDstPort();

		logger.info("SessionID="+this.SESSION_ID+",srcMRN="+srcMRN+",dstMRN="+dstMRN);
		
		byte[] message = null;
		
		if (type == MessageTypeDecider.NULL_MRN) {
			message = "Error: Null MRNs.".getBytes(Charset.forName("UTF-8"));
		}
		else if (type == MessageTypeDecider.NULL_SRC_MRN) {
			message = "Error: Null source MRN.".getBytes(Charset.forName("UTF-8"));
		}
		else if (type == MessageTypeDecider.NULL_DST_MRN) {
			message = "Error: Null destination MRN.".getBytes(Charset.forName("UTF-8"));
		}
		else if (type == MessageTypeDecider.POLLING) {
			parser.parseLocInfo(req);
			
			String srcIP = parser.getSrcIP();
			int srcPort = parser.getSrcPort();
			int srcModel = parser.getSrcModel();
			String svcMRN = parser.getSvcMRN();
			
			//@Deprecated
			//message = srh.processPollingMessage(srcMRN, srcIP, srcPort, srcModel);
			SessionManager.sessionInfo.put(SESSION_ID, "p");
			MMSLog.nMsgWaitingPollClnt++;
			
			srh.processPollingMessage(outputChannel, ctx, srcMRN, srcIP, srcPort, srcModel, svcMRN);
			
			return;
		} 
		else if (type == MessageTypeDecider.RELAYING_TO_SC) {
			
			//@Deprecated
			//srh.putSCMessage(dstMRN, req);
			
			srh.putSCMessage(srcMRN, dstMRN, req.content().toString(Charset.forName("UTF-8")).trim());
    		message = "OK".getBytes(Charset.forName("UTF-8"));
		} 
		else if (type == MessageTypeDecider.RELAYING_TO_MULTIPLE_SC){
			String [] dstMRNs = parser.getMultiDstMRN();
			logger.debug("SessionID="+this.SESSION_ID+" multicast");
			for (int i = 0; i < dstMRNs.length;i++){
				srh.putSCMessage(srcMRN, dstMRNs[i], req.content().toString(Charset.forName("UTF-8")).trim());
			}
    		message = "OK".getBytes(Charset.forName("UTF-8"));
		} 
		else if (type == MessageTypeDecider.RELAYING_TO_SERVER) {
        	try {
        		if (protocol.equals("http")) {
				    message = outputChannel.sendMessage(req, dstIP, dstPort, httpMethod);
				    logger.info("SessionID="+this.SESSION_ID+" HTTP");
        		} 
        		else if (protocol.equals("https")) { 
        			message = outputChannel.secureSendMessage(req, dstIP, dstPort, httpMethod);
        			logger.info("SessionID="+this.SESSION_ID+" HTTPS");
        		} 
        		else {
        			message = "".getBytes();
        			logger.info("SessionID="+this.SESSION_ID+" No protocol");
        		}
			} 
        	catch (Exception e) {
				logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
			}
		} 
		else if (type == MessageTypeDecider.REGISTER_CLIENT) {
			parser.parseLocInfo(req);
			
			String srcIP = parser.getSrcIP();
			int srcPort = parser.getSrcPort();
			int srcModel = parser.getSrcModel();
			
			String res = mch.registerClientInfo(srcMRN, srcIP, srcPort, srcModel);
			if (res.equals("OK")){
				message = "Registering succeeded".getBytes();
			} 
			else {
				message = "Registering failed".getBytes();
			}
			
		} 
		else if (type == MessageTypeDecider.STATUS){
    		String status;
    		
			try {
				status = MMSLog.getStatus();
				message = status.getBytes(Charset.forName("UTF-8"));
			} 
			catch (UnknownHostException e) {
				logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
			} 
			catch (IOException e) {
				logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
			}
		}
		else if (type == MessageTypeDecider.EMPTY_MNSDummy) {
    		try {
				emptyMNS();
				message = "OK".getBytes(Charset.forName("UTF-8"));
			} 
    		catch (UnknownHostException e) {
				logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
			} 
    		catch (IOException e) {
				logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
			}
		} 
		else if (type == MessageTypeDecider.REMOVE_MNS_ENTRY) {
    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
    		Map<String,List<String>> params = qsd.parameters();
    		logger.info("SessionID="+this.SESSION_ID+" Remove MRN=" + params.get("mrn").get(0));
    		try {
				removeEntryMNS(params.get("mrn").get(0));
				message = "OK".getBytes(Charset.forName("UTF-8"));
			} 
    		catch (UnknownHostException e) {
				logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
			} 
    		catch (IOException e) {
				logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
			} 
		} 
		else if (type == MessageTypeDecider.POLLING_METHOD) {
			QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
    		Map<String,List<String>> params = qsd.parameters();
    		String method = params.get("method").get(0);
    		String svcMRN = params.get("svcMRN").get(0);
    		if (method != null && svcMRN != null) {
    			if (method.equals("normal")) {

    				PollingMethodRegDummy.pollingMethodReg.put(svcMRN, PollingMethodRegDummy.NORMAL_POLLING);
    				message = "OK".getBytes(Charset.forName("UTF-8"));
    				logger.warn("SessionID="+this.SESSION_ID+",svcMRN="+svcMRN+" polling method is switched to normal polling");

	    		} 
	    		else if (method.equals("long")) {

	    			PollingMethodRegDummy.pollingMethodReg.put(svcMRN, PollingMethodRegDummy.LONG_POLLING);
    				message = "OK".getBytes(Charset.forName("UTF-8"));
     				logger.warn("SessionID="+this.SESSION_ID+",svcMRN="+svcMRN+" polling method is switched to long polling");
	    		
	    		} 
    		}
    		else {
    			message = "Wrong  parameter".getBytes(Charset.forName("UTF-8"));
    		}
		} 
		else if (type == MessageTypeDecider.EMPTY_QUEUE_LOGS) {
			MMSLog.queueLogForClient.setLength(0);
			message = "OK".getBytes(Charset.forName("UTF-8"));
		}

		else if (type == MessageTypeDecider.UNKNOWN_MRN) {
			message = "No Device having that MRN".getBytes();
		} 
		else if (type == MessageTypeDecider.UNKNOWN_HTTP_TYPE) {
			message = "Unknown http type".getBytes();
		}
		
		outputChannel.replyToSender(ctx, message);
	}
	

  
  private void emptyMNS() throws UnknownHostException, IOException{ //

  	Socket MNSSocket = new Socket("localhost", 1004);
  	
  	BufferedWriter outToMNS = new BufferedWriter(
					new OutputStreamWriter(MNSSocket.getOutputStream(),Charset.forName("UTF-8")));

  	logger.info("SessionID="+this.SESSION_ID+" "+"Empty-MNS");
  	outToMNS.write("Empty-MNS:");
  	outToMNS.flush();
  	outToMNS.close();
  	MNSSocket.close();
  	
  	return;
  }
  
  private void removeEntryMNS(String mrn) throws UnknownHostException, IOException{ //
  	
  	Socket MNSSocket = new Socket("localhost", 1004);
  	
  	BufferedWriter outToMNS = new BufferedWriter(
					new OutputStreamWriter(MNSSocket.getOutputStream(),Charset.forName("UTF-8")));
  	
  	logger.info("SessionID="+this.SESSION_ID+" "+"Remove-Entry:"+mrn);
  	outToMNS.write("Remove-Entry:"+mrn);
  	outToMNS.flush();
  	outToMNS.close();
  	MNSSocket.close();
  	
  	return;
  }
  

 
}
