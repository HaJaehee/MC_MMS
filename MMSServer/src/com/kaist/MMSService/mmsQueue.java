package com.kaist.MMSService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;

public class mmsQueue {
	
	class channelRequestType{
		ChannelHandlerContext ctx;
		FullHttpRequest req;
	}
	
	HashMap<String, Queue> queue;
	
	public mmsQueue(){
		System.out.println("Queue initialized");
		queue = new HashMap<String, Queue>();
	}
	
	public synchronized void saveQueue(ChannelHandlerContext ctx, FullHttpRequest req){
		String dstMRN;
    	dstMRN = req.headers().get("dstMRN");
    	System.out.println("dstMRN: "  + dstMRN);
    	if (queue.containsKey(dstMRN)){
    		
    	}else{
    		
    	}
	}
}
