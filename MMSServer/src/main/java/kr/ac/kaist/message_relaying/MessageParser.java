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
*/
/* -------------------------------------------------------- */

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Arrays;

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
	private GeolocationCircleInfo geoCircleInfo = null;
	private GeolocationPolygonInfo geoPolygonInfo = null;
	private JSONArray geoDstInfo = null;
	private double seqNum = -1;
	private String hexSignedData = null;


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
		geoCircleInfo = null;
		geoPolygonInfo = null;
		geoDstInfo = null;
		hexSignedData = null;
		seqNum = -1;
	}
	


	void parseMessage(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception{
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
			if (req.headers().get("geocasting").equals("circle")) {
				isGeocasting = true;
				try {
					setGeoCircleInfo(req);
					logger.debug("SessionID="+this.SESSION_ID+" Geocasting request. Lat="+geoCircleInfo.getGeoLat()+", Long="+geoCircleInfo.getGeoLong()+", Radius="+geoCircleInfo.getGeoRadius()+".");
				} 
				catch (ParseException e) {
					logger.warn("SessionID="+this.SESSION_ID+" Failed to parse geolocation info.");
				}
			} 
			else if (req.headers().get("geocasting").equals("polygon")) {
				isGeocasting = true;
				try {
					setGeoPolygonInfo(req);
					//TODO
					logger.debug("SessionID="+this.SESSION_ID+" Geocasting request. Lat="+geoPolygonInfo.getGeoLatList()+", Long="+geoPolygonInfo.getGeoLongList()+".");
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
	
	void parseSvcMRNAndHexSign(FullHttpRequest req) throws IOException{
		String content = req.content().toString(Charset.forName("UTF-8")).trim();

		if (content.length() == 0) {
			throw new IOException ("Invalid content.");
		}
		String[] sepContent = content.split("\n");
		if (sepContent.length > 0) {
			if (!sepContent[0].toLowerCase().startsWith("urn")) { //TODO: will be deprecated
				String[] svcMRNInfo = sepContent[0].split(":");
				srcPort = Integer.parseInt(svcMRNInfo[0]);
				srcModel = svcMRNInfo[1];
				if (svcMRNInfo.length > 2) {
					svcMRN = svcMRNInfo[2];
					for ( int i = 3; i<svcMRNInfo.length; i++){
						svcMRN += ":"+svcMRNInfo[i];
					}
				}
			}
			else {
				srcPort = 0;
				srcModel = "1";
				svcMRN = sepContent[0];
			}
			
			if (sepContent.length > 1 && sepContent[1].length()>0) {
				hexSignedData = sepContent[1];
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
			logger.warn("SessionID="+SESSION_ID+" "+e.getClass().getName()+" "+e.getStackTrace()[0]+".");
			for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
				logger.warn("SessionID="+SESSION_ID+" "+e.getStackTrace()[i]+".");
			}
		}
	}
	
	void parseGeocastInfo (String geocastInfo) {
		
		try {
			JSONParser parser = new JSONParser();
			geoDstInfo = (JSONArray) parser.parse(geocastInfo);
			
		} catch (org.json.simple.parser.ParseException e) {
			logger.warn("SessionID="+SESSION_ID+" "+e.getClass().getName()+" "+e.getStackTrace()[0]+".");
			for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
				logger.warn("SessionID="+SESSION_ID+" "+e.getStackTrace()[i]+".");
			}
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
	
	public void setGeoCircleInfo (FullHttpRequest req) throws Exception {
		try {
			geoCircleInfo = new GeolocationCircleInfo();
			geoCircleInfo.setGeoLat(Float.parseFloat(req.headers().get("lat")));
			geoCircleInfo.setGeoLong(Float.parseFloat(req.headers().get("long")));
			geoCircleInfo.setGeoRadius(Float.parseFloat(req.headers().get("radius")));
		} 
		catch (Exception e) {
			throw e;
		}
	}
	
	public GeolocationCircleInfo getGeoCircleInfo() {
		return geoCircleInfo;
	}
	
	public void setGeoPolygonInfo (FullHttpRequest req) throws Exception {
		try {
			geoPolygonInfo = new GeolocationPolygonInfo();
			float[] geoLatList = parseToFloatList(req.headers().get("lat"));
			float[] geoLongList = parseToFloatList(req.headers().get("long"));
			if (geoLatList.length < 3 || geoLongList.length < 3 || geoLatList.length != geoLongList.length) {
				throw new Exception();
			}
			geoPolygonInfo.setGeoLatList(geoLatList);
			geoPolygonInfo.setGeoLongList(geoLongList);
		} 
		catch (Exception e) {
			throw e;
		}
	}
	
	private float[] parseToFloatList (String input) throws ParseException, NullPointerException {
		float[] ret = null;
		if (input != null) {
			try {
				input = input.trim();
				JSONParser parser = new JSONParser();
				JSONArray arr = (JSONArray) parser.parse(input);
				ret = new float[arr.size()];
				for (int i = 0 ; i < arr.size() ; i++) {
					ret[i] = Float.parseFloat(arr.get(i).toString());
				}
			}
			catch (ParseException e) {
				throw e;
			}
			
			return ret;
		}
		else {
			throw new NullPointerException();
		}
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
}
