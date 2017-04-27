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
*/
/* -------------------------------------------------------- */


import java.net.InetAddress;
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
	
	private static final String TAG = "[MRH_MessageInputChannel] ";
	
	private String protocol = "";
	
	public MRH_MessageInputChannel(String protocol) {
		super();
		this.protocol = protocol;
	}
	
//	when coming http message
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		try{
			req.retain();
			
			if(MMSConfiguration.LOGGING)System.out.println("\n"+TAG+"Message received");
			new MessageRelayingHandler(ctx, req, protocol);
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
    	MMSLog.nMsgWaitingPollClnt--;
    	if(MMSConfiguration.LOGGING){
			System.out.print(TAG);
			cause.printStackTrace();
		}
        ctx.close();
    }
}
