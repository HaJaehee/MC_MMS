package kr.ac.kaist.message_queue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;

/* -------------------------------------------------------- */
/** 
File name : MessageQueueEnqueuer.java
	
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-19
Version : 0.5.0 

Rev. history : 2017-04-27
Version : 0.5.1
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-29
Version : 0.5.3
	Added system log features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-17
Version : 0.5.6
	Removed UTF-8 encode
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-13
Version : 0.6.0
	An unused logger statement removed 
	Replaced from random int sessionId to String sessionId as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-29
Version : 0.6.0
	Added brief logging features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-10-25
Version : 0.6.0
	Added MMSLogForDebug features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-08-05
Version : 0.8.0
	Change ip address of rabbitmq from "localhost" to "rabbitmq-db".
Modifier : Jaehyun Park (jae519@kaist.ac.kr)


Rev. history : 2018-10-05
Version : 0.8.0
	Change the host of rabbit mq from "rabbitmq-db" to "MMSConfiguration.getRabbitMqHost()".
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-06
Version : 0.9.0
	Added Rabbit MQ port number, username and password into ConnectionFactory.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

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

Rev. history : 2019-07-11
Version : 0.9.3
	Updated mqChannel closing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-12
Version : 0.9.3
	Updated mqChannel, mqConnection closing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-14
Version : 0.9.4
	Introduced MRH_MessageInputChannel.ChannelBean.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-14
Version : 0.9.4
	Updated MRH_MessageInputChannel.ChannelBean.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-09-17
Version : 0.9.5
	Indicated maximum priority of a queue.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

/* -------------------------------------------------------- */

public class MessageQueueEnqueuer {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageQueueEnqueuer.class);
	protected String sessionId = "";
	
	protected MMSLog mmsLog = null;
	
	protected static ConnectionFactory connFac = null;
	
	
	public MessageQueueEnqueuer (String sessionId) {
		this.sessionId = sessionId;
		mmsLog = MMSLog.getInstance();
		
	}
	
	
	protected byte[] enqueueMessage(MRH_MessageInputChannel.ChannelBean bean) {

		byte[] message = null;
		Connection connection = null;
		Channel channel = null;
		String queueName = bean.getParser().getDstMRN()+"::"+bean.getParser().getSrcMRN();
		if(logger.isTraceEnabled()) {
			mmsLog.trace(logger, this.sessionId, "Enqueue="+queueName +" Message=" + StringEscapeUtils.escapeXml(bean.getReq().content().toString(Charset.forName("UTF-8")).trim()));
		 }
		 else {
			 mmsLog.debug(logger, this.sessionId, "Enqueue="+queueName+".");
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
			args.put("x-max-priority", 10);
//			args.put("maxPriority", 10);
			
			channel.queueDeclare(queueName, true, false, false, args);
			
			channel.basicPublish("", queueName, null, bean.getReq().content().toString(Charset.forName("UTF-8")).trim().getBytes());
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
