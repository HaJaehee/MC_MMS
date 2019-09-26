package kr.ac.kaist.message_queue.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.message_queue.MessageQueueEnqueuer;
import kr.ac.kaist.message_queue.MessageQueueManager;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel.ChannelBean;

/* -------------------------------------------------------- */
/** 
File name : PriorityMessageQueueManager.java
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-09-10
Version : 0.9.5 

Rev. history : 2019-09-10
Version : 0.9.5
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-09-23
Version : 0.9.5
	Added max, min priority.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class PriorityMessageQueueManager extends MessageQueueManager {
	
	protected int priority;
	private static final Logger logger = LoggerFactory.getLogger(PriorityMessageQueueManager.class);
	public static final int DEFAULT_PRIORITY = 0;
	public static final int MAX_PRIORITY = 10;
	public static final int MIN_PRIORITY = 0;

	public PriorityMessageQueueManager(String sessionId, int proirty) {
		super(sessionId);
		
		this.priority = priority;
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public byte[] enqueueMessage(ChannelBean bean) {
		// TODO Auto-generated method stub
		PriorityMessagQueueEnqueuer mqe = new PriorityMessagQueueEnqueuer(this.sessionId);
		return mqe.enqueueMessage(bean);
	}
}
