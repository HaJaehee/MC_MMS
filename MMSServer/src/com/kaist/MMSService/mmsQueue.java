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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;

public class mmsQueue {
	
	
	public static HashMap<String, byte[]> queue = new HashMap<String, byte[]>();
	
	//public mmsQueue(){
	//}
	public static synchronized byte[] getMessage(String mrn) throws Exception{
		//System.out.println("get queue:" + mrn);
    	if (queue.containsKey(mrn)){
    		byte[] ret = queue.get(mrn);
    		queue.remove(mrn);
    		return ret;
    	}else{
    		throw new Exception("No entry");
    	}
	}
	public static synchronized void putMessage(String mrn, FullHttpRequest req){
		//String dstMRN;
    	//dstMRN = req.headers().get("dstMRN");
    	//System.out.println("dstMRN: "  + dstMRN);
    	if (queue.containsKey(mrn)){
    		//byte[] ret = queue.get(mrn);
    		String ret = queue.get(mrn).toString();
    		//String ret = new String(queue.get(mrn));
    		//byte[] requestBytes = new byte[req.content().capacity()];//= new byte[2048];//= new byte[req.content().capacity()];
    		String requestBytes = req.content().toString(Charset.forName("UTF-8"));
        	//byte[] requestBytes = req.content().toString(Charset.forName("UTF-8")).getBytes();
    		String newEntry = ret + "," + requestBytes;
    		
    		//byte[] newEntry  = new byte[ret.length + requestBytes.length + 1];
    		//System.arraycopy(ret, 0, newEntry, 0, ret.length);
    		//newEntry[ret.length] = 44; // set ","
    		//System.arraycopy(requestBytes, 0, newEntry, ret.length + 1, requestBytes.length);
    		queue.put(mrn, newEntry.getBytes());
    	}else{
    		
    		byte[] requestBytes = new byte[req.content().capacity()];
    		//System.out.println("put queue:" + requestBytes + " mrn:" + mrn);
    		req.content().getBytes(0, requestBytes);
    		queue.put(mrn, requestBytes);
    	}
	}
}
