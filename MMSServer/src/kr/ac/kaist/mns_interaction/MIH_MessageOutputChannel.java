package kr.ac.kaist.mns_interaction;

/* -------------------------------------------------------- */
/** 
File name : MIH_MessageOutputChannel.java
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-02-01
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

import kr.ac.kaist.mms_server.MMSConfiguration;

public class MIH_MessageOutputChannel {
	private static final String TAG = "[MIH_MessageOutputChannel] ";
	String sendToMNS(String request) {
    	try{
	    	//String modifiedSentence;
	    	String returnedIP = null;
	    	
	    	Socket MNSSocket = new Socket("localhost", 1004);
	    	
	    	
	    	BufferedWriter outToMNS = new BufferedWriter(
						new OutputStreamWriter(MNSSocket.getOutputStream(),Charset.forName("UTF-8")));
	    	
	    	if(MMSConfiguration.LOGGING)System.out.println(TAG+request);
	    	ServerSocket Sock = new ServerSocket(0);
	    	int rplPort = Sock.getLocalPort();
	    	if(MMSConfiguration.LOGGING)System.out.println(TAG+"Reply port : "+rplPort);
	    	outToMNS.write(request+","+rplPort);
	    	outToMNS.flush();
	    	outToMNS.close();
	    	MNSSocket.close();
	    	
	    	
	    	Socket ReplySocket = Sock.accept();
	    	BufferedReader inFromMNS = new BufferedReader(
	    			new InputStreamReader(ReplySocket.getInputStream(),Charset.forName("UTF-8")));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = inFromMNS.readLine()) != null) {
				response.append(inputLine.trim());
			}
			
	    	returnedIP = response.toString();
	    	if(MMSConfiguration.LOGGING)System.out.println(TAG+"FROM SERVER: " + returnedIP);
	    	
	    	inFromMNS.close();
	    	
	    	if (returnedIP.equals("No")) {
	    		return "No";
	    	} else if (returnedIP.equals("OK")) {
	    		return "OK";
	    	}
	    	
	    	returnedIP = returnedIP.substring(15);
	    	
	    	return returnedIP;
    	}
    	catch (Exception e) {
    		if(MMSConfiguration.LOGGING)e.printStackTrace();
    		
			return null;
		}
	}
}
