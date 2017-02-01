package kr.ac.kaist.mns_interaction;

/* -------------------------------------------------------- */
/** 
File name : MIH_MessageOutputChannel.java
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.2.00
Rev. history : 2017-02-01
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import kr.ac.kaist.mms_server.MMSConfiguration;

public class MIH_MessageOutputChannel {

	String sendToMNS(String request) {
    	try{
	    	//String modifiedSentence;
	    	String returnedIP = null;
	    	
	    	Socket CMSocket = new Socket("localhost", 1004);
	    	
	    	
	    	BufferedWriter outToCM = new BufferedWriter(
						new OutputStreamWriter(CMSocket.getOutputStream(),Charset.forName("UTF-8")));
	    	
	    	if(MMSConfiguration.LOGGING)System.out.println(request);
	    	ServerSocket Sock = new ServerSocket(0);
	    	int rplPort = Sock.getLocalPort();
	    	if(MMSConfiguration.LOGGING)System.out.println("Reply port : "+rplPort);
	    	outToCM.write(request+","+rplPort);
	    	outToCM.flush();
	    	outToCM.close();
	    	CMSocket.close();
	    	
	    	
	    	Socket ReplySocket = Sock.accept();
	    	BufferedReader inFromCM = new BufferedReader(
	    			new InputStreamReader(ReplySocket.getInputStream(),Charset.forName("UTF-8")));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = inFromCM.readLine()) != null) {
				response.append(inputLine.trim());
			}
			
	    	returnedIP = response.toString();
	    	if(MMSConfiguration.LOGGING)System.out.println("FROM SERVER: " + returnedIP);
	    	
	    	inFromCM.close();
	    	
	    	if (returnedIP.equals("No")) {
	    		return "No";
	    	} else if (returnedIP.equals("OK")) {
	    		return "OK";
	    	}
	    	
	    	returnedIP = returnedIP.substring(14);
	    	
	    	return returnedIP;
    	}
    	catch (Exception e) {
    		if(MMSConfiguration.LOGGING)e.printStackTrace();
    		
			return null;
		}
	}
}
