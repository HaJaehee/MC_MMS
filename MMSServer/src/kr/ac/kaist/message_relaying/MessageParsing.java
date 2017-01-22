package kr.ac.kaist.message_relaying;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

public class MessageParsing {
	private String srcMRN;
	private String dstIP;
	private String dstMRN;
	private int dstPort;
	private int dstModel;
	private String uri;
	private HttpMethod httpMethod;
	
	public MessageParsing(){
		srcMRN = null;
		dstMRN = null;
		uri = null;
		httpMethod = null;
		dstIP = null;
		dstPort = 0;
		dstModel = 0;
	}
	
	public void parsingMessage(FullHttpRequest req) {
		srcMRN = req.headers().get("srcMRN");
		dstMRN = req.headers().get("dstMRN");
		uri = req.getUri();
		httpMethod = req.getMethod();
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
	
}
