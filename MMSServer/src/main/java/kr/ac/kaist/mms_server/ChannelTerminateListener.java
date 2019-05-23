package kr.ac.kaist.mms_server;

/* -------------------------------------------------------- */
/** 
File name : ChannelTerminateListener.java
	
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-23
Version : 0.9.1

*/

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import io.netty.channel.ChannelHandlerContext;


public interface ChannelTerminateListener {
	public void terminate(ChannelHandlerContext ctx);
}
