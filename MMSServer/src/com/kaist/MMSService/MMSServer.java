/*
 * MMS Server Main.
 *  
 */
package com.kaist.MMSService;



import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;


public class MMSServer {
	private static final int HTTP_PORT=8088;
	private static final int UDP_PORT=8089;
	
	//final static String index = System.getProperty("user.dir") + "/res/h2/index.html";
	
	public static void main(String[] args) throws Exception{
		System.out.println("[MMS Server] Now starting MMS HTTP server");
		Thread locationUpdate = new Thread(new runUDPserver(UDP_PORT));
		locationUpdate.start();
		NettyStartupUtil.runServer(HTTP_PORT, pipeline -> {
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(19999));
            pipeline.addLast(new HttpRelayHandler());
        });
	}
	
}
class runUDPserver implements Runnable{
	int UDP_PORT;
	public runUDPserver (int UDP_PORT){
		this.UDP_PORT = UDP_PORT;
	}
	@Override
	public void run() {
		System.out.println("[MMS Server] Now starting MMS UDP server");
		try (UdpServer server = new UdpServer(UDP_PORT)) { 
			server.run(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}

