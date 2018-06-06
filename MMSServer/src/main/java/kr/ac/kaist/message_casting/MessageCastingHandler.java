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
*/
/* -------------------------------------------------------- */

import kr.ac.kaist.mns_interaction.MNSInteractionHandler;

import java.text.ParseException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCastingHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageCastingHandler.class);
	private String SESSION_ID = "";
	
	private MNSInteractionHandler mih = null;
	
	public MessageCastingHandler(String sessionId) {
		this.SESSION_ID = sessionId;
	
		initializeModule();
	}
	
	private void initializeModule() {
		mih = new MNSInteractionHandler(this.SESSION_ID);
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
	public String registerClientInfo (String srcMRN, String srcIP, int srcPort, String srcModel){
		return mih.registerClientInfo (srcMRN, srcIP, srcPort, srcModel);
	}
}
