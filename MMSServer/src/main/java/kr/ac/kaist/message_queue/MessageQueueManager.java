package kr.ac.kaist.message_queue;
/* -------------------------------------------------------- */
/** 
File name : MessageQueueManager.java
	Manager of the Message Queue.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-19
Version : 0.5.0 

Rev. history : 2017-04-29
Version : 0.5.3
	Added system log features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-09
Version : 0.9.0
	Added getTotalQueueNumber function using rabbitmqadmin.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-09
Version : 0.9.0
	Replaced from function using rabbitmqadmin to function using Rabbit MQ management restful API.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;


public class MessageQueueManager {
	
	private String SESSION_ID = "";
	private static final Logger logger = LoggerFactory.getLogger(MessageQueueManager.class);
	private MRH_MessageOutputChannel outputChannel = null;
	
	public MessageQueueManager(String sessionId) {
		
		this.SESSION_ID = sessionId;
		initializeModule();
	}
	
	private void initializeModule () {
		outputChannel = new MRH_MessageOutputChannel(SESSION_ID);
	}
	
	public void dequeueMessage (MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN, String svcMRN, String pollingMethod) {
		MessageQueueDequeuer mqd = new MessageQueueDequeuer(this.SESSION_ID);
		mqd.dequeueMessage(outputChannel, ctx, srcMRN, svcMRN, pollingMethod);
	}
	
	public void enqueueMessage (String srcMRN, String dstMRN, String message) {
		MessageQueueEnqueuer mqe = new MessageQueueEnqueuer(this.SESSION_ID);
		mqe.enqueueMessage(srcMRN, dstMRN, message);
	}
	
	public long getTotalQueueNumber ()  {
		
		long ret = 0;
		
		try {
			byte[] response = null;
			
			if (MMSConfiguration.getRabbitMqManagingProtocol().equals("http")) {
				response = outputChannel.sendMessage(MMSConfiguration.getRabbitMqManagingHost(), 
														MMSConfiguration.getRabbitMqManagingPort(), 
														HttpMethod.GET, 
														"/api/queues", 
														MMSConfiguration.getRabbitMqUser(), 
														MMSConfiguration.getRabbitMqPasswd());
			}
			else if (MMSConfiguration.getRabbitMqManagingProtocol().equals("https")){
				response = outputChannel.secureSendMessage(MMSConfiguration.getRabbitMqManagingHost(), 
						MMSConfiguration.getRabbitMqManagingPort(), 
						HttpMethod.GET, 
						"/api/queues", 
						MMSConfiguration.getRabbitMqUser(), 
						MMSConfiguration.getRabbitMqPasswd());
			}
			
			String strRes = new String(response);
			
			JSONArray jary = new JSONArray();
			JSONParser parser = new JSONParser();
			
			jary = (JSONArray) parser.parse(strRes);
			ret = jary.size();
			
		} 
		catch (IOException e) {
			MMSLog mmsLog = MMSLog.getInstance();
			mmsLog.warnException(logger, SESSION_ID, ErrorCode.RABBITMQ_MANAGEMENT_CONNECTION_OPEN_ERROR.toString(), e, 5);

		} 
		catch (ParseException e) {
			MMSLog mmsLog = MMSLog.getInstance();
			mmsLog.warnException(logger, SESSION_ID, ErrorCode.RABBITMQ_MANAGEMENT_CONNECTION_OPEN_ERROR.toString(), e, 5);

		}
		
		return ret;
		
		
		// Unused rabbitmqadmin.
		/*ProcessRunner pr = new ProcessRunner();
		String processOutput = "No items";
		try {
			processOutput = pr.runProcess();
		} catch (IOException | InterruptedException e) {
			logger.warn("SessionID="+SESSION_ID+" MessageQueueManager has a problem when executing rabbitmqadmin.");
			logger.warn("SessionID="+SESSION_ID+" "+e.getClass().getName()+" "+e.getStackTrace()[0]+".");
			for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
				logger.warn("SessionID="+SESSION_ID+" "+e.getStackTrace()[i]+".");
			}
		} 
		
		return getTotalQueueNumberFromProcOutput(processOutput);*/ //Unused rabbitmqadmin -- end.
	}
	
	// Unused rabbitmqadmin.
	/*private long getTotalQueueNumberFromProcOutput (String processOutput) {
	
		if (processOutput.equals("No items")) {
			return 0;
		}
		else {
			int count = 0;
			for (int i = 0; i < processOutput.length(); i++) {
			    if (processOutput.charAt(i) == '\n') {
			        count++;
			    }
			}
			return count-3;
			// Subtract the number of negligible lines.
		}
	}
	
	private class ProcessRunner { 
	    public String runProcess() throws IOException,    InterruptedException {
	    	String[] command = {""};
	    	if (SystemUtils.IS_OS_LINUX) {
	    		command = new String[] { "./rabbitmq-binary/rabbitmqadmin", "list", "queues", "vhost", "name" };
			}
	    	else if (SystemUtils.IS_OS_WINDOWS) {
	    		command = new String[] { "python.exe", "./rabbitmq-binary/rabbitmqadmin", "list", "queues", "vhost", "name" };
	    	}
	    	
	        ProcessRunner runner = new ProcessRunner();
	        StringBuilder processOutput = runner.byRuntime(command);
	        return processOutput.toString();
	    }
	    private StringBuilder byRuntime(String[] command) throws IOException, InterruptedException {
	        Runtime runtime = Runtime.getRuntime();
	        Process process = runtime.exec(command);
	        return printStream(process);
	    }
	
	    private StringBuilder printStream(Process process) throws IOException, InterruptedException {
	        process.waitFor();
	        StringBuilder ret = new StringBuilder();
	        OutputStream ost = new OutputStream() {
				
				@Override
				public void write(byte[] b, int off, int len) throws IOException {
					ret.append(new String(b).trim());
				}
	
				@Override
				public void write(int b) throws IOException {
					// Do nothing.
				}
			};
	        try (InputStream psout = process.getInputStream()) {
	            copy(psout, ost);
	        }
	        return ret;
	    }
	
	    private void copy(InputStream input, OutputStream output) throws IOException {
	        byte[] buffer = new byte[1024];
	        int n = 0;
	        while ((n = input.read(buffer)) != -1) {
	            output.write(buffer, 0, n);
	        }
	    }
		    
	}*/// Unused rabbitmqadmin -- end.
}
