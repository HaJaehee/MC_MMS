package kr.ac.kaist.message_relaying;

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

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.message_queue.MMSQueue;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

public class MessageTypeDecision {
	static final int POLLING = 1; // it means polling message 
	static final int RELAYINGTOSC = 2; // it means relaying to SC
	static final int RELAYINGTOSERVER = 3; // it means relaying to SR, IR or SP
	static final int REGISTERCLIENT = 4; // it means registering MMS client 
	static final int UNKNOWNMRN = 5; // it means unknown MRN
	static final int UNKNOWNHTTPTYPE = 6; // it means unknown http type
	static final int STATUS = 7;
	static final int LOGS = 8;
	static final int CLEANLOGS = 9;
	static final int SAVELOGS = 10;
	static final int EMPTYQUEUE = 11;
	static final int EMPTYCMDUMMY = 12;
	static final int REMOVECMENTRY = 13;
	
	int decideType(MessageParsing parser, MessageCastingHandler mch) {
		String srcMRN = parser.getSrcMRN();
		String dstMRN = parser.getDstMRN();
		HttpMethod httpMethod = parser.getHttpMethod();
		String uri = parser.getUri();
		
//    	When polling
    	if (httpMethod == HttpMethod.POST && uri.equals("/polling")) {
    		return POLLING; 
    	}
    	
//		when registering
    	else if (httpMethod == httpMethod.POST && uri.equals("/registering")) {
    		return REGISTERCLIENT;
    	}
    	
//		when logging
    	else if (MMSConfiguration.logProviding && httpMethod == HttpMethod.GET && uri.equals("/logs")){
    		return LOGS;
    	} else if (MMSConfiguration.logProviding && httpMethod == HttpMethod.GET && uri.equals("/status")){
    		return STATUS;
    	} else if (MMSConfiguration.logProviding && httpMethod == HttpMethod.GET && uri.equals("/cleanlogs")){ 
    		return CLEANLOGS;
    	} else if (MMSConfiguration.logProviding && httpMethod == HttpMethod.GET && uri.equals("/savelogs")){    		
    		return SAVELOGS;
    	} else if (MMSConfiguration.emptyQueue && httpMethod == HttpMethod.GET && uri.equals("/emptyqueue")){ 
    		return EMPTYQUEUE;
    	} else if (MMSConfiguration.emptyCMDummy && httpMethod == HttpMethod.GET && uri.equals("/emptycmdummy")){ 
    		return EMPTYCMDUMMY;
    	} else if (MMSConfiguration.removeEntryCMDummy && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/removecmentry", 0, 14)){ 
    		return REMOVECMENTRY;
    	}
    	
//    	When relaying
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
