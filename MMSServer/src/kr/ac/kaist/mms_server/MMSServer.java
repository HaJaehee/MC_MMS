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

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;


public class MMSServer {
	private static final String TAG = "[MMSServer] ";
	
	public static void main(String[] args) throws Exception{

		if(MMSConfiguration.LOGGING)System.out.println(TAG+"Now starting MMS HTTP server");
		NettyStartupUtil.runServer(MMSConfiguration.HTTP_PORT, pipeline -> {   //runServer(int port, Consumer<ChannelPipeline> initializer)
			pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(19999));
            pipeline.addLast(new MRH_MessageInputChannel("http"));
        });
	}
}

