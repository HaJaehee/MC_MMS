package kr.ac.kaist.message_relaying;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import javax.swing.text.html.HTMLDocument.Iterator;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import kr.ac.kaist.mms_server.MMSConfiguration;

public class MessageOutputChannel {
	private final String USER_AGENT = "MMSClient/0.1";
	private static Map<String,List<String>> storedHeader = null;
	private static boolean isStoredHeader = false;
	
	void setResponseHeader(Map<String, List<String>> storingHeader){
		isStoredHeader = true;
		storedHeader = storingHeader;
	}
	
	void replyToSender(ChannelHandlerContext ctx, byte[] data){
    	ByteBuf textb = Unpooled.copiedBuffer(data);
    	long responseLen = data.length;
    	HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
    	if (isStoredHeader){
			Set<String> resHeaderKeyset = storedHeader.keySet(); 
			for (Iterator<String> resHeaderIterator = resHeaderKeyset.iterator();resHeaderIterator.hasNext();) {
				String key = resHeaderIterator.next();
				List<String> values = storedHeader.get(key);
				for (Iterator<String> valueIterator = values.iterator();valueIterator.hasNext();) {
					String value = valueIterator.next();
					//System.out.println("key-value:  " + key + ", " + value);
					if (key != null) {
						res.headers().set(key,value);
					}
				}
			}
    	} else {
    		res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8");
    	}
    	
    	HttpUtil.setContentLength(res, responseLen);
    	ctx.write(res);
    	ctx.write(textb);
        ChannelFuture f = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        f.addListener(ChannelFutureListener.CLOSE);
    }
	
//  to do relaying
	byte[] sendMessage(FullHttpRequest req, String IPAddress, int port, HttpMethod httpMethod) throws Exception { // 
	  	if(MMSConfiguration.logging)System.out.println("uri?:" + req.getUri());
		String url = "http://" + IPAddress + ":" + port + req.getUri();
		if(MMSConfiguration.logging)System.out.println(url);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		HttpHeaders httpHeaders = req.headers();
		
		
//		Setting HTTP method
		if (httpMethod == httpMethod.POST) {
			con.setRequestMethod("POST");
		} else if (httpMethod == httpMethod.GET) {
			con.setRequestMethod("GET");
		}
		
//		Setting remaining headers
		for (Iterator<Map.Entry<String, String>> htr = httpHeaders.iterator(); htr.hasNext();) {
			Map.Entry<String, String> htrValue = htr.next();
			//System.out.println(htrValue.getKey() + " " + htrValue.getValue());
			if (!htrValue.getKey().equals("srcMRN") && !htrValue.getKey().equals("dstMRN")) {
				con.setRequestProperty(htrValue.getKey(), htrValue.getValue());
			}
		}

		String urlParameters = req.content().toString(Charset.forName("UTF-8")).trim();
		con.setRequestProperty("Content-Length", urlParameters.length() + "");
		
		
		// Send post/get request
		con.setDoOutput(true);
		BufferedWriter wr = new BufferedWriter(
				new OutputStreamWriter(con.getOutputStream(),Charset.forName("UTF-8")));
		wr.write(urlParameters);
		wr.flush();
		wr.close();
		
		try{
			int responseCode = con.getResponseCode();
			Map<String,List<String>> resHeaders = con.getHeaderFields();
			setResponseHeader(resHeaders);
			
			if(MMSConfiguration.logging)System.out.println("\nSending '"+(httpMethod==httpMethod.POST?"POST":"GET")+"' request to URL : " + url);
			if(MMSConfiguration.logging)System.out.println((httpMethod==httpMethod.POST?"POST":"GET")+" parameters : " + urlParameters);
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
			return ret.getBytes();
		} catch (Exception e) {
			if(MMSConfiguration.logging)e.printStackTrace();
			return "No Reply".getBytes();
			
		}
			//return response.toString();	
	}
}
