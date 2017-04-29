package kr.ac.kaist.mns_interaction;

/* -------------------------------------------------------- */
/** 
File name : MNSDummy.java
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-02-01
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import kr.ac.kaist.mms_server.MMSConfiguration;

public class MNSDummy {
	
	private static String TAG = "[MNSDummy] ";
	//All MRN to IP Mapping is in hashmap 
	private static HashMap<String, String> MRNtoIP = new HashMap<String, String>();
	
	public static void main(String argv[]) throws Exception
    {
       
       ServerSocket Sock = new ServerSocket(1004);
       System.out.println(TAG+"MNSDummy started");
//       -------------Put MRN --> IP Information -------------
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
          if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"MNSDummy: packet incomming");
          
          BufferedReader dataReceived =
             new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(),Charset.forName("UTF-8")));

          String inputLine;
          StringBuffer buf = new StringBuffer();
          while ((inputLine = dataReceived.readLine()) != null) {
        	  buf.append(inputLine.trim());
          }
          String data = buf.toString();
          if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+data);
          
          String dataToReply = "MNSDummy-Reply:";
       
          if (data.regionMatches(0, "MRN-Request:", 0, 12)){

        	  data = data.substring(12);
        	  int rplPort = Integer.parseInt(data.split(",")[1]);
        	  data = data.split(",")[0];
        	  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"MNSDummy:data: " + data);
        	  if (MRNtoIP.containsKey(data))
        		  dataToReply += MRNtoIP.get(data);
        	  else{
        		  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"No MRN to IP Mapping");
        	  	  dataToReply = "No";
        	  }
        	  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+dataToReply);
        	  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"MNSDummy:END");
              
        	  Socket ReplySocket = new Socket("localhost",rplPort);
        	  
        	  BufferedWriter out = new BufferedWriter(
    					new OutputStreamWriter(ReplySocket.getOutputStream(),Charset.forName("UTF-8")));
        	  out.write(dataToReply);
              out.flush();
              out.close();
              ReplySocket.close();
              
          }else if (data.regionMatches(0, "Location-Update:", 0, 16)){
        	  data = data.substring(16);
        	  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"MNSDummy:data: " + data);
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

        	  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"MNSDummy:data: " + data);
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
        		  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"No MRN to IP Mapping");
        	  	  dataToReply = "No";
        	  }
        	  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+dataToReply);
        	  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"MNSDummy:END");
              
        	  Socket ReplySocket = new Socket("localhost",rplPort);
        	  
        	  BufferedWriter out = new BufferedWriter(
    					new OutputStreamWriter(ReplySocket.getOutputStream(),Charset.forName("UTF-8")));
        	  out.write(dataToReply);
              out.flush();
              out.close();
              ReplySocket.close();
              
          }else if (data.equals("Empty-MNS:") && MMSConfiguration.EMPTY_MNS_DUMMY){
        	  MRNtoIP.clear();
        	  
        	  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"MNSDummy:EMPTY");
        	  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"MNSDummy:END");
              
          }else if (data.regionMatches(0, "Remove-Entry:", 0, 13) && MMSConfiguration.REMOVE_ENTRY_MNS_DUMMY){
        	  String mrn = data.substring(13);
        	  MRNtoIP.remove(mrn);

        	  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"MNSDummy:REMOVE "+mrn);
        	  if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"MNSDummy:END");
          }      
       }
    }
}
