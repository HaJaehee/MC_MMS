package kr.ac.kaist.mms_server;

/* -------------------------------------------------------- */
/** 
File name : SecureMMSServerInitializer.java
	It is initializer class of MMS Secure Server.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-20
Version : 0.4.0
*/
/* -------------------------------------------------------- */

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;

public class SecureMMSServerInitializer extends ChannelInitializer<SocketChannel> {

	private String TAG = "[SecureMMSServerInitializer] ";
    private final SslContext sslCtx;

    public SecureMMSServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Add SSL handler first to encrypt and decrypt everything.
        // In this example, we use a bogus certificate in the server side
        // and accept any invalid certificates in the client side.
        // You will need something more complicated to identify both
        // and server in the real world.
        pipeline.addLast(sslCtx.newHandler(ch.alloc()));

        // On top of the SSL handler, add the text line codec.
		pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(19999));

        // and then business logic.
        pipeline.addLast(new MRH_MessageInputChannel("https"));
    }
}