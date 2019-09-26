package kr.ac.kaist.message_queue.v2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;

import io.netty.channel.ChannelHandlerContext;
import kr.ac.kaist.message_queue.MessageQueueDequeuer;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import kr.ac.kaist.message_relaying.MessageTypeDecider;
import kr.ac.kaist.message_relaying.SessionManager;
import kr.ac.kaist.mms_server.ChannelTerminateListener;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

/* -------------------------------------------------------- */
/** 
File name : MessageLimitSizeDequeuer.java
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-09-16
Version : 0.9.5 

Rev. history : 2019-09-16
Version : 0.9.5
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-09-17
Version : 0.9.5
	Indicated maximum priority of a queue.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-09-23
Version : 0.9.5
	Fixed bug.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class MessageLimitSizeDequeuer extends MessageQueueDequeuer {
	private static final Logger logger = LoggerFactory.getLogger(MessageLimitSizeDequeuer.class);
	
	protected AMQP.Queue.DeclareOk dok;
	
	public MessageLimitSizeDequeuer(String sessionId) {
		super(sessionId);
	}	
	
	/**
	 * 
	 * @param messages	stored messages
	 * @param res		dequeued message
	 * @return			if the summation of size of both stored messages and dequeued message 
	 * 						exceeds the maximum contents,
	 * 					return false
	 */
	protected boolean checkMessageSize(StringBuffer messages, GetResponse res) {
		final int MAX_SIZE = MMSConfiguration.getMaxContentSize();
		
		String input = encodeMessage(res);
		
		if (input != null) {
			int current_size = messages.toString().getBytes().length;
			int input_size = input.getBytes().length;
			
//			System.out.println("current size: " + current_size);
//			System.out.println("input size: " + input_size);
//			System.out.println("MAX Size: " + MAX_SIZE);
			
			if (MAX_SIZE < current_size + input_size) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param messages
	 * @param body
	 * @return			if the summation of size of both stored messages and dequeued message exceeds the maximum contents,
	 * 					return false
	 */
	protected boolean checkMessageSize(StringBuffer messages, String body) {
		final int MAX_SIZE = MMSConfiguration.getMaxContentSize();
		
		if (body != null) {
			int current_size = messages.toString().getBytes().length;
			int input_size = body.getBytes().length;
			
			if (MAX_SIZE < current_size + input_size) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param messages
	 * @param body
	 * @return			if the summation of size of both stored messages and dequeued message exceeds the maximum contents,
	 * 					return false
	 */
	protected boolean checkMessageSize(DequeuedMessages messages, String body) {
		final int MAX_SIZE = MMSConfiguration.getMaxContentSize();

		int current_size = messages.getMessageBuffer().toString().getBytes().length;
		int input_size = body.getBytes().length;
		
		if (MAX_SIZE < current_size + input_size) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param res
	 * @return content	if encoding from byte to string is succeeded, return the content,
	 * 					else, return null.
	 */
	public String encodeMessage(GetResponse res) {
		String content = null;
		try {
			content = "" + URLEncoder.encode(new String(res.getBody()),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			mmsLog.info(logger, sessionId, ErrorCode.MESSAGE_ENCODING_ERROR.toString());
			return null;
		}
		
		return content;
	}
	
	/**
	 * Setting connection pool and getting queue channel.
	 * @return true		if setting and getting are succeeded
	 * 		   false	other cases.
	 */
	protected boolean setQueueConnection() {
		int connId = (int) (Long.decode("0x"+this.sessionId) % connectionPoolSize);
		
		if (connectionPool.get(connId) == null || !connectionPool.get(connId).isOpen()) {
			try {
				connectionPool.set(connId, connFac.newConnection());
			} catch (IOException | TimeoutException e) {
				mmsLog.warnException(logger, sessionId, ErrorCode.RABBITMQ_CONNECTION_OPEN_ERROR.toString(), e, 5);
				this.clear(true, true);
				return false;
			}

		}
		
		try {
			mqChannel = connectionPool.get(connId).createChannel();
		} catch (IOException e1) {
			mmsLog.warnException(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_OPEN_ERROR.toString(), e1, 5);
			this.clear(true, true);
			return false;
		}

		try {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("x-max-priority", 10);
			
			dok = mqChannel.queueDeclare(queueName, true, false, false, args);
		}
		catch (IOException e) {
			mmsLog.warn(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_OPEN_ERROR.toString());
			this.clear(true, true);
			return false;
		}
		
		return true;
	}
	
	protected boolean consumeMessage(DequeuedMessages dqMessages) {
		final boolean[] dequeue_flag = new boolean[1];
		dequeue_flag[0] = false;

		try {
			GetResponse res = mqChannel.basicGet(queueName, false);
			
			boolean isExceeded = checkMessageSize(dqMessages.getMessageBuffer(), res);
			
			if (isExceeded) {
		    	mqChannel.basicNack(res.getEnvelope().getDeliveryTag(), false, true);
		    	dequeue_flag[0] = false;
//		    	System.out.println("\u001B[34m" + "메시지 초과" + "\u001B[0m");
//		    	mmsLog.debug(logger, sessionId, "메시지 초과");
		    }
		    else {
		    	mqChannel.basicAck(res.getEnvelope().getDeliveryTag(), false);
		    	dqMessages.append(res);
		    	dequeue_flag[0] = true;
		    	
//		    	System.out.println("\u001B[34m" + "메시지 버퍼에 넣음" + "\u001B[0m");
//		    	mmsLog.debug(logger, sessionId, "메시지 버퍼에 넣음");
		    }
//			mqChannel.basicConsume(queueName, false, consumer);
//			mqChannel.basicConsume(queueName, false, new DefaultConsumer(mqChannel) {
//				@Override
//				public void handleDelivery(String consumerTag, Envelope envelope, 
//						BasicProperties properties, byte[] body) throws IOException {
//					String message = new String(body, "UTF-8");
//				    MessageLimitSizeDequeuer.this.consumerTag = consumerTag;
//				    
//				    boolean isExceeded = checkMessageSize(dqMessages, message);
//				    mmsLog.debug(logger, sessionId, "메시지 버퍼 크기 검사");
//				    
//				    if (isExceeded) {
//				    	mqChannel.basicNack(envelope.getDeliveryTag(), false, true);
//				    	dequeue_flag[0] = false;
////				    	System.out.println("\u001B[34m" + "메시지 초과" + "\u001B[0m");
//				    	mmsLog.debug(logger, sessionId, "메시지 초과");
//				    }
//				    else {
//				    	mqChannel.basicAck(envelope.getDeliveryTag(), false);
//				    	dqMessages.append(message);
//				    	dequeue_flag[0] = true;
//				    	
////				    	System.out.println("\u001B[34m" + "메시지 버퍼에 넣음" + "\u001B[0m");
//				    	mmsLog.debug(logger, sessionId, "메시지 버퍼에 넣음");
//				    }
//				    
//				    mqChannel.basicCancel(consumerTag);
//				}
//			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			mmsLog.warn(logger, sessionId, ErrorCode.RABBITMQ_DEQUEUE_FAIL.toString());
			return dequeue_flag[0];
		}
		
		return dequeue_flag[0];
	}
	
	@Override
	public void run() {
		// TODO: require to run the method of parent of parent (super.super.method).
//		super.run();
		
		if(!setQueueConnection()) {
			return;
		}
		
		int enqueued_message_count = dok.getMessageCount();
		DequeuedMessages dqMessages = new DequeuedMessages(sessionId);
		
//		mmsLog.debug(logger, sessionId, "메시지 꺼내기 시작");
//		mmsLog.debug(logger, sessionId, "enqueue된 메시지 수: " + enqueued_message_count);
		
		for (int i = 0; i < enqueued_message_count; i++) {
			if (!consumeMessage(dqMessages)) {
				break;
			}
		}
		
//		mmsLog.debug(logger, sessionId, "메시지 꺼내기 종료");
		
		if (dqMessages.getMessageCount() > 0) { //If the queue has a message
			
			mmsLog.debug(logger, this.sessionId, "Dequeue="+queueName+".");
	  
	    	if (SessionManager.getSessionType(this.sessionId) != null) {
	    		SessionManager.removeSessionInfo(this.sessionId);
	    	}
	    	if(SeamlessRoamingHandler.getDuplicationInfoCnt(duplicationId)!=0) {
	    		SeamlessRoamingHandler.releaseDuplicationInfo(duplicationId);
	    	}
	    	try {
	    		bean.getOutputChannel().replyToSender(bean, dqMessages.getMessages().getBytes());
	    		if(bean != null && bean.refCnt() > 0) {
					//System.out.println("The request is released.");
	    			bean.release();
	    			bean = null;
				}
	    	}
	    	catch (IOException e) {
	    		mmsLog.info(logger, sessionId, ErrorCode.CLIENT_DISCONNECTED.toString());
	    		for (String msg : dqMessages.getBackupMessages()) {
	    			try {
						mqChannel.basicPublish("", queueName, null, msg.getBytes());
					} catch (IOException e1) {
						mmsLog.warn(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_OPEN_ERROR.toString());
						dqMessages.clear();
	    	    		this.clear(true, true);
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
	    		dqMessages.clear();
	    		this.clear(true, true);
	    	}
		} 
		else { //If the queue does not have any message, message count == 0
            dqMessages.clear();

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
					this.clear(true, true);
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
						    MessageLimitSizeDequeuer.this.consumerTag = consumerTag;
						    if(mqChannel != null && mqChannel.isOpen()) {
						    	if (bean.getCtx() != null && !bean.getCtx().isRemoved()){
							    	StringBuffer message = new StringBuffer();
									message.append("[\""+URLEncoder.encode(dqMessage,"UTF-8")+"\"]");
									
									mmsLog.debug(logger, sessionId, "Dequeue="+queueName+".");
	
							    	if (SessionManager.getSessionType(sessionId) != null) {
							    		SessionManager.removeSessionInfo(sessionId);
							    	}
							    	
							    	if(SeamlessRoamingHandler.getDuplicationInfoCnt(duplicationId) != 0) {
							    		SeamlessRoamingHandler.releaseDuplicationInfo(duplicationId);
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

						int duplicateInfoCnt = SeamlessRoamingHandler.getDuplicationInfoCnt(duplicationId);
						if (duplicateInfoCnt != 0) {
							SeamlessRoamingHandler.releaseDuplicationInfo(duplicationId);
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
		}
	    
    	if (pollingMethod != null && pollingMethod == MessageTypeDecider.msgType.POLLING) { // Polling method: normal polling
    		if (mqChannel != null && mqChannel.isOpen()) {
	    		try {
					mqChannel.close(320, "Service stoppted.");
				} catch (IOException | TimeoutException e) {
					mmsLog.warnException(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_CLOSE_ERROR.toString(), e, 5);
				}
	    	}
    	}	
	}
}
