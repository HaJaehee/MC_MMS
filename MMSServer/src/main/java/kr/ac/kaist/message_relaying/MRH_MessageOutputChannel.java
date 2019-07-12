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
	
Rev. history : 2018-04-23
Version : 0.7.1
	Removed RESOURCE_LEAK, IMPROPER_CHECK_FOR_UNUSUAL_OR_EXCEPTIONAL_CONDITION hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-06-27
Version : 0.7.1
	Fixed large response issues.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	
	Jaehyun Park (jae519@kaist.ac.kr)
	KyungJun Park (kjpark525@kaist.ac.kr)
	
Rev. history : 2018-06-28
Version : 0.7.1
	Fixed large response issues.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	
	Jaehyun Park (jae519@kaist.ac.kr)
	
Rev. history : 2018-07-03
Version : 0.7.2
	Added handling input messages by FIFO scheduling.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-19
Version : 0.7.2
	Added handling input messages by reordering policy.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)


Rev. history : 2019-04-18
Version : 0.8.2
	Modulates related to sendMessage.
	Add thread ConnectionThread class.
	Add asynchronous version of SendMessage.
Modifier : Yunho Choi (choiking10@kaist.ac.kr)

Rev. history : 2019-05-09
Version : 0.8.2
	Added sendMessage function for connecting to Rabbit MQ management server to implement restful API.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-22
Version : 0.9.1
	Added secureSendMessage function for connecting to Rabbit MQ management HTTPS server to implement restful API.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-14
Version : 0.9.2
	Refactoring.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-03
Version : 0.9.3
	Added multi-thread safety.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-08
Version : 0.9.3
	Added resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-09
Version : 0.9.3
	Revised for coding rule conformity.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-10
Version : 0.9.3
	Updated resource managing codes.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

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
import java.util.Base64;
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
import kr.ac.kaist.mms_server.ErrorCode;
import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import kr.ac.kaist.mms_server.MMSLogForDebug;
import kr.ac.kaist.seamless_roaming.SeamlessRoamingHandler;

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
	
	public MRH_MessageOutputChannel(String sessionId) {
		this.SESSION_ID = sessionId;
		initializeModule();
	}
	
	private void initializeModule() {
		mmsLogForDebug = MMSLogForDebug.getInstance();
		mmsLog = MMSLog.getInstance();
	}
	
	void setResponseHeader(Map<String, List<String>> storingHeader){
		isStoredHeader = true;
		storedHeader = storingHeader;
	}
	
	public void replyToSender(ChannelHandlerContext ctx, byte[] data, boolean realtimeLog, int responseCode) throws IOException{
		this.realtimeLog = realtimeLog;
		this.responseCode = responseCode;
		replyToSender(ctx, data);
	}
	
	public void replyToSender(ChannelHandlerContext ctx, byte[] data, boolean realtimeLog) throws IOException{
		this.realtimeLog = realtimeLog;
		replyToSender(ctx, data);
	}
	
	public void replyToSender(ChannelHandlerContext ctx, byte[] data) throws IOException{
    	if (!realtimeLog) {
    		mmsLog.info(logger, this.SESSION_ID, "Reply to sender.");
		}
    	
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
    	
        SessionManager.removeSessionInfo(SESSION_ID);
        if (logger.isTraceEnabled()) {
        	mmsLog.trace(logger, this.SESSION_ID, "Message has been sent completely.");
        }
    }

	public HttpURLConnection requestMessage(String IPAddress, int port, HttpMethod httpMethod, String uri, String username, String password) throws IOException {  
		String url = "http://" + IPAddress + ":" + port + uri;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		mmsLog.info(logger, this.SESSION_ID, "Try connecting to url="+url);

		
		//		Setting HTTP method
		if (httpMethod == httpMethod.POST) {
			con.setRequestMethod("POST");
		} else if (httpMethod == httpMethod.GET) {
			con.setRequestMethod("GET");
		}
		
		String authBasic = username+":"+password;
		Base64.Encoder encoder = Base64.getEncoder();
		byte[] encodedBytes = encoder.encode(authBasic.getBytes(Charset.forName("UTF-8")));
		
		con.setRequestProperty("Authorization","Basic "+new String(encodedBytes));

		// get request doesn't have http body
		 if (logger.isTraceEnabled()) {
			 mmsLog.trace(logger, this.SESSION_ID, (httpMethod==httpMethod.POST?"POST":"GET")+" request to URL=" + url + "\n"
				+ (httpMethod==httpMethod.POST?"POST":"GET")+"\n");
		 }
		return con;
	}
	
	public HttpURLConnection requestMessage(ChannelHandlerContext ctx, FullHttpRequest req, String IPAddress, int port, HttpMethod httpMethod, String srcMRN, String dstMRN) throws IOException {  
		
		String url = "http://" + IPAddress + ":" + port + req.uri();
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		mmsLog.info(logger, this.SESSION_ID, "Try connecting to url="+url);

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
		 if (logger.isTraceEnabled()) {
			 mmsLog.trace(logger, this.SESSION_ID, (httpMethod==httpMethod.POST?"POST":"GET")+" request to URL=" + url + "\n"
				+ (httpMethod==httpMethod.POST?"POST":"GET")+" parameters=" + urlParameters+"\n");
		 }
		return con;
	}

	public HttpURLConnection requestSecureMessage(ChannelHandlerContext ctx, FullHttpRequest req, String IPAddress, int port, HttpMethod httpMethod, String srcMRN, String dstMRN) throws IOException {  
		

	  	hv = getHV();
	  	
		String url = "https://" + IPAddress + ":" + port + req.uri();
		
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		mmsLog.info(logger, this.SESSION_ID, "Try connecting to url="+url);

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
		 if (logger.isTraceEnabled()) {
			 mmsLog.trace(logger, this.SESSION_ID, (httpMethod==httpMethod.POST?"POST":"GET")+" request to URL=" + url + "\n"
				+ (httpMethod==httpMethod.POST?"POST":"GET")+" parameters=" + urlParameters+"\n");
		 }
		
		return con;
	}
	
	public HttpURLConnection requestSecureMessage(String IPAddress, int port, HttpMethod httpMethod, String uri, String username, String password) throws IOException {  

	  	hv = getHV();
	  	
		String url = "https://" + IPAddress + ":" + port + uri;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		mmsLog.info(logger, this.SESSION_ID, "Try connecting to url="+url);
		
		//		Setting HTTP method
		if (httpMethod == httpMethod.POST) {
			con.setRequestMethod("POST");
		} else if (httpMethod == httpMethod.GET) {
			con.setRequestMethod("GET");
		}
		
		String authBasic = username+":"+password;
		Base64.Encoder encoder = Base64.getEncoder();
		byte[] encodedBytes = encoder.encode(authBasic.getBytes(Charset.forName("UTF-8")));
		
		con.setRequestProperty("Authorization","Basic "+new String(encodedBytes));

		// get request doesn't have http body
		 if (logger.isTraceEnabled()) {
			 mmsLog.trace(logger, this.SESSION_ID, (httpMethod==httpMethod.POST?"POST":"GET")+" request to URL=" + url + "\n"
				+ (httpMethod==httpMethod.POST?"POST":"GET")+"\n");
		 }
		return con;
	}
	
	
	public byte[] getResponseMessage(HttpURLConnection con) throws IOException {
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
		mmsLog.info(logger, this.SESSION_ID, "Receive a response." + " Response Code=" + responseCode);

		return retBuffer;
	}

	//  To use restful API
	public byte[] sendMessage(String IPAddress, int port, HttpMethod httpMethod, String uri, String username, String password)  throws IOException {  
		return getResponseMessage(requestMessage(IPAddress, port, httpMethod, uri, username, password));
	}
	
    //  To do relaying
	public byte[] sendMessage(ChannelHandlerContext ctx, FullHttpRequest req, String IPAddress, int port, HttpMethod httpMethod, String srcMRN, String dstMRN) throws IOException {  
		return getResponseMessage(requestMessage(ctx, req, IPAddress, port, httpMethod, srcMRN, dstMRN));
	}
	
	public ConnectionThread asynchronizeSendMessage(ChannelHandlerContext ctx, FullHttpRequest req, String IPAddress, int port, HttpMethod httpMethod, String srcMRN, String dstMRN) throws IOException {  
		HttpURLConnection con = requestMessage(ctx, req, IPAddress, port, httpMethod, srcMRN, dstMRN);
		return new ConnectionThread(con, ctx, req);
	}

	
	// To do secure relaying
	public byte[] secureSendMessage(ChannelHandlerContext ctx, FullHttpRequest req, String IPAddress, int port, HttpMethod httpMethod, String srcMRN, String dstMRN) throws NullPointerException, IOException { // 
		HttpURLConnection con = requestSecureMessage(ctx, req, IPAddress, port, httpMethod, srcMRN, dstMRN);
		return getResponseMessage(con);
	}
	
	//  To use restful API
	public byte[] secureSendMessage(String IPAddress, int port, HttpMethod httpMethod, String uri, String username, String password)  throws IOException {  
		return getResponseMessage(requestSecureMessage(IPAddress, port, httpMethod, uri, username, password));
	}
	
	public ConnectionThread asynchronizeSendSecureMessage(ChannelHandlerContext ctx, FullHttpRequest req, String IPAddress, int port, HttpMethod httpMethod, String srcMRN, String dstMRN) throws NullPointerException, IOException { // 
		HttpURLConnection con = requestSecureMessage(ctx, req, IPAddress, port, httpMethod, srcMRN, dstMRN);
		return new ConnectionThread(con, ctx, req);
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
        	mmsLog.warnException(logger, SESSION_ID, "", e, 5);
		} catch (KeyManagementException e) {
			mmsLog.warnException(logger, SESSION_ID, "", e, 5);
		}
        
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
            	mmsLog.info(logger, SESSION_ID, "URL Host=" + urlHostName + " vs " + session.getPeerHost()+".");
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

	public class ConnectionThread extends Thread {
		private ChannelHandlerContext ctx = null;
		private HttpURLConnection con = null;
		private FullHttpRequest req = null;
		private byte[] data;
		public ConnectionThread(HttpURLConnection con, ChannelHandlerContext ctx, FullHttpRequest req) {
			this.con = con;
			this.ctx = ctx;
			this.req = req;
			data = null;
		}
		public void terminate() {
			data = null;
	       	con.disconnect();
	       	try {
				con.getInputStream().close();
				con = null;
			} 
	       	catch (IOException e) {
	       		mmsLog.info(logger, SESSION_ID, ErrorCode.MESSAGE_RELAYING_FAIL_DISCONNECT.toString());
			}
	    }
		public byte[] getData() {
			byte[] ret = null;
			if (data != null) {
				ret = new byte[data.length];
				for (int i = 0; i < data.length; i++) {
					ret[i] = data[i];
				}
			}
			return ret;
		}
		public void run(){
			try {
				data = getResponseMessage(con);
			} 
			catch (IOException e) {
	    		mmsLog.info(logger, SESSION_ID, ErrorCode.MESSAGE_RELAYING_FAIL_UNREACHABLE.toString());
			} 
			finally {
				if (data == null) {
					data = ErrorCode.MESSAGE_RELAYING_FAIL_UNREACHABLE.getUTF8Bytes();
				}
				try {
					replyToSender(ctx, data);
					if (req != null && req.refCnt() > 0) {
						req.release();
						req = null;
					}
					con = null;
					ctx = null;
				} catch (IOException e) {
					mmsLog.infoException(logger, SESSION_ID, ErrorCode.CLIENT_DISCONNECTED.toString(), e, 5);
				}

			}
        } 
	}
}
