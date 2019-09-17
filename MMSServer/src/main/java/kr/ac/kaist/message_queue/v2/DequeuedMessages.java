package kr.ac.kaist.message_queue.v2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.GetResponse;

import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSLog;

/* -------------------------------------------------------- */
/** 
File name : DequeuedMessages.java
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-09-16
Version : 0.9.5 

Rev. history : 2019-09-16
Version : 0.9.5
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

*/
/* -------------------------------------------------------- */

class DequeuedMessages {
	private static final Logger logger = LoggerFactory.getLogger(DequeuedMessages.class);
	
	protected StringBuffer message_buffer;
	protected ArrayList<String> backupMsg; 
	protected int current_message_count;
	protected int enqueued_message_count;
	protected String sessionId;
	
	protected MMSLog mmsLog;
	
	DequeuedMessages(String sessionId) {
		this(sessionId, 0);
	}
	
	DequeuedMessages(String sessionId, int enqueued_message_count) {
		// TODO Auto-generated constructor stub
		this.sessionId = sessionId;

		message_buffer = new StringBuffer();
		backupMsg = new ArrayList<String>(); 
		current_message_count = 0;
		this.enqueued_message_count = enqueued_message_count;
		
		mmsLog = MMSLog.getInstance();
	}
	
	protected String encodeMessage(GetResponse res) {
		String encoded_message = null;
		
		try {
			encoded_message = "\""+URLEncoder.encode(new String(res.getBody()),"UTF-8")+"\"";
		} catch (UnsupportedEncodingException e) {
			mmsLog.info(logger, sessionId, ErrorCode.MESSAGE_ENCODING_ERROR.toString());
			
			return null;
		}
		
		return encoded_message;
	}
	
	protected StringBuffer makeJsonMessage() {
		StringBuffer jsonMessage = new StringBuffer();
		jsonMessage.append("[");
		jsonMessage.append(message_buffer);
		jsonMessage.append("]");
		
		return jsonMessage;
	}
	
	public void append(GetResponse res) {
		String current_message = encodeMessage(res);
		
		if (current_message != null) {
		
			if (current_message_count > 0) {
				message_buffer.append(",");
			}
			
			message_buffer.append(current_message);
			backupMsg.add(0,new String(res.getBody()));
			
			current_message_count++;
		}
		
		return;
	}
	
	public void append(String body) {
		if (current_message_count > 0) {
			message_buffer.append(",");
		}
		
		message_buffer.append("\"" + body + "\"");
		backupMsg.add(0, body);
		
		current_message_count++;

		return;
	}
	
	public StringBuffer getMessageBuffer() {
		return message_buffer;
	}
	
	public String getMessages() {
		return makeJsonMessage().toString();
	}
	
	public ArrayList<String> getBackupMessages() {
		return backupMsg;
	}
	
	public int getMessageCount() {
		return current_message_count;
	}
	
	public void clear() {
		message_buffer = null;
		backupMsg = null;
		mmsLog = null;
	}

}
