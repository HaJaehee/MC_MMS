package kr.ac.kaist.mms_client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MMSClientHandler {
	private RcvHandler rcvHandler = null;
	private PollHandler pollHandler = null;
	private SendHandler sendHandler = null;
	private MIR mir = null;
	private MSR msr = null;
	private MSP msp = null;
	private String clientMRN = "";
	private int clientPort = 0;
	private ResCallBack resCallBack = null;
	private JSONObject headerField = null;
	
	public MMSClientHandler(String clientMRN) throws IOException{
		this.sendHandler = new SendHandler(clientMRN);
		this.clientMRN = clientMRN;
	}
	
	public interface ResCallBack{
		void callbackMethod(String data);
	}
	public interface ReqCallBack{
		String callbackMethod(String data);
	}
	
	public void setResCallBack(ResCallBack callback){
		this.resCallBack = callback;
	}

	public void setReqCallBack(ReqCallBack callback){
		 if (this.rcvHandler != null) {
			 this.rcvHandler.hrh.setReqCallBack(callback);
		 }
		 if (this.pollHandler != null) {
			 this.pollHandler.ph.setReqCallBack(callback);
		 }
		 if (this.mir != null) {
			 this.mir.hrh.setReqCallBack(callback);
		 }
		 if (this.msr != null) {
			 this.msr.hrh.setReqCallBack(callback);
		 }
		 if (this.msp != null) {
			 this.msp.hrh.setReqCallBack(callback);
		 }
	}
	
	public void setPolling (String dstMRN, int interval) throws IOException
	{
		this.pollHandler = new PollHandler(clientMRN, dstMRN, interval, headerField);
	}
	
	public void setPort (int port) throws IOException
	{
		this.clientPort = port;
		this.rcvHandler = new RcvHandler(port);
	}
	
	public void setMSR (int port) throws IOException
	{
		this.clientPort = port;
		this.msr = new MSR(port);
	}
	
	
	public void setMIR (int port) throws IOException
	{
		this.clientPort = port;
		this.mir = new MIR(port);
	}
	
	public void setMSP (int port) throws IOException
	{
		this.clientPort = port;
		this.msp = new MSP(port);
	}
	
	//HJH
	public void setMsgHeader(JSONObject headerField) throws Exception{
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
		return this.sendHandler.sendHttpGet(dstMRN, "", headerField);
	}
	
	//HJH
	public String sendGetMsg(String dstMRN, String params) throws Exception{
		return this.sendHandler.sendHttpGet(dstMRN, params, headerField);
	}
	
	//OONI
	public String requestFile(String dstMRN, String fileName) throws Exception{
		return this.sendHandler.sendHttpPostFile(dstMRN, fileName, headerField);
	}
	
	
	//OONI
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
					if(MMSConfiguration.logging)System.out.println("send location update");
					
					DatagramSocket dSock = new DatagramSocket();
					InetAddress server = InetAddress.getByName(MMSConfiguration.CMURL);
					byte[] data = ("location_update:"+ clientMRN + "," + clientPort + "," + MSGtype).getBytes();
					DatagramPacket outPacket = new DatagramPacket(data, data.length, server, MMSConfiguration.CMPort);
					dSock.send(outPacket);
					
					Thread.sleep(MMSConfiguration.locUpdateInterval);
				}while(infiniteLoop);
				
			} catch (InterruptedException |  IOException e) {
				// TODO Auto-generated catch block
				if(MMSConfiguration.logging)e.printStackTrace();
			}
						
		}
	}
	
	private class RcvHandler extends MMSRcvHandler{
		RcvHandler(int port) throws IOException {
			super(port);

		}
	}
	
	private class SendHandler extends MMSSndHandler{
		SendHandler(String clientMRN) {
			super(clientMRN);
		}
	}
	
	private class PollHandler extends MMSRcvHandler{
		PollHandler(String clientMRN, String dstMRN, int interval, JSONObject headerField) throws IOException {
			super(clientMRN, dstMRN, interval, clientPort, 1, headerField);
		}
	}
	
	private class MSR extends MMSRcvHandler{
		MSR(int port) throws IOException {
			super(port);

		}
	}
	
	private class MIR extends MMSRcvHandler{
		MIR(int port) throws IOException {
			super(port);

		}
	}
	
	private class MSP extends MMSRcvHandler{
		MSP(int port) throws IOException {
			super(port);

		}
	}
	
	@Deprecated
	public void locUpdate () {
		Thread locationUpdate = new Thread(new LocUpdate(1, true));
		locationUpdate.start();
	}
	
}

