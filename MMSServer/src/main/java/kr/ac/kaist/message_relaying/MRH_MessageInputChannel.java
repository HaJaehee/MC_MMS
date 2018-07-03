package kr.ac.kaist.message_relaying;
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

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-15
Version : 0.7.0
	Added realtime log functions
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	Jaehyun Park (jae519@kaist.ac.kr)
	
Rev. history : 2018-04-23
Version : 0.7.1
	Removed NULL_RETURN_STD hazard.
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
import kr.ac.kaist.mms_server.MMSLogForDebug;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;
import java.io.IOException;


public class MRH_MessageInputChannel extends SimpleChannelInboundHandler<FullHttpRequest>{
	
	private static final Logger logger = LoggerFactory.getLogger(MRH_MessageInputChannel.class); 

	private String SESSION_ID = "";

	private MessageParser parser;
	private String protocol = "";
	private MMSLog mmsLog = null;
	private MMSLogForDebug mmsLogForDebug = null;
	
	public MRH_MessageInputChannel(String protocol) {
		super();
		this.protocol = protocol;
		this.parser = new MessageParser();
		mmsLog = MMSLog.getInstance();
		mmsLogForDebug = MMSLogForDebug.getInstance();
	}
	
//	when coming http message
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		//System.out.println("Message in channelRead0");
		
		try{
			req.retain();

			parser.parseMessage(ctx, req);
      
			logger.info("Message received.");
			SESSION_ID = ctx.channel().id().asShortText();

			SessionManager.sessionInfo.put(SESSION_ID, "");
			

			new MessageRelayingHandler(ctx, req, protocol, SESSION_ID);
		} finally {
          req.release();
      }
	}
	
	
	
	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
	    //System.out.println("incomming message");
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

    			logger.warn("SessionID="+this.SESSION_ID+" The polling client is disconnected.");
    		} else {
    			logger.warn("SessionID="+this.SESSION_ID+" The client is disconnected.");

    		}
    	}
    	if (!ctx.isRemoved()){
    		ctx.close();
    	}
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

//    	ctx.channel().
    	String clientType = SessionManager.sessionInfo.get(SESSION_ID);
//    	ctx.pipeline().get(HttpHeaderValues.class);
//    	channels.
    	
    	if (cause instanceof IOException){
    		int srcPort = 0;
        	String srcIP = null;
        	String[] reqInfo;
        	final int minDynamicPort = 49152;
     
        	if (parser.getSrcIP() == null) {
            	InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        	    InetAddress inetaddress = socketAddress.getAddress();
        	    MNSInteractionHandler handler = new MNSInteractionHandler(SESSION_ID);
        	    if (inetaddress != null) {
        	    	srcIP = inetaddress.getHostAddress(); // IP address of client
        	    }
        	    srcPort = socketAddress.getPort(); // port number of client
        	    String request = null;
        	    if(srcPort >= minDynamicPort) {
        	    	request = srcIP + ":0";
        	    } 
              else {
        	    	request = srcIP + ":" + srcPort;
        	    }
        	    
        	    String srcMRN = handler.requestIPtoMRN(request);

        	    reqInfo = new String[2];
        	    reqInfo[0] = srcIP;
        	    reqInfo[1] = srcMRN;

        	} 
          else {
        		reqInfo = new String[5];
        		reqInfo[0] = parser.getSrcIP();
        		reqInfo[1] = parser.getSrcMRN();
        		reqInfo[2] = parser.getDstIP();
        		reqInfo[3] = parser.getDstMRN();
        		reqInfo[4] = parser.getSvcMRN();
        	}
    		
    	    printError(srcIP, reqInfo, clientType);
    	}
    	if (clientType != null) {
    		SessionManager.sessionInfo.remove(SESSION_ID);
      }
    	if (!ctx.isRemoved()){
    		  ctx.close();
      }
    }
    
    private void printError(String channelID, String[] reqInfo, String clientType){
        // reqInfo is ordering to srcIP, srcMRN, dstIP, dstMRN, svcMRN

  //    	System.out.println("\n/*****************************************/");
  //		System.out.println("The connection is disconnected by the client");
  //    	System.out.println("Error Channel ID: " + channelID);
        String errorlog = null;

        if (clientType != null){
          if(clientType.equals("p")){
  //	    		System.out.println("Client type: Polling Client");
            errorlog = new String("Client Type=Polling");

          } else {
  //	    		System.out.println("Client type: Normal Client");
            errorlog = new String("Client Type=Normal");
          }
        }
        else {
  //    		System.out.println("Client type is unknown");
          errorlog = new String("Client Type=Unknown");
        }

  //		System.out.println("srcIP: " + reqInfo[0]);
  //		System.out.println("srcMRN: " +  reqInfo[1]);
        errorlog += " srcIP=" + reqInfo[0] + " srcMRN=" + reqInfo[1];
      if (reqInfo.length == 5){
  //			System.out.println("dstIP: " +  reqInfo[2]);
  //			System.out.println("dstMRN: " +  reqInfo[3]);
  //			System.out.println("svcMRN: " + reqInfo[4]);
        errorlog += " dstIP=" + reqInfo[2] + " dstMRN=" + reqInfo[3] + " svcMRN=" + reqInfo[4];
      }
  //    	System.out.println("/*****************************************/");
		
      errorlog = "SessionID="+this.SESSION_ID+" The client is disconnected, " + errorlog + ".";
		  logger.warn(errorlog);
		  if(MMSConfiguration.WEB_LOG_PROVIDING) {
				mmsLog.addBriefLogForStatus(errorlog);
				mmsLogForDebug.addLog(this.SESSION_ID, errorlog);
		}
    }
}
