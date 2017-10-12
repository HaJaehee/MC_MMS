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

Rev. history : 2017-07-28
Version : 0.5.9
	MMS replies message array into JSONArray form. And messages are encoded by URLEncoder, UTF-8.
	(Secure)MMSPollHandler parses JSONArray and decodes messages by URLDecoder, UTF-8.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-13
Version : 0.6.0
	Fixed channel.close() and connection.close() bugs
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-29
Version : 0.6.0
	Added brief logging features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.IOException;
import java.net.URLEncoder;
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
import kr.ac.kaist.mms_server.Base64Coder;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.seamless_roaming.PollingMethodRegDummy;



class MessageQueueDequeuer extends Thread{
	
	private static final Logger logger = LoggerFactory.getLogger(MessageQueueDequeuer.class);
	private String SESSION_ID = "";
	
	private String queueName = null;
	private String srcMRN = null;
	private String svcMRN = null;
	private MRH_MessageOutputChannel outputChannel = null;
	private ChannelHandlerContext ctx = null;
	private Channel channel = null;
	private Connection connection = null;
	
	MessageQueueDequeuer (String sessionId) {
		this.SESSION_ID = sessionId;
	}
	
	void dequeueMessage (MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN, String svcMRN) {
		
		this.queueName = srcMRN+"::"+svcMRN;
		this.srcMRN = srcMRN;
		this.svcMRN = svcMRN;
		this.outputChannel = outputChannel;
		this.ctx = ctx;
		
		
		this.start();

	
		return;
	}
	

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		String longSpace = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		logger.debug("SessionID="+this.SESSION_ID+" Dequeue, queue name="+queueName+".");
	    try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(queueName, true, false, false, null);
			logger.trace("SessionID="+this.SESSION_ID+" Start dequeueing messages.");
			
			GetResponse res = null;
			StringBuffer message = new StringBuffer();
			message.append("[");
			int msgCount = 0;
			do { //Check that the queue having queueName has a message
				res = channel.basicGet(queueName, true);
				
				if (res != null){
					if (msgCount > 0) {
						message.append(",");
					}
					message.append("\""+URLEncoder.encode(new String(res.getBody()),"UTF-8")+"\"");
					msgCount++;

				} 
			    
			} while (res != null);
			
			if (msgCount > 0) { //If the queue has a message
				message.append("]");
				//if(MMSConfiguration.WEB_LOG_PROVIDING)MMSLog.queueLogForClient.append("[MessageQueueDequeuer] "+queueName +"<br/>");
				if(MMSConfiguration.WEB_LOG_PROVIDING)MMSLog.addBriefLogForStatus("SessionID="+this.SESSION_ID+" Dequeue="+queueName+".");
				logger.debug("SessionID="+this.SESSION_ID+" Dequeue="+queueName+" .");
		    	
		    	if (SessionManager.sessionInfo.get(this.SESSION_ID) != null) {
		    		SessionManager.sessionInfo.remove(this.SESSION_ID);
		    	}

			    outputChannel.replyToSender(ctx, message.toString().getBytes());
			} 
			else { //If the queue does not have any message, message count == 0
				message.setLength(0);
				if (PollingMethodRegDummy.pollingMethodReg.get(svcMRN) == null
						 || PollingMethodRegDummy.pollingMethodReg.get(svcMRN) == PollingMethodRegDummy.NORMAL_POLLING) {
					if(MMSConfiguration.WEB_LOG_PROVIDING)MMSLog.addBriefLogForStatus("SessionID="+this.SESSION_ID+" Queue="+queueName+" is emtpy.");
					logger.debug("SessionID="+this.SESSION_ID+" Queue="+queueName+" is emtpy.");
			    	if (SessionManager.sessionInfo.get(this.SESSION_ID) != null) {
			    		SessionManager.sessionInfo.remove(this.SESSION_ID);
			    	}

				    outputChannel.replyToSender(ctx, message.toString().getBytes());
				}
				else { //If polling method of service having svcMRN is long polling
					//Enroll a delivery listener to the queue channel in order to get a message from the queue.
					if(MMSConfiguration.WEB_LOG_PROVIDING)MMSLog.addBriefLogForStatus("SessionID="+this.SESSION_ID+" Client is waiting message queue="+queueName+".");
					logger.debug("SessionID="+this.SESSION_ID+" Client is waiting message.");
					QueueingConsumer consumer = new QueueingConsumer(channel);
					channel.basicConsume(queueName, false, consumer);
					QueueingConsumer.Delivery delivery = consumer.nextDelivery();
					if(!ctx.isRemoved()){
						message.append("[\""+URLEncoder.encode(new String(delivery.getBody()),"UTF-8")+"\"]");
						//if(MMSConfiguration.WEB_LOG_PROVIDING)MMSLog.queueLogForClient.append("[MessageQueueDequeuer] "+queueName +"<br/>");
						if(MMSConfiguration.WEB_LOG_PROVIDING)MMSLog.addBriefLogForStatus("SessionID="+this.SESSION_ID+" Dequeue="+queueName+".");
						logger.debug("SessionID="+this.SESSION_ID+" Dequeue="+queueName+".");
				    	
				    	if (SessionManager.sessionInfo.get(this.SESSION_ID) != null) {
				    		SessionManager.sessionInfo.remove(this.SESSION_ID);
				    	}
					    outputChannel.replyToSender(ctx, message.toString().getBytes());
						channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
					} else {
						message.append(new String(delivery.getBody()));
						/*if(MMSConfiguration.WEB_LOG_PROVIDING) {
							MMSLog.queueLogForClient.append("[MessageQueueDequeuer] "+queueName +"<br/>");
							MMSLog.queueLogForClient.append("[MessageQueueDequeuer] "+srcMRN+" is disconnected<br/>");
							MMSLog.queueLogForClient.append(longSpace+"Requeue="+queueName +"<br/>");
						}*/
						if(MMSConfiguration.WEB_LOG_PROVIDING) {
							MMSLog.addBriefLogForStatus("SessionID="+this.SESSION_ID+" Dequeue="+queueName+".");
							MMSLog.addBriefLogForStatus("SessionID="+this.SESSION_ID+" "+srcMRN+" is disconnected. Requeue.");
						}
						logger.debug("SessionID="+this.SESSION_ID+" Dequeue="+queueName+".");
						logger.warn("SessionID="+this.SESSION_ID+" "+srcMRN+" is disconnected. Requeue.");
						channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
					}
					
					
					//Enroll a delivery listener to the queue channel in order to get a message from the queue.
					//However, it blocks exactly this thread.
				}
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
			
		} 
	    catch (IOException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
		} 
	    catch (TimeoutException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
		} 
	    catch (ShutdownSignalException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
		} 
	    catch (ConsumerCancelledException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
		} 
	    catch (InterruptedException e) {
			logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
		} 
	    finally {
	    	if (channel != null) {
	    		try {
					channel.close();
				} catch (IOException | TimeoutException e) {
					logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
				}
	    	}
			if (connection != null) {
				try {
					connection.close();
				} catch (IOException e) {
					logger.warn("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
				}
			}
		}
		
		
	}

}
