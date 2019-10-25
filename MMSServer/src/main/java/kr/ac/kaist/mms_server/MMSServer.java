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

Rev. history : 2017-04-27
Version : 0.5.2
	Added MMSStatusAutoSaver thread starting code
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr) 

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-07-24
Version : 0.5.9
	Set MEX_CONTENT_SIZE to HttpObjectAggregator
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2017-11-21
Version : 0.7.0
	MMSServer will start after waiting initialization of SecureMMSServer.  
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-08-13
Version : 0.7.3
	From this version, MMS reads system arguments and configurations from "MMS-configuration/MMS.conf" file.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-07
Version : 0.9.0
	Added initialization of SessionManager.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)


Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
	Modified for requiring MMS keystore in MMS.conf.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-12
Version : 0.9.2
	Fixed bugs related to connection pool.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-14
Version : 0.9.2
	Added RABBIT_MQ_CONN_POOL.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import kr.ac.kaist.message_queue.MessageQueueDequeuer;
import kr.ac.kaist.message_queue.v2.MessageLimitSizeDequeuer;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import kr.ac.kaist.message_relaying.SessionManager;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MMSServer {

	private static Logger logger = null;
	
	public static void main(String[] args){
		
		File f = new File("./logs");
		if (SystemUtils.IS_OS_WINDOWS) {
			f.mkdirs();
		}
		else if (SystemUtils.IS_OS_LINUX) {
			f = new File("/var/mms/logs");
			f.mkdirs();
		}
		f = new File("./MMS-configuration");
		f.mkdirs();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			
		}
		
		new MMSConfiguration(args);
		logger = LoggerFactory.getLogger(MMSServer.class);
		
		
		try {
			
			logger.error("MUST check that MNS server is online="+MMSConfiguration.getMnsHost()+":"+MMSConfiguration.getMnsPort()+".");
			logger.error("MUST check that Rabbit MQ server is online="+MMSConfiguration.getRabbitMqHost()+":"+MMSConfiguration.getRabbitMqPort()+".");
			
			try {
				InetAddress ip = InetAddress.getByName(MMSConfiguration.getMnsHost());
				if (ip == null) {
					throw new UnknownHostException("Unknown MNS Host.");
				}
			}
			catch (UnknownHostException | SecurityException e) {
				MMSLog mmsLog = MMSLog.getInstance();
				mmsLog.errorException(logger, "", "", e, 10);
				
				Scanner sc = new Scanner(System.in);
				sc.nextLine();
				System.exit(5);
			}

			
			logger.error("Now starting MMS session manager.");
			SessionManager.getInstance(); //initialize SessionManager.
			Thread.sleep(1000);
			
			logger.error("Now starting MMS logging module.");
			MMSLog.getInstance(); //initialize MMSLog.
			MMSLogForDebug.getInstance(); //initialize MMSLogsForDebug.
			Thread.sleep(1000);
			
			logger.error("Now setting message queue connection pool size.");
			MessageLimitSizeDequeuer.setConnectionPool(MMSConfiguration.getRabbitMqConnPool());
			Thread.sleep(1500);
			
			if (MMSConfiguration.isHttpsEnabled()) {
				logger.error("Now starting MMS HTTPS server.");
				new SecureMMSServer().runServer(); // MMS HTTPS server thread.
				Thread.sleep(1000);
			}
			
			logger.error("Now starting MMS HTTP server.");
			NettyStartupUtil.runServer(MMSConfiguration.getHttpPort(), pipeline -> {   //runServer(int port, Consumer<ChannelPipeline> initializer)
				pipeline.addLast(new HttpServerCodec());
				pipeline.addLast(new HttpObjectAggregator(MMSConfiguration.getMaxContentSize()));
	            pipeline.addLast(new MRH_MessageInputChannel("http"));
	        });// MMS HTTP server thread.
		}
		catch (InterruptedException e) {
			MMSLog mmsLog = MMSLog.getInstance();
			mmsLog.errorException(logger, "", "", e, 10);

			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			System.exit(7);
		}
		catch (Exception e) {
			MMSLog mmsLog = MMSLog.getInstance();
			mmsLog.errorException(logger, "", "", e, 10);
			
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			System.exit(8);
		}
	}
}

