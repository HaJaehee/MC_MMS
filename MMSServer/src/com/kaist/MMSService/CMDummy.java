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
       MRNtoIP.put("mrn:kor:123123", "127.0.0.1:8901"); // SC
       MRNtoIP.put("mrn:kor:123124", "127.0.0.1:8902"); // SP
       MRNtoIP.put("mrn:kor:123125", "127.0.0.1:8903"); // MIR
       MRNtoIP.put("mrn:kor:123126", "127.0.0.1:8904"); // MSR
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
        	  else
        		  System.out.println("No MRN to IP Mapping");
        	  System.out.println("CMDUMMY:END");
              out.writeBytes(dataToReply + '\n');
        	  
          }else if (data.regionMatches(0, "Location-Update:", 0, 16)){
        	  data = data.substring(16);
        	  //System.out.println("CMDUMMY:data: " + data);
        	  String[] data_sub = data.split(",");
        	  // data_sub = IP_address, MRN, Port
        	  MRNtoIP.put(data_sub[1], data_sub[0] + ":" + data_sub[2]);
        	  /*
        	  if (MRNtoIP.containsKey(data_sub[1]))
        		  dataToReply += MRNtoIP.get(data);
        	  else
        		  System.out.println("No MRN to IP Mapping");*/        		  
        	  
          }
         
       }
    }
}
