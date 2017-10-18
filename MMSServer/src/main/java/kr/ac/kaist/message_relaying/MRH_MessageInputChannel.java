package kr.ac.kaist.message_relaying;

import java.io.IOException;

/* -------------------------------------------------------- */
/** 
File name : MRH_MessageInputChannel.java
	
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.4.0

Rev. history : 2017-03-22
	Added SSL handler and modified MessageRelayingHandler in order to handle HTTPS functionalities.
	Added member variable protocol in order to handle HTTPS.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-29
Version : 0.5.3
	Added system log features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-05-06
Version : 0.5.5
	Added SessionManager features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;

public class MRH_MessageInputChannel extends SimpleChannelInboundHandler<FullHttpRequest>{
	
	private static final Logger logger = LoggerFactory.getLogger(MRH_MessageInputChannel.class); 
	private int SESSION_ID = 0;
	private String channelID;
	private Random rd = new Random();
	private MessageParser parser;
	private String protocol = "";
	
	public MRH_MessageInputChannel(String protocol) {
		super();
		this.SESSION_ID = rd.nextInt( Integer.MAX_VALUE );
		this.protocol = protocol;
		this.channelID = null;
		this.parser = new MessageParser();
	}
	
//	when coming http message
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		System.out.println("Message in channelRead0");
		
		try{
			req.retain();
			logger.info("Message received");
			parser.parseMessage(ctx, req);
			SessionManager.sessionInfo.put(SESSION_ID, "");
//			SessionManager.channelInfo.put(channelID, "");
			new MessageRelayingHandler(ctx, req, protocol, SESSION_ID);
		} finally {
          req.release();
      }
	}
	
	
	
	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
	    System.out.println("incomming message");
		if (ctx.pipeline().get(SslHandler.class) != null){
			// Once session is secured, send a greeting and register the channel to the global channel
	        // list so the channel received the messages from others.
	        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
		        new GenericFutureListener<Future<Channel>>() {
		            @Override
		            public void operationComplete(Future<Channel> future) throws Exception {
		                ctx.writeAndFlush(
		                        "Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
		                ctx.writeAndFlush(
		                        "Your session is protected by " +
		                                ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
		                                " cipher suite.\n");
		
		                channels.add(ctx.channel());
		            }
		        });
		}
	}
	
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
    	String clientType = SessionManager.sessionInfo.get(SESSION_ID);
    	if (clientType != null) {
    		SessionManager.sessionInfo.remove(SESSION_ID);
    		if (clientType.equals("p")) {
    			MMSLog.nMsgWaitingPollClnt--;
    			logger.warn("SessionID="+this.SESSION_ID+" The polling client is disconnected");
    			System.out.println("polling client is disconnected in handler removed");
    		} else {
    			logger.warn("SessionID="+this.SESSION_ID+" The client is disconnected");
    			System.out.println("client is disconnected in handler removed");
    		}
    	}
        ctx.close();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
     	channelID = ctx.channel().id().asShortText();
//    	ctx.channel().
    	String clientType = SessionManager.sessionInfo.get(SESSION_ID);
//    	ctx.pipeline().get(HttpHeaderValues.class);
//    	channels.
    	
    	if(cause instanceof IOException){
    		int srcPort = 0;
        	String srcIP = null;
        	String[] reqInfo;
        	final int minDynamicPort = 49152;
        	
        	
        	
        	if(parser.getSrcIP() == null){
            	InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        	    InetAddress inetaddress = socketAddress.getAddress();
        	    MNSInteractionHandler handler = new MNSInteractionHandler(SESSION_ID);
        	    srcIP = inetaddress.getHostAddress(); // IP address of client
        	    srcPort = socketAddress.getPort(); // port number of client
        	    String request = null;
        	    if(srcPort >= minDynamicPort) {
        	    	request = srcIP + ":0";
        	    } else {
        	    	request = srcIP + ":" + srcPort;
        	    }
        	    
        	    String srcMRN = handler.requestIPtoMRN(request);

        	    reqInfo = new String[2];
        	    reqInfo[0] = srcIP;
        	    reqInfo[1] = srcMRN;

        	} else {
        		reqInfo = new String[5];
        		reqInfo[0] = parser.getSrcIP();
        		reqInfo[1] = parser.getSrcMRN();
        		reqInfo[2] = parser.getDstIP();
        		reqInfo[3] = parser.getDstMRN();
        		reqInfo[4] = parser.getSvcMRN();
        	}
    		
    	    printError(srcIP, reqInfo, clientType);
    	}
    	
    	if(ctx.channel().isActive())
    		ctx.close();
    }
    
    private void printError(String channelID, String[] reqInfo, String clientType){
    	// reqInfo is ordering to srcIP, srcMRN, dstIP, dstMRN, svcMRN
    	
    	System.out.println("\n/*****************************************/");
		System.out.println("The connection is disconnected by the client");
    	System.out.println("Error Channel ID: " + channelID);

    	if(clientType != null){
	    	if(clientType.equals("p"))
	    		System.out.println("Client type: Polling Client");
	    	else
	    		System.out.println("Client type: Normal Client");
    	}
    	else
    		System.out.println("Client type is unknown");
		
		System.out.println("srcIP: " + reqInfo[0]);
		System.out.println("srcMRN: " +  reqInfo[1]);
		if(reqInfo.length == 5){
			System.out.println("dstIP: " +  reqInfo[2]);
			System.out.println("dstMRN: " +  reqInfo[3]);
			System.out.println("svcMRN: " + reqInfo[4]);
		}
    	System.out.println("/*****************************************/");
    }
}
