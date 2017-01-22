package kr.ac.kaist.message_relaying;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

public class MessageRelayingHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	private static final String TAG = "MessageRelayingHandler";
	
	private MessageParsing parser;
	private MessageTypeDecision typeDecider;
	private MessageOutputChannel outputChannel;
	
	private SeamlessRoamingHandler srh;
	private MessageCastingHandler mch;
	
	private void initializeSubModule() {
		parser = new MessageParsing();
		typeDecider = new MessageTypeDecision();
		outputChannel = new MessageOutputChannel();
	}
	
	private void initializeModule() {
		srh = new SeamlessRoamingHandler();
		mch = new MessageCastingHandler();
	}
	
	public MessageRelayingHandler() {
		super();
		
		initializeModule();
		initializeSubModule();
	}
	
	private void processRelaying(int type, ChannelHandlerContext ctx, FullHttpRequest req){
		String srcMRN = parser.getSourceMRN();
		String dstMRN = parser.getDestinationMRN();
		HttpMethod httpMethod = parser.getHttpMethod();
		String uri = parser.getUri();
		String dstIP = parser.getDestinationIP();
		int dstPort = parser.getDestinationPort();
		
		byte[] message = null;
		
		if (type == MessageTypeDecision.POLLING) {
			parser.parsingLocationInfo(req);
			
			String srcIP = parser.getSourceIP();
			int srcPort = parser.getSourcePort();
			int srcModel = parser.getSoruceModel();
			
			message = srh.processPollingMessage(srcMRN, srcIP, srcPort, srcModel);
		}
		else if (type == MessageTypeDecision.RELAYINGTOSC) {
			srh.putSCMessage(dstMRN, req);
    		message = "OK".getBytes();
		}
		else if (type == MessageTypeDecision.RELAYINGTOSERVER) {
        	try {
				message = outputChannel.sendMessage(req, dstIP, dstPort, httpMethod);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (type == MessageTypeDecision.UNKNOWNMRN) {
			message = "No Device having that MRN".getBytes();
		}
		else if (type == MessageTypeDecision.UNKNOWNHTTPTYPE) {
			message = "Unknown http type".getBytes();
		}
		
		outputChannel.replyToSender(ctx, message);
	}
	
//	when coming http message
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		try{
			req.retain();
			parser.parsingMessage(ctx, req);
			
//			received data by SC
//			System.out.println(req.content().toString(Charset.forName("UTF-8")).trim());
			
			int type = typeDecider.doTypeDecision(parser, mch);
			processRelaying(type, ctx, req);
			
		} finally {
            req.release();
        }
	}
}
