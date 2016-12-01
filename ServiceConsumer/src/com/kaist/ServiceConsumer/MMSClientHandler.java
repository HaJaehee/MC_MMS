package com.kaist.ServiceConsumer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;



public class MMSClientHandler {
	private rcvH rcvHdr;
	private sndH sndHdr;
	private String myMRN;
	private int myPort;
	interface resCallBack{
		void callbackMethod(String data);
	}
	interface reqCallBack{
		String callbackMethod(String data);
	}
	private resCallBack myCallBack;
	
	public void setResCallBack(resCallBack callback){
		this.myCallBack = callback;
	}
	//private reqCallBack myreqCallBack;
	public void setReqCallBack(reqCallBack callback){
		 this.rcvHdr.mh.setReqCallBack(callback);
	}
	
	public MMSClientHandler(String myMRN , int port) throws IOException{
		this.rcvHdr = new rcvH(port);
		this.sndHdr = new sndH(myMRN);
		this.myMRN = myMRN;
		this.myPort = port;
	}
	
	
	
	public String sendMSG(String dstMRN, String data) throws Exception{
		return this.sndHdr.sendPost(dstMRN, data);
	}
	class locUpdate implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while(true){
					Thread.sleep(1000);
					//System.out.println("send location update");
					DatagramSocket dSock = new DatagramSocket();
					InetAddress server = InetAddress.getByName("localhost");
					byte[] data = ("location_update:"+ myMRN + "," + myPort).getBytes();
					DatagramPacket outPacket = new DatagramPacket(data, data.length, server, 8089);
					dSock.send(outPacket);
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
			Thread locationUpdate = new Thread(new locUpdate());
			locationUpdate.start();
		}
		
	}
	class sndH extends MMSSndHandler{
		
		public sndH(String myMRN) {
			super(myMRN);
		}
		
	}
}
