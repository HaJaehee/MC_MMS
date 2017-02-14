package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : MMSClientHandler.java
	It provides APIs for MMS clients. 
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03
Version : 0.3.01
Rev. history : 2017-02-01
	Added setting header field features. 
	Added locator registering features.
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

public class MMSClientHandler {
	private RcvHandler rcvHandler = null;
	private PollHandler pollHandler = null;
	private SendHandler sendHandler = null;
	private String clientMRN = "";
	private int clientPort = 0;
	private Map<String,String> headerField = null;
	
	public MMSClientHandler(String clientMRN) throws IOException{
		this.sendHandler = new SendHandler(clientMRN);
		this.clientMRN = clientMRN;
	}
	
	public interface Callback{
		String callbackMethod(Map<String,List<String>> headerField, String message);
	}
	

	public void setCallback(Callback callback){
		setResCallback(callback);
		setReqCallback(callback);
	}
	
	private void setResCallback(Callback callback){
		 if (this.pollHandler != null) {
			 this.pollHandler.ph.setResCallback(callback);
		 }
	}
	
	private void setReqCallback(Callback callback){
		 if (this.rcvHandler != null && this.rcvHandler.hrh != null) {
			 this.rcvHandler.hrh.setReqCallback(callback);
		 }
	}
	
	@Deprecated
	public void setPolling (String dstMRN, int interval) throws IOException{
		startPolling(dstMRN, interval);
	}
	
	public void startPolling (String dstMRN, int interval) throws IOException{
		this.pollHandler = new PollHandler(clientMRN, dstMRN, interval, headerField);
	}
	
	public void setPort (int port) throws IOException{
		this.clientPort = port;
		this.rcvHandler = new RcvHandler(port);
		String response = registerLocator(port);	
	}
	
	@Deprecated
	public void setMSR (int port) throws IOException{
		setPort (port);
	}
	
	@Deprecated
	public void setMIR (int port) throws IOException{
		setPort (port);
	}
	
	@Deprecated
	public void setMSP (int port) throws IOException{
		setPort (port);
	}
	
	public void setPort (int port, String context) throws IOException{
		this.clientPort = port;
		this.rcvHandler = new RcvHandler(port, context);
		String response = registerLocator(port);	
	}
	
	public void setPort (int port, String fileDirectory, String fileName) throws IOException {
		this.clientPort = port;
		this.rcvHandler = new RcvHandler(port, fileDirectory, fileName);
		String response = registerLocator(port);	
	}
	
	public void addContext (String context) {
		this.rcvHandler.addContext(context);
	}
	
	public void addFileContext (String fileDirectory, String fileName) {
		this.rcvHandler.addFileContext(fileDirectory, fileName);
	}
	
	private String registerLocator(int port){
		try {
			return new MMSSndHandler(clientMRN).registerLocator(port);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.LOGGING)e.printStackTrace();
			return "";
		}
	}
	
	//HJH
	public void setMsgHeader(Map<String,String> headerField) throws Exception{
		this.headerField = headerField;
	}
	
	public String sendPostMsg(String dstMRN, String loc, String data) throws Exception{
		return this.sendHandler.sendHttpPost(dstMRN, loc, data, headerField);
	}
	
	public String sendPostMsg(String dstMRN, String data) throws Exception{
		return this.sendHandler.sendHttpPost(dstMRN, "", data, headerField);
	}
	
	//HJH
	public String sendGetMsg(String dstMRN) throws Exception{
		return this.sendHandler.sendHttpGet(dstMRN, "", "", headerField);
	}
	
	//HJH
	public String sendGetMsg(String dstMRN, String loc, String params) throws Exception{
		return this.sendHandler.sendHttpGet(dstMRN, loc, params, headerField);
	}
	
	//OONI
	public String requestFile(String dstMRN, String fileName) throws Exception{
		return this.sendHandler.sendHttpGetFile(dstMRN, fileName, headerField);
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
	
	private class RcvHandler extends MMSRcvHandler{
		RcvHandler(int port) throws IOException {
			super(port);
		}
		RcvHandler(int port, String context) throws IOException {
			super(port, context);
		}
		RcvHandler(int port, String fileDirectory, String fileName) throws IOException {
			super(port, fileDirectory, fileName);
		}
	}
	
	private class SendHandler extends MMSSndHandler{
		SendHandler(String clientMRN) {
			super(clientMRN);
		}
	}
	
	private class PollHandler extends MMSRcvHandler{
		PollHandler(String clientMRN, String dstMRN, int interval, Map<String, String> headerField) throws IOException {
			super(clientMRN, dstMRN, interval, clientPort, 1, headerField);
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

