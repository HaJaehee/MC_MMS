package kr.ac.kaist.mms_server;
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
/* -------------------------------------------------------- */
/** 
File name : SecureMMSServer.java
	It is executable class of MMS Secure Server.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-20
Version : 0.4.0

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;





import java.security.cert.CertificateException;
import java.util.Scanner;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;


public final class SecureMMSServer extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(SecureMMSServer.class);

	
	public void runServer() {
		this.start();
	}
	
	@Override
    public void run() {
        super.run();
	    // ----- for information, not working
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
	    sslengine.setEnabledCipherSuites(sslengine.getSupportedCipherSuites());
	    */   
    	// ----- for information, not working
    	
        try {
	    	// ----- use keystore
	    	
		    final KeyStore ks = KeyStore.getInstance("JKS");
		    
		    String pass = "winslab";
		    
		    ks.load(new ByteArrayInputStream(Base64Coder.decode(MMSKeystore.data)), pass.toCharArray());
		    String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
		    final KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
		    kmf.init(ks, pass.toCharArray());
		    KeyManager[] km = kmf.getKeyManagers();
		    SslContext sslCtx = SslContextBuilder.forServer(kmf).build();
		    
		    // ----- use keystore
		
			
			// ----- self sign
			/*
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
			            .build();
			            */
			// ----- self sign
			
			
	        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	        EventLoopGroup workerGroup = new NioEventLoopGroup();
	        logger.error("Now starting MMS HTTPS server.");
	        try {
	            ServerBootstrap b = new ServerBootstrap();
	            b.group(bossGroup, workerGroup)
	             .channel(NioServerSocketChannel.class)
	             .handler(new LoggingHandler(LogLevel.INFO))
	             .childHandler(new SecureMMSServerInitializer(sslCtx));
	            logger.error("Ready for 0.0.0.0:" + MMSConfiguration.getHttpsPort());
	            b.bind(MMSConfiguration.getHttpsPort()).sync().channel().closeFuture().sync();
	        } catch (InterruptedException e) {
	        	MMSLog mmsLog = MMSLog.getInstance();
				mmsLog.errorException(logger, "", "", e, 10);
				
			} finally {
	            bossGroup.shutdownGracefully();
	            workerGroup.shutdownGracefully();
	        }
			
		} 
        catch (CertificateException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | IOException e) {
        	MMSLog mmsLog = MMSLog.getInstance();
			mmsLog.errorException(logger, "", "", e, 10);
			
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			System.exit(10);
		} 

    }
}