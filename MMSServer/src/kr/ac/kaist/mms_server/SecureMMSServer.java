package kr.ac.kaist.mms_server;



/* -------------------------------------------------------- */
/** 
File name : SecureMMSServer.java
	It is executable class of MMS Secure Server.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-20
Version : 0.4.0
*/
/* -------------------------------------------------------- */

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;


public final class SecureMMSServer {

    static final int PORT = Integer.parseInt(System.getProperty("port", "443"));

    public static void main(String[] args) throws Exception {
        
	    //for information, not working
	    /*
	    SSLContext serverContext = SSLContext.getInstance("TLS"); //from JDK 7 supports
	    final KeyStore ks = KeyStore.getInstance("JKS");
	    
	    String pass = "lovesm13";
	    ks.load(new FileInputStream(System.getProperty("user.dir")+"/mmskeystore.jks"), pass.toCharArray());
	    final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	    kmf.init(ks, pass.toCharArray());
	    
	    KeyManager[] km = kmf.getKeyManagers();
	    
	    serverContext.init(km, null , null);
	    
	    SslHandler sslHandler = new SslHandler(serverContext.createSSLEngine());
	    SSLEngine sslengine = serverContext.createSSLEngine();
	    sslengine.setUseClientMode(false);
	    sslengine.setEnableSessionCreation(true);
	    sslengine.setEnabledProtocols(sslengine.getSupportedProtocols());
	    sslengine.setEnabledCipherSuites(sslengine.getSupportedCipherSuites());*/
    	
	    final KeyStore ks = KeyStore.getInstance("JKS");
	    
	    String pass = "lovesm13";
	    
	    ks.load(new ByteArrayInputStream(Base64Coder.decode(MMSKeystore.data)), pass.toCharArray());
	    String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
	    final KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
	    kmf.init(ks, pass.toCharArray());
	    KeyManager[] km = kmf.getKeyManagers();
	
        //SelfSignedCertificate ssc = new SelfSignedCertificate();
        //SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
        //    .build();
        SslContext sslCtx = SslContextBuilder.forServer(kmf).build();
        
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new SecureMMSServerInitializer(sslCtx));

            b.bind(PORT).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}