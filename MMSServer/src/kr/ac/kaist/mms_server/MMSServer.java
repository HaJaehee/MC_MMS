package kr.ac.kaist.mms_server;

/* -------------------------------------------------------- */
/** 
File name : MMSServer.java
	It is executable class of MMS Server.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehyun Park (jae519@kaist.ac.kr)
Creation Date : 2016-12-03
Version : 0.3.01
*/
/* -------------------------------------------------------- */

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Security;
import java.util.function.Consumer;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.haproxy.HAProxyProxiedProtocol.TransportProtocol;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import kr.ac.kaist.message_relaying.MessageRelayingHandler;


public class MMSServer {
	private static final String TAG = "MMSServer";
	
	public static void main(String[] args) throws Exception{

		if(MMSConfiguration.LOGGING)System.out.println("[MMS Server] Now starting MMS HTTP server");
		NettyStartupUtil.runServer(MMSConfiguration.HTTP_PORT, pipeline -> {   //runServer(int port, Consumer<ChannelPipeline> initializer)
			pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(19999));
            pipeline.addLast(new MRH_MessageInputChannel("http"));
        });
	}
}

