/*
 * MMS Server Main.
 *  
 */
package kr.ac.kaist.mms_server;


import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import kr.ac.kaist.message_relaying.HttpRelayHandler;
import kr.ac.kaist.message_relaying.MessageInputChannel;
import kr.ac.kaist.message_relaying.MessageRelayingHandler;


public class MMSServer {
	private static final String TAG = "MMSServer";
	
	public static void main(String[] args) throws Exception{
		if(MMSConfiguration.logging)System.out.println("[MMS Server] Now starting MMS HTTP server");
		NettyStartupUtil.runServer(MMSConfiguration.HTTP_PORT, pipeline -> {
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(19999));
            pipeline.addLast(new MessageRelayingHandler());
        });
	}
}

