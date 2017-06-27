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
*/
/* -------------------------------------------------------- */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSStatusAutoSaver;
import kr.ac.kaist.mms_server.MMSSystemLogAutoSaver;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

public class MessageRelayingHandler  {
	private String TAG = "[MessageRelayingHandler:";
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
		this.TAG += SESSION_ID + "] ";
		
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
		if(MMSConfiguration.CONSOLE_LOGGING){
			System.out.println(TAG+"SessionID="+this.SESSION_ID+",srcMRN="+srcMRN+",dstMRN="+dstMRN);
			//System.out.println(TAG+req.content().toString(Charset.forName("UTF-8")).trim());
		}
		
		
		if(MMSConfiguration.SYSTEM_LOGGING){
			MMSLog.systemLog.append(TAG+"SessionID="+this.SESSION_ID+",srcMRN="+srcMRN+",dstMRN="+dstMRN+"\n");
			//MMSLog.systemLog.append(TAG+req.content().toString(Charset.forName("UTF-8")).trim()+"\n");
		}
		
		
		
		byte[] message = null;
		
		if (type == MessageTypeDecider.POLLING) {
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
		} else if (type == MessageTypeDecider.RELAYING_TO_SC) {
			
			//@Deprecated
			//srh.putSCMessage(dstMRN, req);
			
			srh.putSCMessage(srcMRN, dstMRN, req.content().toString(Charset.forName("UTF-8")).trim());
    		message = "OK".getBytes(Charset.forName("UTF-8"));
		} else if (type == MessageTypeDecider.RELAYING_TO_SERVER) {
        	try {
        		if (protocol.equals("http")) {
				    message = outputChannel.sendMessage(req, dstIP, dstPort, httpMethod);
        		} else { //protocol.equals("https")
        			message = outputChannel.secureSendMessage(req, dstIP, dstPort, httpMethod);
        		}
			} catch (Exception e) {
				if(MMSConfiguration.CONSOLE_LOGGING){
					System.out.print(TAG);
					e.printStackTrace();
				}
				if(MMSConfiguration.SYSTEM_LOGGING){
					MMSLog.systemLog.append(TAG+"Exception\n");
				}
				
			}
		} else if (type == MessageTypeDecider.REGISTER_CLIENT) {
			parser.parseLocInfo(req);
			
			String srcIP = parser.getSrcIP();
			int srcPort = parser.getSrcPort();
			int srcModel = parser.getSrcModel();
			
			String res = mch.registerClientInfo(srcMRN, srcIP, srcPort, srcModel);
			if (res.equals("OK")){
				message = "Registering succeeded".getBytes();
			} else {
				message = "Registering failed".getBytes();
			}
			
		} else if (type == MessageTypeDecider.STATUS){
    		String status;
    		
			try {
				status = MMSLog.getStatus();
				message = status.getBytes(Charset.forName("UTF-8"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.CONSOLE_LOGGING){
					System.out.print(TAG);
					e.printStackTrace();
				}
				if(MMSConfiguration.SYSTEM_LOGGING){
					MMSLog.systemLog.append(TAG+"UnknownHostException\n");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.CONSOLE_LOGGING){
					System.out.print(TAG);
					e.printStackTrace();
				}
				if(MMSConfiguration.SYSTEM_LOGGING){
					MMSLog.systemLog.append(TAG+"IOException\n");
				}
				
			}
		} else if (type == MessageTypeDecider.LOGS) {
    		String status;
			try {
				status = MMSLog.getStatus();
				MMSLog.log = status + MMSLog.log;
	    		
	    		message = MMSLog.log.getBytes(Charset.forName("UTF-8"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.CONSOLE_LOGGING){
					System.out.print(TAG);
					e.printStackTrace();
				}
				if(MMSConfiguration.SYSTEM_LOGGING){
					MMSLog.systemLog.append(TAG+"UnknownHostException\n");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.CONSOLE_LOGGING){
					System.out.print(TAG);
					e.printStackTrace();
				}
				if(MMSConfiguration.SYSTEM_LOGGING){
					MMSLog.systemLog.append(TAG+"IOException\n");
				}
			}
		} else if (type == MessageTypeDecider.SAVE_LOGS) {
    		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    		String logfile = "./log_"+timeStamp+".txt";
    		BufferedWriter wr;
			try {
				wr = new BufferedWriter(new FileWriter(logfile));
				String log = MMSLog.log.replaceAll("<br/>", "\n");
	    		wr.write(log);
	    		wr.flush();
	    		wr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.CONSOLE_LOGGING){
					System.out.print(TAG);
					e.printStackTrace();
				}
				if(MMSConfiguration.SYSTEM_LOGGING){
					MMSLog.systemLog.append(TAG+"IOException\n");
				}
			}
    		message = "OK".getBytes(Charset.forName("UTF-8"));
		} /*else if (type == MessageTypeDecision.EMPTY_QUEUE) {
			MMSQueue.queue.clear();
    		message = "OK".getBytes(Charset.forName("UTF-8"));
		} */else if (type == MessageTypeDecider.EMPTY_MNSDummy) {
    		try {
				emptyMNS();
				message = "OK".getBytes(Charset.forName("UTF-8"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.CONSOLE_LOGGING){
					System.out.print(TAG);
					e.printStackTrace();
				}
				if(MMSConfiguration.SYSTEM_LOGGING){
					MMSLog.systemLog.append(TAG+"UnknownHostException\n");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.CONSOLE_LOGGING){
					System.out.print(TAG);
					e.printStackTrace();
				}
				if(MMSConfiguration.SYSTEM_LOGGING){
					MMSLog.systemLog.append(TAG+"IOException\n");
				}
				
			}
		} else if (type == MessageTypeDecider.REMOVE_MNS_ENTRY) {
    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
    		Map<String,List<String>> params = qsd.parameters();
    		if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Remove MRN: " + params.get("mrn").get(0));
    		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Remove MRN: " + params.get("mrn").get(0)+"\n");
    		try {
				removeEntryMNS(params.get("mrn").get(0));
				message = "OK".getBytes(Charset.forName("UTF-8"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.CONSOLE_LOGGING){
					System.out.print(TAG);
					e.printStackTrace();
				}
				if(MMSConfiguration.SYSTEM_LOGGING){
					MMSLog.systemLog.append(TAG+"UnknownHostException\n");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.CONSOLE_LOGGING){
					System.out.print(TAG);
					e.printStackTrace();
				}
				if(MMSConfiguration.SYSTEM_LOGGING){
					MMSLog.systemLog.append(TAG+"IOException\n");
				}
				
			} 
		}
		else if (type == MessageTypeDecider.CLEAN_LOGS) {
    		MMSLog.MNSLog = "";
    		MMSLog.queueLogForClient.setLength(0);
    		MMSLog.log = "";
    		message = "OK".getBytes(Charset.forName("UTF-8"));
		} else if (type == MessageTypeDecider.AUTO_SAVE_STATUS_ON){
			if (MMSConfiguration.AUTO_SAVE_STATUS) {
				message = "Is already on".getBytes(Charset.forName("UTF-8"));
			} else {
				MMSConfiguration.AUTO_SAVE_STATUS = true;
				if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Auto save status on");
	    		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Auto save status on\n");
				if (!MMSConfiguration.AUTO_SAVE_STATUS_THREAD.isAlive()){
					MMSConfiguration.AUTO_SAVE_STATUS_THREAD = new MMSStatusAutoSaver();
				}
				message = "OK".getBytes(Charset.forName("UTF-8"));
			}
		} else if (type == MessageTypeDecider.AUTO_SAVE_STATUS_OFF){
			if (!MMSConfiguration.AUTO_SAVE_STATUS) {
				message = "Is already off".getBytes(Charset.forName("UTF-8"));
			} else {
				MMSConfiguration.AUTO_SAVE_STATUS = false;
				if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Auto save status off");
	    		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Auto save status off\n");
	    		if (MMSConfiguration.AUTO_SAVE_STATUS_THREAD.isAlive()){
					MMSConfiguration.AUTO_SAVE_STATUS_THREAD.interrupt();
				}
				message = "OK, auto_save_status_thread is going to stop.".getBytes(Charset.forName("UTF-8"));
			}
		} else if (type == MessageTypeDecider.AUTO_SAVE_SYSTEM_LOG_ON){
			if (MMSConfiguration.AUTO_SAVE_SYSTEM_LOG) {
				message = "Is already on".getBytes(Charset.forName("UTF-8"));
			} else {
				MMSConfiguration.AUTO_SAVE_SYSTEM_LOG = true;
				if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Auto save system log on");
	    		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Auto save system log on\n");
				if (!MMSConfiguration.AUTO_SAVE_SYSTEM_LOG_THREAD.isAlive()){
					MMSConfiguration.AUTO_SAVE_SYSTEM_LOG_THREAD = new MMSSystemLogAutoSaver();
				} 
				message = "OK".getBytes(Charset.forName("UTF-8"));
			}
		} else if (type == MessageTypeDecider.AUTO_SAVE_SYSTEM_LOG_OFF){
			if (!MMSConfiguration.AUTO_SAVE_SYSTEM_LOG) {
				message = "Is already off".getBytes(Charset.forName("UTF-8"));
			} else {
				MMSConfiguration.AUTO_SAVE_SYSTEM_LOG = false;
				if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Auto save system log off");
	    		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Auto save system log off\n");
	    		if (MMSConfiguration.AUTO_SAVE_SYSTEM_LOG_THREAD.isAlive()){
					MMSConfiguration.AUTO_SAVE_SYSTEM_LOG_THREAD.interrupt();
				} 
				message = "OK, auto_save_system_log_thread is going to stop.".getBytes(Charset.forName("UTF-8"));
			}
		} else if (type == MessageTypeDecider.AUTO_SAVE_STATUS_INTERVAL){
			QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
    		Map<String,List<String>> params = qsd.parameters();
    		try {
    			long interval = Long.parseLong(params.get("interval").get(0));
    			if (interval >= 30000) {
    				MMSConfiguration.AUTO_SAVE_STATUS_INTERVAL = interval;
    				if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Auto save status interval: " + interval+ "(ms)");
    	    		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Auto save status interval: " + interval+"(ms)\n");
    	    		if(MMSConfiguration.AUTO_SAVE_STATUS && MMSConfiguration.AUTO_SAVE_STATUS_THREAD.isAlive()){
	    	    		MMSConfiguration.AUTO_SAVE_STATUS_THREAD.interrupt();
	    	    		MMSConfiguration.AUTO_SAVE_STATUS_THREAD = new MMSStatusAutoSaver();
    	    		}
    				message = "OK".getBytes(Charset.forName("UTF-8")); 
    			} else {
    				message = "Interval must be same or larger than 30000(ms).".getBytes(Charset.forName("UTF-8")); 
    			}
    		} catch (NumberFormatException e){
    			// TODO Auto-generated catch block
				if(MMSConfiguration.CONSOLE_LOGGING){
					System.out.print(TAG);
					e.printStackTrace();
				}
				if(MMSConfiguration.SYSTEM_LOGGING){
					MMSLog.systemLog.append(TAG+"NumberFormatException\n");
				}
				message = "NumberFormatException".getBytes(Charset.forName("UTF-8")); 
    		}
    		
			
		} else if (type == MessageTypeDecider.AUTO_SAVE_SYSTEM_LOG_INTERVAL){
			QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
    		Map<String,List<String>> params = qsd.parameters();
    		try {
    			long interval = Long.parseLong(params.get("interval").get(0));
    			if (interval >= 30000) {
    				MMSConfiguration.AUTO_SAVE_SYSTEM_LOG_INTERVAL = interval;
    				if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Auto save system log interval: " + interval + "(ms)");
    	    		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Auto save system log interval: " + interval+"(ms)\n");
    	    		if(MMSConfiguration.AUTO_SAVE_SYSTEM_LOG && MMSConfiguration.AUTO_SAVE_SYSTEM_LOG_THREAD.isAlive()){
	    	    		MMSConfiguration.AUTO_SAVE_SYSTEM_LOG_THREAD.interrupt();
	    	    		MMSConfiguration.AUTO_SAVE_SYSTEM_LOG_THREAD = new MMSSystemLogAutoSaver();
    	    		}
    				message = "OK".getBytes(Charset.forName("UTF-8")); 
    			} else {
    				message = "Interval must be same or larger than 30000(ms).".getBytes(Charset.forName("UTF-8")); 
    			}
    		} catch (NumberFormatException e){
    			// TODO Auto-generated catch block
				if(MMSConfiguration.CONSOLE_LOGGING){
					System.out.print(TAG);
					e.printStackTrace();
				}
				if(MMSConfiguration.SYSTEM_LOGGING){
					MMSLog.systemLog.append(TAG+"NumberFormatException\n");
				}
				message = "NumberFormatException".getBytes(Charset.forName("UTF-8")); 
    		}
		} else if (type == MessageTypeDecider.CONSOLE_LOGGING_ON) {
			if (MMSConfiguration.CONSOLE_LOGGING) {
				message = "Is already on".getBytes(Charset.forName("UTF-8"));
			} else {
				MMSConfiguration.CONSOLE_LOGGING = true;
				message = "OK".getBytes(Charset.forName("UTF-8"));
			}
		} else if (type == MessageTypeDecider.CONSOLE_LOGGING_OFF) {
			if (!MMSConfiguration.CONSOLE_LOGGING) {
				message = "Is already off".getBytes(Charset.forName("UTF-8"));
			} else {
				MMSConfiguration.CONSOLE_LOGGING = false;
				message = "OK".getBytes(Charset.forName("UTF-8"));
			}
		} else if (type == MessageTypeDecider.WEB_LOG_PROVIDING_ON) {
			if (MMSConfiguration.WEB_LOG_PROVIDING) {
				message = "Is already on".getBytes(Charset.forName("UTF-8"));
			} else {
				MMSConfiguration.WEB_LOG_PROVIDING = true;
				message = "OK".getBytes(Charset.forName("UTF-8"));
			}
		} else if (type == MessageTypeDecider.WEB_LOG_PROVIDING_OFF) {
			if (!MMSConfiguration.WEB_LOG_PROVIDING) {
				message = "Is already off".getBytes(Charset.forName("UTF-8"));
			} else {
				MMSConfiguration.WEB_LOG_PROVIDING = false;
				message = "OK".getBytes(Charset.forName("UTF-8"));
			}
		}
		
		
		
		
		
		else if (type == MessageTypeDecider.UNKNOWN_MRN) {
			message = "No Device having that MRN".getBytes();
		} else if (type == MessageTypeDecider.UNKNOWN_HTTP_TYPE) {
			message = "Unknown http type".getBytes();
		}
		
		outputChannel.replyToSender(ctx, message);
	}
	

  
  private void emptyMNS() throws UnknownHostException, IOException{ //

  	Socket MNSSocket = new Socket("localhost", 1004);
  	
  	BufferedWriter outToMNS = new BufferedWriter(
					new OutputStreamWriter(MNSSocket.getOutputStream(),Charset.forName("UTF-8")));
  	
  	if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Empty-MNS");
  	if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Empty-MNS\n");
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
  	
  	if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Remove-Entry:"+mrn);
  	if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Remove-Entry:"+mrn+"\n");
  	outToMNS.write("Remove-Entry:"+mrn);
  	outToMNS.flush();
  	outToMNS.close();
  	MNSSocket.close();
  	
  	return;
  }
  

 
}
