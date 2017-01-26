package kr.ac.kaist.message_relaying;

import io.netty.handler.codec.http.HttpMethod;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.message_queue.MMSQueue;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

public class MessageTypeDecision {
	static final int POLLING = 1; // type is polling message 
	static final int RELAYINGTOSC = 2; // type is relaying to SC
	static final int RELAYINGTOSERVER = 3; // type is relaying to SR, IR or SP
	static final int UNKNOWNMRN = 4; // type is unknown MRN
	static final int UNKNOWNHTTPTYPE = 5; // type is unknown http type
	
	int decideType(MessageParsing parser, MessageCastingHandler mch) {
		String srcMRN = parser.getSrcMRN();
		String dstMRN = parser.getDstMRN();
		HttpMethod httpMethod = parser.getHttpMethod();
		String uri = parser.getUri();
		
//    	When polling
    	if (httpMethod == HttpMethod.POST && uri.equals("/polling")) {
    		return POLLING; 
    	}
    	
//    	When Relaying
    	else if (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.GET) {
    		String dstInfo = mch.requestDstInfo(dstMRN);
    		
        	if (dstInfo.equals("No")) {
        		return UNKNOWNMRN;
        	}

        	parser.parsingDstInfo(dstInfo);
        	int model = parser.getDstModel();
        	
        	if (model == 2) {//model B (destination MSR, MIR, or MSP as servers)
        		return RELAYINGTOSERVER;
        	} else {//when model A, it puts the message into the queue
        		return RELAYINGTOSC;
        	}
    	} else {
    		return UNKNOWNHTTPTYPE;
    	}
	}
}
