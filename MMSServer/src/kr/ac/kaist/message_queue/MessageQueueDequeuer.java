package kr.ac.kaist.message_queue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import io.netty.channel.ChannelHandlerContext;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.mms_server.MMSConfiguration;

/* -------------------------------------------------------- */
/** 
File name : MessageQueueDequeuer.java
	
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-19
Version : 0.5.0 

Rev. history : 2017-04-26 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */


public class MessageQueueDequeuer {
	
	private static final String TAG = "[MessageQueueDequeuer] ";
	
	String dequeueMessage (MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN, String svcMRN) {
		
		
		String queueName = srcMRN+"::"+svcMRN;
		 if(MMSConfiguration.LOGGING)System.out.println(TAG+" [*] Queue name = "+queueName);
		
	    try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(queueName, true, false, false, null);
			if(MMSConfiguration.LOGGING)System.out.println(TAG+" [*] Waiting for messages. To exit press CTRL+C");
			
			//Busy waiting
//			GetResponse res = null;
//			while (res == null){
//				res = channel.basicGet(queueName, true);
//				if (res != null){
//					String message = new String(res.getBody(), "UTF-8");
//					if(MMSConfiguration.LOGGING)System.out.println(TAG+" [x] Received '" + message + "'");
//					outputChannel.replyToSender(ctx, res.getBody());
//				}
//			}
			//Busy waiting
			//It consumes CPU resources a lot.
			
			
			//Enroll a callback to queue channel
//			Consumer consumer = new DefaultConsumer(channel)
//			{
//			  @Override
//			  public void handleDelivery(String consumerTag, Envelope envelope,
//			                             AMQP.BasicProperties properties, byte[] body) {
//				 
//				String message = new String(body, "UTF-8");
//			    if(MMSConfiguration.LOGGING)System.out.println(TAG+" [x] Received '" + message + "'");
//			    if(MMSConfiguration.LOGGING)System.out.print(TAG+"\""+message+"\"");
//			    outputChannel.replyToSender(ctx, body);		  
//			  	}
//			};
//			channel.basicConsume(queueName, true, consumer);
			//Enroll a callback to queue channel
			//If there are some messages in the queue, callback is called and messages are retrieved from the queue 
			//		until the queue is empty.
			//It do not block this thread.
			
			//Enroll a delivery listener to the queue channel in order to get a message from the queue.
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, false, consumer);
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			outputChannel.replyToSender(ctx, delivery.getBody());
		    if(MMSConfiguration.LOGGING) {
		    	String message = new String(delivery.getBody(), "UTF-8");
		    	System.out.println(TAG+" [x] Received '" + message + "'");
		    	System.out.print(TAG+"\""+message+"\"");
		    }
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			channel.close();
			connection.close();
			//Enroll a delivery listener to the queue channel in order to get a message from the queue.
			//However, it blocks exactly this thread.
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 if(MMSConfiguration.LOGGING)e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			 if(MMSConfiguration.LOGGING)e.printStackTrace();
		} catch (ShutdownSignalException e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.LOGGING)e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.LOGGING)e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.LOGGING)e.printStackTrace();
		}
		
		return null;
	}

}
