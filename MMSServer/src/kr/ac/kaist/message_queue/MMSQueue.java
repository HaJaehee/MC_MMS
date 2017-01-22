package kr.ac.kaist.message_queue;

import io.netty.handler.codec.http.*;
import kr.ac.kaist.mms_server.MMSConfiguration;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;

public class MMSQueue {
	
	
	public static HashMap<String, String> queue = new HashMap<String, String>();
	
	//public mmsQueue(){
	//}
	public static synchronized byte[] getMessage(String mrn) throws Exception{
		if(MMSConfiguration.logging)System.out.println("get queue:" + mrn);
    	if (queue.containsKey(mrn)){
    		String ret = queue.get(mrn).trim() + "\0";
    		queue.remove(mrn);
    		if(MMSConfiguration.logging)System.out.println("dequeue" + ret);
    		return ret.getBytes(Charset.forName("UTF-8"));
    	}else{
    		throw new Exception("No entry");
    	}
	}
	public static synchronized void putMessage(String mrn, FullHttpRequest req) throws UnsupportedEncodingException{
		
    	if (queue.containsKey(mrn)){
    		String ret = queue.get(mrn).trim();
    		if(MMSConfiguration.logging)System.out.println("queuing: " + ret);
    		String requestBytes = req.content().toString(Charset.forName("UTF-8")).trim();
    		String newEntry = ret + "\n" + requestBytes;
    		if(MMSConfiguration.logging)System.out.println("new Entry: " + newEntry);
    		queue.put(mrn, newEntry);
    	}else{
    		String requestBytes = req.content().toString(Charset.forName("UTF-8")).trim();
    		
    		queue.put(mrn, requestBytes);
    	}
	}
}
