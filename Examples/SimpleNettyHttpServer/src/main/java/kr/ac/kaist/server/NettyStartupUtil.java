package kr.ac.kaist.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NettyStartupUtil {
	private static final Logger logger = LoggerFactory.getLogger(NettyStartupUtil.class);

	static void runServer(int port, ChannelHandler childHandler, Consumer<ServerBootstrap> block) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.TRACE));
            b.childHandler(childHandler);
            block.accept(b);
            Channel ch = b.bind(port).sync().channel();
            //System.err.println( TAG + "Ready for 0.0.0.0:" + port);
            logger.error("Ready for 0.0.0.0:" + port);
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    static void runServer(int port, ChannelHandler childHandler) throws Exception {
        runServer(port, childHandler, b -> {});
    }

    static void runServer(int port, Consumer<ChannelPipeline> initializer) throws Exception {
        runServer(port, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                initializer.accept(ch.pipeline());
            }
        });
    }
}
