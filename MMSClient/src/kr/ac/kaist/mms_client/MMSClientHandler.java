package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : MMSClientHandler.java
	It provides APIs for MMS clients. 
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03

Rev. history : 2017-02-01
Version : 0.3.01
	Added setting header field features. 
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-02-14
	fixed http get request bugs
	fixed http get file request bugs
	added setting context features
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-27
Version : 0.5.1
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-05-02
Version : 0.5.4
	Added setting response header
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-05-06
Version : 0.5.5
	The polling interval must be larger than 0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-18
Version : 0.5.6
	Changed the variable Map<String,String> headerField to Map<String,List<String>>
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-23
Version : 0.5.7
	Update javadoc
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2017-06-27
Version : 0.5.8
	Geo-location Update
Modifier : Jaehyun Park (jae519@kaist.ac.kr)

Rev. history : 2017-07-28
Version : 0.5.9
	Changed from PollingResponseCallback.callbackMethod(Map<String,List<String>> headerField, message) 
	     to PollingResponseCallback.callbackMethod(Map<String,List<String>> headerField, List<String> messages) 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * It is an object that can communicate to MMS through HTTP and send or receive messages of other objects.
 * @version 0.5.7
 * @see SecureMMSClientHandler
 */
public class MMSClientHandler {
	
	private String TAG = "[MMSClientHandler] ";
	
	private RcvHandler rcvHandler = null;
	private PollHandler pollHandler = null;
	private SendHandler sendHandler = null;
	private String clientMRN = "";
	private int clientPort = 0;
	private Map<String,List<String>> headerField = null;
	private GeoReporter geoReporter = null;
	/**
	 * The Constructor of MMSClientHandler class
	 * @param	clientMRN		the MRN of client
	 * @throws	IOException 	if exception occurs
	 */	
	public MMSClientHandler(String clientMRN) throws IOException{
		this.clientMRN = clientMRN;
		rcvHandler = null;
		pollHandler = null;
		sendHandler = null;
	}
	
	/**
	 * This interface is used to handle the response to polling request.
	 * @see		MMSClientHandler#startPolling(String, String, int, PollingResponseCallback)
	 */
	public interface PollingResponseCallback{
		/**
		 * Argument list<String> messages means the list of messages about polling response.
		 * Argument Map<String,List<String>> HeaderField is a set of headers for polling response.
		 * @param headerField
		 * @param messages
		 */
		void callbackMethod(Map<String,List<String>> headerField, List<String> messages);
	}
	
	/**
	 * This interface is used to handle the response to be sent when a message is received.
	 * @see		MMSClientHandler#setServerPort(int, RequestCallback)
	 * @see		MMSClientHandler#setServerPort(int, String, RequestCallback)
	 */
	public interface RequestCallback{
		/**
		 * When a client sends an HTTP request to a server, the server performs a RequestCallback after receiving the request. 
		 * Argument list<String> messages means the list of messages about HTTP requests.
		 * Argument Map<String,List<String>> HeaderField is a set of headers for HTTP requests.
		 * @param headerField
		 * @param message
		 * @return
		 */
		String respondToClient(Map<String,List<String>> headerField, String message);
		int setResponseCode();
		Map<String,List<String>> setResponseHeader();
	}
	
	/**
	 * This interface is used to handle the response to be received when a message is sent.
	 * @see		MMSClientHandler#setSender(ResponseCallback)
	 */
	public interface ResponseCallback{
		/**
		 * When the server sends a response to the HTTP request sent by the client, the client performs a ResponseCallback.
		 * Argument list<String> messages means the list of messages about response.
		 * Argument Map<String,List<String>> HeaderField is a set of headers for response.
		 * @param headerField
		 * @param message
		 */
		void callbackMethod(Map<String,List<String>> headerField, String message);
	}
	
	/**
	 * This method is that client requests polling. If setting this method, send polling request
	 * per interval. In the MMS that received the polling request, if there is a message toward the client, 
	 * the message is send to the MMS client, which requests polling, and in the MMS client,
	 * the callbackMethod is executed. Depending on whether it is the way of normal polling or long polling,
	 * the way of response is different.
	 * @param	dstMRN			the MRN of MMS to request polling
	 * @param	svcMRN			the MRN of service, which may send to client
	 * @param	interval		the frequency of polling (unit of time: ms)
	 * @param	callback		the callback interface of {@link PollingResponseCallback}
	 * @throws	IOException 	if exception occurs
	 * @see 	PollingResponseCallback
	 */	
	public void startPolling (String dstMRN, String svcMRN, int interval, PollingResponseCallback callback) throws IOException{
		if (this.sendHandler != null) {
			System.out.println(TAG+"Failed! MMSClientHandler must have exactly one function! It already has done setSender()");
		} else if (this.rcvHandler != null) {
			System.out.println(TAG+"Failed! MMSClientHandler must have exactly one function! It already has done setServerPort() or setFileServerPort()");
		} else {
			if (interval > 0) {
				this.pollHandler = new PollHandler(clientMRN, dstMRN, svcMRN, interval, headerField);
				this.pollHandler.ph.setPollingResponseCallback(callback);
				this.pollHandler.ph.start();
			} else {
				System.out.println(TAG+"Long-polling mode"); //TODO: Long-polling could have trouble when session disconnect.
				this.pollHandler = new PollHandler(clientMRN, dstMRN, svcMRN, interval, headerField);
				this.pollHandler.ph.setPollingResponseCallback(callback);
				this.pollHandler.ph.start();
			}
		}
	}
	/**
	 * This method is that stop polling requests using interrupt signal. 
	 */
	public void stopPolling (){
		this.pollHandler.ph.interrupt();
	}
	/**
	 * This method is developing now, so do not use this method.
	 * @param svcMRN
	 * @param interval
	 * @throws IOException
	 */
	public void startGeoReporting (String svcMRN, int interval) throws IOException{
		if (this.sendHandler != null) {
			System.out.println(TAG+"Failed! MMSClientHandler must have exactly one function! It already has done setSender()");
		} else if (this.rcvHandler != null) {
			System.out.println(TAG+"Failed! MMSClientHandler must have exactly one function! It already has done setServerPort() or setFileServerPort()");
		} else {
			if (interval > 0) {
				this.geoReporter = new GeoReporter(clientMRN, svcMRN, interval);
				this.geoReporter.gr.start();
			} else {
				System.out.println(TAG+"Failed! The interval must be larger than 0");
			}
		}
	}
	
	private boolean isErrorForSettingServerPort (){
		if (this.sendHandler != null) {
			System.out.println(TAG+"Failed! MMSClientHandler must have exactly one function! It already has done setSender()");
			return true;
		} else if (this.pollHandler != null) {
			System.out.println(TAG+"Failed! MMSClientHandler must have exactly one function! It already has done startPolling()");
			return true;
		}
		return false;
	}
	
	/**
	 * This method configures client's port to act as a HTTP server and create a rcvHandler object.
	 * It is used in a network that supports push method. It receives all messages toward itself.
	 * When a message is received via the callback method, it is possible to handle the response to be sent.
	 * @param	port			port number
	 * @param	callback		callback interface of {@link RequestCallback}
	 * @throws	IOException 	if exception occurs
	 * @see 	#setServerPort(int, String, RequestCallback)
	 * @see 	#addContext(String)
	 */	
	public void setServerPort (int port, RequestCallback callback) throws IOException{
		if (!isErrorForSettingServerPort()){
			this.rcvHandler = new RcvHandler(port);
			setPortAndCallback(port, callback);
		}
	}
	
	/**
	 * This method configures client's port to act as a HTTP server and create a rcvHandler object.
	 * It is used in a network that supports push method. This method configures default context and 
	 * it receives messages that url matches the default context. When a message is received via the 
	 * callback method, it is possible to handle the response to be sent.
	 * @param	port			port number
	 * @param	context			context (e.g. /get/messages/)
	 * @param	callback		callback interface of {@link RequestCallback}
	 * @throws	IOException 	if exception occurs
	 * @see 	#setServerPort(int, RequestCallback)
	 * @see 	#addContext(String)
	 */	
	public void setServerPort (int port, String context, RequestCallback callback) throws IOException{
		if (!isErrorForSettingServerPort()){
			this.rcvHandler = new RcvHandler(port, context);
			setPortAndCallback(port, callback);
		}
	}
	
	/**
	 * This method configures client's port to act as a HTTP file server and create a rcvHandler object.
	 * It is used in a network that supports push method. This method configures default context and 
	 * it receives messages that url matches the default context. When a message is received via the 
	 * callback method, it is possible to handle the response to be sent.
	 * @param	port			the port number
	 * @param	fileDirectory	the file path (e.g. /files/ocean/waves/)
	 * @param	fileName		the file name (e.g. mokpo.xml)
	 * @throws	IOException 	if exception occurs
	 * @see 	#addFileContext(String, String)
	 */	
	public void setFileServerPort (int port, String fileDirectory, String fileName) throws IOException {
		if (!isErrorForSettingServerPort()){
			this.clientPort = port;
			this.rcvHandler = new RcvHandler(port, fileDirectory, fileName);
			registerLocator(port);	
		}
	}
	
	private void setPortAndCallback (int port, RequestCallback callback) {
		this.clientPort = port;
		this.rcvHandler.hrh.setRequestCallback(callback);
		registerLocator(port);	
	}
	
	/**
	 * Add context to client's HTTP server
	 * @param 	context			the context (e.g. /get/messages/)
	 * @see 	#setServerPort(int, RequestCallback)
	 * @see 	#setServerPort(int, String, RequestCallback)
	 */
	public void addContext (String context) {
		if(this.rcvHandler != null) {
			this.rcvHandler.addContext(context);
		} else {
			System.out.println(TAG+"Failed! HTTP server is required! Do setServerPort()");
		}
	}
	
	/**
	 * Add file directory and file name to client's HTTP file server
	 * @param 	fileDirectory	the file path (e.g. /files/ocean/waves/)
	 * @param 	fileName		the file name (e.g. mokpo.xml)
	 * @see 	#setFileServerPort(int, String, String)
	 */
	public void addFileContext (String fileDirectory, String fileName) {
		if(this.rcvHandler != null) {
			this.rcvHandler.addFileContext(fileDirectory, fileName);
		} else {
			System.out.println(TAG+"Failed! HTTP file server is required! Do setFileServerPort()");
		}
	}
	
	/**
	 * This method is used to set in MMS client in order to send message. If using this method, it is possible 
	 * to use sendPostMsg and sendGetMsg method. When the client send a message, it can handle the response to 
	 * be received via callback interface.
	 * @param 	callback		the callback interface of {@link ResponseCallback} 
	 * @see 	#sendGetMsg(String)
	 * @see	 	#sendGetMsg(String, String, String)
	 * @see 	#sendPostMsg(String, String)
	 * @see 	#sendPostMsg(String, String, String)
	 * @see		ResponseCallback
	 */
	public void setSender (ResponseCallback callback) {
		if (this.rcvHandler != null) {
			System.out.println(TAG+"Failed! MMSClientHandler must have exactly one function! It already has done setServerPort()");
		} else if (this.pollHandler != null) {
			System.out.println(TAG+"Failed! MMSClientHandler must have exactly one function! It already has done startPolling()");
		} else {
			this.sendHandler = new SendHandler(clientMRN);
			this.sendHandler.setResponseCallback(callback);
		}
		
	}
	
	@Deprecated
	private void registerLocator(int port){
		try {
			new MMSSndHandler(clientMRN).registerLocator(port);
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(MMSConfiguration.LOGGING){
				System.out.print(TAG);
				e.printStackTrace();
			}
			
			return;
		}
	}
	
	//HJH
	/**
	 * When sending a message, add custom header to HTTP header field
	 * @param 	headerField		Key and value for additional header
	 * @throws 	Exception		if exception occurs
	 */
	public void setMsgHeader(Map<String,List<String>> headerField) throws Exception{
		this.headerField = headerField;
	}
	
	/**
	 * Send a POST message to the destination MRN that url matches the location via MMS
	 * @param 	dstMRN			the destination MRN to send data
	 * @param 	loc				url location
	 * @param 	data			the data to send
	 * @throws 	Exception		if exception occurs
	 * @see		#sendPostMsg(String, String)
	 * @see		#setSender(ResponseCallback)
	 */
	public void sendPostMsg(String dstMRN, String loc, String data) throws Exception{
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpPost(dstMRN, loc, data, headerField);
		}
	}
	
	/**
	 * Send a POST message to the destination MRN via MMS
	 * @param 	dstMRN			the destination MRN to send data
	 * @param 	data			the data to send
	 * @throws 	Exception		if exception occurs
	 * @see		#sendPostMsg(String, String, String)
	 * @see		#setSender(ResponseCallback)
	 */
	public void sendPostMsg(String dstMRN, String data) throws Exception{
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpPost(dstMRN, "", data, headerField);
		}
	}
	
	//HJH
	/**
	 * Send a GET message to the destination MRN via MMS
	 * @param 	dstMRN			the destination MRN
	 * @throws 	Exception		if exception occurs
	 * @see		#sendGetMsg(String, String, String)
	 * @see		#setSender(ResponseCallback)
	 */
	public void sendGetMsg(String dstMRN) throws Exception{
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpGet(dstMRN, "", "", headerField);
		}
	}
	
	//HJH
	/**
	 * Send a GET message which the destination MRN is that url matches the location via MMS and setting
	 * parameter
	 * @param 	dstMRN			the destination MRN
	 * @param	loc				url location
	 * @param	params			parameter
	 * @throws 	Exception		if exception occurs
	 * @see		#sendGetMsg(String)
	 * @see		#setSender(ResponseCallback)
	 */
	public void sendGetMsg(String dstMRN, String loc, String params) throws Exception{
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpGet(dstMRN, loc, params, headerField);
		}
	}
	
	//OONI
	/**
	 * Use when requesting a file from the destination. Send a GET message to a file server mapping to destination MRN
	 * to request a file that matches the parameterized filename.
	 * @param 	dstMRN			the destination MRN to send a message
	 * @param 	fileName		file path and name (e.g. "/get/test.xml")
	 * @return					returning result of saving file
	 * 							<code>null</code> if saving file is failed.
	 * @throws 	Exception
	 */
	public String requestFile(String dstMRN, String fileName) throws Exception{
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
			return null;
		} else {
			return this.sendHandler.sendHttpGetFile(dstMRN, fileName, headerField);
		}
	}
	
	private class RcvHandler extends MMSRcvHandler{
		RcvHandler(int port) throws IOException {
			super(port);
		}
		RcvHandler(int port, String context) throws IOException {
			super(port, context);
		}
		RcvHandler(int port, String fileDirectory, String fileName) throws IOException {
			super(port, fileDirectory, fileName);
		}
	}
	
	private class SendHandler extends MMSSndHandler{
		SendHandler(String clientMRN) {
			super(clientMRN);
		}
	}
	
	private class PollHandler extends MMSPollHandler{

		PollHandler(String clientMRN, String dstMRN, String svcMRN, int interval, Map<String, List<String>> headerField) throws IOException {
			super(clientMRN, dstMRN, svcMRN, interval, clientPort, 1, headerField);
		}
	}
	
	private class GeoReporter extends MMSGeoInfoReporter{
		GeoReporter(String clientMRN, String svcMRN, int interval) throws IOException {
			super(clientMRN, svcMRN, interval, clientPort, 1);
		}
	}
}

