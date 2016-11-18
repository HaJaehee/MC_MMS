package com.kaist.MMSService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import io.netty.channel.ChannelHandler.Sharable; 
import io.netty.channel.ChannelHandlerContext; 
import io.netty.channel.SimpleChannelInboundHandler; 
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil; 

public class UdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	private void requestToCM(String request) throws UnknownHostException, IOException{
    	
    	//String modifiedSentence;
    	String returnedIP;
    	
    	Socket CMSocket = new Socket("localhost", 1004);
    	DataOutputStream outToCM = new DataOutputStream(CMSocket.getOutputStream());
    	BufferedReader inFromCM = new BufferedReader(new InputStreamReader(CMSocket.getInputStream()));
    	
    	outToCM.writeBytes(request + '\n');
    	CMSocket.close();
    	return;
    	
    }
	@Override 
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		
		InetSocketAddress socketAddress = msg.sender();
	    InetAddress inetaddress = socketAddress.getAddress();
	    String ipAddress = inetaddress.getHostAddress();
	    this.requestToCM("Location-Update:" + ipAddress + "," + msg.content().toString(CharsetUtil.UTF_8).substring(16));
		//System.out.println("channelRead0: " + msg.content().toString(CharsetUtil.UTF_8));
	    
    } 
	 public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	        cause.printStackTrace();
	        ctx.close();
	    }
}
