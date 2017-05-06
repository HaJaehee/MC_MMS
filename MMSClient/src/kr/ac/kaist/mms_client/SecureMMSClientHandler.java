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
*/
/* -------------------------------------------------------- */

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SecureMMSClientHandler {
	
	private String TAG = "[SecureMMSClientHandler] ";
	private RcvHandler rcvHandler = null;
	private PollHandler pollHandler = null;
	private SendHandler sendHandler = null;
	private String clientMRN = "";
	private int clientPort = 0;
	private Map<String,String> headerField = null;
	
	public SecureMMSClientHandler(String clientMRN) throws IOException{
		this.clientMRN = clientMRN;
		rcvHandler = null;
		pollHandler = null;
		sendHandler = null;
	}
	
	public interface PollingResponseCallback{
		void callbackMethod(Map<String,List<String>> headerField, String message);
	}
	public interface RequestCallback{
		String respondToClient(Map<String,List<String>> headerField, String message);
		int setResponseCode();
		Map<String,List<String>> setResponseHeader();
	}
	
	public interface ResponseCallback{
		void callbackMethod(Map<String,List<String>> headerField, String message);
	}

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

	public void setServerPort (int port, String jksDirectory, String jksPassword, RequestCallback callback) throws Exception{
		if (!isErrorForSettingServerPort()){
			this.rcvHandler = new RcvHandler(port, jksDirectory, jksPassword);
			setPortAndCallback(port, callback);
		}
	}
	
	public void setServerPort (int port, String context, String jksDirectory, String jksPassword, RequestCallback callback) throws Exception{
		if (!isErrorForSettingServerPort()){
			this.rcvHandler = new RcvHandler(port, context, jksDirectory, jksPassword);
			setPortAndCallback(port, callback);
		}
	}
	
	public void setFileServerPort (int port, String fileDirectory, String fileName, String jksDirectory, String jksPassword, RequestCallback callback) throws Exception {
		if (!isErrorForSettingServerPort()){
			this.rcvHandler = new RcvHandler(port, fileDirectory, fileName);
			setPortAndCallback(port, callback);
		}
	}
	
	public void addContext (String context) {
		if(this.rcvHandler != null) {
			this.rcvHandler.addContext(context);
		} else {
			System.out.println("Failed! HTTP server is required! Do setServerPort()");
		}
	}
	
	public void addFileContext (String fileDirectory, String fileName) {
		if(this.rcvHandler != null) {
			this.rcvHandler.addFileContext(fileDirectory, fileName);
		} else {
			System.out.println(TAG+"Failed! HTTP file server is required! Do setFileServerPort()");
		}
	}
	
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
	public void setMsgHeader(Map<String,String> headerField) throws Exception{
		this.headerField = headerField;
	}
	
	
	public void sendPostMsg(String dstMRN, String loc, String data) throws Exception{
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpsPost(dstMRN, loc, data, headerField);
		}
	}
	
	public void sendPostMsg(String dstMRN, String data) throws Exception{
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpsPost(dstMRN, "", data, headerField);
		}
	}
	
	//HJH
	public void sendGetMsg(String dstMRN) throws Exception{
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpsGet(dstMRN, "", "", headerField);
		}
	}
	
	//HJH
	public void sendGetMsg(String dstMRN, String loc, String params) throws Exception{
		if (this.sendHandler == null) {
			System.out.println(TAG+"Failed! HTTP client is required! Do setSender()");
		} else {
			this.sendHandler.sendHttpsGet(dstMRN, loc, params, headerField);
		}
	}
	
	//OONI
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
		
		
		PollHandler(String clientMRN, String dstMRN, String svcMRN, int interval, Map<String, String> headerField) throws IOException {
			super(clientMRN, dstMRN, svcMRN, interval, clientPort, 1, headerField);
		}
	}

}

