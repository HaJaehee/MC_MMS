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

Rev. history : 2018-07-10
Version : 0.7.2
	Fixed unsecure codes.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
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
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;
import kr.ac.kaist.seamless_roaming.PollingMethodRegDummy;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;


public class MessageRelayingHandler  {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageRelayingHandler.class);

	private String SESSION_ID = "";

	private MessageParser parser = null;
	private MessageTypeDecider typeDecider = null;
	private MRH_MessageOutputChannel outputChannel = null;
	
	private SeamlessRoamingHandler srh = null;
	private MessageCastingHandler mch = null;
	
	private MMSLog mmsLog = null;
	private MMSLogForDebug mmsLogForDebug = null;
	
	private String protocol = "";
	
	public MessageRelayingHandler(ChannelHandlerContext ctx, FullHttpRequest req, String protocol, String sessionId) {		
		this.protocol = protocol;
		this.SESSION_ID = sessionId;
		
		initializeModule();
		initializeSubModule();
		parser.parseMessage(ctx, req);
		
		MessageTypeDecider.msgType type = null;
		try {
			type = typeDecider.decideType(parser, mch);
		} 
		catch (ParseException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
		}

		
		
		processRelaying(type, ctx, req);
		
	}
	
	private void initializeModule() {
		srh = new SeamlessRoamingHandler(this.SESSION_ID);
		mch = new MessageCastingHandler(this.SESSION_ID);
		mmsLog = MMSLog.getInstance();
		mmsLogForDebug = MMSLogForDebug.getInstance();
	}
	
	private void initializeSubModule() {
//		parser = new MessageParser(this.SESSION_ID);
		parser = new MessageParser();
		typeDecider = new MessageTypeDecider(this.SESSION_ID);
		outputChannel = new MRH_MessageOutputChannel(this.SESSION_ID);
	}

	private void processRelaying(MessageTypeDecider.msgType type, ChannelHandlerContext ctx, FullHttpRequest req){
		
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
			
			try {
				mmsLogForDebug.addSessionId(srcMRN, this.SESSION_ID);
			}
			catch (NullPointerException e) {
				logger.info("SessionID="+this.SESSION_ID+" MMSLogForDebug problem detected MRN="+srcMRN+".");
				mmsLogForDebug.removeMrn(srcMRN);
				mmsLogForDebug.addMrn(srcMRN);
				mmsLogForDebug.addSessionId(srcMRN, this.SESSION_ID);
			}

			try {
				mmsLogForDebug.addSessionId(dstMRN, this.SESSION_ID);
			}
			catch (NullPointerException e) {
				logger.info("SessionID="+this.SESSION_ID+" MMSLogForDebug problem detected MRN="+dstMRN+".");
				mmsLogForDebug.removeMrn(dstMRN);
				mmsLogForDebug.addMrn(dstMRN);
				mmsLogForDebug.addSessionId(dstMRN, this.SESSION_ID);
			}
			
			if (type != MessageTypeDecider.msgType.REALTIME_LOG) {
				logger.info("SessionID="+this.SESSION_ID+" In header, srcMRN="+srcMRN+", dstMRN="+dstMRN+".");
				if(MMSConfiguration.WEB_LOG_PROVIDING) {
					String log = "SessionID="+this.SESSION_ID+" In header, srcMRN="+srcMRN+", dstMRN="+dstMRN+".";
					mmsLog.addBriefLogForStatus(log);
					mmsLogForDebug.addLog(this.SESSION_ID, log);
				}
			
			
				logger.trace("SessionID="+this.SESSION_ID+" Payload="+StringEscapeUtils.escapeXml(req.content().toString(Charset.forName("UTF-8")).trim()));	
				if(MMSConfiguration.WEB_LOG_PROVIDING&&logger.isTraceEnabled()) {
					String log = "SessionID="+this.SESSION_ID+" Payload="+StringEscapeUtils.escapeXml(req.content().toString(Charset.forName("UTF-8")).trim());
					mmsLog.addBriefLogForStatus(log);
					mmsLogForDebug.addLog(this.SESSION_ID, log);
				}
			}
			
			
			
			if (type == MessageTypeDecider.msgType.NULL_MRN) {
				message = "Error: Null MRNs.".getBytes(Charset.forName("UTF-8"));
			}
			else if (type == MessageTypeDecider.msgType.NULL_SRC_MRN) {
				message = "Error: Null source MRN.".getBytes(Charset.forName("UTF-8"));
			}
			else if (type == MessageTypeDecider.msgType.NULL_DST_MRN) {
				message = "Error: Null destination MRN.".getBytes(Charset.forName("UTF-8"));
			}
			else if (type == MessageTypeDecider.msgType.POLLING) {
				parser.parseLocInfo(req);
				
				int srcPort = parser.getSrcPort();
				String srcModel = parser.getSrcModel();
				String svcMRN = parser.getSvcMRN();
			
				try {
					mmsLogForDebug.addSessionId(svcMRN, this.SESSION_ID);
				}
				catch (NullPointerException e){
					logger.info("SessionID="+this.SESSION_ID+" MMSLogForDebug problem detected with MRN="+svcMRN+".");
					mmsLogForDebug.removeMrn(svcMRN);
					mmsLogForDebug.addMrn(svcMRN);
					mmsLogForDebug.addSessionId(svcMRN, this.SESSION_ID);
				}
				
				if(MMSConfiguration.WEB_LOG_PROVIDING) {
					if(mmsLogForDebug.isItsLogListEmtpy(this.SESSION_ID)) {
						mmsLogForDebug.addLog(this.SESSION_ID, "SessionID="+this.SESSION_ID+" In header, srcMRN="+srcMRN+", dstMRN="+dstMRN+".");
						if(logger.isTraceEnabled()) {
							mmsLogForDebug.addLog(this.SESSION_ID, "SessionID="+this.SESSION_ID+" Payload="+StringEscapeUtils.escapeXml(req.content().toString(Charset.forName("UTF-8")).trim()));
						}
					}
				}

				srh.processPollingMessage(outputChannel, ctx, srcMRN, srcIP, srcPort, srcModel, svcMRN);
				
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
			
			else if (type == MessageTypeDecider.msgType.RELAYING_TO_SERVER) {
	        	message = mch.unicast(outputChannel, req, dstIP, dstPort, protocol, httpMethod);
			} 
			else if (type == MessageTypeDecider.msgType.GEOCASTING) {
				
				JSONArray geoDstInfo = parser.getGeoDstInfo();
				message = mch.geocast(outputChannel, req, srcMRN, geoDstInfo, protocol, httpMethod);
				
			}
			// TODO this condition has to be deprecated.
			else if (type == MessageTypeDecider.msgType.REGISTER_CLIENT) {
				parser.parseLocInfo(req);
				
				
				int srcPort = parser.getSrcPort();
				String srcModel = parser.getSrcModel();
				
				String res = mch.registerClientInfo(srcMRN, srcIP, srcPort, srcModel);
				if (res.equals("OK")){
					message = "Registering succeeded".getBytes();
				} 
				else {
					message = "Registering failed".getBytes();
				}
				
			} 
			else if (type == MessageTypeDecider.msgType.STATUS){
	    		String status;
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
				if(MMSConfiguration.WEB_LOG_PROVIDING) {
					String log = "SessionID="+this.SESSION_ID+" Get MMS status and logs.";
					logger.info(log);
					mmsLog.addBriefLogForStatus(log);
				}
	    		if (params.get("mrn") == null) {
	    			try {
						status = mmsLog.getStatus("");
						message = status.getBytes(Charset.forName("UTF-8"));
					} 
					catch (UnknownHostException e) {
						logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
					} 
					catch (IOException e) {
						logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
					}
	    		}
	    		else {
	
					try {
	    				status = mmsLog.getStatus(params.get("mrn").get(0));
						message = status.getBytes(Charset.forName("UTF-8"));
					} 
					catch (UnknownHostException e) {
						logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
					} 
					catch (IOException e) {
						logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
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
	    			message = "Wrong parameter".getBytes(Charset.forName("UTF-8"));
	    		}
				
				message = (callback+"("+realtimeLog+")").getBytes(Charset.forName("UTF-8"));
			}
			else if (type == MessageTypeDecider.msgType.ADD_ID_IN_REALTIME_LOG_IDS) {
				
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		if (params.get("id") != null) {
	    			mmsLog.addIdToBriefRealtimeLogEachIDs(params.get("id").get(0));
	    			logger.warn("SessionID="+this.SESSION_ID+" Added an ID using realtime log service="+params.get("id").get(0)+".");
	    			message = "OK".getBytes(Charset.forName("UTF-8"));
	    		}
	    		else {
	    			message = "Wrong parameter".getBytes(Charset.forName("UTF-8"));
	    		}
			}
			else if (type == MessageTypeDecider.msgType.REMOVE_ID_IN_REALTIME_LOG_IDS) {
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		if (params.get("id") != null) {
	    			mmsLog.removeIdFromBriefRealtimeLogEachIDs(params.get("id").get(0));
	    			logger.warn("SessionID="+this.SESSION_ID+" Removed an ID using realtime log service="+params.get("id").get(0)+".");
	    			message = "OK".getBytes(Charset.forName("UTF-8"));
	    		}
	    		else {
	    			message = "Wrong parameter".getBytes(Charset.forName("UTF-8"));
	    		}
			}
			else if (type == MessageTypeDecider.msgType.ADD_MRN_BEING_DEBUGGED) {
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		if (params.get("mrn")!=null) {
	    			String mrn = params.get("mrn").get(0);
	    			mmsLogForDebug.addMrn(mrn);
	    			logger.warn("SessionID="+this.SESSION_ID+" Added a MRN being debugged="+mrn+".");
	    			message = "OK".getBytes(Charset.forName("UTF-8"));
	    		}
	    		else {
	    			message = "Wrong parameter".getBytes(Charset.forName("UTF-8"));
	    		}
			}
			else if (type == MessageTypeDecider.msgType.REMOVE_MRN_BEING_DEBUGGED) {
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		if (params.get("mrn")!=null) {
	    			String mrn = params.get("mrn").get(0);
	    			mmsLogForDebug.removeMrn(mrn);
	    			logger.warn("SessionID="+this.SESSION_ID+" Removed debug MRN="+mrn+".");
	    			message = "OK".getBytes(Charset.forName("UTF-8"));
	    		}
	    		else {
	    			message = "Wrong parameter".getBytes(Charset.forName("UTF-8"));
	    		}
			}
	
			else if (type == MessageTypeDecider.msgType.REMOVE_MNS_ENTRY) {
	    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		logger.warn("SessionID="+this.SESSION_ID+" Remove MRN=" + params.get("mrn").get(0)+".");
	    		if (params.get("mrn")!=null && !params.get("mrn").get(0).equals(MMSConfiguration.MMS_MRN)) {
	    			try {
						removeEntryMNS(params.get("mrn").get(0));
						message = "OK".getBytes(Charset.forName("UTF-8"));
					} 
		    		catch (UnknownHostException e) {
						logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
					} 
		    		catch (IOException e) {
						logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
					} 
	    		}
	    		else {
					message = "Wrong parameter".getBytes(Charset.forName("UTF-8"));
				}
			} 
			else if (type == MessageTypeDecider.msgType.ADD_MNS_ENTRY) {
				QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
				Map<String,List<String>> params = qsd.parameters();
				logger.warn("SessionID="+this.SESSION_ID+" Add MRN=" + params.get("mrn").get(0) + " IP=" + params.get("ip").get(0) + " Port=" + params.get("port").get(0) + " Model=" + params.get("model").get(0)+".");
				if (params.get("mrn")!=null && !params.get("mrn").get(0).equals(MMSConfiguration.MMS_MRN)) {
					try {
						addEntryMNS(params.get("mrn").get(0), params.get("ip").get(0), params.get("port").get(0), params.get("model").get(0));
						message = "OK".getBytes(Charset.forName("UTF-8"));
					}
					catch (UnknownHostException e) {
						logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
					} 
		    		catch (IOException e) {
						logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
					} 
				}
				else {
					message = "Wrong parameter.".getBytes(Charset.forName("UTF-8"));
				}
			}
			else if (type == MessageTypeDecider.msgType.POLLING_METHOD) {
				QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
	    		Map<String,List<String>> params = qsd.parameters();
	    		if (params.get("method")==null || params.get("svcMRN")==null) {
	    			message = "Wrong parameter.".getBytes(Charset.forName("UTF-8"));
	    		}
	    		else {
		    		String method = params.get("method").get(0);
		    		String svcMRN = params.get("svcMRN").get(0);
		    		if (method != null && svcMRN != null && !svcMRN.equals(MMSConfiguration.MMS_MRN)) {
		    			if (method.equals("normal")) {
		
		    				PollingMethodRegDummy.pollingMethodReg.put(svcMRN, PollingMethodRegDummy.NORMAL_POLLING);
		    				message = "OK".getBytes(Charset.forName("UTF-8"));
		    				logger.warn("SessionID="+this.SESSION_ID+" svcMRN="+svcMRN+" polling method is switched to normal polling.");
		
			    		} 
			    		else if (method.equals("long")) {
		
			    			PollingMethodRegDummy.pollingMethodReg.put(svcMRN, PollingMethodRegDummy.LONG_POLLING);
		    				message = "OK".getBytes(Charset.forName("UTF-8"));
		     				logger.warn("SessionID="+this.SESSION_ID+" svcMRN="+svcMRN+" polling method is switched to long polling.");
			    		
			    		} 
			    		else if (method.equals("remove")) {
			    			
			    			PollingMethodRegDummy.pollingMethodReg.remove(svcMRN);
		    				message = "OK".getBytes(Charset.forName("UTF-8"));
		     				logger.warn("SessionID="+this.SESSION_ID+" svcMRN="+svcMRN+" polling method is removed.");
			    		
			    		}
		    		}
		    		else {
		    			message = "Wrong parameter".getBytes(Charset.forName("UTF-8"));
		    		}
	    		}
			} 
			/*
			else if (type == MessageTypeDecider.EMPTY_QUEUE_LOGS) {
				MMSLog.setLength(0);
				message = "OK".getBytes(Charset.forName("UTF-8"));
			}*/
			else if (type == MessageTypeDecider.msgType.DST_MRN_IS_THIS_MMS_MRN) {
				message = "Hello, MMS!".getBytes();
			}
			else if (type == MessageTypeDecider.msgType.SRC_MRN_IS_THIS_MMS_MRN) {
				message = "You are not me.".getBytes();
			}
			else if (type == MessageTypeDecider.msgType.UNKNOWN_MRN) {
				message = "No Device having that MRN.".getBytes();
			} 
			
		} catch (NullPointerException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
		} finally {
			if (type != MessageTypeDecider.msgType.POLLING) {
				if (message == null) {
					message = "INVALID MESSAGE.".getBytes();
					logger.info("SessionID="+this.SESSION_ID+" "+"INVALID MESSAGE.");
					outputChannel.replyToSender(ctx, message, isRealtimeLog, 400);
				}
				else {
					outputChannel.replyToSender(ctx, message, isRealtimeLog);
			
				}
			}
		}
	}
	
/*
//This method will be
  @Deprecated
  private void emptyMNS() throws UnknownHostException, IOException{ //

  	Socket MNSSocket = new Socket("localhost", 1004);
  	OutputStreamWriter osw = new OutputStreamWriter(MNSSocket.getOutputStream(),Charset.forName("UTF-8"));
  	BufferedWriter outToMNS = new BufferedWriter(osw);

  	logger.info("SessionID="+this.SESSION_ID+" "+"Empty-MNS.");
  	outToMNS.write("Empty-MNS:");
  	outToMNS.flush();
  	osw.close();
  	outToMNS.close();
  	MNSSocket.close();
  	
  	return;
  }
  */
  
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

		  MNSSocket = new Socket("127.0.0.1", 1004);
		  MNSSocket.setSoTimeout(5000);
		  pw = new PrintWriter(MNSSocket.getOutputStream());
		  isr = new InputStreamReader(MNSSocket.getInputStream());
		  br = new BufferedReader(isr);
		  String inputLine = null;
		  StringBuffer response = new StringBuffer();


		  logger.warn("SessionID="+this.SESSION_ID+" Remove-Entry="+mrn+".");

		  pw.println("Remove-Entry:"+mrn);
		  pw.flush();
		  if (!MNSSocket.isOutputShutdown()) {
			  MNSSocket.shutdownOutput();
		  }


		  while ((inputLine = br.readLine()) != null) {
			  response.append(inputLine);
		  }


		  queryReply = response.toString();
		  logger.trace("SessionID="+this.SESSION_ID+" From server=" + queryReply+".");


	  } catch (UnknownHostException e) {
		  logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
		  
	  } catch (IOException e) {
		  logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
		  
	  } finally {
		  if (pw != null) {
			  pw.close();
		  }
		  if (isr != null) {
			  try {
				  isr.close();
			  } catch (IOException e) {
				  logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
			  }
		  }
		  if (br != null) {
			  try {
				  br.close();
			  } catch (IOException e) {
				  logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
			  }
		  }
		  if (MNSSocket != null) {
			  try {
				  MNSSocket.close();
			  } catch (IOException e) {
				  logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
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

		  MNSSocket = new Socket("127.0.0.1", 1004);
		  MNSSocket.setSoTimeout(5000);
		  pw = new PrintWriter(MNSSocket.getOutputStream());
		  isr = new InputStreamReader(MNSSocket.getInputStream());
		  br = new BufferedReader(isr);
		  String inputLine = null;
		  StringBuffer response = new StringBuffer();


		  logger.warn("SessionID="+this.SESSION_ID+" Add-Entry="+mrn+".");

		  pw.println("Add-Entry:"+mrn+","+ip+","+port+","+model);
		  pw.flush();
		  if (!MNSSocket.isOutputShutdown()) {
			  MNSSocket.shutdownOutput();
		  }


		  while ((inputLine = br.readLine()) != null) {
			  response.append(inputLine);
		  }


		  queryReply = response.toString();
		  logger.trace("SessionID="+this.SESSION_ID+" From server=" + queryReply+".");

	  } catch (UnknownHostException e) {
		  logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
		 
	  } catch (IOException e) {
		  logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
		  
	  } finally {
		  if (pw != null) {
			  pw.close();
		  }
		  if (isr != null) {
			  try {
				  isr.close();
			  } catch (IOException e) {
				  logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
			  }
		  }
		  if (br != null) {
			  try {
				  br.close();
			  } catch (IOException e) {
				  logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
			  }
		  }
		  if (MNSSocket != null) {
			  try {
				  MNSSocket.close();
			  } catch (IOException e) {
				  logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
			  }
		  }
	  }
	  return;
  }
  
  
}
