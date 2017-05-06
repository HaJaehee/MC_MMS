package kr.ac.kaist.message_queue;


/* -------------------------------------------------------- */
/** 
File name : MessageQueueDequeuer.java
	
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-19
Version : 0.5.0 

Rev. history : 2017-04-26 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-27
Version : 0.5.1
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-29
Version : 0.5.3
	Added system log features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-05-06
Version : 0.5.5
	Added SessionManager features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

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
import kr.ac.kaist.message_relaying.SessionManager;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;



class MessageQueueDequeuer extends Thread{
	
	private String TAG = "[MessageQueueDequeuer:";
	private int SESSION_ID = 0;
	
	private String queueName = null;
	private String srcMRN = null;
	private MRH_MessageOutputChannel outputChannel = null;
	private ChannelHandlerContext ctx = null;
	
	MessageQueueDequeuer (int sessionId) {
		this.SESSION_ID = sessionId;
		this.TAG += SESSION_ID + "] ";
	}
	
	void dequeueMessage (MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN, String svcMRN) {
		
		this.queueName = srcMRN+"::"+svcMRN;
		this.srcMRN = srcMRN;
		this.outputChannel = outputChannel;
		this.ctx = ctx;
		
		
		this.start();

	
		return;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+" Queue name = "+queueName);
		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+" Queue name = "+queueName+"\n");
		
	    try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(queueName, true, false, false, null);
			if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+" Waiting for messages. To exit press CTRL+C");
			if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+" Waiting for messages. To exit press CTRL+C\n");
			
			//Busy waiting
//			GetResponse res = null;
//			while (res == null){
//				res = channel.basicGet(queueName, true);
//				if (res != null){
//					String message = new String(res.getBody(), "UTF-8");
//					if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+" [x] Received '" + message + "'");
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
//			    if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+" [x] Received '" + message + "'");
//			    if(MMSConfiguration.CONSOLE_LOGGING)System.out.print(TAG+"\""+message+"\"");
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
			if(!ctx.isRemoved()){
				String message = new String(delivery.getBody(), "UTF-8");
				if(MMSConfiguration.WEB_LOG_PROVIDING)MMSLog.queueLogForClient.append(TAG+queueName +"<br/>"+ "　　　　[Message] "+message +"<br/>");
				if(MMSConfiguration.AUTO_SAVE_STATUS)MMSLog.queueLogForSAS.append(TAG+queueName +"\n"+ "　　　　[Message] "+message +"\n");
			    if(MMSConfiguration.CONSOLE_LOGGING) {
			    	System.out.println(TAG+" Received '" + message + "'");
			    	System.out.print(TAG+"'"+message+"'\n");
			    }
			    
			    if(MMSConfiguration.SYSTEM_LOGGING) {
			    	MMSLog.systemLog.append(TAG+" Received '" + message + "'\n");
			    	MMSLog.systemLog.append(TAG+"'"+message+"'\n");
			    }
			    
			    if (SessionManager.sessionInfo.get(SESSION_ID).equals("p")) {
			    	MMSLog.nMsgWaitingPollClnt--;
			    }
			    outputChannel.replyToSender(ctx, delivery.getBody());
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			} else {
				String message = new String(delivery.getBody(), "UTF-8");
				if(MMSConfiguration.WEB_LOG_PROVIDING) {
					MMSLog.queueLogForClient.append(TAG+queueName +"<br/>"+ "　　　　[Message] "+message +"<br/>");
					MMSLog.queueLogForClient.append(TAG+srcMRN+" is disconnected<br/>");
					MMSLog.queueLogForClient.append("　　　　[Requeue] "+queueName +"<br/>"+ "　　　　[Message] "+message +"<br/>");
				}
				if(MMSConfiguration.AUTO_SAVE_STATUS){
					MMSLog.queueLogForSAS.append(TAG+queueName +"\n"+ "　　　　[Message] "+message +"\n");
					MMSLog.queueLogForSAS.append(TAG+srcMRN+" is disconnected<br/>");
					MMSLog.queueLogForSAS.append("　　　　[Requeue] "+queueName +"\n"+ "　　　　[Message] "+message +"\n");
				}
				if(MMSConfiguration.CONSOLE_LOGGING) {
					System.out.println(TAG+" Received '" + message + "'");
					System.out.println(TAG+" MRH_MessageOutputChannel disconnected");
			    	System.out.println(TAG+" Requeue '" + message + "'");
			    }
				if(MMSConfiguration.SYSTEM_LOGGING) {
					MMSLog.systemLog.append(TAG+" Received '" + message + "'\n");
					MMSLog.systemLog.append(TAG+" MRH_MessageOutputChannel disconnected\n");
					MMSLog.systemLog.append(TAG+" Requeue '" + message + "'\n");
				}
				channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
			}
			
			channel.close();
			connection.close();
			//Enroll a delivery listener to the queue channel in order to get a message from the queue.
			//However, it blocks exactly this thread.
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.CONSOLE_LOGGING){
				System.out.print(TAG);
				e.printStackTrace();
			}
			if(MMSConfiguration.SYSTEM_LOGGING){
				MMSLog.systemLog.append(TAG+"IOException\n");
			}
			
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.CONSOLE_LOGGING){
				System.out.print(TAG);
				e.printStackTrace();
			}
			if(MMSConfiguration.SYSTEM_LOGGING){
				MMSLog.systemLog.append(TAG+"TimeoutException\n");
			}
			
		} catch (ShutdownSignalException e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.CONSOLE_LOGGING){
				System.out.print(TAG);
				e.printStackTrace();
			}
			if(MMSConfiguration.SYSTEM_LOGGING){
				MMSLog.systemLog.append(TAG+"ShutdownSignalException\n");
			}
			
		} catch (ConsumerCancelledException e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.CONSOLE_LOGGING){
				System.out.print(TAG);
				e.printStackTrace();
			}
			if(MMSConfiguration.SYSTEM_LOGGING){
				MMSLog.systemLog.append(TAG+"ConsumerCancelledException\n");
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.CONSOLE_LOGGING){
				System.out.print(TAG);
				e.printStackTrace();
			}
			if(MMSConfiguration.SYSTEM_LOGGING){
				MMSLog.systemLog.append(TAG+"InterruptedException\n");
			}
			
		} finally {
			this.interrupt();
		}
		
		
	}

}
