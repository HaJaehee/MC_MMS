package com.kaist.MMSService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class HttpRelayHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	private final String USER_AGENT = "MMSClient/0.1";
	
	//public static mmsQueue myqueue;
    public HttpRelayHandler(){
        // super(); // set auto-release to false
    	//myqueue = new mmsQueue();
    	//System.out.println("adsfasdfa");
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
    	req.retain();
    	processHttpRequest(ctx,req);
    }

    private void processHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        try {
        	//Destination MRN에 대해서 확인한다.
        	String dstMRN;
        	dstMRN = req.headers().get("dstMRN");
        	
        	
        	//System.out.println("dstMRN: "  + dstMRN);
        	//System.out.println(req.getUri());
        	
        	if (req.getUri().equals("/polling")){
        		//Queue에서 값을 빼온다.
        		String srcMRN = req.headers().get("srcMRN");
        		try{
        			//System.out.println("srcMRN : " + srcMRN);
        			byte[] msg = mmsQueue.getMessage(srcMRN);
        			System.out.println("get some value: " + msg.toString());
        			replyToSender(ctx, msg);
        		}catch(Exception e){
        			byte[] msg = "EMPTY".getBytes();
        			//System.out.println("get EMPTY");
        			replyToSender(ctx, msg);
        			return;
        		}
        		
        	} else{
	        	//CM에 MRN을 질의한다.              
        		//System.out.println("dstMRN: " + dstMRN);
	        	String IPAddress = requestToCM("MRN-Request:" + dstMRN);
	        	//System.out.println("IPAddress = " + IPAddress);
	        	if (IPAddress.equals("No")){
	        		mmsQueue.putMessage(dstMRN, req);
	        		replyToSender(ctx, "No Device having that MRN".getBytes());
	        		return;
	        	}
	        	int port = Integer.parseInt(IPAddress.split(":")[1]);
	        	int model = Integer.parseInt(IPAddress.split(":")[2]);
	        	IPAddress = IPAddress.split(":")[0];
	        	
	        	//System.out.println("MRN: " + dstMRN + " IPAddress: " + IPAddress + " port:" + port + " model: " + model);
	        	if (model == 2){ //model B일 경우 (destination이 MSR, MIR, SP일 경우)
	        		// 릴레이
		        	HttpRelayHandler http = new HttpRelayHandler();
		        	
		        	byte[] response = http.sendPost(req, IPAddress, port);
		        	replyToSender(ctx, response);
		        	
	        	}else{
	        		mmsQueue.putMessage(dstMRN, req);
	        		replyToSender(ctx, "OK".getBytes());
	        		return;
	        	}
        	}
            
        } finally {
            req.release();
        }
    }
    
    private void replyToSender(ChannelHandlerContext ctx, byte[] data){ // Sender에게 데이터를 return 한다
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
    
    private String requestToCM(String request) throws UnknownHostException, IOException{ //CM에 Component의 위치를 질의한다.
    	
    	//String modifiedSentence;
    	String returnedIP;
    	
    	Socket CMSocket = new Socket("localhost", 1004);
    	DataOutputStream outToCM = new DataOutputStream(CMSocket.getOutputStream());
    	BufferedReader inFromCM = new BufferedReader(new InputStreamReader(CMSocket.getInputStream()));
    	
    	outToCM.writeBytes(request + '\n');
    	returnedIP = inFromCM.readLine();
    	//System.out.println("FROM SERVER: " + returnedIP);
    	if (returnedIP.equals("No"))
    		return "No";
    	returnedIP = returnedIP.substring(14);
    	CMSocket.close();
    	return returnedIP;
    	
    }
    
    
    private byte[] sendPost(FullHttpRequest req, String IPAddress, int port) throws Exception { // Server에 Data를 보낸다.
    	//System.out.println("uri?:" + req.getUri());
		String url = "http://" + IPAddress + ":" + port + req.getUri();
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		//ByteBuf imsi = Unpooled.buffer();
    	//req.content().getBytes(0, imsi, req.content().capacity());
    	//System.out.println(imsi.toString(CharsetUtil.UTF_8));
		//String urlParameters = imsi.toString(CharsetUtil.UTF_8);
		String urlParameters = req.content().toString(Charset.forName("UTF-8"));

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		try{
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(byteArr));
			//StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				//response.append(inputLine);
				out.append(inputLine); out.newLine();
			}
			out.close();
			in.close();
			
	
			//return result
			return byteArr.toByteArray();
		}catch(Exception e){
			
			return "No Reply".getBytes();
		}
		//return response.toString();

	}

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
