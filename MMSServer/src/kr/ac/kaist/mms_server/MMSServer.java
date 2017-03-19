package kr.ac.kaist.mms_server;

/* -------------------------------------------------------- */
/** 
File name : MMSServer.java
	Executable class 
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehyun Park (jae519@kaist.ac.kr)
Creation Date : 2016-12-03
Version : 0.3.01
Rev. history : 2017-03-19
	Added HTTPS features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
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

/* -------------------------------------------------------- */
/** 
File name : MMSServer.java
	It is executable class of MMS Server.
Author : Jaehyun Park (jae519@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01
*/
/* -------------------------------------------------------- */

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import kr.ac.kaist.message_relaying.MessageRelayingHandler;


public class MMSServer {
	private static final String TAG = "MMSServer";
	
	public static void main(String[] args) throws Exception{
		
	    
	    TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	    
	    SSLContext serverContext = SSLContext.getInstance("TLS"); //from JDK 7 supports
	    final KeyStore ks = KeyStore.getInstance("JKS");
	    tmFactory.init(ks);
	    
	    String pass = "lovesm13";
	    ks.load(new FileInputStream(System.getProperty("user.dir")+"/mmskeystore.jks"), pass.toCharArray());
	    final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	    kmf.init(ks, pass.toCharArray());
	    
	    KeyManager[] km = kmf.getKeyManagers();
	    javax.net.ssl.TrustManager[] tm = tmFactory.getTrustManagers();
	    
	    serverContext.init(km, tm , null);
	    
	    //SslHandler sslHandler = new SslHandler(serverContext.createSSLEngine());
	    SSLEngine sslengine = serverContext.createSSLEngine();
	    sslengine.setUseClientMode(false);
	    sslengine.setEnableSessionCreation(true);
	    sslengine.setEnabledProtocols(sslengine.getSupportedProtocols());
	    sslengine.setEnabledCipherSuites(sslengine.getSupportedCipherSuites());
	    
		if(MMSConfiguration.LOGGING)System.out.println("[MMS Server] Now starting MMS HTTP server");
		NettyStartupUtil.runServer(MMSConfiguration.HTTP_PORT, pipeline -> {   //runServer(int port, Consumer<ChannelPipeline> initializer)
			pipeline.addLast("ofchannelcrypto", new SslHandler(sslengine));
			pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(19999));
            pipeline.addLast(new MRH_MessageInputChannel());
        });
	}
}

