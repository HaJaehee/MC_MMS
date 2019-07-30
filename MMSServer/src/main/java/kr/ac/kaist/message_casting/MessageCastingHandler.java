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
	Replaced from random int sessionId to String sessionId as connection context channel id.
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
	Moved jobs, related to the casting feature, from MessageRelayingHandler to MessageCastingHandler.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-03
Version : 0.7.2
	Added handling input messages by FIFO scheduling.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-27
Version : 0.7.2
	Added geocasting features which cast message to circle or polygon area.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling request is not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-04-18
Version : 0.8.2
	Add asynchronous version of unicast.
Modifier : Yunho Choi (choiking10@kaist.ac.kr)

Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-10
Version : 0.9.3
	Added resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-14
Version : 0.9.4
	Introduced MRH_MessageInputChannel.ChannelBean.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-14
Version : 0.9.4
	Updated MRH_MessageInputChannel.ChannelBean.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel.ConnectionThread;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

public class MessageCastingHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageCastingHandler.class);
	private String sessionId = "";
	
	private MNSInteractionHandler mih = null;
	private SeamlessRoamingHandler srh = null;
	
	private MMSLog mmsLog = null; 
	
	public MessageCastingHandler(String sessionId) {
		this.sessionId = sessionId;
	
		initializeModule();
	}
	
	private void initializeModule() {
		mih = new MNSInteractionHandler(this.sessionId);
		srh = new SeamlessRoamingHandler(this.sessionId);
		mmsLog = MMSLog.getInstance();
	}
	
	public String queryMNSForDstInfo(String srcMRN, String dstMRN, String srcIP) throws ParseException{
		return processDstInfo (mih.requestDstInfo (srcMRN, dstMRN, srcIP));
	}
	
	public String queryMNSForDstInfo (String srcMRN, String dstMRN, float geoLat, float geoLong, float geoRadius) throws ParseException{
		return processDstInfo (mih.requestDstInfo (srcMRN, dstMRN, geoLat, geoLong, geoRadius));
	}
	
	public String queryMNSForDstInfo (String srcMRN, String dstMRN, float[] geoLat, float[] geoLong) throws ParseException{
		return processDstInfo (mih.requestDstInfo (srcMRN, dstMRN, geoLat, geoLong));
	}
	
	public String processDstInfo (String dstInfo) throws ParseException{
					
	
		if (dstInfo != null && dstInfo.regionMatches(2, "poll", 0, 4)){ // if the returned dstInfo contains json format do parsing.
			mmsLog.debug(logger, this.sessionId, "Multicasting occured.");
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
	
	/*public byte[] castMsgsToMultipleCS (MRH_MessageInputChannel.ChannelBean bean, String content) {
		mmsLog.debug(logger, this.sessionId, "multicast.");
		String [] dstMRNs = bean.getParser().getMultiDstMRN();
		for (int i = 0; i < dstMRNs.length;i++){
			srh.putSCMessage(srcMRN, dstMRNs[i], content);
		}
		return "OK".getBytes(Charset.forName("UTF-8"));
	}*/
	public byte[] unicast (MRH_MessageInputChannel.ChannelBean bean) {
		
		byte[] message = null;
		try {
    		if (bean.getProtocol().equals("http")) {
    			message = bean.getOutputChannel().sendMessage(bean);
    			mmsLog.info(logger, this.sessionId, "Protocol=HTTP.");
    		} 
    		else if (bean.getProtocol().equals("https")) { 
    			message = bean.getOutputChannel().secureSendMessage(bean);
    			mmsLog.info(logger, this.sessionId, "Protocol=HTTPS.");
    		} 
    		else {
    			mmsLog.info(logger, this.sessionId, "No protocol.");
    		}
		} 
    	catch (IOException e) {
			mmsLog.infoException(logger, this.sessionId, ErrorCode.MESSAGE_RELAYING_FAIL_UNREACHABLE.toString(), e, 5);
		}
		
		return message;
	}
	
	public ConnectionThread asynchronizedUnicast(MRH_MessageInputChannel.ChannelBean bean) {
		ConnectionThread thread = null;
		try {
    		if (bean.getProtocol().equals("http")) {
    			thread = bean.getOutputChannel().asynchronizeSendMessage(bean);
    			thread.start();
    			mmsLog.info(logger, this.sessionId, "Protocol=HTTP.");
    		} 
    		else if (bean.getProtocol().equals("https")) { 
    			thread = bean.getOutputChannel().asynchronizeSendSecureMessage(bean);
    			thread.start();
    			mmsLog.info(logger, this.sessionId, "Protocol=HTTPS.");
    		} 
    		else {
    			mmsLog.info(logger, this.sessionId, "No protocol.");
    		}
    		
		} 
    	catch (IOException e) {
    		mmsLog.infoException(logger, this.sessionId, ErrorCode.MESSAGE_RELAYING_FAIL_UNREACHABLE.toString(), e, 5);
		}
		return thread;
	}
	
	
	public byte[] geocast (MRH_MessageInputChannel.ChannelBean bean) {
		
		if (bean.getParser().getGeoDstInfo() != null) {
			Iterator iter = bean.getParser().getGeoDstInfo().iterator();
			while (iter.hasNext()) {
				JSONObject obj = (JSONObject) iter.next(); 
				String connType = (String) obj.get("connType");
				//TODO: MUST implement exception handling. 
				if (connType == null) {
					String exc = (String) obj.get("exception");
					if (exc != null) {
						mmsLog.infoException(logger, this.sessionId, "MNS query exception occured=\""+exc+"\".", new ConnectException(), 5);
						return null;
					}
				}
				
				else if (connType.equals("polling")) {
					
					String dstMRNInGeoDstInfo = (String) obj.get("dstMRN");
					String netTypeInGeoDstInfo = (String) obj.get("netType");
					return srh.putSCMessage(bean);
		    		
				}
				else if (connType.equals("push")) {
					
		        	try {
		        		String dstMRNInGeoDstInfo = (String) obj.get("dstMRN");
		        		String dstIPInGeoDstInfo = (String) obj.get("IPAddr");
		        		int dstPortInGeoDstInfo = Integer.parseInt((String) obj.get("portNum"));
		        		
		        		if (bean.getProtocol().equals("http")) {
						    bean.getOutputChannel().sendMessage(bean);
						    mmsLog.info(logger, this.sessionId, "Protocol=HTTP.");
		        		} 
		        		else if (bean.getProtocol().equals("https")) { 
		        			bean.getOutputChannel().secureSendMessage(bean);
		        			mmsLog.info(logger, this.sessionId, "Protocol=HTTPS.");
		        		} 
		        		else {
		        			mmsLog.info(logger, this.sessionId, "No protocol.");
		        		}
					} 
		        	catch (IOException e) {
		        		mmsLog.infoException(logger, this.sessionId, ErrorCode.MESSAGE_RELAYING_FAIL_UNREACHABLE.toString(), e, 5);
					}
				}
				
				
			}
			return "OK".getBytes(Charset.forName("UTF-8"));
		}
		return null;
	}
	
	
}
