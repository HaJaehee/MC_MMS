package kr.ac.kaist.message_handler;
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

Rev. history : 2018-07-03
Version : 0.7.2
	Added handling input messages by FIFO scheduling.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.\
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */


import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


public class MessageInputChannel extends SimpleChannelInboundHandler<FullHttpRequest>{
	
	private static final Logger logger = LoggerFactory.getLogger(MessageInputChannel.class); 

	private String SESSION_ID = "";

	private String protocol = "";
	//private MMSLog mmsLog = null;
	//private MMSLogForDebug mmsLogForDebug = null;
	
	public MessageInputChannel(String protocol) {
		super();
		this.protocol = protocol;

	}
	
//	when coming http message
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		//System.out.println("Message in channelRead0");
		
		try{
			req.retain();

      
			logger.info("Message received.");
			SESSION_ID = ctx.channel().id().asShortText();

			//SessionManager.sessionInfo.put(SESSION_ID, "");
			
			MessageOutputChannel out = new MessageOutputChannel(SESSION_ID);
			out.replyToSender(ctx, "OK".getBytes());
			
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
	
}
