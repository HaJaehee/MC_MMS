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

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-27
Version : 0.5.8
	Variable multiDstMRN is added for multicast.
Modifier : Jaehyun Park (jae519@kaist.ac.kr)
		   Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-04-23
Version : 0.7.1
	Removed RESOURCE_LEAK, PRIVATE_COLLECTION hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	

Rev. history : 2018-06-06
Version : 0.7.1
	Added isGeocastingMsg boolean variable and getIsGeocastingMsg method.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-18
Version : 0.7.2
	Added handling input messages by reordering policy.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-27
Version : 0.7.2
	Added geocasting features which cast message to circle or polygon area.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-10-05
Version : 0.8.0
	Added polling client verification optionally.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-10-11
Version : 0.8.0
	Modified polling client verification.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-10-16
Version : 0.8.0
	Modified in order to interact with MNS server.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-19
Version : 0.8.2
	MMS server is able to parse a polling request message which is a JSON format.\
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history: 2019-04-12
Version : 0.8.2
	Modified for coding rule conformity.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-17
Version : 0.9.1
	From now, MessageParser is initialized in MRH_MessageInputChannel class.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-29
Version : 0.9.1
	Resolved a bug related to realtime log function.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-14
Version : 0.9.2
	Refactoring.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import kr.ac.kaist.message_casting.GeolocationCircleInfo;
import kr.ac.kaist.message_casting.GeolocationPolygonInfo;
import kr.ac.kaist.message_relaying.MessageTypeDecider.msgType;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;

public class MessageParser {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageParser.class);

	private String SESSION_ID = "";
	private String srcIP = null;
	private String srcMRN = null;
	private String dstIP = null;
	private String dstMRN = null;
	private String[] multiDstMRN = null;
	private int dstPort = 0;
	private String dstModel = null;
	private String uri = null;
	private HttpMethod httpMethod = null;
	private String svcMRN = null;
	private String netType = null;
	private boolean isGeocasting = false;

	private boolean isJSONOfPollingFormat = false;
	private GeolocationCircleInfo geoCircleInfo = null;
	private GeolocationPolygonInfo geoPolygonInfo = null;
	private JSONArray geoDstInfo = null;
	private long seqNum = -1;
	private String hexSignedData = null;
	private MMSLog mmsLog = null;
	private MMSLogForDebug mmsLogForDebug = null;
	private boolean isRealtimeLogReq = false;


	MessageParser(String sessionId){
		this.SESSION_ID = sessionId;
		srcIP = null;
		srcMRN = null;
		dstIP = null;
		dstMRN = null;
		uri = null;
		httpMethod = null;
		dstPort = 0;
		dstModel = null;
		svcMRN = null;
		netType = null;
		isGeocasting = false;
		geoCircleInfo = null;
		geoPolygonInfo = null;
		geoDstInfo = null;
		hexSignedData = null;
		isRealtimeLogReq = false;
		seqNum = -1;
		mmsLog = MMSLog.getInstance();
		mmsLogForDebug = MMSLogForDebug.getInstance();
	}
	
	void parseMessage(ChannelHandlerContext ctx, FullHttpRequest req) throws NullPointerException, NumberFormatException, IOException{
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
	    InetAddress inetaddress = socketAddress.getAddress();
	    uri = req.uri();
		httpMethod = req.method();
		
	    if (inetaddress != null) {
	    	srcIP = inetaddress.getHostAddress(); // IP address of client
	    } 
	    else {
	    	throw new NullPointerException();
	    }
		srcMRN = req.headers().get("srcMRN");
		dstMRN = req.headers().get("dstMRN");
		
		if (srcMRN != null && dstMRN != null && dstMRN.equals(MMSConfiguration.getMmsMrn()) && httpMethod == HttpMethod.POST && (uri.equals("/polling")||uri.equals("/long-polling"))) {
			//When polling
			parseSvcMRNAndHexSign(req);
			
		}
		
		if (srcMRN == null && dstMRN == null && MMSConfiguration.isWebLogProviding() && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/realtime-log?id", 0, 16)){
			//If a request is a realtime logging service request.
			isRealtimeLogReq = true;
		} 
		
		String o = req.headers().get("seqNum");
		if (o != null) {
			//seqNum must be positive and lower than MAXIMUM VALUE of long. seqNum must be checked.
			seqNum = Long.parseLong(o);
			new BigInteger(o);
			if (seqNum < 0) {
				mmsLog.info(logger, SESSION_ID, ErrorCode.SEQUENCE_NUMBER_IS_NEGATIVE.toString());
				throw new NumberFormatException();
			}
		}

		if (this.SESSION_ID != null && req.headers().get("geocasting") != null) {
			if (req.headers().get("geocasting").equals("circle")) {
				isGeocasting = true;	
				setGeoCircleInfo(req);
				if (logger.isDebugEnabled()) {
					mmsLog.debug(logger, this.SESSION_ID, "Geocasting circle request. In header, Lat="+geoCircleInfo.getGeoLat()+", Long="+geoCircleInfo.getGeoLong()+", Radius="+geoCircleInfo.getGeoRadius()+".");
				}
			} 
			else if (req.headers().get("geocasting").equals("polygon")) {
				isGeocasting = true;
				try {
					setGeoPolygonInfo(req);
					
					if (logger.isDebugEnabled()) {
						float [] geoLatList = geoPolygonInfo.getGeoLatList();
						float [] geoLongList = geoPolygonInfo.getGeoLongList();
						StringBuffer strGeoPolyInfo = new StringBuffer();
						strGeoPolyInfo.append("In header, Lat=[");
						for (int i = 0 ; i < geoLatList.length ; i++) {
							strGeoPolyInfo.append("\""+geoLatList[i]+"\"");
							if (i != geoLatList.length-1) {
								strGeoPolyInfo.append(",");
							}
						}
						strGeoPolyInfo.append("], Long=[");
						for (int i = 0 ; i < geoLongList.length ; i++) {
							strGeoPolyInfo.append("\""+geoLongList[i]+"\"");
							if (i != geoLongList.length-1) {
								strGeoPolyInfo.append(",");
							}
						}
						strGeoPolyInfo.append("]");
						mmsLog.debug(logger, this.SESSION_ID, "Geocasting polygon request. "+strGeoPolyInfo.toString()+".");

					}
				} 
				catch (ParseException e) {
					mmsLog.info(logger, this.SESSION_ID, ErrorCode.WRONG_GEOCASTING_INFO.toString());
				}
			} 
			else {
				isGeocasting = false;
			}
		} 
		else {
			isGeocasting = false;
		}
		
		
	}
	
	private void parsePollingRequestToJSON(String httpContents) throws org.json.simple.parser.ParseException{
		JSONObject pollingRequestContents = null;
		JSONParser parser = new JSONParser();
		
		pollingRequestContents = (JSONObject) parser.parse(httpContents);
		
		for (Object key : pollingRequestContents.keySet()){
			String keyStr = (String) key;
			if (keyStr.equals("svcMRN")) {
				svcMRN = (String) pollingRequestContents.get("svcMRN");
//				System.out.println("[Parser] serviceMRN: " + svcMRN);
				mmsLog.debug(logger, this.SESSION_ID, "Service MRN: " + svcMRN + ".");
			}
			else if (keyStr.equals("certificate")) {
				hexSignedData = (String) pollingRequestContents.get("certificate");
				mmsLog.debug(logger, this.SESSION_ID, "Client's certificate is included.");
			}
		}
	}
	
	//TODO: will be deprecated after version 0.9.0
	@Deprecated
	private void parsePollingRequestToString(String httpContents){
		String[] sepContent = httpContents.split("\n");
		if (sepContent.length > 0) {
			if (!sepContent[0].toLowerCase().startsWith("urn")) { 
				String[] svcMRNInfo = sepContent[0].split(":");
				if (svcMRNInfo.length > 2) {
					svcMRN = svcMRNInfo[2];
					for ( int i = 3; i<svcMRNInfo.length; i++){
						svcMRN += ":"+svcMRNInfo[i];
					}
				}
			}
			else {
				svcMRN = sepContent[0];
			}
			
			if (sepContent.length > 1 && sepContent[1].length()>0) {
				hexSignedData = sepContent[1];
			}
		}
	}
	
	void parseSvcMRNAndHexSign(FullHttpRequest req) throws IOException{
		String content = req.content().toString(Charset.forName("UTF-8")).trim();
		
		if (content.length() == 0) {
			throw new IOException ("Invalid content.");
		}
		
		try {
			parsePollingRequestToJSON(content);
//			System.out.println("[Test Message] the svcMRN is " + svcMRN);
//			System.out.println("[Test Message] the certificate is " + hexSignedData.substring(6));
			isJSONOfPollingFormat = true;
			if (this.svcMRN == null) {
				mmsLog.info(logger, this.SESSION_ID, "The service MRN is not included.");
			}
			
			return ;
		} 
		catch (org.json.simple.parser.ParseException e) {
			mmsLog.info(logger, this.SESSION_ID, ErrorCode.JSON_FORMAT_ERROR.toString());
			
			isJSONOfPollingFormat = false;
		}

		// parsePollingRequestToString(content);
	}
	
	void parseDstInfo(String dstInfo){
		
		try {
			JSONObject json = new JSONObject();
			JSONParser parser = new JSONParser();
			
			json = (JSONObject) parser.parse(dstInfo);
			
			dstModel = (String) json.get("connType");
			if (dstModel.equals("push")) {
				dstIP = (String) json.get("IPAddr");
				dstMRN = (String) json.get("dstMRN");
				dstPort = Integer.parseInt((String) json.get("portNum"));
			}
			else if (dstModel.equals("polling")) {
				dstMRN = (String) json.get("dstMRN");
				netType = (String) json.get("netType");
			}
    	
		}
		catch (org.json.simple.parser.ParseException e) {
			mmsLog.info(logger, this.SESSION_ID, ErrorCode.MNS_WRONG_FORMAT_ERROR.toString());
		}
	}
	
	void parseGeocastInfo (String geocastInfo) {
		
		try {
			JSONParser parser = new JSONParser();
			geoDstInfo = (JSONArray) parser.parse(geocastInfo);
			
		} catch (org.json.simple.parser.ParseException e) {
			mmsLog.info(logger, this.SESSION_ID, ErrorCode.WRONG_GEOCASTING_INFO.toString());
		}
	}
	
	void parseMultiDstInfo(String dstInfo){
		mmsLog.debug(logger, this.SESSION_ID, "Destination info="+dstInfo+".");
		String[] dstMRNs = dstInfo.substring(13).split(",");
		multiDstMRN = dstMRNs;
	}

	// HTTP Information //
	String getUri() { return uri; }
	HttpMethod getHttpMethod() { return httpMethod; }
	
	// Destination Information //
	String getDstIP() { return dstIP; }
	int getDstPort() { return dstPort; }
	public String getDstMRN() { return dstMRN; }
	String getDstModel() { return dstModel; }
	long getSeqNum() { return seqNum;	}
	
	// Destination Special Information //
	String[] getMultiDstMRN() { 
		String[] ret = null;
		ret = Arrays.copyOf(this.multiDstMRN, this.multiDstMRN.length);
		
		return ret; 
	}
	
	// Source Information //
	public String getSrcIP(){ return srcIP; }
	public String getSrcMRN() { return srcMRN; }
	
	// Service Information //
	public String getSvcMRN (){ return svcMRN; }
	
	// Geolocation Information // 
	public boolean isGeocastingMsg (){
		return isGeocasting;
	}
	
	public void setGeoCircleInfo (FullHttpRequest req) throws NumberFormatException, NullPointerException {
		geoCircleInfo = new GeolocationCircleInfo();
		if (req.headers().get("lat") == null) {
			throw new NullPointerException("In header, \"lat\" item is missing.");
		}
		if (req.headers().get("long") == null) {
			throw new NullPointerException("In header, \"long\" item is missing.");
		}
		if (req.headers().get("radius") == null) {
			throw new NullPointerException("In header, \"radius\" item is missing.");
		}
		geoCircleInfo.setGeoLat(Float.parseFloat(req.headers().get("lat")));
		geoCircleInfo.setGeoLong(Float.parseFloat(req.headers().get("long")));
		geoCircleInfo.setGeoRadius(Float.parseFloat(req.headers().get("radius")));	
	}
	
	public GeolocationCircleInfo getGeoCircleInfo() {
		return geoCircleInfo;
	}
	
	public void setGeoPolygonInfo (FullHttpRequest req) throws NumberFormatException, ParseException, NullPointerException {
		
		geoPolygonInfo = new GeolocationPolygonInfo();
		if (req.headers().get("lat") == null) {
			throw new NullPointerException("In header, \"lat\" item is missing.");
		}
		if (req.headers().get("long") == null) {
			throw new NullPointerException("In header, \"long\" item is missing.");
		}
		float[] geoLatList = parseToFloatList(req.headers().get("lat"));
		float[] geoLongList = parseToFloatList(req.headers().get("long"));
		if (geoLatList.length < 3 || geoLongList.length < 3 || geoLatList.length != geoLongList.length) {
			mmsLog.info(logger, SESSION_ID, ErrorCode.WRONG_GEOCASTING_INFO.toString());
		}
		geoPolygonInfo.setGeoLatList(geoLatList);
		geoPolygonInfo.setGeoLongList(geoLongList);
	}
	
	private float[] parseToFloatList (String input) throws ParseException, NumberFormatException,  NullPointerException {
		float[] ret = null;
		
		input = input.trim();
		JSONParser parser = new JSONParser();
		JSONArray arr = (JSONArray) parser.parse(input);
		ret = new float[arr.size()];
		for (int i = 0 ; i < arr.size() ; i++) {
			ret[i] = Float.parseFloat(arr.get(i).toString());
		}
		
		return ret;
	}
	
	//TODO
	public GeolocationPolygonInfo getGeoPolygonInfo() {
		return geoPolygonInfo;
	}
	
	public JSONArray getGeoDstInfo () {
		return geoDstInfo;
	}
	
	public String getHexSignedData () {
		return hexSignedData;
	}
	
	public boolean isJSONOfPollingMsg() {
		return isJSONOfPollingFormat;
	}
	
	public boolean isRealtimeLogReq() {
		return isRealtimeLogReq;
	}
}
