package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : MRH_MessageInputChannel.java
	
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.4.0

Rev. history : 2017-03-22
	Added SSL handler and modified MessageRelayingHandler in order to handle HTTPS functionalities.
	Added member variable protocol in order to handle HTTPS.
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

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int sessionId to String SessionId as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-15
Version : 0.7.0
	Added realtime log functions
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	Jaehyun Park (jae519@kaist.ac.kr)
	
Rev. history : 2018-04-23
Version : 0.7.1
	Removed NULL_RETURN_STD hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-03
Version : 0.7.2
	Added handling input messages by FIFO scheduling.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-04-18
Version : 0.8.2
	Catch channelInactive event and terminate Http request.
Modifier : Yunho Choi (choiking10@kaist.ac.kr)

Rev. history : 2019-05-09
Version : 0.9.0
	Added session counting functions.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-10
Version : 0.9.1
	If client is disconnected, 
	drop the duplicate id from duplicate hash map.
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)

Rev. history : 2019-05-17
Version : 0.9.1
	From now, MessageParser is initialized in MRH_MessageInputChannel class.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-23
Version : 0.9.1
	Fixed a problem where rabbitmq connection was not terminated even when client disconnected by using context-channel attribute.
Modifier : Yunho Choi (choiking10@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-29
Version : 0.9.1
	Resolved a bug related to realtime log function.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-13
Version : 0.9.2
	HOTFIX: Resolved a bug related to message ordering.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr),
		Yunho Choi (choiking10@kaist.ac.kr)
		
Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-20
Version : 0.9.2
	HOTFIX: polling authentication bug.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-03
Version : 0.9.3
	Added multi-thread safety.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-07
Version : 0.9.3
	Added resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-08
Version : 0.9.3
	Updated resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-09
Version : 0.9.3
	Revised for coding rule conformity.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-10
Version : 0.9.3
	Updated resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-14
Version : 0.9.4
	Introduced MRH_MessageInputChannel.ChannelBean.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-16
Version : 0.9.4
 	Revised bugs related to MessageOrderingHandler and SeamlessRoamingHandler.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
 
Rev. history : 2019-07-16
Version : 0.9.4
 	Added bean release() in channelInactive().
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-09-25
Version : 0.9.5
 	Revised bugs related to not allowing duplicated long polling request
 	    when a MMS Client loses connection with MMS because of unexpected network disconnection.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel.ConnectionThread;
import kr.ac.kaist.mms_server.ChannelTerminateListener;
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;
import kr.ac.kaist.mns_interaction.MNSInteractionHandler;
import io.netty.util.ReferenceCountUtil;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

import java.io.IOException;


public class MRH_MessageInputChannel extends SimpleChannelInboundHandler<FullHttpRequest>{
	public static final AttributeKey<LinkedList<ChannelTerminateListener>> TERMINATOR = AttributeKey.newInstance("terminator");

	private static final Logger logger = LoggerFactory.getLogger(MRH_MessageInputChannel.class); 

	private String sessionId = "";

	private MessageParser parser;
	private String protocol = "";
	private MMSLog mmsLog = null;
	private MMSLogForDebug mmsLogForDebug = null;
    private MessageRelayingHandler relayingHandler;
    
    private ChannelBean bean = null;
	
    private String duplicationId="";
    
	public MRH_MessageInputChannel(String protocol) {
		super();
		this.protocol = protocol;
	}
	
	/*
	public boolean isRemainJob(ChannelHandlerContext ctx) {
		ConnectionThread thread = relayingHandler.getConnectionThread();
        if (thread != null) {
        	return true;
        }
        LinkedList<ChannelTerminateListener> listeners = ctx.channel().attr(TERMINATOR).get();
        for(ChannelTerminateListener listener: listeners) {
        	return true;
        }
        return false;
	}*/
	
    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        try {
            if (acceptInboundMessage(msg)) {
                imsg = (FullHttpRequest) msg;
                channelRead0(ctx, imsg);
            } else {
                release = false;
                ctx.fireChannelRead(msg);
            }
        } finally {
        	// TODO Carefully inspect this code. There is a risk of memory leak.
            if (!isRemainJob(ctx) && release) {
                ReferenceCountUtil.release(msg);
            }
        }
    }*/
    
//	when coming http message
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		//System.out.println("Message in channelRead0");
		
		try {
			mmsLog = MMSLog.getInstance();
			mmsLogForDebug = MMSLogForDebug.getInstance();

			sessionId = ctx.channel().id().asShortText();
			SessionManager.putSessionInfo(sessionId, "");
			
			this.parser = new MessageParser(sessionId);
			bean = new ChannelBean(protocol, ctx, req, sessionId, parser);
			bean.retain();

			ctx.channel().attr(TERMINATOR).set(new LinkedList<ChannelTerminateListener>());

			//System.out.println("0-"+bean.refCnt());
			//System.out.println("0-"+bean.getReq().refCnt());
			try {
				parser.parseMessage(ctx, req);

			} catch (IOException | NumberFormatException | NullPointerException  e) {
				mmsLog.info(logger, sessionId, ErrorCode.MESSAGE_PARSING_ERROR.toString());
//				bean.getOutputChannel().replyToSender(bean, ErrorCode.MESSAGE_PARSING_ERROR.getUTF8Bytes(), 400);
//
//				return;
			} 
			if (!parser.isRealtimeLogReq()) {
				mmsLog.info(logger, sessionId, "Receive a message."); 
			}// If a request is not a realtime logging service request.
			
			String svcMRN = parser.getSvcMRN();
    		String srcMRN = parser.getSrcMRN();
    		duplicationId = srcMRN+svcMRN;

            relayingHandler = new MessageRelayingHandler(bean);
			//System.out.println("Successfully processed");
		}
		/*catch (Exception e) {
			e.printStackTrace();
		}*/
		finally {
			bean.release();
		}
	}
	
	
	
	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    	super.channelInactive(ctx);

        //System.out.println(sessionId +", ChannelInactive");
		//System.out.println(sessionId +", REFERENCE COUNT="+bean.getReq().refCnt());

        if (relayingHandler != null) {
        	ConnectionThread thread = relayingHandler.getConnectionThread();
        	if (thread != null) {
            	if (bean.refCnt() > 0) {
					mmsLog.info(logger, sessionId, ErrorCode.CLIENT_DISCONNECTED.toString());
					bean.release();
				}

                thread.terminate();
            }
        	relayingHandler = null;
        	
        }
        

        synchronized (ctx) {
        	LinkedList<ChannelTerminateListener> listeners = ctx.channel().attr(TERMINATOR).get();
            for(ChannelTerminateListener listener: listeners) {
            	listener.terminate(ctx);
            }
            ctx.channel().attr(TERMINATOR).get().clear(); // Clear the attribute. 
		}
        
        
		if (bean != null) {
			while (bean.refCnt() > 0) {
				bean.release();
			}
			bean = null;
		}
		
		if (!ctx.isRemoved()) {
			ctx.close();
		}
    }

	
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
	    //System.out.println("incomming message");
		if (ctx.pipeline().get(SslHandler.class) != null){
			// Once session is secured, send a greeting and register the channel to the global channel
	        // list so the channel received the messages from others.
	        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
		        new GenericFutureListener<Future<Channel>>() {
		            @Override
		            public void operationComplete(Future<Channel> future) throws Exception {
		                ctx.writeAndFlush(
		                        "Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
		                ctx.writeAndFlush(
		                        "Your session is protected by " +
		                                ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
		                                " cipher suite.\n");
		
		                channels.add(ctx.channel());
		            }
		        });
		}
	}
	// TODO: Youngjin Kim must inspect this following code.
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
    	String clientType = SessionManager.getSessionType(sessionId);
    	if (clientType != null) {
    		SessionManager.removeSessionInfo(sessionId);

    		if (clientType.equals("p")) {
    			mmsLog.info(logger, this.sessionId, ErrorCode.POLLING_CLIENT_DISCONNECTED.toString());
    		} 
    		else if (clientType.equals("lp")) {
    			mmsLog.info(logger, this.sessionId, ErrorCode.LONG_POLLING_CLIENT_DISCONNECTED.toString());
    		}
    		else {
    			mmsLog.info(logger, this.sessionId, ErrorCode.CLIENT_DISCONNECTED.toString());
    		}
    	}
    	if (!ctx.isRemoved()){
    		ctx.close();
    	}
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

//    	ctx.channel().
    	String clientType = SessionManager.getSessionType(sessionId);
//    	ctx.pipeline().get(HttpHeaderValues.class);
//    	channels.
    	
    	if (cause instanceof IOException && parser != null){
    		int srcPort = 0;
        	String srcIP = null;
        	String[] reqInfo;
        	final int minDynamicPort = 49152;
     
        	if (parser.getSrcIP() == null) {
            	InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        	    InetAddress inetaddress = socketAddress.getAddress();
        	    MNSInteractionHandler handler = new MNSInteractionHandler(sessionId);
        	    if (inetaddress != null) {
        	    	srcIP = inetaddress.getHostAddress(); // IP address of client
        	    }
        	    srcPort = socketAddress.getPort(); // port number of client
        	    String request = null;
        	    if(srcPort >= minDynamicPort) {
        	    	request = srcIP + ":0";
        	    } 
              else {
        	    	request = srcIP + ":" + srcPort;
        	    }
        	    
        	    String srcMRN = handler.requestIPtoMRN(request);

        	    reqInfo = new String[2];
        	    reqInfo[0] = srcIP;
        	    reqInfo[1] = srcMRN;

        	} 
          else {
        		reqInfo = new String[5];
        		reqInfo[0] = parser.getSrcIP();
        		reqInfo[1] = parser.getSrcMRN();
        		reqInfo[2] = parser.getDstIP();
        		reqInfo[3] = parser.getDstMRN();
        		reqInfo[4] = parser.getSvcMRN();
        	
        	}
    		
    	    printError(srcIP, reqInfo, clientType);
    	}
    	if (clientType != null) {
    		SessionManager.removeSessionInfo(sessionId);    		
      }

    	if (!ctx.isRemoved()){
    		  ctx.close();
      }

    }
    
    private void printError(String channelID, String[] reqInfo, String clientType){
        // reqInfo is ordering to srcIP, srcMRN, dstIP, dstMRN, svcMRN

  //    	System.out.println("\n/*****************************************/");
  //		System.out.println("The connection is disconnected by the client");
  //    	System.out.println("Error Channel ID: " + channelID);
        String errorlog = null;

        if (clientType != null){
          if(clientType.equals("p")){
  //	    System.out.println("Client type: Polling Client");
            errorlog = new String("Client Type=Polling");
          } 
           else if(clientType.equals("lp")){
  //		System.out.println("Client type: Long Polling Client");
            errorlog = new String("Client Type=Long Polling");
          } 
           else {
  //	    System.out.println("Client type: Normal Client");
            errorlog = new String("Client Type=Normal");
          }
        }
        else {
  //      System.out.println("Client type is unknown");
          errorlog = new String("Client Type=Unknown");
        }

  //	System.out.println("srcIP: " + reqInfo[0]);
  //	System.out.println("srcMRN=" +  reqInfo[1]);
        errorlog += " srcIP=" + reqInfo[0] + " srcMRN=" + reqInfo[1];
      if (reqInfo.length == 5){
  //	System.out.println("dstIP=" +  reqInfo[2]);
  //	System.out.println("dstMRN=" +  reqInfo[3]);
  //	System.out.println("svcMRN=" + reqInfo[4]);
        errorlog += " dstIP=" + reqInfo[2] + " dstMRN=" + reqInfo[3] + " svcMRN=" + reqInfo[4];
      }
  //  System.out.println("/*****************************************/");
	
      mmsLog.info(logger, this.sessionId, ErrorCode.CLIENT_DISCONNECTED.toString() + " " + errorlog + ".");
     
    }
    
    public class ChannelBean {
    	private FullHttpRequest req = null;
    	private ChannelHandlerContext ctx = null;
    	private String sessionId = null;
    	private String protocol = null;
    	private MessageParser parser = null;
    	private MRH_MessageOutputChannel moc = null;
    	private MessageTypeDecider.msgType type = null;
    	private int refCnt = 0;
    	
    	
    	ChannelBean (String protocol, ChannelHandlerContext ctx, FullHttpRequest req, String sessionId, MessageParser parser){
    		this.protocol = protocol;
    		this.ctx = ctx;
    		this.req = req;
    		this.sessionId = sessionId;
    		this.parser = parser;
    		this.refCnt = this.req.refCnt();
    	}
    	
    	public void setOutputChannel(MRH_MessageOutputChannel moc) {
    		this.moc = moc;
    	}
    	
    	public void setType(MessageTypeDecider.msgType type) {
    		this.type = type;
    	}
    	
    	public FullHttpRequest getReq() {
    		return req;
    	}
    	
    	public ChannelHandlerContext getCtx() {
    		return ctx;
    	}
    	
    	public MRH_MessageOutputChannel getOutputChannel() {
    		return moc;
    	}
    	
    	public String getProtocol() {
    		return protocol;
    	}
    	
    	public String getSessionId() {
    		return sessionId;
    	}
    	
    	public MessageParser getParser() {
    		return parser;
    	}
    	
    	public MessageTypeDecider.msgType getType(){
    		return type;
    	}
    	
 
    	public synchronized void release() {
    		//System.out.print(sessionId+", ");
    		if (req != null && req.refCnt() > 0 && refCnt > 0) {
	    		req.release();
	    		refCnt--;
				//System.out.println("REFERENCE COUNT="+req.refCnt());
    		}
    		if (req != null && req.refCnt() == 0 && refCnt == 1) {
    	    	refCnt = 0;
    	    	//System.out.println("REFERENCE COUNT=0");
    		}
    	}
    	
    	public synchronized void retain() {
    		if (req != null) {
	    		req.retain();
	    		refCnt++;
    		}
    	}
    	
    	public synchronized int refCnt() {
    		return refCnt;
    	}
    }
}
