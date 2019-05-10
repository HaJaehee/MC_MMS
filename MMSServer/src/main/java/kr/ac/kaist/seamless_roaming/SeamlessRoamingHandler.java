package kr.ac.kaist.seamless_roaming;

/* -------------------------------------------------------- */
/** 
File name : SeamlessRoamingHandler.java
	It takes polling messages and forwards messages if there are any messages in MMSQueue.
	It forwards locator of MSC to MIM.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-10-16
Version : 0.8.0
	Modified in order to interact with MNS server.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.\
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-07
Version : 0.9.0
	Duplicated polling requests are not allowed.
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.message_relaying.SessionManager;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeamlessRoamingHandler {

	private static final Logger logger = LoggerFactory.getLogger(SeamlessRoamingHandler.class);
	private String SESSION_ID = "";

	private PollingMessageHandler pmh = null;
	private SCMessageHandler scmh = null;
	private MNSInteractionHandler mih = null;

	public static HashMap<String, String> duplicateInfo = new HashMap<>();

	public SeamlessRoamingHandler(String sessionId) {
		this.SESSION_ID = sessionId;

		initializeModule();
		initializeSubModule();
	}
	
	
	private void initializeModule() {
		mih = new MNSInteractionHandler(this.SESSION_ID);

	}

	private void initializeSubModule() {
		pmh = new PollingMessageHandler(this.SESSION_ID);
		scmh = new SCMessageHandler(this.SESSION_ID);
	}

	// TODO: Youngjin Kim must inspect this following code.
	// Poll SC message in queue.
	public void processPollingMessage(MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx, String srcMRN,
			String srcIP, String pollingMethod, String svcMRN) {

		if (pollingMethod.equals("normal")) {
			SessionManager.sessionInfo.put(SESSION_ID, "p");
		} else if (pollingMethod.equals("long")) {
			SessionManager.sessionInfo.put(SESSION_ID, "lp");
		}

		// TODO: Duplicated polling request is not allowed.
		/*
		 * Jaehee Ha CODE HERE.
		 * 
		 */

		// TODO: will be deprecated.
		/*
		 * if
		 * (MMSConfiguration.MNS_HOST().equals("localhost")||MMSConfiguration.MNS_HOST()
		 * .equals("127.0.0.1")) { pmh.updateClientInfo(mih, srcMRN, srcIP); }
		 */

		// Youngjin code

		String DUPLICATE_ID = srcMRN + svcMRN;
		//System.out.println("Duplicate ID : "+DUPLICATE_ID);

		if (duplicateInfo.containsKey(DUPLICATE_ID)) {
			//System.out.println("duplicate long polling request");
			
			outputChannel.replyToSender(ctx, "duplicate long polling request".getBytes());
			//// print message?
		} else {
			duplicateInfo.put(DUPLICATE_ID, "y");
			pmh.dequeueSCMessage(outputChannel, ctx, srcMRN, svcMRN, pollingMethod);
		}

		// System.out.println("[Test Message] srcMRN: " + srcMRN + " svcMRN: " +
		// svcMRN);
		// pmh.dequeueSCMessage(outputChannel, ctx, srcMRN, svcMRN, pollingMethod);
	}

//	save SC message into queue
	public void putSCMessage(String srcMRN, String dstMRN, String message) {
		scmh.enqueueSCMessage(srcMRN, dstMRN, message);
	}
}
