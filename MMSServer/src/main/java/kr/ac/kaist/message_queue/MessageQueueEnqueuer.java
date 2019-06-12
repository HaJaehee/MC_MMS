package kr.ac.kaist.message_queue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

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
*/

/* -------------------------------------------------------- */

class MessageQueueEnqueuer {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageQueueEnqueuer.class);
	private String SESSION_ID = "";
	
	private MMSLog mmsLog = null;
	private static  Connection connection = null;
	private static ConnectionFactory connFac = null;
	private Channel channel = null;
	
	public MessageQueueEnqueuer (String sessionId) {
		this.SESSION_ID = sessionId;
		mmsLog = MMSLog.getInstance();
		
	}
	
	
	void enqueueMessage(String srcMRN, String dstMRN, String message) {
		
		String queueName = dstMRN+"::"+srcMRN;
		if(logger.isTraceEnabled()) {
			mmsLog.trace(logger, this.SESSION_ID, "Enqueue="+queueName +" Message=" + StringEscapeUtils.escapeXml(message));
		 }
		 else {
			 mmsLog.debug(logger, this.SESSION_ID, "Enqueue="+queueName+".");
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
			channel.queueDeclare(queueName, true, false, false, null);
			
			channel.basicPublish("", queueName, null, message.getBytes());
			channel.close();
			connection.close();
			
		} 
		catch (IOException e) {
			mmsLog.warnException(logger, SESSION_ID, "", e, 5);
			
		} 
		catch (TimeoutException e) {
			mmsLog.warnException(logger, SESSION_ID, "", e, 5);
		}
		finally {
    		if (channel != null) {
	    		try {
					channel.close();
				} catch (IOException | TimeoutException e) {
					mmsLog.warnException(logger, SESSION_ID, "", e, 5);
				}
	    	}
		}
	}
}
