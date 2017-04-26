package kr.ac.kaist.message_queue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

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
*/
/* -------------------------------------------------------- */

class MessageQueueEnqueuer {
	
	private static final String TAG = "[MessageQueueEnqueuer] ";
	
	void enqueueMessage(String srcMRN, String dstMRN, String message) {
		
		String queueName = dstMRN+"::"+srcMRN;
		 if(MMSConfiguration.LOGGING)System.out.println(TAG+" [*] Queue name = "+queueName);
		 MMSLog.queueLog += TAG+queueName +"<br/>"+ "[Message] " + message +"<br/>";
		
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			Channel channel;
			
			channel = connection.createChannel();
			channel.queueDeclare(queueName, true, false, false, null);
			
			channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
			if(MMSConfiguration.LOGGING)System.out.println(TAG+" [x] Sent '" + message + "'");
			
			channel.close();
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 if(MMSConfiguration.LOGGING)e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			 if(MMSConfiguration.LOGGING)e.printStackTrace();
		}
	}
}
