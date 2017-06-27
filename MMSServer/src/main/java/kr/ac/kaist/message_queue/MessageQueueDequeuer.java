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

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final Logger logger = LoggerFactory.getLogger(MessageQueueDequeuer.class);
	private int SESSION_ID = 0;
	
	private String queueName = null;
	private String srcMRN = null;
	private MRH_MessageOutputChannel outputChannel = null;
	private ChannelHandlerContext ctx = null;
	
	MessageQueueDequeuer (int sessionId) {
		this.SESSION_ID = sessionId;
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
		
		logger.debug("SessionID="+this.SESSION_ID+" Queue name="+queueName);
	    try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(queueName, true, false, false, null);
			logger.trace("SessionID="+this.SESSION_ID+" Waiting for messages");
			if(MMSConfiguration.POLLING_METHOD == MMSConfiguration.NORMAL_POLLING){
				GetResponse res = channel.basicGet(queueName, true);
				String message = "";
				if (res != null){
					message = new String(res.getBody());
					
				} 
				
				if(MMSConfiguration.WEB_LOG_PROVIDING)MMSLog.queueLogForClient.append("[MessageQueueDequeuer] "+queueName +"<br/>"+ "��������Message: "+message +"<br/>");
				logger.trace("SessionID="+this.SESSION_ID+" Received=" + message);

			    if (SessionManager.sessionInfo.get(SESSION_ID).equals("p")) {
			    	MMSLog.nMsgWaitingPollClnt--;
			    }
			    outputChannel.replyToSender(ctx, message.getBytes());
			}
				
				//Busy waiting
//			GetResponse res = null;
//			while (res == null){
//				res = channel.basicGet(queueName, true);
//				if (res != null){
//					String message = new String(res.getBody());
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
//				String message = new String(body);
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
			
			else if (MMSConfiguration.POLLING_METHOD == MMSConfiguration.LONG_POLLING) {
			//Enroll a delivery listener to the queue channel in order to get a message from the queue.
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, false, consumer);
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			if(!ctx.isRemoved()){
				String message = new String(delivery.getBody());
				if(MMSConfiguration.WEB_LOG_PROVIDING)MMSLog.queueLogForClient.append("[MessageQueueDequeuer] "+queueName +"<br/>"+ "��������Message: "+message +"<br/>");

			    logger.trace("SessionID="+this.SESSION_ID+" Received=" + message);
			    if (SessionManager.sessionInfo.get(SESSION_ID).equals("p")) {
			    	MMSLog.nMsgWaitingPollClnt--;
			    }
			    outputChannel.replyToSender(ctx, message.getBytes());
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			} else {
				String message = new String(delivery.getBody());
				if(MMSConfiguration.WEB_LOG_PROVIDING) {
					MMSLog.queueLogForClient.append("[MessageQueueDequeuer] "+queueName +"<br/>"+ "��������Message: "+message +"<br/>");
					MMSLog.queueLogForClient.append("[MessageQueueDequeuer] "+srcMRN+" is disconnected<br/>");
					MMSLog.queueLogForClient.append("��������Requeue: "+queueName +"<br/>"+ "��������Message: "+message +"<br/>");
				}

				logger.warn("SessionID="+this.SESSION_ID+" "+srcMRN+" is disconnected. Requeue=" + message);
				channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
			}
			
			channel.close();
			connection.close();
			//Enroll a delivery listener to the queue channel in order to get a message from the queue.
			//However, it blocks exactly this thread.
			}
			
		} catch (IOException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
		} catch (TimeoutException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
		} catch (ShutdownSignalException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
		} catch (ConsumerCancelledException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
		} catch (InterruptedException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage());
		} finally {
			this.interrupt();
		}
		
		
	}

}
