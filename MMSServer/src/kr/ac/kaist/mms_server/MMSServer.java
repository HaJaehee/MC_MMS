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
*/
/* -------------------------------------------------------- */

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;


public class MMSServer {
	private static String TAG = "[MMSServer] ";
	
	public static void main(String[] args) throws Exception{
		
		
		new SecureMMSServer().runServer();
		MMSConfiguration.AUTO_SAVE_STATUS_THREAD = new MMSStatusAutoSaver();
		MMSConfiguration.AUTO_SAVE_SYSTEM_LOG_THREAD = new MMSSystemLogAutoSaver();
		
		if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Now starting MMS HTTP server");
		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Now starting MMS HTTP server\n");
		NettyStartupUtil.runServer(MMSConfiguration.HTTP_PORT, pipeline -> {   //runServer(int port, Consumer<ChannelPipeline> initializer)
			pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(19999));
            pipeline.addLast(new MRH_MessageInputChannel("http"));
        });
		
		
	}
}

