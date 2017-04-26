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

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

public class MessageRelayingHandler  {
	private static final String TAG = "[MessageRelayingHandler] ";

	private MessageParser parser = null;
	private MessageTypeDecider typeDecider = null;
	private MRH_MessageOutputChannel outputChannel = null;
	
	private SeamlessRoamingHandler srh = null;
	private MessageCastingHandler mch = null;
	
	private String protocol = "";
	
	public MessageRelayingHandler(ChannelHandlerContext ctx, FullHttpRequest req, String protocol) {		
		initializeModule();
		initializeSubModule();
		this.protocol = protocol;
		
		parser.parseMessage(ctx, req);
		
		int type = typeDecider.decideType(parser, mch);
		processRelaying(type, ctx, req);
	}
	
	private void initializeModule() {
		srh = new SeamlessRoamingHandler();
		mch = new MessageCastingHandler();
	}
	
	private void initializeSubModule() {
		parser = new MessageParser();
		typeDecider = new MessageTypeDecider();
		outputChannel = new MRH_MessageOutputChannel();
	}

	private void processRelaying(int type, ChannelHandlerContext ctx, FullHttpRequest req){
		String srcMRN = parser.getSrcMRN();
		String dstMRN = parser.getDstMRN();
		HttpMethod httpMethod = parser.getHttpMethod();
		String uri = parser.getUri();
		String dstIP = parser.getDstIP();
		int dstPort = parser.getDstPort();
		if(MMSConfiguration.LOGGING)System.out.println(TAG+req.content().toString(Charset.forName("UTF-8")).trim());
		
		byte[] message = null;
		
		if (type == MessageTypeDecider.POLLING) {
			parser.parseLocInfo(req);
			
			String srcIP = parser.getSrcIP();
			int srcPort = parser.getSrcPort();
			int srcModel = parser.getSrcModel();
			String svcMRN = parser.getSvcMRN();
			
			//@Deprecated
			//message = srh.processPollingMessage(srcMRN, srcIP, srcPort, srcModel);
			
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
				if(MMSConfiguration.LOGGING)e.printStackTrace();
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
				status = getStatus();
				message = status.getBytes(Charset.forName("UTF-8"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.LOGGING)e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.LOGGING)e.printStackTrace();
			}
		} else if (type == MessageTypeDecider.LOGS) {
    		String status;
			try {
				status = getStatus();
				MMSLog.log = status + MMSLog.log;
	    		
	    		message = MMSLog.log.getBytes(Charset.forName("UTF-8"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.LOGGING)e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.LOGGING)e.printStackTrace();
			}
		} else if (type == MessageTypeDecider.SAVE_LOGS) {
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
				if(MMSConfiguration.LOGGING)e.printStackTrace();
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
				if(MMSConfiguration.LOGGING)e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.LOGGING)e.printStackTrace();
			}
		} else if (type == MessageTypeDecider.REMOVE_MNS_ENTRY) {
    		QueryStringDecoder qsd = new QueryStringDecoder(req.uri(),Charset.forName("UTF-8"));
    		Map<String,List<String>> params = qsd.parameters();
    		if(MMSConfiguration.LOGGING)System.out.println(TAG+"remove mrn: " + params.get("mrn").get(0));
    		try {
				removeEntryMNS(params.get("mrn").get(0));
				message = "OK".getBytes(Charset.forName("UTF-8"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.LOGGING)e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.LOGGING)e.printStackTrace();
			} 
		}
		else if (type == MessageTypeDecider.CLEAN_LOGS) {
    		MMSLog.MNSLog = "";
    		MMSLog.queueLog = "";
    		MMSLog.log = "";
    		message = "OK".getBytes(Charset.forName("UTF-8"));
		} else if (type == MessageTypeDecider.UNKNOWN_MRN) {
			message = "No Device having that MRN".getBytes();
		} else if (type == MessageTypeDecider.UNKNOWN_HTTP_TYPE) {
			message = "Unknown http type".getBytes();
		}
		
		outputChannel.replyToSender(ctx, message);
	}
	
//  When LOGGING MNS
	private String dumpMNS() throws UnknownHostException, IOException{ //
  	
  	//String modifiedSentence;
  	String dumpedMNS = "";
  	
  	Socket MNSSocket = new Socket("localhost", 1004);
  	
  	BufferedWriter outToMNS = new BufferedWriter(
					new OutputStreamWriter(MNSSocket.getOutputStream(),Charset.forName("UTF-8")));
  	
  	if(MMSConfiguration.LOGGING)System.out.println(TAG+"Dump-MNS:");
  	ServerSocket Sock = new ServerSocket(0);
  	int rplPort = Sock.getLocalPort();
  	if(MMSConfiguration.LOGGING)System.out.println(TAG+"Reply port : "+rplPort);
  	outToMNS.write("Dump-MNS:"+","+rplPort);
  	outToMNS.flush();
  	outToMNS.close();
  	MNSSocket.close();
  	
  	
  	Socket ReplySocket = Sock.accept();
  	BufferedReader inFromMNS = new BufferedReader(
  			new InputStreamReader(ReplySocket.getInputStream(),Charset.forName("UTF-8")));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = inFromMNS.readLine()) != null) {
			response.append(inputLine.trim());
		}
		
  	dumpedMNS = response.toString();
  	if(MMSConfiguration.LOGGING)System.out.println(TAG+"Dumped MNS: " + dumpedMNS);
  	inFromMNS.close();
  	if (dumpedMNS.equals("No"))
  		return "No MRN to IP mapping";
  	dumpedMNS = dumpedMNS.substring(15);
  	return dumpedMNS;
  }
  
  private void emptyMNS() throws UnknownHostException, IOException{ //

  	Socket MNSSocket = new Socket("localhost", 1004);
  	
  	BufferedWriter outToMNS = new BufferedWriter(
					new OutputStreamWriter(MNSSocket.getOutputStream(),Charset.forName("UTF-8")));
  	
  	if(MMSConfiguration.LOGGING)System.out.println(TAG+"Empty-MNS:");
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
  	
  	if(MMSConfiguration.LOGGING)System.out.println(TAG+"Remove-Entry:"+mrn);
  	outToMNS.write("Remove-Entry:"+mrn);
  	outToMNS.flush();
  	outToMNS.close();
  	MNSSocket.close();
  	
  	return;
  }
  
  private String getStatus ()  throws UnknownHostException, IOException{
  	
		String status = "";
		
		//@Deprecated
		/*
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
		*/
		
		status += "MMS Queue log:<br/>";
		status += MMSLog.queueLog + "<br/>";

		status += "MNS Dummy:<br/>";
		status += dumpMNS() + "<br/>";
  	
  	return status;
  }
 
}
