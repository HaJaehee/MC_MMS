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

*/
/* -------------------------------------------------------- */

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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
//	private static HashMap<String, String> IPtoMRN = new HashMap<String, String>();
	
	public static void main(String argv[]) throws Exception
    {
       
       ServerSocket Sock = new ServerSocket(1004);
       logger.error("MNSDummy started");
//       -------------Put MRN --> IP Information -------------
//		 MRN table structure:           IP_Address:PortNumber:Model
//       (Geo-location added version)   IP_Address:PortNumber:Model:Geo-location
//
//
//       MRNtoIP.put("urn:mrn:imo:imo-no:1000002", "127.0.0.1:8901:1"); // SC2
//       MRNtoIP.put("urn:mrn:imo:imo-no:1000001", "127.0.0.1:8902:1"); // SC1
//       MRNtoIP.put("urn:mrn:imo:imo-no:1000003", "127.0.0.1:8903:1"); // SC3
//       MRNtoIP.put("urn:mrn:smart-navi:device:mir1", "127.0.0.1:8904:2"); // MIR
//       MRNtoIP.put("urn:mrn:smart-navi:device:msr1", "127.0.0.1:8905:2"); // MSR
//       MRNtoIP.put("urn:mrn:smart-navi:device:tm-server", "127.0.0.1:8902:2"); // SP2
//       MRNtoIP.put("urn:mrn:simple:simple:server", "143.248.57.72:8080:2");
       
//       MRNtoIP.put("urn:mrn:smart-navi:service:si-id:ocean-grid", "127.0.0.1:8910:2");
//       MRNtoIP.put("urn:mrn:smart-navi:device:mir1", "52.78.97.177:8904:2");
//       MRNtoIP.put("urn:mrn:smart-navi:service:si-id:text-messenger", "127.0.0.1:8909:2");
//       MRNtoIP.put("urn:mrn:imo:imo-no:1000006", "143.248.57.72:0:1");
//       MRNtoIP.put("urn:mrn:imo:imo-no:1000005", "223.62.215.216:0:1");
//       MRNtoIP.put("urn:mrn:simple:simple:server", "143.248.57.72:8080:2");
//       MRNtoIP.put("urn:mrn:smart-navi:device:chat-server", "52.78.97.177:8907:2");
//       MRNtoIP.put("urn:mrn:smart-navi:device:msr1", "52.78.97.177:8921:2");
//       MRNtoIP.put("urn:mrn:smart-navi:device:portal-server", "223.39.131.16:0:1");
//       MRNtoIP.put("urn:mrn:imo:imo-no:1000007", "143.248.57.72:0:1");
//       MRNtoIP.put("urn:mrn:smart-navi:device:og-server", "52.78.97.177:8920:2");
//       MRNtoIP.put("urn:mrn:imo:imo-no:1000008", "218.158.173.219:0:1");
//       MRNtoIP.put("urn:mrn:smart-navi:device:chat-server-kaist", "52.78.97.177:18902:2");
       
       
       //-----------------------------------------------------
       
       while(true)
       {
          Socket connectionSocket = Sock.accept();

          logger.debug("Packet incomming");
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
	        	  	  logger.debug("No MRN to IP Mapping");
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
          		  logger.info(geoMRN);;
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
        	  
        	  Set<String> keys = MRNtoIP.keySet();
        	  Iterator<String> keysIter = keys.iterator();
        	  
        	  if (keysIter.hasNext()){
        		  do{
        			  String key = keysIter.next();
        			  String value = MRNtoIP.get(key);
        			  
        			  dataToReply = dataToReply + key + "," + value + "<br/>";
        		  }while(keysIter.hasNext());
        	  }
        	  else{
        	  	  logger.debug("No MRN to IP Mapping");
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
              
          }else if (data.equals("Empty-MNS:") && MMSConfiguration.WEB_MANAGING){
        	  MRNtoIP.clear();
        	  logger.info("MNSDummy:EMPTY");
            
          }else if (data.regionMatches(0, "Remove-Entry:", 0, 13) && MMSConfiguration.WEB_MANAGING){
        	  String mrn = data.substring(13);
        	  MRNtoIP.remove(mrn);
        	  logger.info("MNSDummy:REMOVE "+mrn);
          //Geo-location update function.  
          }else if (data.regionMatches(0, "Geo-location-Update:", 0, 20)){
        	  //TODO:Processing geo-location update message
        	  //data format: Geo-location-update:
        	  String[] data_sub = data.split(",");
        	  logger.debug("MNSDummy:Geolocationupdate "+data_sub[1]);
        	  MRNtoIP.put(data_sub[1], "127.0.0.1" + ":" + data_sub[2] + ":" + data_sub[3] + ":" + data_sub[4]);
          } else if(data.regionMatches(0, "IP-Request:", 0, 11)){
        	  String address = data.substring(11).split(",")[0];
        	  System.out.println("Incomming Address: " + address);
        	  String[] parseAddress = address.split(":");
        	  String mrn = null;
        	  for(String value : MRNtoIP.keySet()){
        		  String[] parseValue = MRNtoIP.get(value).split(":");
        		  System.out.println("Value:" + parseValue.toString());
        		  if(parseAddress[0].equals(parseValue[0]) 
        				  && parseAddress[1].equals(parseValue[1])){
        			  mrn = value;
        			  break;
        		  }
        	  }
        	  
        	  if(mrn == null){
        		  dataToReply += "Unregistered MRN";
        	  } else {
        		  dataToReply += mrn;
        	  }
        	  
        	  int rplPort = Integer.parseInt(data.split(",")[1]);
        	  Socket ReplySocket = new Socket("localhost", rplPort);
        	  
        	  BufferedWriter out = new BufferedWriter(
    					new OutputStreamWriter(ReplySocket.getOutputStream(),Charset.forName("UTF-8")));
        	  out.write(dataToReply);
              out.flush();
              out.close();
              ReplySocket.close();
          }
       }
    }
}
