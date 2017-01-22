package kr.ac.kaist.message_relaying;

import io.netty.handler.codec.http.HttpMethod;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.message_queue.MMSQueue;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

public class MessageTypeDecision {
	public static final int POLLING = 1; // type is polling message 
	public static final int RELAYINGTOSC = 2; // type is relaying to SC
	public static final int RELAYINGTOSERVER = 3; // type is relaying to SR, IR or SP
	public static final int UNKNOWNMRN = 4; // type is unknown MRN
	public static final int UNKNOWNHTTPTYPE = 5; // type is unknown http type
	
	public int doTypeDecision(MessageParsing parser, MessageCastingHandler mch) {
		String srcMRN = parser.getSourceMRN();
		String dstMRN = parser.getDestinationMRN();
		HttpMethod httpMethod = parser.getHttpMethod();
		String uri = parser.getUri();
		
//    	When polling
    	if (httpMethod == HttpMethod.POST && uri.equals("/polling")) 
    		return POLLING; 
    	
//    	When Relaying
    	else if (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.GET){
    		String dstInfo = mch.requestDstInfo(dstMRN);
    		
        	if (dstInfo.equals("No"))
        		return UNKNOWNMRN;

        	parser.parsingDstInfo(dstInfo);
        	int model = parser.getDestinationModel();
        	
//        	model B (destination MSR, MIR, or MSP as servers)
        	if (model == 2)
        		return RELAYINGTOSERVER;
//        	when model A, it puts the message into the queue
        	else
        		return RELAYINGTOSC;
    	}
    	else
    		return UNKNOWNHTTPTYPE;
	}
}
