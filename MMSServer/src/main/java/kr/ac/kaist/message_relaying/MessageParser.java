package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : MessageParser.java
	It parses information of the message and saves it to variables. 
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-27
Version : 0.5.8
	Variable multiDstMRN is added for multicast.
Modifier : Jaehyun Park (jae519@kaist.ac.kr)
		   Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-04-23
Version : 0.7.1
	Removed RESOURCE_LEAK hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	
*/
/* -------------------------------------------------------- */

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

public class MessageParser {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageParser.class);

	private String SESSION_ID = "";
	private String srcIP = null;
	private String srcMRN = null;
	private String dstIP = null;
	private String dstMRN = null;
	private String[] multiDstMRN = null;
	private int srcPort = 0;
	private int dstPort = 0;
	private int srcModel = 0;
	private int dstModel = 0;
	private String uri = null;
	private HttpMethod httpMethod = null;
	private String svcMRN = null;
	

	MessageParser(){
		this("");
	}
	

	MessageParser(String sessionId){
		this.SESSION_ID = sessionId;
		srcIP = null;
		srcMRN = null;
		dstIP = null;
		dstMRN = null;
		uri = null;
		httpMethod = null;
		srcPort = 0;
		dstPort = 0;
		srcModel = 0;
		dstModel = 0;
		svcMRN = null;
	}
	
	void parseMessage(ChannelHandlerContext ctx, FullHttpRequest req) throws NullPointerException{
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
	    InetAddress inetaddress = socketAddress.getAddress();

	    if (inetaddress != null) {
	    	srcIP = inetaddress.getHostAddress(); // IP address of client
	    } else {
	    	throw new NullPointerException();
	    }
		srcMRN = req.headers().get("srcMRN");
		dstMRN = req.headers().get("dstMRN");
		
		uri = req.uri();
		httpMethod = req.method();
	}
	
	void parseLocInfo(FullHttpRequest req){
		String locInfo = req.content().toString(Charset.forName("UTF-8")).trim();
		
		String[] locInforms = locInfo.split(":");
		srcPort = Integer.parseInt(locInforms[0]);
		srcModel = Integer.parseInt(locInforms[1]);
		if (locInforms.length > 2) {
			svcMRN = locInforms[2];
			for ( int i = 3; i<locInforms.length; i++){
				svcMRN += ":"+locInforms[i];
			}
		}

	}
	
	void parseDstInfo(String dstInfo){
		String[] dstInforms = dstInfo.split(":");
		dstIP = dstInforms[0];
    	dstPort = Integer.parseInt(dstInforms[1]);
    	dstModel = Integer.parseInt(dstInforms[2]);
    	
	}
	void parseMultiDstInfo(String dstInfo){
		logger.debug("SessionID="+this.SESSION_ID+" Destination info="+dstInfo+".");
		String[] dstMRNs = dstInfo.substring(13).split(",");
		multiDstMRN = dstMRNs;
	}

	// HTTP Information //
	String getUri() { return uri; }
	HttpMethod getHttpMethod() { return httpMethod; }
	
	// Destination Information //
	String getDstIP() { return dstIP; }
	int getDstPort() { return dstPort; }
	String getDstMRN() { return dstMRN; }
	int getDstModel() { return dstModel; }
	
	// Destination Special Information //
	String[] getMultiDstMRN() { return multiDstMRN; }
	
	// Source Information //
	String getSrcIP(){ return srcIP; }
	int getSrcPort(){ return srcPort; }
	String getSrcMRN() { return srcMRN; }
	int getSrcModel(){ return srcModel; }
	
	// Service Information //
	String getSvcMRN (){ return svcMRN; }
}
