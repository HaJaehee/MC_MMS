package kr.ac.kaist.message_queue;
/* -------------------------------------------------------- */
/** 
File name : MessageQueueDequeuer.java
	
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-19
Version : 0.5.0 

Rev. history : 2017-04-26 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-27
Version : 0.5.1
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-29
Version : 0.5.3
	Added system log features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-05-06
Version : 0.5.5
	Added SessionManager features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-07-28
Version : 0.5.9
	MMS replies message array into JSONArray form. And messages are encoded by URLEncoder, UTF-8.
	(Secure)MMSPollHandler parses JSONArray and decodes messages by URLDecoder, UTF-8.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-13
Version : 0.6.0
	Fixed mqChannel.close() and mqConnection.close() bugs
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int sessionId to String sessionId as mqConnection context mqChannel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-29
Version : 0.6.0
	Added brief logging features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-10-25
Version : 0.6.0
	Added MMSLogForDebug features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-06-25
Version : 0.7.1
	Updated AMQP client to version 5.3.0.
	Revised long polling mechanism.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-06-25
Version : 0.7.2
	Fixed closing mqChannel mqConnection problem.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-08-05
Version : 0.8.0
	Change ip address of rabbitmq from "localhost" to "rabbitmq-db.
Modifier : Jaehyun Park (jae519@kaist.ac.kr)

Rev. history : 2018-10-05
Version : 0.8.0
	Change the host of rabbit mq from "rabbitmq-db" to "MMSConfiguration.getRabbitMqHost()".
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)


Rev. history : 2019-05-06
Version : 0.9.0
	Added Rabbit MQ port number, username and password into ConnectionFactory.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-10
Version : 0.9.0
	Duplicated polling requests are not allowed.
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)

Rev. history : 2019-05-10
Version : 0.9.1
	Added function which drops duplicate polling request for normal polling.
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)

Rev. history : 2019-05-23
Version : 0.9.1
	Fixed a problem where rabbitmq mqConnection was not terminated even when client disconnected by using context-mqChannel attribute.
Modifier : Yunho Choi (choiking10@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-01
Version : 0.9.2
	Let Rabbit MQ Channels share the one Rabbit MQ mqConnection.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-03
Version : 0.9.2
	Created Rabbit MQ mqConnection pool.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-12
Version : 0.9.2
	Fixed bugs related to mqConnection pool.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-21
Version : 0.9.2
	Fixed mqChannel error.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-03
Version : 0.9.3
	Added multi-thread safety.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-07
Version : 0.9.3
	Added resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-09
Version : 0.9.3
	Revised for coding rule conformity.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-10
Version : 0.9.3
	Updated resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-11
Version : 0.9.3
	Updated mqChannel closing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-11
Version : 0.9.3
	Fixed bug related to duplicated long polling session.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-14
Version : 0.9.4
	Introduced MRH_MessageInputChannel.ChannelBean.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-14
Version : 0.9.4
	Updated MRH_MessageInputChannel.ChannelBean.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-16
Version : 0.9.4
 	Revised bugs related to MessageOrderingHandler and SeamlessRoamingHandler.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-09-11
Version : 0.9.5
 	Added a function that message is split along the maximum message size.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-09-25
Version : 0.9.5
 	Revised bugs related to not allowing duplicated long polling request
 	    when a MMS Client loses connection with MMS because of unexpected network disconnection.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-10-11
Version : 0.9.6
 	Commented out unused codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-10-25
Version : 0.9.6
 	Added isTermintated.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Basic;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.message_relaying.MessageTypeDecider;
import kr.ac.kaist.message_relaying.SessionManager;
import kr.ac.kaist.mms_server.Base64Coder;
import kr.ac.kaist.mms_server.ChannelTerminateListener;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;



public class MessageQueueDequeuer extends Thread{
	
	private static final Logger logger = LoggerFactory.getLogger(MessageQueueDequeuer.class);
	protected String sessionId = "";
	protected String duplicationId="";
	protected MRH_MessageInputChannel.ChannelBean bean = null;
	protected String queueName = null;
	protected String srcMRN = null;
	protected String svcMRN = null;
	protected MessageTypeDecider.msgType pollingMethod = MessageTypeDecider.msgType.POLLING;
	protected Channel mqChannel = null;
	protected String consumerTag = null;
	protected boolean isTerminated = false;
	protected static ArrayList<Connection> connectionPool = null;
	protected static ConnectionFactory connFac = null;
	protected static int connectionPoolSize = 0;


	
	protected MMSLog mmsLog = null;

	protected MessageQueueDequeuer (String sessionId) {
		this.sessionId = sessionId;
		this.consumerTag = sessionId;
		this.isTerminated = false;
		mmsLog = MMSLog.getInstance();
	}
	
	public void dequeueMessage (MRH_MessageInputChannel.ChannelBean bean) {
		
		
		this.srcMRN = bean.getParser().getSrcMRN();
		this.svcMRN = bean.getParser().getSvcMRN();
		this.bean = bean;
		this.queueName = srcMRN+"::"+svcMRN;
		this.pollingMethod = bean.getType();
		this.duplicationId = srcMRN+svcMRN;
		
		this.start();

	
		return;
	}
	
	public static void setConnectionPool (int poolSize) {
		connectionPoolSize = poolSize;
    	if (connFac == null) {
			connFac = new ConnectionFactory();
			connFac.setHost(MMSConfiguration.getRabbitMqHost());
			connFac.setPort(MMSConfiguration.getRabbitMqPort());
			connFac.setUsername(MMSConfiguration.getRabbitMqUser());
			connFac.setPassword(MMSConfiguration.getRabbitMqPasswd());
		}
		if (connectionPool == null) {
			connectionPool = new ArrayList<Connection>();
			for (int i = 0 ; i < connectionPoolSize ; i++) {
				connectionPool.add(null);
			}
		}
	}
	
	@Deprecated
	public void run() {
//		Implementations are in MessageLimitSizeDequeuer class.
	}

	public void clear(boolean clearMqChannel, boolean clearMrns) {
		this.pollingMethod = null;
		this.duplicationId = null;
		if (clearMrns) {
			this.srcMRN = null;
			this.svcMRN = null;
			this.queueName = null;
		}
		if (clearMqChannel) {
			if(bean != null && bean.refCnt() > 0) {
				//System.out.println("The request is released.");
				//System.out.println("5-"+bean.refCnt());
				//System.out.println("5-"+bean.getReq().refCnt());
				bean.release();
				bean = null;
			}
			this.mqChannel = null;
		}
	}
}
