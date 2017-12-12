package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : MRH_MessageOutputChannel.java
	It outputs the messages to destination of message through the Internet using HTTP. 
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.4.0

Rev. history : 2017-03-22
	Added secureSendMessage() method in order to handle HTTPS.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-29
Version : 0.5.3
	Added system log features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-05-02
Version : 0.5.4
	Added image relaying feature
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	Jaehyun Park (jae519@kaist.ac.kr)
	
Rev. history : 2017-05-06
Version : 0.5.5
	Added SessionManager features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-26
Version : 0.6.0
	Replaced from random int SESSION_ID to String SESSION_ID as connection context channel id.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-10-25
Version : 0.6.0
	Added MMSLogsForDebug features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-15
Version : 0.7.0
	Added realtime log functions
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	Jaehyun Park (jae519@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
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
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;

public class MRH_MessageOutputChannel{
	
	private static final Logger logger = LoggerFactory.getLogger(MRH_MessageOutputChannel.class);
	

	private String SESSION_ID = "";
	private static Map<String,List<String>> storedHeader = null;
	private static boolean isStoredHeader = false;
	private HostnameVerifier hv = null;
	private int responseCode = 200;
	private boolean realtimeLog = false;
	private MMSLogForDebug mmsLogForDebug = null;
	private MMSLog mmsLog = null;
	
	MRH_MessageOutputChannel(String sessionId) {
		// TODO Auto-generated constructor stub
		this.SESSION_ID = sessionId;
		mmsLogForDebug = MMSLogForDebug.getInstance();
		mmsLog = MMSLog.getInstance();
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
    	ByteBuf textb = Unpooled.copiedBuffer(data);

		if (!realtimeLog) {
	    	logger.info("SessionID="+this.SESSION_ID+" Reply to sender.");
	    	if(MMSConfiguration.WEB_LOG_PROVIDING) {
	    		String log = "SessionID="+this.SESSION_ID+" Reply to sender.";
	    		mmsLog.addBriefLogForStatus(log);
	    		mmsLogForDebug.addLog(this.SESSION_ID, log);
	    	}
		}
    	
    	long responseLen = data.length;
    	HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, getHttpResponseStatus(responseCode));
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
    	//System.out.println("Ready to send message in MRH_output");
    	ctx.write(res);
    	ctx.write(textb);
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        future.addListener(ChannelFutureListener.CLOSE);
        ctx.close();
        SessionManager.sessionInfo.remove(SESSION_ID);
        
        logger.trace("SessionID=" + this.SESSION_ID + " Message is sent completely.");
    }
	
//  to do relaying
	byte[] sendMessage(FullHttpRequest req, String IPAddress, int port, HttpMethod httpMethod) throws Exception { // 

		String url = "http://" + IPAddress + ":" + port + req.uri();
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		logger.info("SessionID="+this.SESSION_ID+" Try connecting to url="+url);
		if(MMSConfiguration.WEB_LOG_PROVIDING) {
			String log = "SessionID="+this.SESSION_ID+" Try connecting to url="+url;
			mmsLog.addBriefLogForStatus(log);
			mmsLogForDebug.addLog(this.SESSION_ID, log);
		}
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
		
		logger.trace("SessionID="+this.SESSION_ID+" "+(httpMethod==httpMethod.POST?"POST":"GET")+"' request to URL : " + url + "\n"
				+ (httpMethod==httpMethod.POST?"POST":"GET")+" parameters : " + urlParameters+"\n"
				+ "Response Code : " + responseCode);

		
		
		InputStream is = con.getInputStream();
		ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		
		int bytesRead = -1;
		while ((bytesRead = is.read(buffer))!=-1){
			byteOS.write(Arrays.copyOfRange(buffer, 0, bytesRead));
		}
		byte[] retBuffer = byteOS.toByteArray();

		is.close();
		logger.info("SessionID="+this.SESSION_ID+" Received a response.");
		if(MMSConfiguration.WEB_LOG_PROVIDING) {
			String log = "SessionID="+this.SESSION_ID+" Received a response.";
			mmsLog.addBriefLogForStatus(log);
			mmsLogForDebug.addLog(this.SESSION_ID, log);
		}
		
		return retBuffer;
	}
	
	
	
	
	
//  to do secure relaying
	byte[] secureSendMessage(FullHttpRequest req, String IPAddress, int port, HttpMethod httpMethod) throws Exception { // 

	  	hv = getHV();
	  	
		String url = "https://" + IPAddress + ":" + port + req.uri();
		
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		logger.info("SessionID="+this.SESSION_ID+" Try connecting to url="+url);
		if(MMSConfiguration.WEB_LOG_PROVIDING) {
			String log = "SessionID="+this.SESSION_ID+" Try connecting to url="+url;
			mmsLog.addBriefLogForStatus(log);
			mmsLogForDebug.addLog(this.SESSION_ID, log);
		}
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
		
		logger.trace("SessionID="+this.SESSION_ID+" "+(httpMethod==httpMethod.POST?"POST":"GET")+" request to URL=" + url + "\n"
				+ (httpMethod==httpMethod.POST?"POST":"GET")+" parameters=" + urlParameters+"\n"
				+ "Response Code=" + responseCode);
		
		InputStream is = con.getInputStream();
		ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		
		int bytesRead = -1;
		while ((bytesRead = is.read(buffer))!=-1){
			byteOS.write(Arrays.copyOfRange(buffer, 0, bytesRead));
		}
		byte[] retBuffer = byteOS.toByteArray();

		is.close();
		logger.info("SessionID="+this.SESSION_ID+" Received a response.");
		if(MMSConfiguration.WEB_LOG_PROVIDING) {
			String log = "SessionID="+this.SESSION_ID+" Received a response.";
			mmsLog.addBriefLogForStatus(log);
			mmsLogForDebug.addLog(this.SESSION_ID, log);
		}
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
        } catch (Exception e) {
        	logger.error("SessionID="+this.SESSION_ID+" "+e.getMessage()+".");
        }
        
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
            	logger.info("SessionID="+SESSION_ID+" URL Host=" + urlHostName + " vs " + session.getPeerHost()+".");
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
