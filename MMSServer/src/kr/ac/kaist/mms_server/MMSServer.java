/*
 * MMS Server Main.
 *  
 */
package kr.ac.kaist.mms_server;


import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import kr.ac.kaist.message_relaying.HttpRelayHandler;
import kr.ac.kaist.message_relaying.MessageRelayingHandler;


public class MMSServer {

	
	//final static String index = System.getProperty("user.dir") + "/res/h2/index.html";
	
	public static void main(String[] args) throws Exception{
		if(MMSConfiguration.logging)System.out.println("[MMS Server] Now starting MMS HTTP server");
		Thread locationUpdate = new Thread(new runUDPserver(MMSConfiguration.UDP_PORT));
		locationUpdate.start();
		NettyStartupUtil.runServer(MMSConfiguration.HTTP_PORT, pipeline -> {
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(19999));
//            pipeline.addLast(new HttpRelayHandler());
            pipeline.addLast(new MessageRelayingHandler());
        });
	}
	
}

class runUDPserver implements Runnable{
	int UDP_PORT;
	public runUDPserver (int UDP_PORT){
		this.UDP_PORT = UDP_PORT;
	}
	@Override
	public void run() {
		if(MMSConfiguration.logging)System.out.println("[MMS Server] Now starting MMS UDP server");
		try (UdpServer server = new UdpServer(UDP_PORT)) { 
			server.run(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}

