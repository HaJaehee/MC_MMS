package kr.ac.kaist.message_queue;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.mms_server.MMSConfiguration;

/* -------------------------------------------------------- */
/** 
File name : MessageQueueDequeuer.java
	
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-19
Version : 0.5.0 
*/
/* -------------------------------------------------------- */


public class MessageQueueDequeuer {
	
	String dequeueMessage (MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN, String svcMRN) {
		
		String queueName = srcMRN+"::"+svcMRN;
		 if(MMSConfiguration.LOGGING)System.out.println(" [*] Queue name = "+queueName);
		
	    try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.queueDeclare(queueName, false, false, false, null);
			if(MMSConfiguration.LOGGING)System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
			
//			GetResponse res = channel.basicGet(queueName, true);
//			
//			String message = new String(res.getBody(), "UTF-8");
//			if(MMSConfiguration.LOGGING)System.out.println(" [x] Received '" + message + "'");
			
			
			Consumer consumer = new DefaultConsumer(channel) {
			  @Override
			  public void handleDelivery(String consumerTag, Envelope envelope,
			                             AMQP.BasicProperties properties, byte[] body)
			      throws IOException {
			    String message = new String(body, "UTF-8");
			    if(MMSConfiguration.LOGGING)System.out.println(" [x] Received '" + message + "'");
			    
			    outputChannel.replyToSender(ctx, message.getBytes("UTF-8"));
			    try {
					channel.close();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					 if(MMSConfiguration.LOGGING)e.printStackTrace();
				}
			    
			  	}
			};
		channel.basicConsume(queueName, true, consumer);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 if(MMSConfiguration.LOGGING)e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			 if(MMSConfiguration.LOGGING)e.printStackTrace();
		}
		
		return null;
	}

}
