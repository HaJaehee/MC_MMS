package com.kaist.MMSClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;



public class MMSClientHandler {
	private rcvH rcvHdr;
	private polH polHdr;
	private sndH sndHdr;
	private MIR mir;
	private MSR msr;
	private String myMRN;
	private int myPort;
	public interface resCallBack{
		void callbackMethod(String data);
	}
	public interface reqCallBack{
		String callbackMethod(String data);
	}
	private resCallBack myCallBack;
	
	public void setResCallBack(resCallBack callback){
		this.myCallBack = callback;
	}
	//private reqCallBack myreqCallBack;
	public void setReqCallBack(reqCallBack callback){
		 if (this.rcvHdr != null)
			 this.rcvHdr.mh.setReqCallBack(callback);
		 if (this.polHdr != null)
			 this.polHdr.ph.setReqCallBack(callback);
		 if (this.mir != null)
			 this.mir.mh.setReqCallBack(callback);
		 if (this.msr != null)
			 this.msr.mh.setReqCallBack(callback);
	}
	
	public MMSClientHandler(String myMRN) throws IOException{
		this.sndHdr = new sndH(myMRN);
		this.myMRN = myMRN;
	}

	public void setPolling (String destMRN, int interval) throws IOException
	{
		this.polHdr = new polH(myMRN, destMRN, interval);
	}
	
	public void setPort (int port) throws IOException
	{
		this.myPort = port;
		this.rcvHdr = new rcvH(port);
	}
	
	public void setMSR (int port) throws IOException
	{
		this.myPort = port;
		this.msr = new MSR(port);
	}
	
	
	public void setMIR (int port) throws IOException
	{
		this.myPort = port;
		this.mir = new MIR(port);
	}
	
	public String sendMSG(String dstMRN, String data) throws Exception{
		return this.sndHdr.sendPost(dstMRN, data);
	}
	
	//OONI
	public String requestFile(String dstMRN, String fileName) throws Exception{
		return this.sndHdr.sendPost2(dstMRN, fileName);
	}
	//OONI
	class locUpdate implements Runnable{

		private int MSGtype;
		public locUpdate(int MSGType) {
			// TODO Auto-generated constructor stub
			this.MSGtype = MSGType;
		}	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while(true){
					//System.out.println("send location update");
					DatagramSocket dSock = new DatagramSocket();
					InetAddress server = InetAddress.getByName(MMSConfiguration.CMURL);
					byte[] data = ("location_update:"+ myMRN + "," + myPort + "," + MSGtype).getBytes();
					DatagramPacket outPacket = new DatagramPacket(data, data.length, server, MMSConfiguration.CMPort);
					dSock.send(outPacket);
					Thread.sleep(1000);
				}
				
			} catch (InterruptedException |  IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		}
		
	
	}
	class rcvH extends MMSRcvHandler{
		public rcvH(int port) throws IOException {
			super(port);
			Thread locationUpdate = new Thread(new locUpdate(2));
			locationUpdate.start();
		}
	}
	
	class sndH extends MMSSndHandler{
		
		public sndH(String myMRN) {
			super(myMRN);
		}
	}
	
	class polH extends MMSRcvHandler{
		public polH(String myMRN, String destMRN, int interval) throws IOException {
			super(myMRN, destMRN, interval);
			Thread locationUpdate = new Thread(new locUpdate(1));
			locationUpdate.start();
		}
	}
	
	class MSR extends MMSRcvHandler{
		public MSR(int port) throws IOException {
			super(port);
			try {
				//System.out.println("send location update");
				DatagramSocket dSock = new DatagramSocket();
				InetAddress server = InetAddress.getByName(MMSConfiguration.CMURL);
				byte[] data = ("location_update:"+ myMRN + "," + myPort + "," + 2).getBytes();
				DatagramPacket outPacket = new DatagramPacket(data, data.length, server, MMSConfiguration.CMPort);
				dSock.send(outPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class MIR extends MMSRcvHandler{
		public MIR(int port) throws IOException {
			super(port);
			try {
				//System.out.println("send location update");
				DatagramSocket dSock = new DatagramSocket();
				InetAddress server = InetAddress.getByName(MMSConfiguration.CMURL);
				byte[] data = ("location_update:"+ myMRN + "," + myPort + "," + 2).getBytes();
				DatagramPacket outPacket = new DatagramPacket(data, data.length, server, MMSConfiguration.CMPort);
				dSock.send(outPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

