import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

//import kr.ac.kaist.mms_server.MMSConfiguration;

public class MNSDummy {
	
	//private static final Logger logger = LoggerFactory.getLogger(MNSDummy.class);
	
	//All MRN to IP Mapping is in hashmap 
	private static HashMap<String, String> MRNtoIP = new HashMap<String, String>();

	
	public static void main(String argv[]) throws Exception
    {
		 ServerSocket Sock = new ServerSocket(8588);
       
       //logger.error("MNSDummy started.");
//       -------------Put MRN --> IP Information -------------
//		 MRN table structure:           IP_Address:PortNumber:Model
//       (Geo-location added version)   IP_Address:PortNumber:Model:Geo-location
       //-----------------------------------------------------
       
       while(true)
       {
    	 
          Socket connectionSocket = Sock.accept();
          
          
          //logger.debug("Packet incomming.");
          
          /**
           * Reader and Writer are starting here.
          **/
          BufferedReader in =
             new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); //Initialize Reader.
          PrintWriter out = new PrintWriter(
					new OutputStreamWriter(connectionSocket.getOutputStream())); //Initialize Writer.
          
          String inputLine;
          StringBuffer buf = new StringBuffer();
          while ((inputLine = in.readLine()) != null) {
        	  buf.append(inputLine.trim());
          }
          
          if (!connectionSocket.isInputShutdown()) { //If the Reader cannot read more lines, shutdown the Reader.
        	  connectionSocket.shutdownInput();
          }
          String data = buf.toString();
          System.out.println(data);
          /**
           * Reader ends.
          **/
          
          // newly designed interfaces
          if (data.startsWith("{")) {
        	  try {
	        	  String dataToReply = "";
        		  
        		  JSONParser queryParser = new JSONParser();
        		 
	        	  JSONObject query = (JSONObject) queryParser.parse(data);
	        	  
	        	  if (query.get("unicasting") != null) {
	        		  JSONObject unicastingQuery = (JSONObject) query.get("unicasting");
	        		  String srcMRN = unicastingQuery.get("srcMRN").toString();
	        		  String dstMRN = unicastingQuery.get("dstMRN").toString();
	        		  String IPAddr = unicastingQuery.get("IPAddr").toString();
	        		  
	        		  String dstInfo = (String)MRNtoIP.get(dstMRN);
	        		  System.out.println(dstInfo);
	        		  if (dstInfo != null) {
	        			  String splittedDstInfo[] = dstInfo.split(":");
		        		  if (splittedDstInfo[2].equals("1")) { //polling model
		        			  JSONObject connTypePolling = new JSONObject();
		        			  connTypePolling.put("connType", "polling");
		        			  connTypePolling.put("dstMRN", dstMRN);
		        			  connTypePolling.put("netType", "LTE-M");
		        			  dataToReply = connTypePolling.toJSONString();
		        		  }
		        		  else if (splittedDstInfo[2].equals("2")) { //push model
		        			  JSONObject connTypePush = new JSONObject();
		        			  connTypePush.put("connType", "push");
		        			  connTypePush.put("dstMRN", dstMRN);
		        			  connTypePush.put("IPAddr", splittedDstInfo[0]);
		        			  connTypePush.put("portNum", splittedDstInfo[1]);
		        			  dataToReply = connTypePush.toJSONString();
		        			  System.out.println(dataToReply);
		        		  }
	        		  }
	        		  else {
	        			  dataToReply = "No";
	        		  }
	        			  
	        		  
	        	  } 
	        	  else if (query.get("geocasting") != null) {
	        		  JSONObject geocastingQuery = (JSONObject) query.get("geocasting");
	        		  String srcMRN = geocastingQuery.get("srcMRN").toString();
	        		  String geoLat = geocastingQuery.get("lat").toString();
	        		  String geoLong = geocastingQuery.get("long").toString();
	        		  String geoRadius = geocastingQuery.get("radius").toString();
	        		  
	          		  float lat = Float.parseFloat(geoLat); 
	          		  float lon = Float.parseFloat(geoLat);
	          		  float rad = Float.parseFloat(geoRadius);
	          		  
	          		  if ( 20000 >= rad && 90 >= Math.abs(lat) && 180 >= Math.abs(lon)) {
		          		  Set<String> keys = MRNtoIP.keySet();
		          		  
		          		  Iterator<String> keysIter = keys.iterator();
		          		  // MRN lists are returned by json format.
		          		  // {"poll":[{"mrn":"urn:mrn:-"},{"mrn":"urn:mrn:-"},{"mrn":"urn:mrn:-"},....]}
		          		  JSONArray objList = new JSONArray();
		          		  
		          		  
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
		                    			  item.put("netType", "LTE-M");
		                    			  if (parsedVal[2].equals("1")) {
		                    				  item.put("connType", "polling");
		                    			  }
		                    			  else if (parsedVal[1].equals("2")) {
		                    				  item.put("connType", "push");
		                    			  }
		                    			  System.out.println(item.toJSONString());
		                    			  objList.add(item);
		                    		  }
		          				  }
		          				  
		          				  
		          			  }while(keysIter.hasNext());
		          		  }
		          		  dataToReply = objList.toJSONString();
		          		  
	          		  }
		        	  else {
		        	   
		        	  }

	          		  
	        	  }
	        	  
	        	  System.out.println(dataToReply);
	        	  out.println(dataToReply); //The Writer replies to requester.
	              out.flush();
	              /**
	               * Writer ends.
	              **/
	              
	              /**
	               * Close Reader, Writer and Connection.
	              **/
	              out.close();
	              in.close();
	              connectionSocket.close();
	              
        	  }
        	  catch (Exception e) {
        		  e.printStackTrace();
        	  }
          }
          //logger.debug(data);
       }
    }
}
