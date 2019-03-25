package kr.ac.kaist.message_handler;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

public class MessageOutputChannel{
	
	private String SESSION_ID = "";
	private static Map<String,List<String>> storedHeader = null;
	private static boolean isStoredHeader = false;
	private HostnameVerifier hv = null;
	private int responseCode = 200;
	private boolean realtimeLog = false;

	
	MessageOutputChannel(String sessionId) {
		this.SESSION_ID = sessionId;

	}
	
	void setResponseHeader(Map<String, List<String>> storingHeader){
		isStoredHeader = true;
		storedHeader = storingHeader;
	}
	
	public void replyToSender(ChannelHandlerContext ctx, byte[] data, boolean realtimeLog, int responseCode) {
		this.realtimeLog = realtimeLog;
		this.responseCode = responseCode;
		replyToSender(ctx, data);
	}
	
	public void replyToSender(ChannelHandlerContext ctx, byte[] data, boolean realtimeLog) {
		this.realtimeLog = realtimeLog;
		replyToSender(ctx, data);
	}
	
	public void replyToSender(ChannelHandlerContext ctx, byte[] data) {

    	long responseLen = data.length;
    	FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, getHttpResponseStatus(responseCode), Unpooled.copiedBuffer(data));
    	if (isStoredHeader){
			Set<String> resHeaderKeyset = storedHeader.keySet(); 
			for (Iterator<String> resHeaderIterator = resHeaderKeyset.iterator();resHeaderIterator.hasNext();) {
				String key = resHeaderIterator.next();
				List<String> values = storedHeader.get(key);
				for (Iterator<String> valueIterator = values.iterator();valueIterator.hasNext();) {
					String value = valueIterator.next();
					
					if (key != null) {
						res.headers().set(key,value);
					}
				}
			}
			isStoredHeader = false;
			storedHeader = null;
			
    	} else {
    		res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8");
    	}
    	
    	HttpUtil.setContentLength(res, responseLen);
    	final ChannelFuture f = ctx.writeAndFlush(res);
    	f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future; //TODO Requeueing function MUST be implemented.  
                
                ctx.close();
            }
        });
    	
        
    }
	
//  to do relaying
	public byte[] sendMessage(FullHttpRequest req, String IPAddress, int port, HttpMethod httpMethod, String srcMRN, String dstMRN) throws IOException {  
		
		String url = "http://" + IPAddress + ":" + port + req.uri();
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
		for (Iterator<Map.Entry<String, String>> htr = httpHeaders.iteratorAsString(); htr.hasNext();) {
			Map.Entry<String, String> htrValue = htr.next();
		
			con.setRequestProperty(htrValue.getKey(), htrValue.getValue());
		}

		String urlParameters = req.content().toString(Charset.forName("UTF-8")).trim();
		con.setRequestProperty("Content-Length", urlParameters.length() + "");
		
		
		if (httpMethod == httpMethod.POST) {
			// Send post request
			con.setDoOutput(true);
			BufferedWriter wr = new BufferedWriter(
					new OutputStreamWriter(con.getOutputStream(),Charset.forName("UTF-8")));
			wr.write(urlParameters);
			wr.flush();
			wr.close();
		} 
		
		// get request doesn't have http body
		responseCode = con.getResponseCode();
	
		Map<String,List<String>> resHeaders = con.getHeaderFields();
		setResponseHeader(resHeaders);
		
	
		InputStream is = con.getInputStream();
		ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		
		int bytesRead = -1;
		while ((bytesRead = is.read(buffer))!=-1){
			byteOS.write(Arrays.copyOfRange(buffer, 0, bytesRead));
		}
		byte[] retBuffer = byteOS.toByteArray();

		is.close();

		
		return retBuffer;
	}
	
	
	
	
	
//  to do secure relaying
	public byte[] secureSendMessage(FullHttpRequest req, String IPAddress, int port, HttpMethod httpMethod, String srcMRN, String dstMRN) throws NullPointerException, IOException { // 

	  	hv = getHV();
	  	
		String url = "https://" + IPAddress + ":" + port + req.uri();
		
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	
		con.setHostnameVerifier(hv);
		
		HttpHeaders httpHeaders = req.headers();
		
		
//		Setting HTTP method
		if (httpMethod == httpMethod.POST) {
			con.setRequestMethod("POST");
		} else if (httpMethod == httpMethod.GET) {
			con.setRequestMethod("GET");
		}
		
		
//		Setting remaining headers
		for (Iterator<Map.Entry<String, String>> htr = httpHeaders.iteratorAsString(); htr.hasNext();) {
			Map.Entry<String, String> htrValue = htr.next();
			
			//if (!htrValue.getKey().equals("srcMRN") && !htrValue.getKey().equals("dstMRN")) {
			con.setRequestProperty(htrValue.getKey(), htrValue.getValue());
			//}
		}

		String urlParameters = req.content().toString(Charset.forName("UTF-8")).trim();
		con.setRequestProperty("Content-Length", urlParameters.length() + "");
		
		
		if (httpMethod == httpMethod.POST) {
			// Send post request
			con.setDoOutput(true);
			BufferedWriter wr = new BufferedWriter(
					new OutputStreamWriter(con.getOutputStream(),Charset.forName("UTF-8")));
			wr.write(urlParameters);
			wr.flush();
			wr.close();
		} 
		
		// get request doesn't have http body
		
		
		responseCode = con.getResponseCode();
		Map<String,List<String>> resHeaders = con.getHeaderFields();
		setResponseHeader(resHeaders);

		InputStream is = con.getInputStream();
		ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		
		int bytesRead = -1;
		while ((bytesRead = is.read(buffer))!=-1){
			byteOS.write(Arrays.copyOfRange(buffer, 0, bytesRead));
		}
		byte[] retBuffer = byteOS.toByteArray();

		is.close();
		
		
		return retBuffer;
	
	}
	
	HostnameVerifier getHV (){
		// Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
        	
		} catch (KeyManagementException e) {

		}
        
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        
        return hv;
	}
	

	
	private HttpResponseStatus getHttpResponseStatus(int responseCode) {
		switch (responseCode) {
		case 200:
			return HttpResponseStatus.OK;
		case 301:
			return HttpResponseStatus.MOVED_PERMANENTLY;
		case 302:
			return HttpResponseStatus.FOUND;
		case 404:
			return HttpResponseStatus.NOT_FOUND;
		case 400:
			return HttpResponseStatus.BAD_REQUEST;
		case 401:
			return HttpResponseStatus.UNAUTHORIZED;
		case 403:
			return HttpResponseStatus.FORBIDDEN;
		case 405:
			return HttpResponseStatus.METHOD_NOT_ALLOWED;
		case 500:
			return HttpResponseStatus.INTERNAL_SERVER_ERROR;
		case 503:
			return HttpResponseStatus.SERVICE_UNAVAILABLE;
		case 504:
			return HttpResponseStatus.GATEWAY_TIMEOUT;
		case 505:
			return HttpResponseStatus.HTTP_VERSION_NOT_SUPPORTED;
		case 303:
			return HttpResponseStatus.SEE_OTHER;
		case 304:
			return HttpResponseStatus.NOT_MODIFIED;
		case 307:
			return HttpResponseStatus.TEMPORARY_REDIRECT;
		case 204:
			return HttpResponseStatus.NO_CONTENT;
		case 206:
			return HttpResponseStatus.PARTIAL_CONTENT;
		case 201:
			return HttpResponseStatus.CREATED;
		case 202:
			return HttpResponseStatus.ACCEPTED;
		case 203:
			return HttpResponseStatus.NON_AUTHORITATIVE_INFORMATION;
		case 205:
			return HttpResponseStatus.RESET_CONTENT;
		case 207:
			return HttpResponseStatus.MULTI_STATUS;
		case 300:
			return HttpResponseStatus.MULTIPLE_CHOICES;
		case 305:
			return HttpResponseStatus.USE_PROXY;
		case 402:
			return HttpResponseStatus.PAYMENT_REQUIRED;
		case 406:
			return HttpResponseStatus.NOT_ACCEPTABLE;
		case 407:
			return HttpResponseStatus.PROXY_AUTHENTICATION_REQUIRED;
		case 408:
			return HttpResponseStatus.REQUEST_TIMEOUT;
		case 409:
			return HttpResponseStatus.CONFLICT;
		case 410:
			return HttpResponseStatus.GONE;
		case 411:
			return HttpResponseStatus.LENGTH_REQUIRED;
		case 412:
			return HttpResponseStatus.PRECONDITION_FAILED;
		case 413:
			return HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE;
		case 414:
			return HttpResponseStatus.REQUEST_URI_TOO_LONG;
		case 415:
			return HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE;
		case 416:
			return HttpResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
		case 417:
			return HttpResponseStatus.EXPECTATION_FAILED;
		case 422:
			return HttpResponseStatus.UNPROCESSABLE_ENTITY;
		case 423:
			return HttpResponseStatus.LOCKED;
		case 424:
			return HttpResponseStatus.FAILED_DEPENDENCY;
		case 426:
			return HttpResponseStatus.UPGRADE_REQUIRED;
		case 428:
			return HttpResponseStatus.PRECONDITION_REQUIRED;
		case 429:
			return HttpResponseStatus.TOO_MANY_REQUESTS;
		case 431:
			return HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE;
		case 501:
			return HttpResponseStatus.NOT_IMPLEMENTED;
		case 502:
			return HttpResponseStatus.BAD_REQUEST;
		case 506:
			return HttpResponseStatus.VARIANT_ALSO_NEGOTIATES;
		case 507:
			return HttpResponseStatus.INSUFFICIENT_STORAGE;
		case 510:
			return HttpResponseStatus.NOT_EXTENDED;
		case 511:
			return HttpResponseStatus.NETWORK_AUTHENTICATION_REQUIRED;
		default:
			return HttpResponseStatus.BAD_REQUEST;
		}
	}
}
