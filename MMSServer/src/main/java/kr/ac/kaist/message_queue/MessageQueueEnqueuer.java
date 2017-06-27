package kr.ac.kaist.message_queue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;

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
*/
/* -------------------------------------------------------- */

class MessageQueueEnqueuer {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageQueueEnqueuer.class);
	private int SESSION_ID = 0;
	
	MessageQueueEnqueuer (int sessionId) {
		this.SESSION_ID = sessionId;

	}
	
	
	void enqueueMessage(String srcMRN, String dstMRN, String message) {
		
		String queueName = dstMRN+"::"+srcMRN;
		 if(MMSConfiguration.WEB_LOG_PROVIDING)MMSLog.queueLogForClient.append("[MessageQueueEnqueuer] "+queueName +"<br/>"+ "��������Message: " + message +"<br/>");

		 logger.trace("SessionID="+this.SESSION_ID+" Queue name="+queueName +" Message=" + message +"\n");
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			Channel channel;
			
			channel = connection.createChannel();
			channel.queueDeclare(queueName, true, false, false, null);
			
			channel.basicPublish("", queueName, null, message.getBytes());
			logger.trace("SessionID="+this.SESSION_ID+" "+"Sent '" + message + "'");
			channel.close();
			connection.close();
		} catch (IOException e) {
			logger.error("SessionID="+this.SESSION_ID+" "+e.getMessage());
		} catch (TimeoutException e) {
			logger.error("SessionID="+this.SESSION_ID+" "+e.getMessage());
		}
	}
}
