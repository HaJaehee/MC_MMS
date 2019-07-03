package kr.ac.kaist.message_handler;


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
