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

Rev. history : 2017-10-25
Version : 0.6.0
	Added MMSLogForDebug features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-06-25
Version : 0.7.1
	Updated AMQP client to version 5.3.0.
	Revised long polling mechanism.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-06-25
Version : 0.7.2
	Fixed closing channel connection problem.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-08-05
Version : 0.8.0
	Change ip address of rabbitmq from "localhost" to "rabbitmq-db.
Modifier : Jaehyun Park (jae519@kaist.ac.kr)

Rev. history : 2018-10-05
Version : 0.8.0
	Change the host of rabbit mq from "rabbitmq-db" to "MMSConfiguration.getRabbitMqHost()".
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)


Rev. history : 2019-05-06
Version : 0.9.0
	Added Rabbit MQ port number, username and password into ConnectionFactory.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-10
Version : 0.9.0
	Duplicated polling requests are not allowed.
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)

Rev. history : 2019-05-10
Version : 0.9.1
	Added function which drops duplicate polling request for normal polling.
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)

Rev. history : 2019-05-23
Version : 0.9.1
	Fixed a problem where rabbitmq connection was not terminated even when client disconnected by using context-channel attribute.
Modifier : Yunho Choi (choiking10@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-01
Version : 0.9.2
	Let Rabbit MQ Channels share the one Rabbit MQ Connection.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-03
Version : 0.9.2
	Created Rabbit MQ Connection pool.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-12
Version : 0.9.2
	Fixed bugs related to connection pool.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringEscapeUtils;
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
import io.netty.channel.ChannelHandlerContext;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.message_relaying.SessionManager;
import kr.ac.kaist.mms_server.Base64Coder;
import kr.ac.kaist.mms_server.ChannelTerminateListener;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;



public class MessageQueueDequeuer extends Thread{
	
	private static final Logger logger = LoggerFactory.getLogger(MessageQueueDequeuer.class);
	private String SESSION_ID = "";
	private String DUPLICATE_ID="";
	private String queueName = null;
	private String srcMRN = null;
	private String svcMRN = null;
	private String pollingMethod = "normal";
	private MRH_MessageOutputChannel outputChannel = null;
	private ChannelHandlerContext ctx = null;
	private Channel channel = null;
	private static ArrayList<Connection> connectionPool = null;
	private static ConnectionFactory connFac = null;
	private static int connectionPoolSize = 0;
	
	
	private MMSLog mmsLog = null;
	MessageQueueDequeuer (String sessionId) {
		this.SESSION_ID = sessionId;
		mmsLog = MMSLog.getInstance();
	}
	
	void dequeueMessage (MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN, String svcMRN, String pollingMethod) {
		
		this.queueName = srcMRN+"::"+svcMRN;
		this.srcMRN = srcMRN;
		this.svcMRN = svcMRN;
		this.outputChannel = outputChannel;
		this.ctx = ctx;
		this.pollingMethod = pollingMethod;
		this.DUPLICATE_ID = srcMRN+svcMRN;		
		
		this.start();

	
		return;
	}
	
	public static void setConnectionPool (int poolSize) {
		connectionPoolSize = poolSize;
    	if (connFac == null) {
			connFac = new ConnectionFactory();
			connFac.setHost(MMSConfiguration.getRabbitMqHost());
			connFac.setPort(MMSConfiguration.getRabbitMqPort());
			connFac.setUsername(MMSConfiguration.getRabbitMqUser());
			connFac.setPassword(MMSConfiguration.getRabbitMqPasswd());
		}
		if (connectionPool == null) {
			connectionPool = new ArrayList<Connection>();
			for (int i = 0 ; i < connectionPoolSize ; i++) {
				connectionPool.add(null);
			}
		}
	}
	
	@Override
	public void run() {
		// TODO: Youngjin Kim must inspect this following code.
		super.run();
		try {

			
			int connId = (int) (Long.decode("0x"+this.SESSION_ID) % connectionPoolSize);
			if (connectionPool.get(connId) == null || !connectionPool.get(connId).isOpen()) {
				connectionPool.set(connId, connFac.newConnection());
			}
			
			channel = connectionPool.get(connId).createChannel();
			ctx.channel().attr(MRH_MessageInputChannel.TERMINATOR).get().add(new ChannelTerminateListener() {
				
				@Override
				public void terminate(ChannelHandlerContext ctx) {
					// TODO Auto-generated method stub
					try {
						
						if(channel != null && channel.isOpen()) {
							channel.close();
						}
					} catch (IOException | TimeoutException e) {
						mmsLog.warnException(logger, SESSION_ID, ErrorCode.RABBITMQ_CHANNEL_CLOSE_ERROR.toString(), e, 5);
				    	
					} 
				}
			});
			
			channel.queueDeclare(queueName, true, false, false, null);
			
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
				
				mmsLog.debug(logger, this.SESSION_ID, "Dequeue="+queueName+".");
		  
		    	if (SessionManager.getSessionInfo().get(this.SESSION_ID) != null) {
		    		SessionManager.getSessionInfo().remove(this.SESSION_ID);
		    	}
		    	if(SeamlessRoamingHandler.getDuplicateInfo().get(DUPLICATE_ID)!=null) {
		    		SeamlessRoamingHandler.getDuplicateInfo().remove(DUPLICATE_ID);
		    	}
			    outputChannel.replyToSender(ctx, message.toString().getBytes());
			} 
			else { //If the queue does not have any message, message count == 0
				message.setLength(0);
				if (pollingMethod.equals("normal") ) {//If polling method is normal polling
					mmsLog.debug(logger, this.SESSION_ID, "Empty queue="+queueName+".");

			    	if (SessionManager.getSessionInfo().get(this.SESSION_ID) != null) {
			    		SessionManager.getSessionInfo().remove(this.SESSION_ID);
			    	}

			    	if(SeamlessRoamingHandler.getDuplicateInfo().get(DUPLICATE_ID)!=null) {
			    		SeamlessRoamingHandler.getDuplicateInfo().remove(DUPLICATE_ID);
			    	}
				    outputChannel.replyToSender(ctx, message.toString().getBytes());
				}
				
				else if (pollingMethod.equals("long")){ //If polling method is long polling
					//Enroll a delivery listener to the queue channel in order to get a message from the queue.
					mmsLog.debug(logger, this.SESSION_ID, "Client is waiting the message queue="+queueName+".");
					
					//TODO: Even though a polling client disconnects long polling session, this DefaultConsumer holds a channel.
					//When a polling client disconnects long polling session, this DefaultConsumer have to free the channel. 
					Consumer consumer = new DefaultConsumer(channel) {
						 @Override
						  public void handleDelivery(String consumerTag, Envelope envelope,
						                             AMQP.BasicProperties properties, byte[] body)
						      throws IOException {
						    String dqMessage = new String(body, "UTF-8");
						    if(!ctx.isRemoved()){
								message.append("[\""+URLEncoder.encode(dqMessage,"UTF-8")+"\"]");
								
								mmsLog.debug(logger, SESSION_ID, "Dequeue="+queueName+".");

						    	if (SessionManager.getSessionInfo().get(SESSION_ID) != null) {
						    		SessionManager.getSessionInfo().remove(SESSION_ID);
						    	}
						    	
						    	if(SeamlessRoamingHandler.getDuplicateInfo().get(DUPLICATE_ID)!=null) {
						    		SeamlessRoamingHandler.getDuplicateInfo().remove(DUPLICATE_ID);
						    	}
						    	
							    outputChannel.replyToSender(ctx, message.toString().getBytes());
								channel.basicAck(envelope.getDeliveryTag(), false);
							} else {
								mmsLog.debug(logger, SESSION_ID, "Dequeue="+queueName+".");
								mmsLog.info(logger, SESSION_ID, ErrorCode.CLIENT_DISCONNECTED.toString()+" srcMRN="+ srcMRN+". Re-enqueue the messages.");

								channel.basicNack(envelope.getDeliveryTag(), false, true);
							}
	
						    try {
						    	if (this.getChannel() != null && this.getChannel().isOpen()) {
						    		this.getChannel().basicCancel(this.getConsumerTag());
						    	}
						    	if (this.getChannel() != null && this.getChannel().isOpen()) {
						    		this.getChannel().close();
						    	}
						    	/*if (connection != null) {
						    		connection.close();
						    	}*/
							} catch (TimeoutException e) {
								mmsLog.warnException(logger, SESSION_ID, ErrorCode.RABBITMQ_CHANNEL_CLOSE_ERROR.toString(), e, 5);
							}

						  }
					};
					channel.basicConsume(queueName, false, consumer);
	
					
					//Enroll a  to the queue channel in order to get a message from the queue.
					//However, it does not block exactly this thread.
				}
				
				
				//QueueingConsumer is deprecated from amqp-client-5.3.0.jar.
				/*
				 else { //If polling method of service having svcMRN is long polling
				 
					//Enroll a delivery listener to the queue channel in order to get a message from the queue.
					if(MMSConfiguration.WEB_LOG_PROVIDING) {
						String log = "SessionID="+this.SESSION_ID+" Client is waiting message queue="+queueName+".";
						mmsLog.addBriefLogForStatus(log);
						mmsLogForDebug.addLog(this.SESSION_ID, log);
					}
					logger.debug("SessionID="+this.SESSION_ID+" Client is waiting message queue="+queueName+".");
					QueueingConsumer consumer = new QueueingConsumer(channel);
					channel.basicConsume(queueName, false, consumer);
					QueueingConsumer.Delivery delivery = consumer.nextDelivery();
					if(!ctx.isRemoved()){
						message.append("[\""+URLEncoder.encode(new String(delivery.getBody()),"UTF-8")+"\"]");
						
						if(MMSConfiguration.WEB_LOG_PROVIDING) {
							String log = "SessionID="+this.SESSION_ID+" Dequeue="+queueName+".";
							mmsLog.addBriefLogForStatus(log);
							mmsLogForDebug.addLog(this.SESSION_ID, log);
						}
						logger.debug("SessionID="+this.SESSION_ID+" Dequeue="+queueName+".");
				    	
				    	if (SessionManager.sessionInfo.get(this.SESSION_ID) != null) {
				    		SessionManager.sessionInfo.remove(this.SESSION_ID);
				    	}
					    outputChannel.replyToSender(ctx, message.toString().getBytes());
						channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
					} else {
						message.append(new String(delivery.getBody()));

						if(MMSConfiguration.WEB_LOG_PROVIDING) {
							String log = "SessionID="+this.SESSION_ID+" Dequeue="+queueName+".";
							mmsLog.addBriefLogForStatus(log);
							mmsLogForDebug.addLog(this.SESSION_ID, log);
							log = "SessionID="+this.SESSION_ID+" "+srcMRN+" is disconnected. Requeue.";
							mmsLog.addBriefLogForStatus(log);
							mmsLogForDebug.addLog(this.SESSION_ID, log);
						}
						logger.debug("SessionID="+this.SESSION_ID+" Dequeue="+queueName+".");
						logger.warn("SessionID="+this.SESSION_ID+" "+srcMRN+" is disconnected. Requeue.");
						channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
					}
					
					
					//Enroll a delivery listener to the queue channel in order to get a message from the queue.
					//However, it blocks exactly this thread.
				}*/
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
	    catch (IOException | TimeoutException | ConsumerCancelledException e) {
	    	mmsLog.warnException(logger, SESSION_ID, ErrorCode.RABBITMQ_CONNECTION_OPEN_ERROR.toString(), e, 5);

		} 

	    finally {
	    	if (pollingMethod.equals("normal")) { // Polling method: normal polling
	    		if (channel != null && channel.isOpen()) {
		    		try {
						channel.close();
					} catch (IOException | TimeoutException e) {
						mmsLog.warnException(logger, SESSION_ID, ErrorCode.RABBITMQ_CHANNEL_CLOSE_ERROR.toString(), e, 5);
					}
		    	}
				/*if (connection != null) {
					try {
						connection.close();
					} catch (IOException e) {
						mmsLog.warnException(logger, SESSION_ID, "", e, 5);
					}
				}*/
	    	}
		}
		
		
	}

}
