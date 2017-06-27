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

*/
/* -------------------------------------------------------- */


import java.net.InetAddress;
import java.util.Random;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;

public class MRH_MessageInputChannel extends SimpleChannelInboundHandler<FullHttpRequest>{
	
	private String TAG = "[MRH_MessageInputChannel:";
	private int SESSION_ID = 0;
	private Random rd = new Random();
	
	private String protocol = "";
	
	public MRH_MessageInputChannel(String protocol) {
		super();
		this.SESSION_ID = rd.nextInt( Integer.MAX_VALUE );
		this.TAG += SESSION_ID + "] ";
		this.protocol = protocol;
	}
	
//	when coming http message
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		try{
			req.retain();
			
			if(MMSConfiguration.CONSOLE_LOGGING)System.out.println("\n"+TAG+"Message received");
			if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append("\n"+TAG+"Message received\n");
			SessionManager.sessionInfo.put(SESSION_ID, "");
			new MessageRelayingHandler(ctx, req, protocol, SESSION_ID);
		} finally {
          req.release();
      }
	}
	
	 static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
	    
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	if (SessionManager.sessionInfo.get(SESSION_ID).equals("p")) {
	    	MMSLog.nMsgWaitingPollClnt--;
    	}
    	SessionManager.sessionInfo.remove(SESSION_ID);
    	if(MMSConfiguration.CONSOLE_LOGGING){
			System.out.print(TAG);
			cause.printStackTrace();
		}
    	if(MMSConfiguration.SYSTEM_LOGGING){
			MMSLog.systemLog.append(TAG+cause.getMessage()+"\n");
		}
        ctx.close();
    }
}
