package kr.ac.kaist.mms_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import io.netty.channel.ChannelHandlerContext; 
import io.netty.channel.SimpleChannelInboundHandler; 
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil; 

public class UdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	private void requestToCM(String request) throws UnknownHostException, IOException{
    	
    	//String modifiedSentence;
    	String returnedIP;
    	
    	Socket CMSocket = new Socket("localhost", 1004);
    	BufferedWriter outToCM = new BufferedWriter(
				new OutputStreamWriter(CMSocket.getOutputStream(),Charset.forName("UTF-8")));
    	if(MMSConfiguration.logging)System.out.println(request);
    	
    	outToCM.write(request);
    	outToCM.flush();
    	
    	CMSocket.close();
    	return;
    	
    }
	@Override 
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		
		InetSocketAddress socketAddress = msg.sender();
	    InetAddress inetaddress = socketAddress.getAddress();
	    String ipAddress = inetaddress.getHostAddress();
	    this.requestToCM("Location-Update:" + ipAddress + "," + msg.content().toString(CharsetUtil.UTF_8).substring(16));
	    if(MMSConfiguration.logging)System.out.println("channelRead0: " + msg.content().toString(CharsetUtil.UTF_8));
	    
    } 
	 public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	        cause.printStackTrace();
	        ctx.close();
	    }
}
