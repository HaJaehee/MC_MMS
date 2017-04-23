package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : SecureMMSClientHandler.java
	It provides APIs for Secure MMS clients. 
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-21
Version : 0.4.0

Version : 0.5.0
Rev. history : 2017-04-20 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import kr.ac.kaist.mms_client.MMSClientHandler.PollingResponseCallback;
import kr.ac.kaist.mms_client.MMSClientHandler.RequestCallback;
import kr.ac.kaist.mms_client.MMSClientHandler.ResponseCallback;

public class SecureMMSClientHandler {
	private RcvHandler rcvHandler = null;
	private PollHandler pollHandler = null;
	private SendHandler sendHandler = null;
	private String clientMRN = "";
	private int clientPort = 0;
	private Map<String,String> headerField = null;
	
	public SecureMMSClientHandler(String clientMRN) throws IOException{
		this.sendHandler = new SendHandler(clientMRN);
		this.clientMRN = clientMRN;
	}
	
	public interface PollingResponseCallback{
		String callbackMethod(Map<String,List<String>> headerField, String message);
	}
	public interface RequestCallback{
		String callbackMethod(Map<String,List<String>> headerField, String message);
		int setResponseCode();
	}
	
	public interface ResponseCallback{
		void callbackMethod(Map<String,List<String>> headerField, String message);
	}


	public void setPollingResponseCallback(PollingResponseCallback callback){
		if (this.pollHandler != null) {
			 this.pollHandler.ph.setPollingResponseCallback(callback);
		 } else {
			 System.out.println("Failed! HTTP Polling handler is required! Do startPolling()!");
		 }
	}
	
	public void setRequestCallback (RequestCallback callback) {
		if (this.rcvHandler != null && this.rcvHandler.hrh != null) {
			 this.rcvHandler.hrh.setRequestCallback(callback);
		 } else {
			 System.out.println("Failed! HTTPS server is required! Do setPort()!");
		 }
	}
	
	public void setResponseCallback (ResponseCallback callback) {
		if (this.sendHandler != null) {
			this.sendHandler.setResponseCallback(callback);
		} else {
			System.out.println("Failed! HTTP client is required!");
		}
	}


	@Deprecated
	public void startPolling (String dstMRN, int interval) throws IOException{
		this.pollHandler = new PollHandler(clientMRN, dstMRN, interval, headerField);
	}
	
	public void startPolling (String dstMRN, String svcMRN, int interval) throws IOException{
		this.pollHandler = new PollHandler(clientMRN, dstMRN, svcMRN, interval, headerField);
	}
	
	public void setPort (int port, String jksDirectory, String jksPassword) throws Exception{
		this.clientPort = port;
		this.rcvHandler = new RcvHandler(port, jksDirectory, jksPassword);
		registerLocator(port);	
	}
	
	public void setPort (int port, String context, String jksDirectory, String jksPassword) throws Exception{
		this.clientPort = port;
		this.rcvHandler = new RcvHandler(port, context, jksDirectory, jksPassword);
		registerLocator(port);	
	}
	
	public void setPort (int port, String fileDirectory, String fileName, String jksDirectory, String jksPassword) throws Exception {
		this.clientPort = port;
		this.rcvHandler = new RcvHandler(port, fileDirectory, fileName);
		registerLocator(port);	
	}
	
	public void addContext (String context) {
		this.rcvHandler.addContext(context);
	}
	
	public void addFileContext (String fileDirectory, String fileName) {
		this.rcvHandler.addFileContext(fileDirectory, fileName);
	}
	
	private void registerLocator(int port){
		try {
			new SecureMMSSndHandler(clientMRN).registerLocator(port);
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.LOGGING)e.printStackTrace();
			return;
		}
	}
	
	//HJH
	public void setMsgHeader(Map<String,String> headerField) throws Exception{
		this.headerField = headerField;
	}
	
	public void sendPostMsg(String dstMRN, String loc, String data) throws Exception{
		this.sendHandler.sendHttpsPost(dstMRN, loc, data, headerField);
	}
	
	public void sendPostMsg(String dstMRN, String data) throws Exception{
		this.sendHandler.sendHttpsPost(dstMRN, "", data, headerField);
	}
	
	//HJH
	public void sendGetMsg(String dstMRN) throws Exception{
		this.sendHandler.sendHttpsGet(dstMRN, "", "", headerField);
	}
	
	//HJH
	public void sendGetMsg(String dstMRN, String loc, String params) throws Exception{
		this.sendHandler.sendHttpsGet(dstMRN, loc, params, headerField);
	}
	
	//OONI
	public String requestFile(String dstMRN, String fileName) throws Exception{
		return this.sendHandler.sendHttpsGetFile(dstMRN, fileName, headerField);
	}
	
	
	//OONI
	/*
	@Deprecated
	class LocUpdate implements Runnable{

		private int MSGtype;
		private boolean infiniteLoop;
		public LocUpdate(int MSGType, boolean infiniteLoop) {
			// TODO Auto-generated constructor stub
			this.MSGtype = MSGType;
			this.infiniteLoop = infiniteLoop;
		}	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				do{
					if(MMSConfiguration.LOGGING)System.out.println("send location update");

					DatagramSocket dSock = new DatagramSocket();
					InetAddress server = InetAddress.getByName(MMSConfiguration.MNS_URL);
					byte[] data = ("location_update:"+ clientMRN + "," + clientPort + "," + MSGtype).getBytes();
					DatagramPacket outPacket = new DatagramPacket(data, data.length, server, MMSConfiguration.MNS_PORT);
					dSock.send(outPacket);
					
					Thread.sleep(MMSConfiguration.LOC_UPDATE_INTERVAL);
				}while(infiniteLoop);
				
			} catch (InterruptedException |  IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.LOGGING)e.printStackTrace();
			}
						
		}
	}
	*/
	
	private class RcvHandler extends SecureMMSRcvHandler{
		RcvHandler(int port, String jksDirectory, String jksPassword) throws Exception {
			super(port, jksDirectory, jksPassword);
		}
		RcvHandler(int port, String context, String jksDirectory, String jksPassword) throws Exception {
			super(port, context, jksDirectory, jksPassword);
		}
		RcvHandler(int port, String fileDirectory, String fileName, String jksDirectory, String jksPassword) throws Exception {
			super(port, fileDirectory, fileName, jksDirectory, jksPassword);
		}
	}
	
	private class SendHandler extends SecureMMSSndHandler{
		SendHandler(String clientMRN) {
			super(clientMRN);
		}
	}
	
	private class PollHandler extends SecureMMSRcvHandler{
		@Deprecated
		PollHandler(String clientMRN, String dstMRN, int interval, Map<String, String> headerField) throws IOException {
			super(clientMRN, dstMRN, interval, clientPort, 1, headerField);
		}
		
		PollHandler(String clientMRN, String dstMRN, String svcMRN, int interval, Map<String, String> headerField) throws IOException {
			super(clientMRN, dstMRN, svcMRN, interval, clientPort, 1, headerField);
		}
	}



	
	/*
	@Deprecated
	public void locUpdate () {
		Thread locationUpdate = new Thread(new LocUpdate(1, true));
		locationUpdate.start();
	}
	*/
}

