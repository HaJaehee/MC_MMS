package kr.ac.kaist.mns_interaction;

/* -------------------------------------------------------- */
/** 
File name : MNSDummy.java
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-06-23
	Added Geo-location Update.
Modifier : Jaehyun Park (jae519@kaist.ac.kr)

Rev. history : 2017-02-01
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-27
Version : 0.5.8
	Added geocasting related features
Modifier : Jaehyun Park (jae519@kaist.ac.kr)


Rev. history : 2017-09-26
Version : 0.6.0
	Added adding mrn entry case 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-29
Version : 0.6.0
	MRNtoIPs are printed into sorted by key(MRN) form .
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.mms_server.MMSConfiguration;

public class MNSDummy {
	private static int UNICASTING = 1;
	private static int GEOCASTING = 2;
	private static int GROUPCASTING = 3;
	
	private static final Logger logger = LoggerFactory.getLogger(MNSDummy.class);
	//All MRN to IP Mapping is in hashmap 
	private static HashMap<String, String> MRNtoIP = new HashMap<String, String>();
	
	public static void main(String argv[]) throws Exception
    {
       
       ServerSocket Sock = new ServerSocket(1004);
       logger.error("MNSDummy started.");
//       -------------Put MRN --> IP Information -------------
//		 MRN table structure:           IP_Address:PortNumber:Model
//       (Geo-location added version)   IP_Address:PortNumber:Model:Geo-location
//
//
//       MRNtoIP.put("urn:mrn:imo:imo-no:1000001","118.220.143.130:0:1");
//       MRNtoIP.put("urn:mrn:imo:imo-no:1000001-kaist","172.25.0.11:0:1");
//       MRNtoIP.put("urn:mrn:imo:imo-no:1000002-kaist","172.25.0.11:0:1");
//       MRNtoIP.put("urn:mrn:smart-navi:client:sv40","106.240.253.98:0:1");
//       MRNtoIP.put("urn:mrn:smart-navi:device:msc1-20170914","175.244.145.136:0:1");
//       MRNtoIP.put("urn:mrn:smart-navi:device:msr1-20170914","221.162.236.234:8982:2");
//       MRNtoIP.put("urn:mrn:smart-navi:device:tm-server","61.37.71.5:8902:2");
//       MRNtoIP.put("urn:mrn:smart-navi:s:kjesv40","1.220.41.11:0:1");
//       MRNtoIP.put("urn:mrn:smart-navi:s:sv40","1.220.41.11:0:1");
//       MRNtoIP.put("urn:mrn:smart-navi:service:kjesv40","1.220.41.11:8902:2");
//       MRNtoIP.put("urn:mrn:smart-navi:service:sv40","1.220.41.11:8088:2");
//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:NXDDS","106.248.228.114:7088:2");
//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:NXRESULT","106.248.228.114:7090:2");
//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S10","203.250.182.94:7088:2");
//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S11","203.250.182.94:7088:2");
//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S20","203.250.182.94:7088:2");
//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S30","203.250.182.94:7088:2");
//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S40","1.220.41.11:8902:2");
//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S51","61.37.71.5:0:1");
//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S52","203.250.182.94:7088:2");
//       MRNtoIP.put("urn:mrn:smart:vessel:imo-no:mof:tmp100fors10","106.248.228.114:7090:2");
//       MRNtoIP.put("urn:mrn:smart:vessel:imo-no:mof:tmp100fors11","203.250.182.94:7080:2");
//       MRNtoIP.put("urn:mrn:smart:vessel:imo-no:mof:tmp400fors40","1.220.41.11:0:1");
//       MRNtoIP.put("urn:mrn:smart:vessel:imo-no:mof:tmp520fors52","112.172.60.133:0:1");
 


       //-----------------------------------------------------
       
       while(true)
       {
          Socket connectionSocket = Sock.accept();

          logger.debug("Packet incomming.");
          BufferedReader dataReceived =
             new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(),Charset.forName("UTF-8")));

          String inputLine;
          StringBuffer buf = new StringBuffer();
          while ((inputLine = dataReceived.readLine()) != null) {
        	  buf.append(inputLine.trim());
          }
          String data = buf.toString();

          logger.debug(data);
          String dataToReply = "MNSDummy-Reply:";
       
          if (data.regionMatches(0, "MRN-Request:", 0, 12)){

        	  data = data.substring(12);
        	  int rplPort = Integer.parseInt(data.split(",")[1]);
        	  data = data.split(",")[0];
        	  
        	  logger.debug("MNSDummy:data=" + data);
        	  if (!data.regionMatches(0, "urn:mrn:mcs:casting:geocast:smart:",0,34)){
	        	  if (MRNtoIP.containsKey(data))
	        		  dataToReply += MRNtoIP.get(data);
	        	  else{
	        	  	  logger.debug("No MRN to IP Mapping.");
	        		  dataToReply = "No";
	        	  }
	              logger.debug(dataToReply);
	        	  Socket ReplySocket = new Socket("localhost",rplPort);
	        	  
	        	  BufferedWriter out = new BufferedWriter(
	    					new OutputStreamWriter(ReplySocket.getOutputStream(),Charset.forName("UTF-8")));
	        	  out.write(dataToReply);
	              out.flush();
	              out.close();
	              ReplySocket.close();
          	  }else{ // if geocasting (urn:mrn:mcs:casting:geocasting:smart:-)
          		  String geoMRN = data.substring(34);
          		  String[] parsedGeoMRN = geoMRN.split("-");
          		  logger.info("Geocasting MRN="+geoMRN+".");
          		  float lat = Float.parseFloat(parsedGeoMRN[1]); 
          		  float lon = Float.parseFloat(parsedGeoMRN[3]);
          		  float rad = Float.parseFloat(parsedGeoMRN[5]);
          		  Set<String> keys = MRNtoIP.keySet();
          		  
          		  Iterator<String> keysIter = keys.iterator();
          		  // MRN lists are returned by json format.
          		  // {"poll":[{"mrn":"urn:mrn:-"},{"mrn":"urn:mrn:-"},{"mrn":"urn:mrn:-"},....]}
          		  JSONArray objlist = new JSONArray();
          		  
          		  
          		  if (keysIter.hasNext()){
          			  do{
          				  String key = keysIter.next();
          				  String value = MRNtoIP.get(key);
          				  String[] parsedVal = value.split(":");
          				  if (parsedVal.length == 4){ // Geo-information exists.
          					  String[] curGeoMRN = parsedVal[3].split("-");
          					  float curLat = Float.parseFloat(curGeoMRN[1]); 
                    		  float curLong = Float.parseFloat(curGeoMRN[3]);
                    		  if (((lat-curLat)*(lat-curLat) + (lon-curLong)*(lon-curLong)) < rad * rad){
                    			  JSONObject item = new JSONObject();
                    			  item.put("dstMRN", key);
                    			  objlist.add(item);
                    		  }
          				  }
          				  
          				  
          			  }while(keysIter.hasNext());
          		  }
          		  JSONObject dstMRNs = new JSONObject();
          		  dstMRNs.put("poll", objlist);
	        	  Socket ReplySocket = new Socket("localhost",rplPort);
	        	  BufferedWriter out = new BufferedWriter(
	    					new OutputStreamWriter(ReplySocket.getOutputStream(),Charset.forName("UTF-8")));
	        	  out.write("MNSDummy-Reply:" + dstMRNs.toString());
	              out.flush();
	              out.close();
	              ReplySocket.close();
          	  }
          }else if (data.regionMatches(0, "Location-Update:", 0, 16)){
        	  data = data.substring(16);
        	
        	  logger.info("MNSDummy:data=" + data);
        	  String[] data_sub = data.split(",");
        	  // data_sub = IP_address, MRN, Port
        	  MRNtoIP.put(data_sub[1], data_sub[0] + ":" + data_sub[2] + ":" + data_sub[3]);
        	  int rplPort = Integer.parseInt(data_sub[4]);
        	  Socket ReplySocket = new Socket("localhost",rplPort);
        	  
        	  BufferedWriter out = new BufferedWriter(
    					new OutputStreamWriter(ReplySocket.getOutputStream(),Charset.forName("UTF-8")));
        	  out.write("OK");
              out.flush();
              out.close();
              ReplySocket.close();
        	  
          }else if (data.regionMatches(0, "Dump-MNS:", 0, 9)){

        	  logger.debug("MNSDummy:data=" + data);
        	  int rplPort = Integer.parseInt(data.split(",")[1]);

        	  if (!MRNtoIP.isEmpty()){
        		  SortedSet<String> keys = new TreeSet<String>(MRNtoIP.keySet());
            	  for (String key : keys) {
            		  String value = MRNtoIP.get(key);
            		  dataToReply = dataToReply + key + "," + value + "<br/>";
            	  }
        	  }
        	  else{
        	  	  logger.debug("No MRN to IP Mapping.");
        		  dataToReply = "No";
        	  }
        	  Socket ReplySocket = new Socket("localhost",rplPort);
        	  
        	  BufferedWriter out = new BufferedWriter(
    					new OutputStreamWriter(ReplySocket.getOutputStream(),Charset.forName("UTF-8")));
        	  out.write(dataToReply);
              out.flush();
              out.close();
              ReplySocket.close();
              
          }else if (data.equals("Empty-MNS:") && MMSConfiguration.WEB_MANAGING){
        	  MRNtoIP.clear();
        	  logger.warn("MNSDummy:EMPTY.");
            
          }else if (data.regionMatches(0, "Remove-Entry:", 0, 13) && MMSConfiguration.WEB_MANAGING){
        	  String mrn = data.substring(13);
        	  MRNtoIP.remove(mrn);
        	  logger.warn("MNSDummy:REMOVE="+mrn+".");
          }else if (data.regionMatches(0, "Add-Entry:", 0, 10) && MMSConfiguration.WEB_MANAGING){
        	  String[] params = data.substring(10).split(",");
        	  String mrn = params[0];
        	  String locator = params[1] +":"+ params[2] +":"+ params[3];
        	  MRNtoIP.put(mrn, locator);
        	  logger.warn("MNSDummy:ADD="+mrn+".");
        	  
          //Geo-location update function.  
          }else if (data.regionMatches(0, "Geo-location-Update:", 0, 20)){
        	  //TODO:Processing geo-location update message
        	  //data format: Geo-location-update:
        	  String[] data_sub = data.split(",");
        	  logger.debug("MNSDummy:Geolocationupdate "+data_sub[1]);
        	  MRNtoIP.put(data_sub[1], "127.0.0.1" + ":" + data_sub[2] + ":" + data_sub[3] + ":" + data_sub[4]);
          }
       }
    }
}
