package kr.ac.kaist.message_relaying;

/* -------------------------------------------------------- */
/** 
File name : MessageRelayingHandler.java
	It relays messages from external components to destination in header field of the messages.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.2.00
Rev. history : 2017-02-01
	Added log providing features.
	Added locator registering features.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.message_queue.MMSQueue;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

public class MessageRelayingHandler  extends SimpleChannelInboundHandler<FullHttpRequest>{
	private static final String TAG = "MessageRelayingHandler";
	
	private MessageParsing parser = null;
	private MessageTypeDecision typeDecider = null;
	private MRH_MessageInputChannel inputChannel = null;
	private MRH_MessageOutputChannel outputChannel = null;
	
	private SeamlessRoamingHandler srh = null;
	private MessageCastingHandler mch = null;
	
	public MessageRelayingHandler() {
		super();
		
		initializeModule();
		initializeSubModule();
	}
	
	private void initializeModule() {
		srh = new SeamlessRoamingHandler();
		mch = new MessageCastingHandler();
	}
	
	private void initializeSubModule() {
		parser = new MessageParsing();
		typeDecider = new MessageTypeDecision();
		inputChannel = new MRH_MessageInputChannel();
		outputChannel = new MRH_MessageOutputChannel();
	}

	private void processRelaying(int type, ChannelHandlerContext ctx, FullHttpRequest req){
		String srcMRN = parser.getSrcMRN();
		String dstMRN = parser.getDstMRN();
		HttpMethod httpMethod = parser.getHttpMethod();
		String uri = parser.getUri();
		String dstIP = parser.getDstIP();
		int dstPort = parser.getDstPort();
		
		byte[] message = null;
		
		if (type == MessageTypeDecision.POLLING) {
			parser.parsingLocInfo(req);
			
			String srcIP = parser.getSrcIP();
			int srcPort = parser.getSrcPort();
			int srcModel = parser.getSrcModel();
			
			message = srh.processPollingMessage(srcMRN, srcIP, srcPort, srcModel);
		} else if (type == MessageTypeDecision.RELAYING_TO_SC) {
			srh.putSCMessage(dstMRN, req);
    		message = "OK".getBytes();
		} else if (type == MessageTypeDecision.RELAYING_TO_SERVER) {
        	try {
				message = outputChannel.sendMessage(req, dstIP, dstPort, httpMethod);
			} catch (Exception e) {
				if(MMSConfiguration.logging)e.printStackTrace();
			}
		} else if (type == MessageTypeDecision.REGISTER_CLIENT) {
			parser.parsingLocInfo(req);
			
			String srcIP = parser.getSrcIP();
			int srcPort = parser.getSrcPort();
			int srcModel = parser.getSrcModel();
			
			String res = mch.registerClientInfo(srcMRN, srcIP, srcPort, srcModel);
			if (res.equals("OK")){
				message = "Registering succeeded".getBytes();
			} else {
				message = "Registering failed".getBytes();
			}
			
		} else if (type == MessageTypeDecision.STATUS){
    		String status;
    		
			try {
				status = getStatus();
				message = status.getBytes(Charset.forName("UTF-8"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.logging)e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.logging)e.printStackTrace();
			}
		} else if (type == MessageTypeDecision.LOGS) {
    		String status;
			try {
				status = getStatus();
				MMSLog.log = status + MMSLog.log;
	    		
	    		message = MMSLog.log.getBytes(Charset.forName("UTF-8"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.logging)e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.logging)e.printStackTrace();
			}
		} else if (type == MessageTypeDecision.SAVE_LOGS) {
    		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    		String logfile = "./log"+timeStamp+".txt";
    		BufferedWriter wr;
			try {
				wr = new BufferedWriter(new FileWriter(logfile));
				String log = MMSLog.log.replaceAll("<br/>", "\n");
	    		wr.write(log);
	    		wr.flush();
	    		wr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.logging)e.printStackTrace();
			}
    		message = "OK".getBytes(Charset.forName("UTF-8"));
		} else if (type == MessageTypeDecision.EMPTY_QUEUE) {
			MMSQueue.queue.clear();
    		message = "OK".getBytes(Charset.forName("UTF-8"));
		} else if (type == MessageTypeDecision.EMPTY_CMDUMMY) {
    		try {
				emptyCM();
				message = "OK".getBytes(Charset.forName("UTF-8"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.logging)e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.logging)e.printStackTrace();
			}
		} else if (type == MessageTypeDecision.REMOVE_CM_ENTRY) {
    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
    		Map<String,List<String>> params = qsd.parameters();
    		if(MMSConfiguration.logging)System.out.println("remove mrn: " + params.get("mrn").get(0));
    		try {
				removeEntryCM(params.get("mrn").get(0));
				message = "OK".getBytes(Charset.forName("UTF-8"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.logging)e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.logging)e.printStackTrace();
			} 
		}
		else if (type == MessageTypeDecision.CLEAN_LOGS) {
    		MMSLog.log = "";
    		message = "OK".getBytes(Charset.forName("UTF-8"));
		} else if (type == MessageTypeDecision.UNKNOWN_MRN) {
			message = "No Device having that MRN".getBytes();
		} else if (type == MessageTypeDecision.UNKNOWN_HTTP_TYPE) {
			message = "Unknown http type".getBytes();
		}
		
		outputChannel.replyToSender(ctx, message);
	}
	
//  When logging CM
	private String dumpCM() throws UnknownHostException, IOException{ //
  	
  	//String modifiedSentence;
  	String dumpedCM = "";
  	
  	Socket CMSocket = new Socket("localhost", 1004);
  	
  	BufferedWriter outToCM = new BufferedWriter(
					new OutputStreamWriter(CMSocket.getOutputStream(),Charset.forName("UTF-8")));
  	
  	if(MMSConfiguration.logging)System.out.println("Dump-CM:");
  	ServerSocket Sock = new ServerSocket(0);
  	int rplPort = Sock.getLocalPort();
  	if(MMSConfiguration.logging)System.out.println("Reply port : "+rplPort);
  	outToCM.write("Dump-CM:"+","+rplPort);
  	outToCM.flush();
  	outToCM.close();
  	CMSocket.close();
  	
  	
  	Socket ReplySocket = Sock.accept();
  	BufferedReader inFromCM = new BufferedReader(
  			new InputStreamReader(ReplySocket.getInputStream(),Charset.forName("UTF-8")));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = inFromCM.readLine()) != null) {
			response.append(inputLine.trim());
		}
		
  	dumpedCM = response.toString();
  	if(MMSConfiguration.logging)System.out.println("Dumped CM: " + dumpedCM);
  	inFromCM.close();
  	if (dumpedCM.equals("No"))
  		return "No MRN to IP mapping";
  	dumpedCM = dumpedCM.substring(14);
  	return dumpedCM;
  }
  
  private void emptyCM() throws UnknownHostException, IOException{ //

  	Socket CMSocket = new Socket("localhost", 1004);
  	
  	BufferedWriter outToCM = new BufferedWriter(
					new OutputStreamWriter(CMSocket.getOutputStream(),Charset.forName("UTF-8")));
  	
  	if(MMSConfiguration.logging)System.out.println("Empty-CM:");
  	outToCM.write("Empty-CM:");
  	outToCM.flush();
  	outToCM.close();
  	CMSocket.close();
  	
  	return;
  }
  
  private void removeEntryCM(String mrn) throws UnknownHostException, IOException{ //
  	
  	Socket CMSocket = new Socket("localhost", 1004);
  	
  	BufferedWriter outToCM = new BufferedWriter(
					new OutputStreamWriter(CMSocket.getOutputStream(),Charset.forName("UTF-8")));
  	
  	if(MMSConfiguration.logging)System.out.println("Remove-Entry:"+mrn);
  	outToCM.write("Remove-Entry:"+","+mrn);
  	outToCM.flush();
  	outToCM.close();
  	CMSocket.close();
  	
  	return;
  }
  
  private String getStatus ()  throws UnknownHostException, IOException{
  	
		String status = "";
		
		HashMap<String, String> queue = MMSQueue.queue;
		status = status + "Message Queue:<br/>";
		Set<String> queueKeys = queue.keySet();
		Iterator<String> queueKeysIter = queueKeys.iterator();
		while (queueKeysIter.hasNext() ){
			String key = queueKeysIter.next();
			if (key==null)
				continue;
			String value = queue.get(key);
			status = status + key + "," + value + "<br/>"; 
		}
		status = status + "<br/>";

		status = status + "CM Dummy:<br/>";
		status = status + dumpCM() + "<br/>";
  	
  	return status;
  }
  
//	when coming http message
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		try{
			if(MMSConfiguration.logging)System.out.println("Message received");
			req.retain();
			parser.parsingMessage(ctx, req);
			
			int type = typeDecider.decideType(parser, mch);
			processRelaying(type, ctx, req);
		} finally {
          req.release();
      }
	}
}
