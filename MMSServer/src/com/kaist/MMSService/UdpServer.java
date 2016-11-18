package com.kaist.MMSService;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import io.netty.channel.socket.nio.NioDatagramChannel; 

public class UdpServer implements Runnable, AutoCloseable {
	EventLoopGroup bossGroup = new NioEventLoopGroup(1); 
    Bootstrap b = new Bootstrap(); 
    int PORT;
	public UdpServer(int PORT) {
		this.PORT = PORT;
        b.group(bossGroup)
         .channel(NioDatagramChannel.class) 
         .handler(new ChannelInitializer<DatagramChannel>() { 
            @Override 
            public void initChannel(DatagramChannel ch) throws Exception { 
                ChannelPipeline p = ch.pipeline(); 
                 
               
                p.addLast(new LoggingHandler("EchoServerUdp Handler 2", LogLevel.TRACE)); 
                p.addLast(new UdpHandler()); 
                p.addLast(new LoggingHandler("EchoServerUdp Handler 1", LogLevel.TRACE)); 
            } 
        }); 
 
    } 
 
    @Override 
    public void run() { 
        try { 
            ChannelFuture f = b.bind(PORT).sync(); 
            // Wait until the server socket is closed. 
            f.channel().closeFuture().sync(); 
        } catch (InterruptedException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } 
    } 
 
    @Override 
    public void close() throws Exception { 
        bossGroup.shutdownGracefully(); 
    } 
}
