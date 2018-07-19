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
*/
/* -------------------------------------------------------- */

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

public class MessageParser {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageParser.class);

	private String SESSION_ID = "";
	private String srcIP = null;
	private String srcMRN = null;
	private String dstIP = null;
	private String dstMRN = null;
	private String[] multiDstMRN = null;
	private int srcPort = 0;
	private int dstPort = 0;
	private String srcModel = null;
	private String dstModel = null;
	private String uri = null;
	private HttpMethod httpMethod = null;
	private String svcMRN = null;
	private String netType = null;
	private boolean isGeocasting = false;
	private geolocationInformation geoInfo = null;
	private JSONArray geoDstInfo = null;
	private double seqNum = -1;


	MessageParser(){
		this("");
	}
	

	MessageParser(String sessionId){
		this.SESSION_ID = sessionId;
		srcIP = null;
		srcMRN = null;
		dstIP = null;
		dstMRN = null;
		uri = null;
		httpMethod = null;
		srcPort = 0;
		dstPort = 0;
		srcModel = null;
		dstModel = null;
		svcMRN = null;
		netType = null;
		isGeocasting = false;
		geoInfo = new geolocationInformation();
		geoDstInfo = null;
		seqNum = -1;
	}
	


	void parseMessage(ChannelHandlerContext ctx, FullHttpRequest req) throws NullPointerException{
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
	    InetAddress inetaddress = socketAddress.getAddress();

	    if (inetaddress != null) {
	    	srcIP = inetaddress.getHostAddress(); // IP address of client
	    } 
	    else {
	    	throw new NullPointerException();
	    }
		srcMRN = req.headers().get("srcMRN");
		dstMRN = req.headers().get("dstMRN");
		String o = req.headers().get("seqNum");
		if (o != null) {
			try {
				//seqNum must be positive and lower than MAXIMUM VALUE of double. seqNum must be checked.
				seqNum = Double.parseDouble(o);
				new BigInteger(o);
				if (seqNum < 0) {
					throw new NumberFormatException();
				}
			}
			catch (NumberFormatException e) {
				throw e;
			}
			
		}
		
		
		
		if (req.headers().get("geocasting") != null) {
			if (req.headers().get("geocasting").equals("true")) {
				isGeocasting = true;
				try {
					setGeoInfo(req);
					logger.debug("SessionID="+this.SESSION_ID+" Geocasting request. Lat="+geoInfo.getGeoLat()+", Long="+geoInfo.getGeoLong()+", Radius="+geoInfo.getGeoRadius()+".");
				} 
				catch (ParseException e) {
					logger.warn("SessionID="+this.SESSION_ID+" Failed to parse geolocation info.");
				}
			} 
			else {
				isGeocasting = false;
			}
		} 
		else {
			isGeocasting = false;
		}
		
		uri = req.uri();
		httpMethod = req.method();
	}
	
	void parseLocInfo(FullHttpRequest req){
		String locInfo = req.content().toString(Charset.forName("UTF-8")).trim();
		
		String[] locInforms = locInfo.split(":");
		srcPort = Integer.parseInt(locInforms[0]);
		srcModel = locInforms[1];
		if (locInforms.length > 2) {
			svcMRN = locInforms[2];
			for ( int i = 3; i<locInforms.length; i++){
				svcMRN += ":"+locInforms[i];
			}
		}

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
    	
		} catch (org.json.simple.parser.ParseException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getClass().getName()+" "+e.getStackTrace()[0]+".");
		}
	}
	
	void parseGeocastInfo (String geocastInfo) {
		
		try {
			JSONParser parser = new JSONParser();
			geoDstInfo = (JSONArray) parser.parse(geocastInfo);
			
		} catch (org.json.simple.parser.ParseException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getClass().getName()+" "+e.getStackTrace()[0]+".");
		}
	}
	
	void parseMultiDstInfo(String dstInfo){
		logger.debug("SessionID="+this.SESSION_ID+" Destination info="+dstInfo+".");
		String[] dstMRNs = dstInfo.substring(13).split(",");
		multiDstMRN = dstMRNs;
	}

	// HTTP Information //
	String getUri() { return uri; }
	HttpMethod getHttpMethod() { return httpMethod; }
	
	// Destination Information //
	String getDstIP() { return dstIP; }
	int getDstPort() { return dstPort; }
	String getDstMRN() { return dstMRN; }
	String getDstModel() { return dstModel; }
	double getSeqNum() { return seqNum;	}
	
	// Destination Special Information //
	String[] getMultiDstMRN() { 
		String[] ret = null;
		ret = Arrays.copyOf(this.multiDstMRN, this.multiDstMRN.length);
		
		return ret; 
	}
	
	// Source Information //
	String getSrcIP(){ return srcIP; }
	int getSrcPort(){ return srcPort; }
	String getSrcMRN() { return srcMRN; }
	String getSrcModel(){ return srcModel; }
	
	// Service Information //
	String getSvcMRN (){ return svcMRN; }
	
	// Geolocation Information // 
	public boolean isGeocastingMsg (){
		return isGeocasting;
	}
	
	public void setGeoInfo (FullHttpRequest req) throws ParseException {
		try {
			geoInfo.setGeoLat(Float.parseFloat(req.headers().get("lat")));
			geoInfo.setGeoLong(Float.parseFloat(req.headers().get("long")));
			geoInfo.setGeoRadius(Float.parseFloat(req.headers().get("radius")));
		} 
		catch (Exception e) {
			throw e;
		}
	}
	
	public geolocationInformation getGeoInfo() {
		return geoInfo;
	}
	
	public JSONArray getGeoDstInfo () {
		return geoDstInfo;
	}
}
