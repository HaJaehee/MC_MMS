package kr.ac.kaist.message_queue.v2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import kr.ac.kaist.message_queue.MessageQueueEnqueuer;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel.ChannelBean;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSConfiguration;

/* -------------------------------------------------------- */
/** 
File name : PriorityMessagQueueEnqueuer.java
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-09-10
Version : 0.9.5 

Rev. history : 2019-09-10
Version : 0.9.5
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-09-17
Version : 0.9.5
	Indicated maximum priority of a queue.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class PriorityMessagQueueEnqueuer extends MessageQueueEnqueuer {
	
	private static final Logger logger = LoggerFactory.getLogger(PriorityMessagQueueEnqueuer.class);

	public PriorityMessagQueueEnqueuer(String sessionId) {
		super(sessionId);
	}

	@Override
	protected byte[] enqueueMessage(ChannelBean bean) {
		Connection connection = null;
		Channel channel = null;
		String queueName = bean.getParser().getDstMRN()+"::"+bean.getParser().getSrcMRN();
		if(logger.isTraceEnabled()) {
			mmsLog.trace(logger, this.sessionId, "Enqueue="+queueName +" Message=" + StringEscapeUtils.escapeXml(bean.getReq().content().toString(Charset.forName("UTF-8")).trim()));
		 }
		 else {
			 mmsLog.debug(logger, this.sessionId, "Enqueue="+queueName+" Priority=" + bean.getParser().getPriority());
		 }
		
		try {
			if (connFac == null) {
				connFac = new ConnectionFactory();
				connFac.setHost(MMSConfiguration.getRabbitMqHost());
				connFac.setPort(MMSConfiguration.getRabbitMqPort());
				connFac.setUsername(MMSConfiguration.getRabbitMqUser());
				connFac.setPassword(MMSConfiguration.getRabbitMqPasswd());
			}
			connection = connFac.newConnection();
			
			channel = connection.createChannel();
			
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("x-max-priority", PriorityMessageQueueManager.MAX_PRIORITY);
//			args.put("13", 10);
			
			channel.queueDeclare(queueName, true, false, false, args);
			
			channel.basicPublish("", queueName,
					new AMQP.BasicProperties.Builder()
					.priority(bean.getParser().getPriority())
					.build(), 
					bean.getReq().content().toString(Charset.forName("UTF-8")).trim().getBytes());
			channel.close(320, "Service stopped.");
			connection.close(320, "Service stopped.", 1000);


		} 
		catch (IOException e) {
			mmsLog.warnException(logger, sessionId, ErrorCode.RABBITMQ_CONNECTION_OPEN_ERROR.toString(), e, 5);
			return ErrorCode.RABBITMQ_CONNECTION_OPEN_ERROR.getUTF8Bytes();
			
		} 
		catch (TimeoutException e) {
			mmsLog.warnException(logger, sessionId, ErrorCode.RABBITMQ_CONNECTION_OPEN_ERROR.toString(), e, 5);
			return ErrorCode.RABBITMQ_CONNECTION_OPEN_ERROR.getUTF8Bytes();
		}
		finally {
    		if (channel != null && channel.isOpen()) {
	    		try {
					channel.close(320, "Service stopped.");
				} catch (IOException | TimeoutException e) {
					mmsLog.warnException(logger, sessionId, ErrorCode.RABBITMQ_CHANNEL_CLOSE_ERROR.toString(), e, 5);
					return ErrorCode.RABBITMQ_CHANNEL_CLOSE_ERROR.getUTF8Bytes();
				}
	    	}
    		if (connection != null && channel.isOpen()) {
	    		try {
	    			connection.close(320, "Service stopped.",1000);
				} catch (IOException e) {
					mmsLog.warnException(logger, sessionId, ErrorCode.RABBITMQ_CONNECTION_CLOSE_ERROR.toString(), e, 5);
					return ErrorCode.RABBITMQ_CONNECTION_CLOSE_ERROR.getUTF8Bytes();
				}
	    	}
		}

		return "OK".getBytes(Charset.forName("UTF-8"));
	}
}
