package kr.ac.kaist.mms_client;
/* -------------------------------------------------------- */
/** 
File name : SecureMMSClientHandler.java
	It provides APIs for Secure MMS clients. 
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-21
Version : 0.4.0

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
	Variable GeoReporter is added.
	Functions startGeoReporting and GeoReporter is added.
Modifier : Jaehyun Park (jae519@kaist.ac.kr)

Rev. history : 2017-07-28
Version : 0.5.9
	Changed from PollingResponseCallback.callbackMethod(Map<String,List<String>> headerField, message) 
	     to PollingResponseCallback.callbackMethod(Map<String,List<String>> headerField, List<String> messages) 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-22
Version : 0.6.0
	Added commentaries.
Modifier : Youngjin Kim (jcdad3000@kaist.ac.kr)

Rev. history : 2017-09-23
Version : 0.6.0
	Polling interval could be 0.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-16
Version : 0.7.0
	adding the code for marking a variable, "interrupted", of pollingHandler. 
	added code: "this.pollHandler.ph.markInterrupted();" in the method stopPolling()
Modifier : Jaehyun Park (jae519@kaist.ac.kr)

Rev. history : 2018-04-23
Version : 0.7.1
	Removed IMPROPER_CHECK_FOR_UNUSUAL_OR_EXCEPTIONAL_CONDITION, EXPOSURE_OF_SYSTEM_DATA hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.IOException;
import java.util.List;
import java.util.Map;



/**
 * It is an object that can communicate to MMS through HTTPS and send or receive messages of other objects.
 * @version 0.7.0
 * @see MMSClientHandler
 */
public class SecureMMSClientHandler {
	
	private String TAG = "[SecureMMSClientHandler] ";
	private RcvHandler rcvHandler = null;
	private PollHandler pollHandler = null;
	private SendHandler sendHandler = null;
	private String clientMRN = "";
	private int clientPort = 0;
	private Map<String,List<String>> headerField = null;
	private GeoReporter geoReporter = null;
	
	
	/**
	 * The Constructor of SecureMMSClientHandler class
	 * @param	clientMRN		the MRN of client
	 * @throws	IOException 	if exception occurs
	 */	
	public SecureMMSClientHandler(String clientMRN) throws IOException, NullPointerException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			throw new NullPointerException();
		}
		this.clientMRN = clientMRN;
		rcvHandler = null;
		pollHandler = null;
		sendHandler = null;
	}
	
	/**
	 * This interface is used to handle the response to polling request.
	 * @see		SecureMMSClientHandler#startPolling(String, String, int, PollingResponseCallback)
	 */
	public interface PollingResponseCallback{
		/**
		 * Argument list&lt;String&gt; messages means the list of messages about polling response.
		 * Argument Map&lt;String,List&lt;String&gt;&gt; HeaderField is a set of headers for polling response.
		 * @param headerField
		 * @param messages
		 */
		void callbackMethod(Map<String,List<String>> headerField, List<String> messages);
	}
	
	/**
	 * This interface is used to handle the response to be sent when a message is received.
	 * @see		SecureMMSClientHandler#setServerPort(int, String, String, RequestCallback)
	 * @see		SecureMMSClientHandler#setServerPort(int, String, String, String, RequestCallback)
	 */
	public interface RequestCallback{
		/**
		 * When a client sends an HTTP request to a server, the server performs a RequestCallback after receiving the request. 
		 * Argument list&lt;String&gt; messages means the list of messages about HTTP requests.
		 * Argument Map&lt;String,List&lt;String&gt;&gt; HeaderField is a set of headers for HTTP requests.
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
	 * @see		SecureMMSClientHandler#setSender(ResponseCallback)
	 */
	public interface ResponseCallback{
		/**
		 * When the server sends a response to the HTTP request sent by the client, the client performs a ResponseCallback.
		 * Argument list&lt;String&gt; messages means the list of messages about response.
		 * Argument Map&lt;String,List&lt;String&gt;&gt; HeaderField is a set of headers for response.
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
			if (interval == 0) {
				System.out.println(TAG+"Long-polling mode"); //TODO: Long-polling could have trouble when session disconnect.
			} else if (interval < 0){
				System.out.println(TAG+"Failed! Polling interval must be 0 or positive integer");
				return;
			}
			this.pollHandler = new PollHandler(clientMRN, dstMRN, svcMRN, interval, headerField);
			this.pollHandler.ph.setPollingResponseCallback(callback);
			this.pollHandler.ph.start();
		}
	}
	/**
	 * This method is that stop polling requests using interrupt signal. 
	 */
	public void stopPolling (){
		this.pollHandler.ph.markInterrupted();
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
	 * This method configures client's port to act as a HTTPS server and create a rcvHandler object.
	 * HTTPS server is configured via jksDirectory and jksPassword which matches that.
	 * It is used in a network that supports push method. It receives all messages toward itself.
	 * When a message is received via the callback method, it is possible to handle the response to be sent.
	 * @param 	port			port number
	 * @param 	jksDirectory	MMS certificate
	 * @param 	jksPassword		password of MMS certificate
	 * @param 	callback		callback interface of {@link RequestCallback}
	 * @throws 	Exception		if exception occurs
	 * @see		#setServerPort(int, String, String, String, RequestCallback)
	 * @see		#addContext(String)
	 */
	public void setServerPort (int port, String jksDirectory, String jksPassword, RequestCallback callback) throws Exception{
		if (!isErrorForSettingServerPort()){
			this.rcvHandler = new RcvHandler(port, jksDirectory, jksPassword);
			setPortAndCallback(port, callback);
		}
	}
	
	/**
	 * This method configures client's port to act as a HTTPS server and create a rcvHandler object.
	 * HTTPS server is configured via jksDirectory and jksPassword which matches that.
	 * It is used in a network that supports push method. This method configures default context and 
	 * it receives messages that url matches the default context. When a message is received via the 
	 * callback method, it is possible to handle the response to be sent.
	 * @param 	port			port number
	 * @param 	context			context (e.g. /get/messages/)
	 * @param 	jksDirectory	MMS certificate
	 * @param 	jksPassword		password of MMS certificate
	 * @param 	callback		callback interface of {@link RequestCallback}
	 * @throws 	Exception		if exception occurs
	 * @see		#setServerPort(int, String, String, RequestCallback)
	 * @see		#addContext(String)
	 */
	public void setServerPort (int port, String context, String jksDirectory, String jksPassword, RequestCallback callback) throws Exception{
		if (!isErrorForSettingServerPort()){
			this.rcvHandler = new RcvHandler(port, context, jksDirectory, jksPassword);
			setPortAndCallback(port, callback);
		}
	}
	
	/**
	 * This method configures client's port to act as a HTTPS file server and create a rcvHandler object.
	 * HTTPS server is configured via jksDirectory and jksPassword which matches that.
	 * It is used in a network that supports push method. This method configures default context and 
	 * it receives messages that url matches the default context. When a message is received via the 
	 * callback method, it is possible to handle the response to be sent.
	 * @param	port			the port number
	 * @param	fileDirectory	the file path (e.g. /files/ocean/waves/)
	 * @param	fileName		the file name (e.g. mokpo.xml)
	 * @throws	IOException 	if exception occurs
	 * @see 	#addFileContext(String, String)
	 */	
	public void setFileServerPort (int port, String fileDirectory, String fileName, String jksDirectory, String jksPassword, RequestCallback callback) throws Exception {
		if (!isErrorForSettingServerPort()){
			this.rcvHandler = new RcvHandler(port, fileDirectory, fileName);
			setPortAndCallback(port, callback);
		}
	}
	
	/**
	 * Add context to client's HTTPS server
	 * @param	context			the context (e.g. /get/messages/)
	 * @see		#setServerPort(int, String, String, RequestCallback)
	 * @see		#setServerPort(int, String, String, String, RequestCallback)
	 */
	public void addContext (String context) {
		if(this.rcvHandler != null) {
			this.rcvHandler.addContext(context);
		} else {
			System.out.println("Failed! HTTP server is required! Do setServerPort()");
		}
	}
	
	/**
	 * Add file directory and file name to client's HTTPS file server
	 * @param 	fileDirectory	the file path (e.g. /files/ocean/waves/)
	 * @param 	fileName		the file name (e.g. mokpo.xml)
	 * @see		#setFileServerPort(int, String, String, String, String, RequestCallback)
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
	
	private void setPortAndCallback (int port, RequestCallback callback) {
		this.clientPort = port;
		this.rcvHandler.hrh.setRequestCallback(callback);
		registerLocator(port);	
	}
	
	@Deprecated
	private void registerLocator(int port){
		try {
			new SecureMMSSndHandler(clientMRN).registerLocator(port);
			return;
		} catch (IOException e) {
			System.out.print(TAG);
			//e.printStackTrace();

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
			this.sendHandler.sendHttpsPost(dstMRN, loc, data, headerField);
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
			this.sendHandler.sendHttpsPost(dstMRN, "", data, headerField);
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
			this.sendHandler.sendHttpsGet(dstMRN, "", "", headerField);
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
			this.sendHandler.sendHttpsGet(dstMRN, loc, params, headerField);
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
			return this.sendHandler.sendHttpsGetFile(dstMRN, fileName, headerField);
		}
	}
	
	
	private class RcvHandler extends SecureMMSRcvHandler{
		RcvHandler(int port, String jksDirectory, String jksPassword) throws Exception {
			super(port, jksDirectory, jksPassword);
		}
		RcvHandler(int port, String context, String jksDirectory, String jksPassword) throws Exception {
			super(port, context, jksDirectory, jksPassword);
		}
		RcvHandler(int port, String fileDirectory, String fileName, String jksDirectory, String jksPassword) throws Exception {
			super(port, fileDirectory, fileName, jksDirectory, jksPassword);
		}
	}
	
	private class SendHandler extends SecureMMSSndHandler{
		SendHandler(String clientMRN) {
			super(clientMRN);
		}
	}
	
	private class PollHandler extends SecureMMSPollHandler{
		
		
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

