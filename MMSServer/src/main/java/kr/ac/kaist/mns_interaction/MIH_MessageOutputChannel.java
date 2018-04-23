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

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-04-23
Version : 0.7.1
	Removed RESOURCE_LEAK hazard.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;

class MIH_MessageOutputChannel {

	private static final Logger logger = LoggerFactory.getLogger(MIH_MessageOutputChannel.class);
	private String SESSION_ID = "";
	
	MIH_MessageOutputChannel(String sessionId) {
		// TODO Auto-generated constructor stub
		this.SESSION_ID = sessionId;

	}
	
	@SuppressWarnings("finally")
	String sendToMNS(String request) {
		
		Socket MNSSocket = null;
		OutputStreamWriter osw = null;
		BufferedWriter outToMNS = null;
		ServerSocket sock = null;
		Socket replySocket = null;
		BufferedReader inFromMNS = null;
		InputStreamReader isr = null;
		String returnedIP = null;
    	try{
	    	//String modifiedSentence;

	    	MNSSocket = new Socket("localhost", 1004);
	    	osw = new OutputStreamWriter(MNSSocket.getOutputStream(),Charset.forName("UTF-8"));
	    	
	    	outToMNS = new BufferedWriter(osw);
	    	
	    	logger.trace("SessionID="+this.SESSION_ID+" "+request+".");
	    	sock = new ServerSocket(0);
	    	int rplPort = sock.getLocalPort();
	    	logger.trace("SessionID="+this.SESSION_ID+" Reply port="+rplPort+".");
	    	outToMNS.write(request+","+rplPort);
	    	outToMNS.flush();
    	} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
    		logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
    		return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
    		return null;
		} finally {
    		if (osw != null) {
    			try {
					osw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		if (outToMNS != null) {
    			try {
					outToMNS.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		if (MNSSocket != null) {
    			try {
					MNSSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
	    	
	    try {
	    	replySocket = sock.accept();
	    	isr = new InputStreamReader(replySocket.getInputStream(),Charset.forName("UTF-8"));
	    	inFromMNS = new BufferedReader(isr);
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = inFromMNS.readLine()) != null) {
				response.append(inputLine.trim());
			}
			
	    	returnedIP = response.toString();
	    	logger.trace("SessionID="+this.SESSION_ID+" From server=" + returnedIP+".");
	    	
	    	
	    	if (returnedIP.equals("No")) {
	    		return "No";
	    	} else if (returnedIP.equals("OK")) {
	    		return "OK";
	    	}
	    	
	    	returnedIP = returnedIP.substring(15);
	    	
	    	
    	} catch (UnknownHostException e) {
    		logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
			return null;
		} catch (IOException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
			return null;
		} finally {
		
			if (replySocket != null) {
				try {
					replySocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (inFromMNS != null) {
				try {
					inFromMNS.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return returnedIP;
		}
	}
}
