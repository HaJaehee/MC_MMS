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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;

public class mmsQueue {
	
	
	public static HashMap<String, String> queue = new HashMap<String, String>();
	
	//public mmsQueue(){
	//}
	public static synchronized byte[] getMessage(String mrn) throws Exception{
		//System.out.println("get queue:" + mrn);
    	if (queue.containsKey(mrn)){
    		String ret = queue.get(mrn).trim() + "\0";
    		queue.remove(mrn);
    		System.out.println("dequeue" + ret);
    		return ret.getBytes(Charset.forName("UTF-8"));
    	}else{
    		throw new Exception("No entry");
    	}
	}
	public static synchronized void putMessage(String mrn, FullHttpRequest req) throws UnsupportedEncodingException{
		
    	if (queue.containsKey(mrn)){
    		String ret = queue.get(mrn).trim();
    		System.out.println("queuing: " + ret);
    		String requestBytes = req.content().toString(Charset.forName("UTF-8")).trim();
    		String newEntry = ret + "\n" + requestBytes;
    		System.out.println("new Entry: " + newEntry);
    		queue.put(mrn, newEntry);
    	}else{
    		String requestBytes = req.content().toString(Charset.forName("UTF-8")).trim();
    		
    		queue.put(mrn, requestBytes);
    	}
	}
}
