package kr.ac.kaist.message_relaying;

/* -------------------------------------------------------- */
/** 
File name : MRH_MessageInputChannel.java
	
Author : 
Creation Date : 2017-01-24
Version : 
*/
/* -------------------------------------------------------- */

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.mms_server.MMSConfiguration;

public class MRH_MessageInputChannel extends SimpleChannelInboundHandler<FullHttpRequest>{
	
//	when coming http message
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		try{
			req.retain();
			if(MMSConfiguration.LOGGING)System.out.println("Message received");
			new MessageRelayingHandler(ctx,req);
		} finally {
          req.release();
      }
	}
}
