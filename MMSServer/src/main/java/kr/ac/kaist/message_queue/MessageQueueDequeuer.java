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
	Fixed mqChannel.close() and mqConnection.close() bugs
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int sessionId to String sessionId as mqConnection context mqChannel id.
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
	Fixed closing mqChannel mqConnection problem.
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
	Fixed a problem where rabbitmq mqConnection was not terminated even when client disconnected by using context-mqChannel attribute.
Modifier : Yunho Choi (choiking10@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-01
Version : 0.9.2
	Let Rabbit MQ Channels share the one Rabbit MQ mqConnection.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-03
Version : 0.9.2
	Created Rabbit MQ mqConnection pool.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-12
Version : 0.9.2
	Fixed bugs related to mqConnection pool.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-21
Version : 0.9.2
	Fixed mqChannel error.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-03
Version : 0.9.3
	Added multi-thread safety.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-07
Version : 0.9.3
	Added resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-09
Version : 0.9.3
	Revised for coding rule conformity.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-10
Version : 0.9.3
	Updated resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-11
Version : 0.9.3
	Updated mqChannel closing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-11
Version : 0.9.3
	Fixed bug related to duplicated long polling session.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-14
Version : 0.9.4
	Introduced MRH_MessageInputChannel.ChannelBean.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-14
Version : 0.9.4
	Updated MRH_MessageInputChannel.ChannelBean.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

 Rev. history : 2019-07-16
 Version : 0.9.4
 	Revised bugs related to MessageOrderingHandler and SeamlessRoamingHandler.
 Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.message_relaying.MessageTypeDecider;
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
	private String sessionId = "";
	private String duplicateId="";
	private MRH_MessageInputChannel.ChannelBean bean = null;
	private String queueName = null;
	private String srcMRN = null;
	private String svcMRN = null;
	private MessageTypeDecider.msgType pollingMethod = MessageTypeDecider.msgType.POLLING;
	private Channel mqChannel = null;
	private String consumerTag = null;
	private static ArrayList<Connection> connectionPool = null;
	private static ConnectionFactory connFac = null;
	private static int connectionPoolSize = 0;

	
	private MMSLog mmsLog = null;
	MessageQueueDequeuer (String sessionId) {
		this.sessionId = sessionId;
		mmsLog = MMSLog.getInstance();
	}
	
	void dequeueMessage (MRH_MessageInputChannel.ChannelBean bean) {
		
		
		this.srcMRN = bean.getParser().getSrcMRN();
		this.svcMRN = bean.getParser().getSvcMRN();
		this.bean = bean;
		this.queueName = srcMRN+"::"+svcMRN;
		this.pollingMethod = bean.getType();
		this.duplicateId = srcMRN+svcMRN;		
		
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
		
		int connId = (int) (Long.decode("0x"+this.sessionId) % connectionPoolSize);
		if (connectionPool.get(connId) == null || !connectionPool.get(connId).isOpen()) {
			try {
				connectionPool.set(connId, connFac.newConnection());
			} catch (IOException | TimeoutException e) {
				mmsLog.warnException(logger, sessionId, ErrorCode.RABBITMQ_CONNECTION_OPEN_ERROR.toString(), e, 5);
				clear(true, true);
				return;
			}

		}
		
		try {
			mqChannel = connectionPool.get(connId).createChannel();
		} catch (IOException e1) {
			mmsLog.warnException(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_OPEN_ERROR.toString(), e1, 5);
			clear(true, true);
			return;
		}

		


		
		try {
			mqChannel.queueDeclare(queueName, true, false, false, null);
		}
		catch (IOException e) {
			mmsLog.warn(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_OPEN_ERROR.toString());
			clear(true, true);
			return;
		}
		
		GetResponse res = null;
		StringBuffer message = new StringBuffer();
		ArrayList<String> backupMsg = new ArrayList<String>();
		message.append("[");
		
		int msgCount = 0;
		do { //Check that the queue having queueName has a message
			try {
				res = mqChannel.basicGet(queueName, true);
			}
			catch (IOException e) {
				mmsLog.warn(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_OPEN_ERROR.toString());
				message = null;
				backupMsg = null;
				clear(true, true);
				return;
			}
			
			if (res != null){
				if (msgCount > 0) {
					message.append(",");
				}
				try {
					message.append("\""+URLEncoder.encode(new String(res.getBody()),"UTF-8")+"\"");
					backupMsg.add(0,new String(res.getBody()));
				} catch (UnsupportedEncodingException e) {
					mmsLog.info(logger, sessionId, ErrorCode.MESSAGE_ENCODING_ERROR.toString());
				}
				msgCount++;

			} 
		    
		} while (res != null);
		
		if (msgCount > 0) { //If the queue has a message
			message.append("]");
			
			mmsLog.debug(logger, this.sessionId, "Dequeue="+queueName+".");
	  
	    	if (SessionManager.getSessionType(this.sessionId) != null) {
	    		SessionManager.removeSessionInfo(this.sessionId);
	    	}
	    	if(SeamlessRoamingHandler.getDuplicateInfoCnt(duplicateId)!=null) {
	    		SeamlessRoamingHandler.releaseDuplicateInfo(duplicateId);
	    	}
	    	try {
	    		bean.getOutputChannel().replyToSender(bean, message.toString().getBytes());
	    		if(bean != null && bean.refCnt() > 0) {
					//System.out.println("The request is released.");
	    			bean.release();
	    			bean = null;
				}
	    	}
	    	catch (IOException e) {
	    		mmsLog.info(logger, sessionId, ErrorCode.CLIENT_DISCONNECTED.toString());
	    		for (String msg : backupMsg) {
	    			try {
						mqChannel.basicPublish("", queueName, null, msg.getBytes());
					} catch (IOException e1) {
						mmsLog.warn(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_OPEN_ERROR.toString());
						backupMsg = null;
	    	    		message = null;
	    	    		clear(true, true);
						return;
					}
	    		}
	    	}
	    	try {
	    		if (mqChannel != null && mqChannel.isOpen()) {
	    			mqChannel.close(320, "Service stoppted.");
		    		mqChannel = null;
	    		}
	    	}
	    	catch (IOException | TimeoutException e) {
	    		mmsLog.warn(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_CLOSE_ERROR.toString());
	    		return;
	    	}
	    	finally {
	    		backupMsg = null;
	    		message = null;
	    		clear(true, true);
	    	}
		} 
		else { //If the queue does not have any message, message count == 0
			message = null;
			backupMsg = null;
			if (pollingMethod == MessageTypeDecider.msgType.POLLING ) {//If polling method is normal polling
				mmsLog.debug(logger, this.sessionId, "Empty queue="+queueName+".");

		    	if (SessionManager.getSessionType(this.sessionId) != null) {
		    		SessionManager.removeSessionInfo(this.sessionId);
		    	}

		    	try {
		    		bean.getOutputChannel().replyToSender(bean, "".getBytes());
		    	}
		    	catch (IOException e) {
		    		mmsLog.info(logger, sessionId, ErrorCode.CLIENT_DISCONNECTED.toString());
		    	}

		    	
		    	try {
		    		if (mqChannel != null && mqChannel.isOpen()) {
			    		mqChannel.close(320, "Service stoppted.");
			    		mqChannel = null;
		    		}
		    	}
		    	catch (IOException | TimeoutException e) {
		    		mmsLog.warn(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_CLOSE_ERROR.toString());
		    		return;
		    	}
				finally {
					clear(true, true);
				}

			}
			
			else if (pollingMethod == MessageTypeDecider.msgType.LONG_POLLING){ //If polling method is long polling
				//Enroll a delivery listener to the queue mqChannel in order to get a message from the queue.
				mmsLog.debug(logger, this.sessionId, "Client is waiting the message queue="+queueName+".");
				
				//TODO: Even though a polling client disconnects long polling session, this DefaultConsumer holds a mqChannel.
				//When a polling client disconnects long polling session, this DefaultConsumer have to free the mqChannel. 
				try {
					mqChannel.basicConsume(queueName, false, new DefaultConsumer(mqChannel) {
						 @Override
						  public void handleDelivery(String consumerTag, Envelope envelope,
						                             AMQP.BasicProperties properties, byte[] body)
						      throws IOException {
						    String dqMessage = new String(body, "UTF-8");
						    MessageQueueDequeuer.this.consumerTag = consumerTag;
						    if(mqChannel != null && mqChannel.isOpen()) {
						    	if (bean.getCtx() != null && !bean.getCtx().isRemoved()){
							    	StringBuffer message = new StringBuffer();
									message.append("[\""+URLEncoder.encode(dqMessage,"UTF-8")+"\"]");
									
									mmsLog.debug(logger, sessionId, "Dequeue="+queueName+".");
	
							    	if (SessionManager.getSessionType(sessionId) != null) {
							    		SessionManager.removeSessionInfo(sessionId);
							    	}
							    	
							    	if(SeamlessRoamingHandler.getDuplicateInfoCnt(duplicateId)!=null) {
							    		SeamlessRoamingHandler.releaseDuplicateInfo(duplicateId);
							    	}
							    	
							    	try {
							    		bean.getOutputChannel().replyToSender(bean, message.toString().getBytes());
							    		if(bean != null && bean.refCnt() > 0) {
											//System.out.println("The request is released.");
							    			bean.release();
							    			bean = null;
										}
							    		
						    			mqChannel.basicAck(envelope.getDeliveryTag(), false);
						    			mqChannel.basicCancel(consumerTag);
						    			mqChannel.close(320, "Service stoppted.");
							    		
								    	clear(true, true);
							    	}
								    catch (IOException | TimeoutException e) {
							    		mmsLog.info(logger, sessionId, ErrorCode.CLIENT_DISCONNECTED.toString());
							    		try {
							    			if (mqChannel != null && mqChannel.isOpen()) {
							    				mqChannel.basicNack(envelope.getDeliveryTag(), false, true);
							    				mqChannel.basicCancel(consumerTag);
							    				mqChannel.close(320, "Service stoppted.");
							    			}
							    		}
							    		catch (AlreadyClosedException | IOException | TimeoutException e1) {
							    			mmsLog.warn(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_OPEN_ERROR.toString());
							    			clear(true, true);
							    			return;
							    		}
							    	}
								} else {
	
									mmsLog.debug(logger, sessionId, "Dequeue="+queueName+".");
									mmsLog.info(logger, sessionId, ErrorCode.CLIENT_DISCONNECTED.toString()+" srcMRN="+ srcMRN+". Re-enqueue the messages.");
									
									try {
										mqChannel.basicNack(envelope.getDeliveryTag(), false, true);
										mqChannel.basicCancel(consumerTag);
										mqChannel.close(320, "Service stoppted.");
									}
									catch (AlreadyClosedException | IOException | TimeoutException e) {
										mmsLog.warn(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_OPEN_ERROR.toString());
										return;
									}
									finally {
										//System.out.println(req.refCnt());
										clear(true, true);
									}
								}
						    }
						}
					});
				}
				catch (IOException e) {
					mmsLog.warn(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_OPEN_ERROR.toString());
					clear(true, true);
				}

				
				//Enroll a  to the queue mqChannel in order to get a message from the queue.
				//However, it does not block exactly this thread.
			}
			
			//TODO: Unexpectedly a mqChannel is shutdown while transferring a response to polling client, MUST A MESSAGE IS REQUEUED.

			if (bean != null && bean.getCtx() != null) {
				bean.getCtx().channel().attr(MRH_MessageInputChannel.TERMINATOR).get().add(new ChannelTerminateListener() {
					
					@Override
					public void terminate(ChannelHandlerContext ctx) {

						Integer duplicateInfoCnt = SeamlessRoamingHandler.getDuplicateInfoCnt(duplicateId);
						if (duplicateInfoCnt != null) {
							SeamlessRoamingHandler.releaseDuplicateInfo(duplicateId);
						}
						if (bean != null && bean.refCnt() > 0) {
							//mmsLog.info(logger, sessionId, ErrorCode.CLIENT_DISCONNECTED.toString());
							try {
								//System.out.println(consumerTag);
								if(consumerTag != null && mqChannel != null && mqChannel.isOpen()) {
									//System.out.println(mqChannel.getDefaultConsumer());
									if (mqChannel.getDefaultConsumer() != null) {
										mqChannel.basicCancel(consumerTag);
									}
								}

							}
							catch (IOException e) {
								mmsLog.warnException(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_OPEN_ERROR.toString(), e, 5);
							}
							try {
								if(mqChannel != null && mqChannel.isOpen()){
									mqChannel.close(320, "Service stoppted.");
								}
							}
							catch (AlreadyClosedException | IOException | TimeoutException e) {
								mmsLog.warnException(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_CLOSE_ERROR.toString(), e, 5);
								return;
							}
							finally {
								clear(true, true);
							}
						}


					}
				});
			}
			
			
			//QueueingConsumer is deprecated from amqp-client-5.3.0.jar.
			/*
			 else { //If polling method of service having svcMRN is long polling
			 
				//Enroll a delivery listener to the queue mqChannel in order to get a message from the queue.
				if(MMSConfiguration.WEB_LOG_PROVIDING) {
					String log = "SessionID="+this.sessionId+" Client is waiting message queue="+queueName+".";
					mmsLog.addBriefLogForStatus(log);
					mmsLogForDebug.addLog(this.sessionId, log);
				}
				logger.debug("SessionID="+this.sessionId+" Client is waiting message queue="+queueName+".");
				QueueingConsumer consumer = new QueueingConsumer(mqChannel);
				mqChannel.basicConsume(queueName, false, consumer);
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				if(!ctx.isRemoved()){
					message.append("[\""+URLEncoder.encode(new String(delivery.getBody()),"UTF-8")+"\"]");
					
					if(MMSConfiguration.WEB_LOG_PROVIDING) {
						String log = "SessionID="+this.sessionId+" Dequeue="+queueName+".";
						mmsLog.addBriefLogForStatus(log);
						mmsLogForDebug.addLog(this.sessionId, log);
					}
					logger.debug("SessionID="+this.sessionId+" Dequeue="+queueName+".");
			    	
			    	if (SessionManager.sessionInfo.get(this.sessionId) != null) {
			    		SessionManager.sessionInfo.remove(this.sessionId);
			    	}
				    outputChannel.replyToSender(ctx, message.toString().getBytes());
					mqChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				} else {
					message.append(new String(delivery.getBody()));
					if(MMSConfiguration.WEB_LOG_PROVIDING) {
						String log = "SessionID="+this.sessionId+" Dequeue="+queueName+".";
						mmsLog.addBriefLogForStatus(log);
						mmsLogForDebug.addLog(this.sessionId, log);
						log = "SessionID="+this.sessionId+" "+srcMRN+" is disconnected. Requeue.";
						mmsLog.addBriefLogForStatus(log);
						mmsLogForDebug.addLog(this.sessionId, log);
					}
					logger.debug("SessionID="+this.sessionId+" Dequeue="+queueName+".");
					logger.warn("SessionID="+this.sessionId+" "+srcMRN+" is disconnected. Requeue.");
					mqChannel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
				}
				
				
				//Enroll a delivery listener to the queue mqChannel in order to get a message from the queue.
				//However, it blocks exactly this thread.
			}*/
		}

			
			//Busy waiting
//			GetResponse res = null;
//			while (res == null){
//				res = mqChannel.basicGet(queueName, true);
//				if (res != null){
//					String message = new String(res.getBody());
//					if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+" [x] Received '" + message + "'");
//					outputChannel.replyToSender(ctx, res.getBody());
//				}
//			}
		//Busy waiting
		//It consumes CPU resources a lot.
		
		
		//Enroll a callback to queue mqChannel
//			Consumer consumer = new DefaultConsumer(mqChannel)
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
//			mqChannel.basicConsume(queueName, true, consumer);
		//Enroll a callback to queue mqChannel
		//If there are some messages in the queue, callback is called and messages are retrieved from the queue 
		//		until the queue is empty.
		//It do not block this thread.
			
	    
    	if (pollingMethod != null && pollingMethod == MessageTypeDecider.msgType.POLLING) { // Polling method: normal polling
    		if (mqChannel != null && mqChannel.isOpen()) {
	    		try {
					mqChannel.close(320, "Service stoppted.");
				} catch (IOException | TimeoutException e) {
					mmsLog.warnException(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_CLOSE_ERROR.toString(), e, 5);
				}
	    	}
			/*if (mqConnection != null) {
				try {
					mqConnection.close();
				} catch (IOException e) {
					mmsLog.warnException(logger, sessionId, "", e, 5);
				}
			}*/
    	}		
	}

	public void clear(boolean clearMqChannel, boolean clearMrns) {
		this.pollingMethod = null;
		this.duplicateId = null;
		if (clearMrns) {
			this.srcMRN = null;
			this.svcMRN = null;
			this.queueName = null;
		}
		if (clearMqChannel) {
			if(bean != null && bean.refCnt() > 0) {
				//System.out.println("The request is released.");
				//System.out.println("5-"+bean.refCnt());
				//System.out.println("5-"+bean.getReq().refCnt());
				bean.release();
				bean = null;
			}
			this.mqChannel = null;
		}
	}
}
