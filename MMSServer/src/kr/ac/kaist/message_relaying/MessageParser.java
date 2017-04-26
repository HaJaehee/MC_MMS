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
*/
/* -------------------------------------------------------- */

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

public class MessageParser {
	private static final String TAG = "[MessageParser] ";
	
	private String srcIP = null;
	private String srcMRN = null;
	private String dstIP = null;
	private String dstMRN = null;
	private int srcPort = 0;
	private int dstPort = 0;
	private int srcModel = 0;
	private int dstModel = 0;
	private String uri = null;
	private HttpMethod httpMethod = null;
	private String svcMRN = null;
	
	MessageParser(){
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
	
	void parseMessage(ChannelHandlerContext ctx, FullHttpRequest req) {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
	    InetAddress inetaddress = socketAddress.getAddress();
	    srcIP = inetaddress.getHostAddress(); // IP address of client
		

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
	
	String getDstMRN() {
		return dstMRN;
	}

	String getSrcMRN() {
		return srcMRN;
	}

	String getUri() {
		return uri;
	}
	
	HttpMethod getHttpMethod() {
		return httpMethod;
	}
	
	String getDstIP() {
		return dstIP;
	}
	
	int getDstPort() {
		return dstPort;
	}
	
	int getDstModel() {
		return dstModel;
	}
	
	String getSrcIP(){
		return srcIP;
	}
	
	int getSrcPort(){
		return srcPort;
	}
	
	int getSrcModel(){
		return srcModel;
	}
	
	String getSvcMRN (){
		return svcMRN;
	}
}
