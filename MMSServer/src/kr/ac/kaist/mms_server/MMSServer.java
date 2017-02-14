package kr.ac.kaist.mms_server;

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
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import kr.ac.kaist.message_relaying.MessageRelayingHandler;


public class MMSServer {
	private static final String TAG = "MMSServer";
	
	public static void main(String[] args) throws Exception{
		if(MMSConfiguration.LOGGING)System.out.println("[MMS Server] Now starting MMS HTTP server");
		NettyStartupUtil.runServer(MMSConfiguration.HTTP_PORT, pipeline -> {
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(19999));
            pipeline.addLast(new MessageRelayingHandler());
        });
	}
}

