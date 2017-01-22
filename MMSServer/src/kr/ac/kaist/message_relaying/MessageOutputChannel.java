package kr.ac.kaist.message_relaying;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import kr.ac.kaist.mms_server.MMSConfiguration;

public class MessageOutputChannel {
	private final String USER_AGENT = "MMSClient/0.1";

	public void replyToSender(ChannelHandlerContext ctx, byte[] data){
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
	
//  to do relaying
	public byte[] sendMessage(FullHttpRequest req, String IPAddress, int port, HttpMethod httpMethod) throws Exception { // 
	  	if(MMSConfiguration.logging)System.out.println("uri?:" + req.getUri());
		String url = "http://" + IPAddress + ":" + port + req.getUri();
		if(MMSConfiguration.logging)System.out.println(url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		//add request header
		if (httpMethod == httpMethod.POST)
			con.setRequestMethod("POST");
		else if (httpMethod == httpMethod.GET)
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
}
