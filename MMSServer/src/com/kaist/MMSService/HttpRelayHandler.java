package com.kaist.MMSService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class HttpRelayHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	private final String USER_AGENT = "MMSClient/0.1";
	
	//public static mmsQueue myqueue;
    public HttpRelayHandler(){
        // super(); // set auto-release to false
    	//myqueue = new mmsQueue();
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
    	req.retain();
    	processHttpRequest(ctx,req);
    }

    private void processHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        try {
        	//Destination MRN
        	String dstMRN;
        	dstMRN = req.headers().get("dstMRN");
        	
        	
        	if(MMSConfiguration.logging)System.out.println("dstMRN: "  + dstMRN);
        	if(MMSConfiguration.logging)System.out.println(req.getUri());
        	
        	if (req.getMethod() == HttpMethod.POST && req.getUri().equals("/polling")){
        		//Queue
        		String srcMRN = req.headers().get("srcMRN");
        		try{
        			if(MMSConfiguration.logging)System.out.println("srcMRN : " + srcMRN);
        			byte[] msg = MMSQueue.getMessage(srcMRN);
        			if(MMSConfiguration.logging)System.out.println("get some value: " + msg.toString());
        			replyToSender(ctx, msg);
        		}catch(Exception e){
        			byte[] msg = "EMPTY".getBytes();
        			if(MMSConfiguration.logging)System.out.println("get EMPTY");
        			replyToSender(ctx, msg);
        			return;
        		}
        		
        	} else if (req.getMethod() == HttpMethod.POST){
	        	            
        		if(MMSConfiguration.logging)System.out.println("dstMRN: " + dstMRN);
	        	String IPAddress = requestToCM("MRN-Request:" + dstMRN);
	        	if(MMSConfiguration.logging)System.out.println("IPAddress = " + IPAddress);
	        	if (IPAddress.equals("No")){
	        		MMSQueue.putMessage(dstMRN, req);
	        		replyToSender(ctx, "No Device having that MRN".getBytes());
	        		return;
	        	}
	        	int port = Integer.parseInt(IPAddress.split(":")[1]);
	        	int model = Integer.parseInt(IPAddress.split(":")[2]);
	        	IPAddress = IPAddress.split(":")[0];
	        	if(MMSConfiguration.logging)System.out.println("model: " + model);
	        	if(MMSConfiguration.logging)System.out.println("MRN: " + dstMRN + " IPAddress: " + IPAddress + " port:" + port + " model: " + model);
	        	if (model == 2){ //model B (destination MSR, MIR, or MSP as servers)
		        	HttpRelayHandler http = new HttpRelayHandler();
		        	
		        	byte[] response = http.sendPost(req, IPAddress, port);
		        	replyToSender(ctx, response);
	        	}else{
	        		MMSQueue.putMessage(dstMRN, req);
	        		replyToSender(ctx, "OK".getBytes());
	        		return;
	        	}
	        	
        	} else if (req.getMethod() == HttpMethod.GET && req.getUri().equals("/logs")){
        		
        		String status = getStatus();
        		
        		MMSLog.log = status + MMSLog.log;
        		
        		byte[] msg = MMSLog.log.getBytes(Charset.forName("UTF-8"));
        		replyToSender(ctx, msg);

        		return;
        		
        	} else if (req.getMethod() == HttpMethod.GET && req.getUri().equals("/status")){

        		String status = getStatus();
        		
        		byte[] msg = status.getBytes(Charset.forName("UTF-8"));
        		replyToSender(ctx, msg);
        		
        		return;
        		
        	}
        	
        	else if (req.getMethod() == HttpMethod.GET && req.getUri().equals("/cleanlogs")){ 
        		MMSLog.log = "";
        		replyToSender(ctx, "OK".getBytes(Charset.forName("UTF-8")));
        		
        	}else if (req.getMethod() == HttpMethod.GET && req.getUri().equals("/savelogs")){
        		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        		String logfile = "./log"+timeStamp+".txt";
        		BufferedWriter wr = new BufferedWriter(new FileWriter(logfile));
        		String log = MMSLog.log.replaceAll("<br/>", "\n");
        		wr.write(log);
        		wr.flush();
        		wr.close();
        		replyToSender(ctx, "OK".getBytes(Charset.forName("UTF-8")));
        		
        	}else if (req.getMethod() == HttpMethod.GET){
        		if(MMSConfiguration.logging)System.out.println("dstMRN: " + dstMRN);
	        	String IPAddress = requestToCM("MRN-Request:" + dstMRN);
	        	if(MMSConfiguration.logging)System.out.println("IPAddress = " + IPAddress);
	        	if (IPAddress.equals("No")){
	        		MMSQueue.putMessage(dstMRN, req);
	        		replyToSender(ctx, "No Device having that MRN".getBytes());
	        		return;
	        	}
	        	int port = Integer.parseInt(IPAddress.split(":")[1]);
	        	int model = Integer.parseInt(IPAddress.split(":")[2]);
	        	IPAddress = IPAddress.split(":")[0];
	        	if(MMSConfiguration.logging)System.out.println("model: " + model);
	        	if(MMSConfiguration.logging)System.out.println("MRN: " + dstMRN + " IPAddress: " + IPAddress + " port:" + port + " model: " + model);
	        	if (model == 2){ //model B (destination MSR, MIR, or MSP as servers)
		        	HttpRelayHandler http = new HttpRelayHandler();
		        	
		        	byte[] response = http.sendGet(req, IPAddress, port);
		        	replyToSender(ctx, response);
	        	}else{
	        		MMSQueue.putMessage(dstMRN, req);
	        		replyToSender(ctx, "OK".getBytes());
	        		return;
	        	}
        	}
            
        } finally {
            req.release();
        }
    }
    
    private void replyToSender(ChannelHandlerContext ctx, byte[] data){ // Sender
    	ByteBuf textb = Unpooled.copiedBuffer(data);
    	long responseLen = data.length;
    	HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
    	res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8");
    	HttpUtil.setContentLength(res, responseLen);
    	ctx.write(res);
    	ctx.write(textb);
        ChannelFuture f = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        f.addListener(ChannelFutureListener.CLOSE);
    }
    
    private String requestToCM(String request) throws UnknownHostException, IOException{ //
    	
    	//String modifiedSentence;
    	String returnedIP = null;
    	
    	Socket CMSocket = new Socket("localhost", 1004);
    	
    	
    	BufferedWriter outToCM = new BufferedWriter(
					new OutputStreamWriter(CMSocket.getOutputStream(),Charset.forName("UTF-8")));
    	
    	if(MMSConfiguration.logging)System.out.println(request);
    	ServerSocket Sock = new ServerSocket(0);
    	int rplPort = Sock.getLocalPort();
    	if(MMSConfiguration.logging)System.out.println("Reply port : "+rplPort);
    	outToCM.write(request+","+rplPort);
    	outToCM.flush();
    	outToCM.close();
    	CMSocket.close();
    	
    	
    	Socket ReplySocket = Sock.accept();
    	BufferedReader inFromCM = new BufferedReader(
    			new InputStreamReader(ReplySocket.getInputStream(),Charset.forName("UTF-8")));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = inFromCM.readLine()) != null) {
			response.append(inputLine.trim());
		}
		
    	returnedIP = response.toString();
    	if(MMSConfiguration.logging)System.out.println("FROM SERVER: " + returnedIP);
    	
    	inFromCM.close();
    	
    	if (returnedIP.equals("No"))
    		return "No";
    	returnedIP = returnedIP.substring(14);
    	return returnedIP;
    }
    
    private String dumpCM() throws UnknownHostException, IOException{ //
    	
    	//String modifiedSentence;
    	String dumpedCM = "";
    	
    	Socket CMSocket = new Socket("localhost", 1004);
    	
    	BufferedWriter outToCM = new BufferedWriter(
					new OutputStreamWriter(CMSocket.getOutputStream(),Charset.forName("UTF-8")));
    	
    	if(MMSConfiguration.logging)System.out.println("Dump-CM:");
    	ServerSocket Sock = new ServerSocket(0);
    	int rplPort = Sock.getLocalPort();
    	if(MMSConfiguration.logging)System.out.println("Reply port : "+rplPort);
    	outToCM.write("Dump-CM:"+","+rplPort);
    	outToCM.flush();
    	outToCM.close();
    	CMSocket.close();
    	
    	
    	Socket ReplySocket = Sock.accept();
    	BufferedReader inFromCM = new BufferedReader(
    			new InputStreamReader(ReplySocket.getInputStream(),Charset.forName("UTF-8")));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = inFromCM.readLine()) != null) {
			response.append(inputLine.trim());
		}
		
    	dumpedCM = response.toString();
    	if(MMSConfiguration.logging)System.out.println("Dumped CM: " + dumpedCM);
    	inFromCM.close();
    	if (dumpedCM.equals("No"))
    		return "No MRN to IP mapping";
    	dumpedCM = dumpedCM.substring(14);
    	return dumpedCM;
    }
    
    private String getStatus ()  throws UnknownHostException, IOException{
    	
		String status = "";
		
		HashMap<String, String> queue = MMSQueue.queue;
		status = status + "Message Queue:<br/>";
		Set<String> queueKeys = queue.keySet();
		Iterator<String> queueKeysIter = queueKeys.iterator();
		while (queueKeysIter.hasNext() ){
			String key = queueKeysIter.next();
			if (key==null)
				continue;
			String value = queue.get(key);
			status = status + key + "," + value + "<br/>"; 
		}
		status = status + "<br/>";

		status = status + "CM Dummy:<br/>";
		status = status + dumpCM() + "<br/>";
    	
    	return status;
    }
    
    
    private byte[] sendPost(FullHttpRequest req, String IPAddress, int port) throws Exception { // 
    	if(MMSConfiguration.logging)System.out.println("uri?:" + req.getUri());
		String url = "http://" + IPAddress + ":" + port + req.getUri();
		if(MMSConfiguration.logging)System.out.println(url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = req.content().toString(Charset.forName("UTF-8")).trim();

		// Send post request
		con.setDoOutput(true);
		BufferedWriter wr = new BufferedWriter(
				new OutputStreamWriter(con.getOutputStream(),Charset.forName("UTF-8")));
		
		wr.write(urlParameters);
		wr.flush();
		wr.close();
		
		try{
			int responseCode = con.getResponseCode();
			if(MMSConfiguration.logging)System.out.println("\nSending 'POST' request to URL : " + url);
			if(MMSConfiguration.logging)System.out.println("Post parameters : " + urlParameters);
			if(MMSConfiguration.logging)System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream(),Charset.forName("UTF-8")));
			String inputLine;
			StringBuffer buf = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				buf = buf.append(inputLine + "\n"); 
			}

			in.close();
			String ret = buf.toString();
			if(MMSConfiguration.logging)System.out.println("Responsed Msg : " + ret); 
			//return result
			return ret.getBytes();
		}catch(Exception e){
			if(MMSConfiguration.logging)e.printStackTrace();
			return "No Reply".getBytes();
			
		}
		//return response.toString();	
	}
    
    private byte[] sendGet(FullHttpRequest req, String IPAddress, int port) throws Exception { // 
    	if(MMSConfiguration.logging)System.out.println("uri?:" + req.getUri());
		String url = "http://" + IPAddress + ":" + port + req.getUri();
		if(MMSConfiguration.logging)System.out.println(url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		//add reuqest header
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = req.content().toString(Charset.forName("UTF-8")).trim();

		// Send post request
		con.setDoOutput(true);
		BufferedWriter wr = new BufferedWriter(
				new OutputStreamWriter(con.getOutputStream(),Charset.forName("UTF-8")));
		
		wr.write(urlParameters);
		wr.flush();
		wr.close();
		
		try{
			int responseCode = con.getResponseCode();
			if(MMSConfiguration.logging)System.out.println("\nSending 'GET' request to URL : " + url);
			if(MMSConfiguration.logging)System.out.println("Post parameters : " + urlParameters);
			if(MMSConfiguration.logging)System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream(),Charset.forName("UTF-8")));
			String inputLine;
			StringBuffer buf = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				buf = buf.append(inputLine + "\n"); 
			}

			in.close();
			String ret = buf.toString();
			if(MMSConfiguration.logging)System.out.println("Responsed Msg : " + ret); 
			//return result
			return ret.getBytes();
		}catch(Exception e){
			if(MMSConfiguration.logging)e.printStackTrace();
			return "No Reply".getBytes();
			
		}
		//return response.toString();	
	}

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
