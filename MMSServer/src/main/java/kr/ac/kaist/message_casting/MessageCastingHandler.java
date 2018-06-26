package kr.ac.kaist.message_casting;
/* -------------------------------------------------------- */
/** 
File name : MessageCastingHandler.java
	If dstMRN in header field means multiple destinations such as geocast or multicast, 
	it relays messages to multiple destinations.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-02-01
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-29
Version : 0.5.3
	Added system log features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-27
Version : 0.5.8
	Variable requestDstInfo is changed to parse multiple MRN case.
Modifier : Jaehyun Park (jae519@kaist.ac.kr)


Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-04-23
Version : 0.7.1
	Removed NULL_RETURN hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-06-06
Version : 0.7.1
	Added geocasting features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-06-26
Version : 0.7.1
	Moved jobs related to the casting from MessageRelayingHandler to MessageCastingHandler.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

public class MessageCastingHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageCastingHandler.class);
	private String SESSION_ID = "";
	
	private MNSInteractionHandler mih = null;
	private SeamlessRoamingHandler srh = null;
	
	public MessageCastingHandler(String sessionId) {
		this.SESSION_ID = sessionId;
	
		initializeModule();
	}
	
	private void initializeModule() {
		mih = new MNSInteractionHandler(this.SESSION_ID);
		srh = new SeamlessRoamingHandler(this.SESSION_ID);
	}
	
	public String queryMNSForDstInfo(String srcMRN, String dstMRN, String srcIP) throws ParseException{
		return processDstInfo (mih.requestDstInfo (srcMRN, dstMRN, srcIP));
	}
	
	public String queryMNSForDstInfo (String srcMRN, float geoLat, float geoLong, float geoRadius) throws ParseException{
		return processDstInfo (mih.requestDstInfo (srcMRN, geoLat, geoLong, geoRadius));
	}
	
	public String processDstInfo (String dstInfo) throws ParseException{
					
	
		if (dstInfo != null && dstInfo.regionMatches(2, "poll", 0, 4)){ // if the returned dstInfo contains json format do parsing.
			logger.debug("SessionID="+this.SESSION_ID+" Multicasting occured.");
			JSONObject jo = (JSONObject)JSONValue.parse(dstInfo);
			JSONArray jl = (JSONArray)jo.get("poll");
			String ret = "MULTIPLE_MRN,";
			for (int i = 0;i < jl.size();i++){
				if (i != jl.size()-1)
					ret = ret + ((JSONObject)jl.get(i)).get("dstMRN") + ",";
				else
					ret = ret + ((JSONObject)jl.get(i)).get("dstMRN");
			}
			return ret;
		}
		return dstInfo;
		
	}
	
	public byte[] castMsgsToMultipleCS (String srcMRN, String[] dstMRNs, String content) {
		logger.debug("SessionID="+this.SESSION_ID+" multicast.");
		for (int i = 0; i < dstMRNs.length;i++){
			srh.putSCMessage(srcMRN, dstMRNs[i], content);
		}
		return "OK".getBytes(Charset.forName("UTF-8"));
	}
	
	public byte[] unicast (MRH_MessageOutputChannel outputChannel, FullHttpRequest req, String dstIP, int dstPort, String protocol, HttpMethod httpMethod) {
		
		byte[] message = null;
		try {
    		if (protocol.equals("http")) {
			    message = outputChannel.sendMessage(req, dstIP, dstPort, httpMethod);
			    logger.info("SessionID="+this.SESSION_ID+" HTTP.");
    		} 
    		else if (protocol.equals("https")) { 
    			message = outputChannel.secureSendMessage(req, dstIP, dstPort, httpMethod);
    			logger.info("SessionID="+this.SESSION_ID+" HTTPS.");
    		} 
    		else {
    			logger.info("SessionID="+this.SESSION_ID+" No protocol.");
    		}
		} 
    	catch (IOException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
		}
		
		return message;
	}
	
	public byte[] geocast (MRH_MessageOutputChannel outputChannel, FullHttpRequest req, String srcMRN, JSONArray geoDstInfo, String protocol, HttpMethod httpMethod) {
		
		if (geoDstInfo != null) {
			Iterator iter = geoDstInfo.iterator();
			while (iter.hasNext()) {
				JSONObject obj = (JSONObject) iter.next(); 
				String connType = (String) obj.get("connType");
				if (connType.equals("polling")) {
					//TODO: implement here
					String dstMRNInGeoDstInfo = (String) obj.get("dstMRN");
					String netTypeInGeoDstInfo = (String) obj.get("netType");
					srh.putSCMessage(srcMRN, dstMRNInGeoDstInfo, req.content().toString(Charset.forName("UTF-8")).trim());
		    		
				}
				else if (connType.equals("push")) {
					//TODO: implement here
		        	try {
		        		String dstMRNInGeoDstInfo = (String) obj.get("dstMRN");
		        		String dstIPInGeoDstInfo = (String) obj.get("IPAddr");
		        		int dstPortInGeoDstInfo = Integer.parseInt((String) obj.get("portNum"));
		        		
		        		if (protocol.equals("http")) {
						    outputChannel.sendMessage(req, dstIPInGeoDstInfo, dstPortInGeoDstInfo, httpMethod);
						    logger.info("SessionID="+this.SESSION_ID+" HTTP.");
		        		} 
		        		else if (protocol.equals("https")) { 
		        			outputChannel.secureSendMessage(req, dstIPInGeoDstInfo, dstPortInGeoDstInfo, httpMethod);
		        			logger.info("SessionID="+this.SESSION_ID+" HTTPS.");
		        		} 
		        		else {
		        			
		        			logger.info("SessionID="+this.SESSION_ID+" No protocol.");
		        		}
					} 
		        	catch (IOException e) {
						logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
					}
				}
			}
			return "OK".getBytes(Charset.forName("UTF-8"));
		}
		return null;
	}
	
	@Deprecated
	public String registerClientInfo (String srcMRN, String srcIP, int srcPort, String srcModel){
		return mih.registerClientInfo (srcMRN, srcIP, srcPort, srcModel);
	}
}
