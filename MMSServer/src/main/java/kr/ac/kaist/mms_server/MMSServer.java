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
*/
/* -------------------------------------------------------- */

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MMSServer {

	private static final Logger logger = LoggerFactory.getLogger(MMSServer.class);
	
	public static void main(String[] args){
		
		try {
			new SecureMMSServer().runServer(); // Thread
			MMSLogForDebug.getInstance(); //initialize MMSLogsForDebug
			
			Thread.sleep(2000);
			
			logger.error("Now starting MMS HTTP server.");
			logger.error("MUST check that MNS server is online: "+MMSConfiguration.MNS_HOST+":"+MMSConfiguration.MNS_PORT);
			NettyStartupUtil.runServer(MMSConfiguration.HTTP_PORT, pipeline -> {   //runServer(int port, Consumer<ChannelPipeline> initializer)
				pipeline.addLast(new HttpServerCodec());
				pipeline.addLast(new HttpObjectAggregator(MMSConfiguration.MAX_CONTENT_SIZE));
	            pipeline.addLast(new MRH_MessageInputChannel("http"));
	        });
		}
		catch (InterruptedException e) {
			logger.warn(e.getMessage());
		}
		catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}
}

