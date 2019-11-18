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

Rev. history : 2018-07-19
Version : 0.7.2
	Added API; message sender guarantees message sequence.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-27
Version : 0.7.2
	Modified the awkward meaning of sentence
Modifier : Kyungjun Park (kjpark525@kaist.ac.kr)


Rev. history : 2018-10-11
Version : 0.8.0
	Modified polling client verification.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registration function.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-22
Version : 0.9.1
	Revised for testing restful API.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-11
Version : 0.9.3
	Updated exception throw-catch phrases.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-24
Version : 0.9.4
	Added timeout parameter to sendPostMsgWithTimeout() methods.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

 Rev. history : 2019-07-26
 Version : 0.9.4
 	Let methods have timeout parameter default.
 Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
 
Rev. history : 2019-10-14
Version : 0.9.6
	Added priority parameter to sendPostMsgWithPriority() methods.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler.RequestCallback;
import kr.ac.kaist.mms_client.MMSClientHandler.ResponseCallback;



/**
 * This handler helps client communicate to MMS over HTTPS. Client uses it to send or receive messages.
 * @version 0.9.5
 * @see MMSClientHandler
 */
public class SecureMMSClientHandler {
	
	private String TAG = "[SecureMMSClientHandler] ";
	private RcvHandler rcvHandler = null;
	private SecureMMSPollHandler pollHandler = null;
	private SendHandler sendHandler = null;
	private String clientMRN = "";
	private int clientPort = 0;
	private Map<String,List<String>> headerField = null;
	
	
	/**
	 * The Constructor of SecureMMSClientHandler class
	 * @param	clientMRN		the MRN of client
	 * //@throws	IOException 	if exception occurs
	 */	
	public SecureMMSClientHandler(String clientMRN) {
	
		this.clientMRN = clientMRN;
		rcvHandler = null;
		pollHandler = null;
		sendHandler = null;
	}
	
	/**
	 * This interface handles the response to polling request.
	 * @see		SecureMMSClientHandler#startPolling(String, String, int, int, PollingResponseCallback)
	 */
	public interface PollingResponseCallback{
		/**
		 * Argument list&lt;String&gt; messages means the list of messages about polling response.
		 * Argument Map&lt;String,List&lt;String&gt;&gt; HeaderField is a set of headers for polling response.
		 * @param headerField	The received header field of the response message 
		 * @param messages		The response messages
		 */
		void callbackMethod(Map<String,List<String>> headerField, List<String> messages);
	}
	
	/**
	 * This interface handles the response to be sent when a message is received.
	 * @see		SecureMMSClientHandler#setServerPort(int, String, String, RequestCallback)
	 * @see		SecureMMSClientHandler#setServerPort(int, String, String, String, RequestCallback)
	 */
	public interface RequestCallback{
		/**
		 * When a client sends an HTTP request to a server, the server performs a RequestCallback after receiving the request. 
		 * Argument list&lt;String&gt; messages means the list of messages about HTTP requests.
		 * Argument Map&lt;String,List&lt;String&gt;&gt; HeaderField is a set of headers for HTTP requests.
		 * @param headerField	The received header field of the request message 
		 * @param message		The request message
		 * @return String		The response message
		 */
		String respondToClient(Map<String,List<String>> headerField, String message);
		/**
		 * When a client sends an HTTP request to a server, the server performs a RequestCallback after receiving the request. 
		 * Argument list&lt;String&gt; messages means the list of messages about HTTP requests.
		 * Argument Map&lt;String,List&lt;String&gt;&gt; HeaderField is a set of headers for HTTP requests.
		 * @return Integer		The response code
		 */
		int setResponseCode();
		
		/**
		 * When a client sends an HTTP request to a server, the server performs a RequestCallback after receiving the request. 
		 * Argument list&lt;String&gt; messages means the list of messages about HTTP requests.
		 * Argument Map&lt;String,List&lt;String&gt;&gt; HeaderField is a set of headers for HTTP requests.
		 * @return Map		The header field of the response message.
		 */
		Map<String,List<String>> setResponseHeader();
	}
	
	/**
	 * This interface handles the response to be received when a message is sent.
	 * @see		SecureMMSClientHandler#setSender(ResponseCallback)
	 */
	public interface ResponseCallback{
		/**
		 * When the server sends a response to the HTTP request sent by the client, the client performs a ResponseCallback.
		 * Argument list&lt;String&gt; messages means the list of messages about response.
		 * Argument Map&lt;String,List&lt;String&gt;&gt; HeaderField is a set of headers for response.
		 * @param headerField	The received header field of the response message 
		 * @param message		The response message
		 */
		void callbackMethod(Map<String,List<String>> headerField, String message);
	}

	
	// TODO: Youngjin Kim must inspect this following code.
	/**
	 * This method helps MMS client to request polling to a MMS. When using this method, MMS client sends polling request
	 * per interval (ms). When the MMS receives the polling request, if there are messages toward the client, 
	 * the messages are sent to the MMS client, who has requested polling, from the MMS. And then the MMS client executes
	 * a callbackMethod. Depending on whether it is the way of normal polling or long polling,
	 * the way of response is different.
	 * @param	dstMRN			the MRN of MMS to request polling
	 * @param	svcMRN			the MRN of service, which may send to client
	 * @param	interval		the frequency of polling (unit of time: ms). If the interval is 0, the client does long polling.
	 * @param timeout	    set timeout parameter to Connection Timeout and Read Timeout.. When long polling, Read Timeout is
	 * 	 *                      ignored.
	 * @param	callback		the callback interface of {@link PollingResponseCallback}
	 * @throws	IOException 	if exception occurs
	 * @see 	PollingResponseCallback
	 */	
	@Deprecated
	public void startPolling (String dstMRN, String svcMRN, int interval, int timeout, PollingResponseCallback callback) throws IOException{
		startPolling (dstMRN, svcMRN, null, interval, timeout, callback);
	}
	
	
	// TODO: Youngjin Kim must inspect this following code.
	/**
	 * This method helps MMS client to request polling to a MMS. When using this method, MMS client sends polling request
	 * per interval (ms). When the MMS receives the polling request, if there are messages toward the client, 
	 * the messages are sent to the MMS client, who has requested polling, from the MMS. And then the MMS client executes
	 * a callbackMethod. Depending on whether it is the way of normal polling or long polling,
	 * the way of response is different.
	 * @param	dstMRN			the MRN of MMS to request polling
	 * @param	svcMRN			the MRN of service, which may send to client
	 * @param	hexSignedData	the hex signed data for client verification
	 * @param	interval		the frequency of polling (unit of time: ms). If the interval is 0, the client does long polling.
	 * @param timeout	    set timeout parameter to Connection Timeout and Read Timeout. When long polling, Read Timeout is
	 *                      ignored.
	 * @param	callback		the callback interface of {@link PollingResponseCallback}
	 * @throws	IOException 	if exception occurs
	 * @see 	PollingResponseCallback
	 */	
	public void startPolling (String dstMRN, String svcMRN, String hexSignedData, int interval, int timeout, PollingResponseCallback callback) throws IOException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			System.out.println("Client MRN must not be null.");
			return;
		}
		if (this.sendHandler != null) {
			System.out.println(TAG+"Failed! MMSClientHandler must have exactly one function! It already has done setSender()");
		} else if (this.rcvHandler != null) {
			System.out.println(TAG+"Failed! MMSClientHandler must have exactly one function! It already has done setServerPort() or setFileServerPort()");
		} else {
			if (interval == 0) {
				System.out.println(TAG+"Long-polling mode"); //TODO: Long-polling could have trouble when session disconnect.
				this.pollHandler = new LongPollHandler(clientMRN, dstMRN, svcMRN, hexSignedData, interval, timeout, headerField);
			} else if (interval < 0){
				System.out.println(TAG+"Failed! Polling interval must be 0 or positive integer");
				return;
			} else {
				this.pollHandler = new PollHandler(clientMRN, dstMRN, svcMRN, hexSignedData, interval, timeout, headerField);
			}
			this.pollHandler.ph.setPollingResponseCallback(callback);
			this.pollHandler.ph.start();
		}
	}
	/**
	 * This method helps client to stop polling requests using interrupt signal. 
	 */
	public void stopPolling (){
		this.pollHandler.ph.markInterrupted();
		this.pollHandler.ph.interrupt();
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
	 * This method configures client's port to act as a HTTPS server and to create the rcvHandler object.
	 * HTTPS server is configured via jksDirectory and jksPassword.
	 * It is used in a network that supports push method. It receives all messages toward itself.
	 * When a message is received via the callback method, it is possible to handle the response to be sent.
	 * @param 	port			port number
	 * @param 	jksDirectory	MMS certificate
	 * @param 	jksPassword		password of MMS certificate
	 * @param 	callback		callback interface of {@link RequestCallback}
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#setServerPort(int, String, String, String, RequestCallback)
	 * @see		#addContext(String)
	 */
	public void setServerPort (int port, String jksDirectory, String jksPassword, RequestCallback callback) throws IOException{
		if (!isErrorForSettingServerPort()){
			this.rcvHandler = new RcvHandler(port, jksDirectory, jksPassword);
			setPortAndCallback(port, callback);
		}
	}
	
	/**
	 * This method configures client's port to act as a HTTPS server and to create the rcvHandler object.
	 * HTTPS server is configured via jksDirectory and jksPassword.
	 * It is used in a network that supports push method. This method configures default context and 
	 * it receives messages that url matches the default context. When a message is received via the 
	 * callback method, it is possible to handle the response to be sent.
	 * @param 	port			port number
	 * @param 	context			context (e.g. /get/messages/)
	 * @param 	jksDirectory	MMS certificate
	 * @param 	jksPassword		password of MMS certificate
	 * @param 	callback		callback interface of {@link RequestCallback}
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#setServerPort(int, String, String, RequestCallback)
	 * @see		#addContext(String)
	 */
	public void setServerPort (int port, String context, String jksDirectory, String jksPassword, RequestCallback callback) throws IOException{
		if (!isErrorForSettingServerPort()){
			this.rcvHandler = new RcvHandler(port, context, jksDirectory, jksPassword);
			setPortAndCallback(port, callback);
		}
	}
	
	/**
	 * This method configures client's port to act as a HTTPS file server and to create the rcvHandler object.
	 * HTTPS server is configured via jksDirectory and jksPassword.
	 * It is used in a network that supports push method. This method configures default context and 
	 * it receives messages that url matches the default context. When a message is received via the 
	 * callback method, it is possible to handle the response to be sent.
	 * @param	port			The port number
	 * @param	fileDirectory	The file path (e.g. /files/ocean/waves/)
	 * @param	fileName		The file name (e.g. mokpo.xml)
	 * @param 	jksDirectory	The directory of the java key store
	 * @param	jksPassword		The password of the java key store
	 * @param 	callback		Callback interface of {@link RequestCallback}
	 * @throws	IOException 	If exception occurs
	 * @see 	#addFileContext(String, String)
	 */	
	public void setFileServerPort (int port, String fileDirectory, String fileName, String jksDirectory, String jksPassword, RequestCallback callback) throws IOException{
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
	 * Terminates the servers.
	 * 
	 * @see #setServerPort(int, String, String, RequestCallback)
	 * @see #setServerPort(int, String, String, String, RequestCallback)
	 * @see #setFileServerPort(int, String, String, String, String, RequestCallback)
	 */
	public void terminateServer() {
		if (this.rcvHandler != null) {
			this.rcvHandler.stopRcv(0);
			this.rcvHandler = null;
		} else {
			System.out.println(TAG + "Failed! HTTP file server is required! Do setFileServerPort()");
		}
	}
	
	
	/**
	 * This method is used to set in MMS client in order to send message. If using this method, it is possible 
	 * to use sendPostMsg and sendGetMsg method. When the client send a message, it can handle the response to 
	 * be received via callback interface.
	 * @param 	callback		the callback interface of {@link ResponseCallback} 
	 * @see 	#sendGetMsg(String, int)
	 * @see	 	#sendGetMsg(String, String, String, int)
	 * @see 	#sendPostMsg(String, String, int)
	 * @see 	#sendPostMsg(String, String, String, int)
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
	}
	
	
	//HJH
	/**
	 * When sending a message, add custom header to HTTP header field
	 * @param 	headerField		Key and value for additional header
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 */
	public void setMsgHeader(Map<String,List<String>> headerField) throws IOException{
		this.headerField = headerField;
	}
	
	/**
	 * Send a POST message via MMS to the destination MRN corresponding to the location URL
	 * @param 	dstMRN			the destination MRN to send data
	 * @param 	loc				URL location
	 * @param 	data			the data to send
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#sendPostMsg(String, String)
	 * @see		#setSender(ResponseCallback)
	 */
	/*
	@Deprecated
	public void sendPostMsg(String dstMRN, String loc, String data) throws IOException, NullPointerException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			throw new NullPointerException();
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpsPost(dstMRN, loc, data, headerField);
		}
	}*/
	
	/**
	 * Send a POST message to the destination MRN via MMS
	 * @param 	dstMRN			the destination MRN to send data
	 * @param 	data			the data to send
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#sendPostMsg(String, String, String)
	 * @see		#setSender(ResponseCallback)
	 */
	/*
	@Deprecated
	public void sendPostMsg(String dstMRN, String data) throws IOException, NullPointerException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			throw new NullPointerException();
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpsPost(dstMRN, "", data, headerField);
		}
	}*/
	
	/**
	 * Send a POST message to the destination MRN via MMS
	 * @param 	dstMRN			the destination MRN to send data
	 * @param 	data			the data to send
	 * @param	timeout			set timeout parameter to Connection Timeout and Read Timeout.
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#setSender(ResponseCallback)
	 */
	public void sendPostMsg(String dstMRN, String data, int timeout) throws IOException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			System.out.println("Client MRN must not be null.");
			return;
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpsPostWithTimeout(dstMRN, "", data, headerField, timeout);
		}
	}
	
	/**
	 * Send a POST message to the destination MRN via MMS
	 * @param 	dstMRN			the destination MRN to send data
	 * @param 	loc				URL location
	 * @param 	data			the data to send
	 * @param	timeout			set timeout parameter to Connection Timeout and Read Timeout.
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#sendPostMsg(String, String, int)
	 * @see		#setSender(ResponseCallback)
	 */
	public void sendPostMsg(String dstMRN, String loc, String data, int timeout) throws IOException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			System.out.println("Client MRN must not be null.");
			return;
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpsPostWithTimeout(dstMRN, loc, data, headerField, timeout);
		}
	}
	
	/**
	 * Send a POST message to the destination MRN via MMS with priority.
	 * 
	 * @param dstMRN the destination MRN to send data
	 * @param data   the data to send
	 * @param priority   message priority
	 * @param timeout set timeout parameter to Connection Timeout and Read Timeout.
	 * @throws IOException if exception occurs
	 * @see #sendPostMsg(String, String, int)
	 * @see #setSender(ResponseCallback)
	 */
	public void sendPostMsgWithPriority(String dstMRN, String data, int priority, int timeout) throws IOException {
		if (clientMRN == null) {
			System.out.println(TAG + "Failed! Client MRN must not be null.");
			System.out.println("Client MRN must not be null.");
			return;
		}
		if (this.sendHandler == null) {
			System.out.println(TAG + "Failed! HTTP client is required! Do setSender()");
		} else {
			if(headerField == null) {
				headerField = new HashMap<String, List<String>>();
			}
			
			List<String> valueList = new ArrayList<String>();
			valueList.add("" + priority);
			headerField.put("priority", valueList);
			
			this.sendHandler.sendHttpsPostWithTimeout(dstMRN, "", data, headerField, timeout);
		}
	}
	
	/**
	 * Send a POST message to the destination MRN via MMS with priority.
	 * 
	 * @param dstMRN the destination MRN to send data
	 * @param loc	 url location
	 * @param data   the data to send
	 * @param priority   message priority
	 * @param timeout set timeout parameter to Connection Timeout and Read Timeout.
	 * @throws IOException if exception occurs
	 * @see #sendPostMsg(String, String, int)
	 * @see #sendPostMsgWithPriority(String, String, int, int)
	 * @see #setSender(ResponseCallback)
	 */
	public void sendPostMsgWithPriority(String dstMRN, String loc, String data, int priority, int timeout) throws IOException {
		if (clientMRN == null) {
			System.out.println(TAG + "Failed! Client MRN must not be null.");
			System.out.println("Client MRN must not be null.");
			return;
		}
		if (this.sendHandler == null) {
			System.out.println(TAG + "Failed! HTTP client is required! Do setSender()");
		} else {
			if(headerField == null) {
				headerField = new HashMap<String, List<String>>();
			}
			
			List<String> valueList = new ArrayList<String>();
			valueList.add("" + priority);
			headerField.put("priority", valueList);
			
			this.sendHandler.sendHttpsPostWithTimeout(dstMRN, loc, data, headerField, timeout);
		}
	}
	
	//HJH
	/**
	 * Send a GET message to the destination MRN via MMS
	 * @param 	dstMRN			the destination MRN
	 * @param	timeout			set timeout parameter to Connection Timeout and Read Timeout.
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#setSender(ResponseCallback)
	 */
	public void sendGetMsg(String dstMRN, int timeout) throws IOException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			System.out.println("Client MRN must not be null.");
			return;
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpsGetWithTimeout(dstMRN, "", "", headerField, timeout);
		}
	}
	
	//HJH
	/**
	 * Send a GET message via MMS to the destination MRN corresponding to the location URL
	 * parameter
	 * @param 	dstMRN			the destination MRN
	 * @param	loc				URL location
	 * @param	params			parameters
	 * @param	timeout			set timeout parameter to Connection Timeout and Read Timeout.
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#sendGetMsg(String, int)
	 * @see		#setSender(ResponseCallback)
	 */
	public void sendGetMsg(String dstMRN, String loc, String params, int timeout) throws IOException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			System.out.println("Client MRN must not be null.");
			return;
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpsGetWithTimeout(dstMRN, loc, params, headerField, timeout);
		}
	}
	
	// HJH
	/**
	 * Send a restful API request message to MMS corresponding to the location and the URL parameter.
	 * 
	 * @param loc    url location
	 * @param params parameters
	 * @param	timeout			set timeout parameter to Connection Timeout and Read Timeout.
	 * @throws IOException if exception occurs
	 * @see #sendGetMsg(String, int)
	 * @see #setSender(ResponseCallback)
	 */
	public void sendApiReq(String loc, String params, int timeout) throws IOException{
		if (this.sendHandler == null) {
			System.out.println(TAG + "Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpsGetWithTimeout(null, loc, params, headerField, timeout);
		}
	}
	
	/*-----------------------------------------------------------------------------------
	 * Message sender supporting message sequence.
	 -----------------------------------------------------------------------------------*/
	
	/**
	 * Send a POST message to the destination MRN that url matches the location via MMS.
	 * Message sender guarantees message sequence.
	 * @param 	dstMRN			the destination MRN to send data
	 * @param 	loc				url location
	 * @param 	data			the data to send
	 * @param 	seqNum			sequence number of message
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#sendPostMsg(String, String)
	 * @see		#setSender(ResponseCallback)
	 */
	/*
	@Deprecated
	public void sendPostMsg(String dstMRN, String loc, String data, int seqNum) throws IOException, NullPointerException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			throw new NullPointerException();
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} 
		else if (seqNum < 0) {
			System.out.println(TAG+"Failed! seqNum must be equal to or greater than zero.");
		}
		else {
			this.sendHandler.sendHttpsPost(dstMRN, loc, data, headerField, seqNum);
		}
	}*/
	
	/**
	 * Send a POST message to the destination MRN that url matches the location via MMS.
	 * Message sender guarantees message sequence.
	 * @param 	dstMRN			the destination MRN to send data
	 * @param 	loc				url location
	 * @param 	data			the data to send
	 * @param 	seqNum			sequence number of message
	 * @param	timeout			set timeout parameter to Connection Timeout and Read Timeout.
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#sendPostMsg(String, String, String, int)
	 * @see		#setSender(ResponseCallback)
	 */
	public void sendPostMsg(String dstMRN, String loc, String data, int seqNum, int timeout) throws IOException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			System.out.println("Client MRN must not be null.");
			return;
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} 
		else if (seqNum < 0) {
			System.out.println(TAG+"Failed! seqNum must be equal to or greater than zero.");
		}
		else {
			this.sendHandler.sendHttpsPostWithTimeout(dstMRN, loc, data, headerField, seqNum, timeout);
		}
	}
	
	/**
	 * Send a POST message to the destination MRN via MMS.
	 * Message sender guarantees message sequence.
	 * @param 	dstMRN			the destination MRN to send data
	 * @param 	data			the data to send
	 * @param 	seqNum			sequence number of message
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#sendPostMsg(String, String, String)
	 * @see		#setSender(ResponseCallback)
	 */
	/*
	@Deprecated
	public void sendPostMsg(String dstMRN, String data, int seqNum) throws IOException, NullPointerException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			throw new NullPointerException();
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} 
		else if (seqNum < 0) {
			System.out.println(TAG+"Failed! seqNum must be equal to or greater than zero.");
		}
		else {
			this.sendHandler.sendHttpsPost(dstMRN, "", data, headerField, seqNum);
		}
	}*/
	
	/**
	 * Send a POST message to the destination MRN via MMS.
	 * Message sender guarantees message sequence.
	 * @param 	dstMRN			the destination MRN to send data
	 * @param 	data			the data to send
	 * @param 	seqNum			sequence number of message
	 * @param	timeout			set timeout parameter to Connection Timeout and Read Timeout.
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#sendPostMsg(String, String, int)
	 * @see		#setSender(ResponseCallback)
	 */
	public void sendPostMsg(String dstMRN, String data, int seqNum, int timeout) throws IOException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			System.out.println("Client MRN must not be null.");
			return;
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} 
		else if (seqNum < 0) {
			System.out.println(TAG+"Failed! seqNum must be equal to or greater than zero.");
		}
		else {
			this.sendHandler.sendHttpsPostWithTimeout(dstMRN, "", data, headerField, seqNum, timeout);
		}
	}
	
	//HJH
	/**
	 * Send a GET message to the destination MRN via MMS.
	 * Message sender guarantees message sequence.
	 * @param 	dstMRN			the destination MRN
	 * @param 	seqNum			sequence number of message
	 * @param	timeout			set timeout parameter to Connection Timeout and Read Timeout.
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#sendGetMsg(String, int)
	 * @see		#setSender(ResponseCallback)
	 */
	public void sendGetMsg(String dstMRN, int seqNum, int timeout) throws IOException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			System.out.println("Client MRN must not be null.");
			return;
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} 
		else if (seqNum < 0) {
			System.out.println(TAG+"Failed! seqNum must be equal to or greater than zero.");
		}
		else {
			this.sendHandler.sendHttpsGetWithTimeout(dstMRN, "", "", headerField, seqNum, timeout);
		}
	}
	
	//HJH
	/**
	 * Send a GET message which the destination MRN is that url matches the location via MMS and setting
	 * parameter.
	 * Message sender guarantees message sequence.
	 * @param 	dstMRN			the destination MRN
	 * @param	loc				url location
	 * @param	params parameters
	 * @param 	seqNum			sequence number of message
	 * @param	timeout			set timeout parameter to Connection Timeout and Read Timeout.
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 * @see		#sendGetMsg(String, String, String, int)
	 * @see		#setSender(ResponseCallback)
	 */
	public void sendGetMsg(String dstMRN, String loc, String params, int seqNum, int timeout) throws IOException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			System.out.println("Client MRN must not be null.");
			return;
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} 
		else if (seqNum < 0) {
			System.out.println(TAG+"Failed! seqNum must be equal to or greater than zero.");
		}
		else {
			this.sendHandler.sendHttpsGetWithTimeout(dstMRN, loc, params, headerField, seqNum, timeout);
		}
	}
	
	/*-----------------------------------------------------------------------------------
	 * END. Message sender supporting message sequence. 
	 -----------------------------------------------------------------------------------*/
	
	
	//OONI
	/**
	 * Use when requesting a file from the destination MRN. Send a GET message to a file server mapping to destination MRN
	 * to request a file that matches the parameterized filename.
	 * @param 	dstMRN			the destination MRN to send a message
	 * @param 	fileName		file path and name (e.g. "/get/test.xml")
	 * @param	timeout			set timeout parameter to Connection Timeout and Read Timeout.
	 * @return					returning result of saving file
	 * 							<code>null</code> if saving file is failed.
	 * //@throws NullPointerException if exception occurs
	 * @throws IOException if exception occurs
	 */
	public String requestFile(String dstMRN, String fileName, int timeout) throws IOException{
		if (clientMRN == null) {
			System.out.println(TAG+"Failed! Client MRN must not be null.");
			System.out.println("Client MRN must not be null.");
			return null;
		}
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
			return null;
		} else {
			return this.sendHandler.sendHttpsGetFileWithTimeout(dstMRN, fileName, headerField, timeout);
		}
	}
	
	
	private class RcvHandler extends SecureMMSRcvHandler {
		RcvHandler(int port, String jksDirectory, String jksPassword) throws IOException {
			super(port, jksDirectory, jksPassword);
		}
		RcvHandler(int port, String context, String jksDirectory, String jksPassword) throws IOException {
			super(port, context, jksDirectory, jksPassword);
		}
		RcvHandler(int port, String fileDirectory, String fileName, String jksDirectory, String jksPassword) throws IOException {
			super(port, fileDirectory, fileName, jksDirectory, jksPassword);
		}
	}
	
	private class SendHandler extends SecureMMSSndHandler{
		SendHandler(String clientMRN) {
			super(clientMRN);
		}
	}
	
	private class PollHandler extends SecureMMSPollHandler{
		
		PollHandler(String clientMRN, String dstMRN, String svcMRN, String hexSignedData, int interval, int timeout, Map<String, List<String>> headerField) throws IOException {
			super(clientMRN, dstMRN, svcMRN, hexSignedData, interval, "normal", timeout, headerField);
		}
	}
	
	private class LongPollHandler extends SecureMMSPollHandler{
		
		LongPollHandler(String clientMRN, String dstMRN, String svcMRN, String hexSignedData, int interval, int timeout, Map<String, List<String>> headerField) throws IOException {
			super(clientMRN, dstMRN, svcMRN, hexSignedData, interval, "long", timeout, headerField);
		}
	}
	
}

