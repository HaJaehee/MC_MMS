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
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class HttpRelayHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	private final String USER_AGENT = "MMSClient/0.1";
	public static mmsQueue myqueue;
    public HttpRelayHandler(){
        // super(); // set auto-release to false
    	myqueue = new mmsQueue();
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
        	System.out.println("dstMRN: "  + dstMRN);
        	
        	//Queue에 저장한다.
        	myqueue.saveQueue(ctx, req);      	
        	
        	//CM에 MRN을 질의한다.              
        	String IPAddress = requestToCM("MRN-Request:" + dstMRN);
        	int port = Integer.parseInt(IPAddress.split(":")[1]);
        	IPAddress = IPAddress.split(":")[0];
        	System.out.println("MRN: " + dstMRN + " IPAddress: " + IPAddress + " port:" + port);
        	
        	HttpRelayHandler http = new HttpRelayHandler();
        	String response = http.sendPost(req, IPAddress, port);
        	ByteBuf textb = Unpooled.copiedBuffer(response, CharsetUtil.UTF_8);
        	long responseLen = response.length();
        	HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        	res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8");
        	HttpUtil.setContentLength(res, responseLen);
        	
        	//if (HttpUtil.isKeepAlive(req)) {
            //    res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            //}
        	
        	ctx.write(res);
        	ctx.write(textb);
            ChannelFuture f = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            System.out.println("EMPTY_LAST_CONTENT");
            f.addListener(ChannelFutureListener.CLOSE);
            //if (!HttpUtil.isKeepAlive(req)) {
            //    f.addListener(ChannelFutureListener.CLOSE);
            //}
            
        } finally {
            req.release();
        }
    }
    private String requestToCM(String request) throws UnknownHostException, IOException{
    	
    	//String modifiedSentence;
    	String returnedIP;
    	
    	Socket CMSocket = new Socket("localhost", 1004);
    	DataOutputStream outToCM = new DataOutputStream(CMSocket.getOutputStream());
    	BufferedReader inFromCM = new BufferedReader(new InputStreamReader(CMSocket.getInputStream()));
    	
    	outToCM.writeBytes(request + '\n');
    	returnedIP = inFromCM.readLine();
    	System.out.println("FROM SERVER: " + returnedIP);
    	returnedIP = returnedIP.substring(14);
    	CMSocket.close();
    	return returnedIP;
    	
    }
    private String sendPost(FullHttpRequest req, String IPAddress, int port) throws Exception {

		String url = "http://" + IPAddress + ":" + port + "/";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		ByteBuf imsi = Unpooled.buffer();
    	req.content().getBytes(0, imsi,req.content().capacity());
    	System.out.println(imsi.toString(CharsetUtil.UTF_8));
		String urlParameters = imsi.toString(CharsetUtil.UTF_8);

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//return result
		
		return response.toString();

	}

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
