/*
 * Dummy Communication Manager.
 * Pass static MRN to IP mapping information.
 * mms server -->  cmdummy  
 *       MRN-Reuqest:specific_mrn
 */
package com.kaist.MMSService;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class CMDummy {
	//All MRN to IP Mapping is in hashmap 
	static HashMap<String, String> MRNtoIP = new HashMap<String, String>();
	
	public static void main(String argv[]) throws Exception
    {
       String data;
       ServerSocket Sock = new ServerSocket(1004);
       System.out.println("CMDUMMY: started");
       //-------------Put MRN --> IP Information -------------
       MRNtoIP.put("urn:mrn:imo:imo-no:1000007", "127.0.0.1:8901:1"); // SC1
       //MRNtoIP.put("urn:mrn:imo:imo-no:0100006", "127.0.0.1:8902:1"); // SC1
       MRNtoIP.put("urn:mrn:imo:imo-no:0010005", "127.0.0.1:8903:1"); // SC1
       MRNtoIP.put("urn:mrn:smart-navi:device:mir1", "127.0.0.1:8904:2"); // MIR
       MRNtoIP.put("urn:mrn:smart-navi:device:msr1", "127.0.0.1:8905:2"); // MSR
       MRNtoIP.put("urn:mrn:smart-navi:device:portal-server", "127.0.0.1:8906:2"); // SP1
       MRNtoIP.put("urn:mrn:smart-navi:device:tm-server", "127.0.0.1:8902:2"); // SP2
       MRNtoIP.put("urn:mrn:smart-navi:device:og-server", "127.0.0.1:8908:2"); // SP3
       MRNtoIP.put("urn:mrn:smart-navi:service:si-id:text-messenger", "127.0.0.1:8909:2"); // SP4
       MRNtoIP.put("urn:mrn:smart-navi:service:si-id:ocean-grid", "127.0.0.1:8910:2"); // SP5
       
       
       
       //-----------------------------------------------------
       
       while(true)
       {
          Socket connectionSocket = Sock.accept();
          //System.out.println("CMDUMMY: packet incomming");
          BufferedReader dataReceived =
             new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
          DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
          data = dataReceived.readLine();
          String dataToReply = "CMDummy-Reply:";
          
          if (data.regionMatches(0, "MRN-Request:", 0, 12)){
        	  data = data.substring(12);
        	  System.out.println("CMDUMMY:data: " + data);
        	  if (MRNtoIP.containsKey(data))
        		  dataToReply += MRNtoIP.get(data);
        	  else{
        		  System.out.println("No MRN to IP Mapping");
        	  	  dataToReply = "No";
        	  }
        	  System.out.println("CMDUMMY:END");
              out.writeBytes(dataToReply + '\n');
        	  
          }else if (data.regionMatches(0, "Location-Update:", 0, 16)){
        	  data = data.substring(16);
        	  //System.out.println("CMDUMMY:data: " + data);
        	  String[] data_sub = data.split(",");
        	  // data_sub = IP_address, MRN, Port
        	  MRNtoIP.put(data_sub[1], data_sub[0] + ":" + data_sub[2] + ":" + data_sub[3]);
        	  /*
        	  if (MRNtoIP.containsKey(data_sub[1]))
        		  dataToReply += MRNtoIP.get(data);
        	  else
        		  System.out.println("No MRN to IP Mapping");*/        		  
        	  
          }
         
       }
    }
}
