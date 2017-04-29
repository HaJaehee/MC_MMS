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

Rev. history : 2017-04-27
Version : 0.5.1
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-29
Version : 0.5.3
	Added system log features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

class MessageQueueEnqueuer {
	
	private String TAG = "[MessageQueueEnqueuer:";
	private int SESSION_ID = 0;
	
	MessageQueueEnqueuer (int sessionId) {
		this.SESSION_ID = sessionId;
		this.TAG += SESSION_ID + "] ";
	}
	
	
	void enqueueMessage(String srcMRN, String dstMRN, String message) {
		
		String queueName = dstMRN+"::"+srcMRN;
		 if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Queue name = "+queueName);
		 if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Queue name = "+queueName+"\n");
		 MMSLog.queueLogForClient.append(TAG+queueName +"<br/>"+ "　　　　[Message] " + message +"<br/>");
		 MMSLog.queueLogForSAS.append(TAG+queueName +"\n"+ "　　　　[Message] " + message +"\n");
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			Channel channel;
			
			channel = connection.createChannel();
			channel.queueDeclare(queueName, true, false, false, null);
			
			channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
			if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Sent '" + message + "'");
			if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Sent '" + message + "'\n");
			
			channel.close();
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.CONSOLE_LOGGING){
				System.out.print(TAG);
				e.printStackTrace();
			}
			if(MMSConfiguration.SYSTEM_LOGGING){
				MMSLog.systemLog.append(TAG+"IOException\n");
			}
			
			
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.CONSOLE_LOGGING){
				System.out.print(TAG);
				e.printStackTrace();
			}
			if(MMSConfiguration.SYSTEM_LOGGING){
				MMSLog.systemLog.append(TAG+"TimeoutException\n");
			}
			
			
		}
	}
}
