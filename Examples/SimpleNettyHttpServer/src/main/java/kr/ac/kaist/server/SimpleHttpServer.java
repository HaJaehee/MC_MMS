package kr.ac.kaist.server;
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

*/
/* -------------------------------------------------------- */

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

