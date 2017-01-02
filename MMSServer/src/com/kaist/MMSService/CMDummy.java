/*
 * Dummy Communication Manager.
 * Pass static MRN to IP mapping information.
 * mms server -->  cmdummy  
 *       MRN-Reuqest:specific_mrn
 */
package com.kaist.MMSService;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CMDummy {
	//All MRN to IP Mapping is in hashmap 
	private static HashMap<String, String> MRNtoIP = new HashMap<String, String>();
	
	public static void main(String argv[]) throws Exception
    {
       
       ServerSocket Sock = new ServerSocket(1004);
       System.out.println("CMDUMMY: started");
       //-------------Put MRN --> IP Information -------------
       //MRNtoIP.put("urn:mrn:imo:imo-no:1000007", "127.0.0.1:8901:1"); // SC1
       //MRNtoIP.put("urn:mrn:imo:imo-no:0100006", "127.0.0.1:8902:1"); // SC1
       //MRNtoIP.put("urn:mrn:imo:imo-no:0010005", "127.0.0.1:8903:1"); // SC1
       //MRNtoIP.put("urn:mrn:smart-navi:device:mir1", "127.0.0.1:8904:2"); // MIR
       //MRNtoIP.put("urn:mrn:smart-navi:device:msr1", "127.0.0.1:8905:2"); // MSR
       //MRNtoIP.put("urn:mrn:smart-navi:device:tm-server", "127.0.0.1:8902:2"); // SP2
       //MRNtoIP.put("urn:mrn:simple:simple:server", "143.248.57.72:8080:2");
       
       MRNtoIP.put("urn:mrn:smart-navi:service:si-id:ocean-grid", "127.0.0.1:8910:2");
       MRNtoIP.put("urn:mrn:smart-navi:device:mir1", "52.78.97.177:8904:2");
       MRNtoIP.put("urn:mrn:smart-navi:service:si-id:text-messenger", "127.0.0.1:8909:2");
       MRNtoIP.put("urn:mrn:imo:imo-no:0100006", "143.248.57.72:0:1");
       MRNtoIP.put("urn:mrn:imo:imo-no:0010005", "223.62.215.216:0:1");
       MRNtoIP.put("urn:mrn:simple:simple:server", "143.248.57.72:8080:2");
       MRNtoIP.put("urn:mrn:smart-navi:device:chat-server", "52.78.97.177:8907:2");
       MRNtoIP.put("urn:mrn:smart-navi:device:msr1", "52.78.97.177:8921:2");
       MRNtoIP.put("urn:mrn:smart-navi:device:portal-server", "223.39.131.16:0:1");
       MRNtoIP.put("urn:mrn:imo:imo-no:1000007", "143.248.57.72:0:1");
       MRNtoIP.put("urn:mrn:smart-navi:device:og-server", "52.78.97.177:8920:2");
       MRNtoIP.put("urn:mrn:imo:imo-no:3030303", "218.158.173.219:0:1");
       MRNtoIP.put("urn:mrn:smart-navi:device:chat-server-kaist", "52.78.97.177:18902:2");
       
       
       //-----------------------------------------------------
       
       while(true)
       {
          Socket connectionSocket = Sock.accept();
          if(MMSConfiguration.logging)System.out.println("CMDUMMY: packet incomming");
          
          BufferedReader dataReceived =
             new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(),Charset.forName("UTF-8")));

          String inputLine;
          StringBuffer buf = new StringBuffer();
          while ((inputLine = dataReceived.readLine()) != null) {
        	  buf.append(inputLine.trim());
          }
          String data = buf.toString();
          if(MMSConfiguration.logging)System.out.println(data);
          
          String dataToReply = "CMDummy-Reply:";
       
          if (data.regionMatches(0, "MRN-Request:", 0, 12)){

        	  data = data.substring(12);
        	  int rplPort = Integer.parseInt(data.split(",")[1]);
        	  data = data.split(",")[0];
        	  if(MMSConfiguration.logging)System.out.println("CMDUMMY:data: " + data);
        	  if (MRNtoIP.containsKey(data))
        		  dataToReply += MRNtoIP.get(data);
        	  else{
        		  if(MMSConfiguration.logging)System.out.println("No MRN to IP Mapping");
        	  	  dataToReply = "No";
        	  }
        	  if(MMSConfiguration.logging)System.out.println(dataToReply);
        	  if(MMSConfiguration.logging)System.out.println("CMDUMMY:END");
              
        	  Socket ReplySocket = new Socket("localhost",rplPort);
        	  
        	  BufferedWriter out = new BufferedWriter(
    					new OutputStreamWriter(ReplySocket.getOutputStream(),Charset.forName("UTF-8")));
        	  out.write(dataToReply);
              out.flush();
              out.close();
              ReplySocket.close();
          }else if (data.regionMatches(0, "Location-Update:", 0, 16)){
        	  data = data.substring(16);
        	  if(MMSConfiguration.logging)System.out.println("CMDUMMY:data: " + data);
        	  String[] data_sub = data.split(",");
        	  // data_sub = IP_address, MRN, Port
        	  MRNtoIP.put(data_sub[1], data_sub[0] + ":" + data_sub[2] + ":" + data_sub[3]);
        	  /*
        	  if (MRNtoIP.containsKey(data_sub[1]))
        		  dataToReply += MRNtoIP.get(data);
        	  else
        		  if(MMSConfiguration.logging)System.out.println("No MRN to IP Mapping");*/        		  
        	  
          }else if (data.regionMatches(0, "Dump-CM:", 0, 8)){

        	  if(MMSConfiguration.logging)System.out.println("CMDUMMY:data: " + data);
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
        		  if(MMSConfiguration.logging)System.out.println("No MRN to IP Mapping");
        	  	  dataToReply = "No";
        	  }
        	  if(MMSConfiguration.logging)System.out.println(dataToReply);
        	  if(MMSConfiguration.logging)System.out.println("CMDUMMY:END");
              
        	  Socket ReplySocket = new Socket("localhost",rplPort);
        	  
        	  BufferedWriter out = new BufferedWriter(
    					new OutputStreamWriter(ReplySocket.getOutputStream(),Charset.forName("UTF-8")));
        	  out.write(dataToReply);
              out.flush();
              out.close();
              ReplySocket.close();
          }else if (data.equals("Empty-CM:") && MMSConfiguration.emptyCMDummy){
        	  MRNtoIP.clear();
        	  
        	  if(MMSConfiguration.logging)System.out.println("CMDUMMY:EMPTY");
        	  if(MMSConfiguration.logging)System.out.println("CMDUMMY:END");
              
          }else if (data.regionMatches(0, "Remove-Entry:", 0, 13) && MMSConfiguration.removeEntryCMDummy){
        	  String mrn = data.substring(13);
        	  MRNtoIP.remove(mrn);

        	  if(MMSConfiguration.logging)System.out.println("CMDUMMY:REMOVE "+mrn);
        	  if(MMSConfiguration.logging)System.out.println("CMDUMMY:END");
              
          }
          
          
       }
    }
}
