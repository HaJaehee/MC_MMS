package kr.ac.kaist.message_relaying;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

public class MessageParsing {
	private static final String TAG = "MessageParsing";
	
	private String srcIP;
	private String srcMRN;
	private String dstIP;
	private String dstMRN;
	private int srcPort;
	private int dstPort;
	private int srcModel;
	private int dstModel;
	private String uri;
	private HttpMethod httpMethod;
	
	public MessageParsing(){
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
	}
	
	public void parsingMessage(ChannelHandlerContext ctx, FullHttpRequest req) {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
	    InetAddress inetaddress = socketAddress.getAddress();
	    srcIP = inetaddress.getHostAddress(); // IP address of client
		
//		System.out.println("MessageParsing/sourceIP:"+srcIP);
		
		srcMRN = req.headers().get("srcMRN");
		dstMRN = req.headers().get("dstMRN");
		uri = req.getUri();
		httpMethod = req.getMethod();
	}
	
	public void parsingLocationInfo(FullHttpRequest req){
		String locationInfo = req.content().toString(Charset.forName("UTF-8")).trim();
		
		srcPort = Integer.parseInt(locationInfo.split(":")[0]);
		srcModel = Integer.parseInt(locationInfo.split(":")[1]);
//		System.out.println(TAG + ":" + srcPort + "/" + srcModel);
	}
	
	public void parsingDstInfo(String dstInfo){
		dstIP = dstInfo.split(":")[0];
    	dstPort = Integer.parseInt(dstInfo.split(":")[1]);
    	dstModel = Integer.parseInt(dstInfo.split(":")[2]);
	}
	
	public String getDestinationMRN() {
		return dstMRN;
	}

	public String getSourceMRN() {
		return srcMRN;
	}

	public String getUri() {
		return uri;
	}
	
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}
	
	public String getDestinationIP() {
		return dstIP;
	}
	
	public int getDestinationPort() {
		return dstPort;
	}
	
	public int getDestinationModel() {
		return dstModel;
	}
	
	public String getSourceIP(){
		return srcIP;
	}
	
	public int getSourcePort(){
		return srcPort;
	}
	
	public int getSoruceModel(){
		return srcModel;
	}
}
