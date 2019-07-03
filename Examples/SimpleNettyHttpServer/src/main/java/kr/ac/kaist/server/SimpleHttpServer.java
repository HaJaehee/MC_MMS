package kr.ac.kaist.server;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import kr.ac.kaist.message_handler.MessageInputChannel;

import java.util.Scanner;

public class SimpleHttpServer {
	
	public static void main(String[] args){
		
		new Configuration(args);
		
		try {
			NettyStartupUtil.runServer(Configuration.HTTP_PORT(), pipeline -> {   //runServer(int port, Consumer<ChannelPipeline> initializer)
				pipeline.addLast(new HttpServerCodec());
				pipeline.addLast(new HttpObjectAggregator(Configuration.MAX_CONTENT_SIZE()));
	            pipeline.addLast(new MessageInputChannel("http"));
	        });
		}
		catch (InterruptedException e) {
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			System.exit(7);
		}
		catch (Exception e) {
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			System.exit(8);
		}
	}
}

