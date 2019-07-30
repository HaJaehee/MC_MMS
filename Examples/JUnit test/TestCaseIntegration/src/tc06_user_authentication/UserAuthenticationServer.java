package tc06_user_authentication;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;

/** 
File name : Server.java
	Polling messsage authentication tests
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-04-16

Rev. history : 2019-04-22
	Add server which is MMSClientHandler.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS6_Server to UserAuthenticationServer
	Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

public class UserAuthenticationServer {
	private String myMRN = null;
	private String dstMRN = null;
	private int port = 8907;
	private MMSClientHandler server = null;
	private MMSClientHandler sender = null;
	
	public UserAuthenticationServer() {
		this.myMRN = UserAuthenticationTest.serverMRN;
		this.dstMRN = UserAuthenticationTest.clientMRN;
		
		try {
			sender = new MMSClientHandler(myMRN);
			sender.setSender(new MMSClientHandler.ResponseCallback() {
				
				@Override
				public void callbackMethod(Map<String, List<String>> headerField, String message) {
					// TODO Auto-generated method stub
					
				}
			});
			
			server = new MMSClientHandler(myMRN);
			server.setServerPort(port, new MMSClientHandler.RequestCallback() {
				//Request Callback from the request message
				//it is called when client receives a message
				
				@Override
				public int setResponseCode() {
					// TODO Auto-generated method stub
					return 200;
				}
				
				@Override
				public String respondToClient(Map<String,List<String>> headerField, String message) {
					
					return "OK";
				}

				@Override
				public Map<String, List<String>> setResponseHeader() {
					// TODO Auto-generated method stub
					return null;
				}
			}); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void terminateServer() {
		server.terminateServer();
	}
	public void sendMessage(String data) {
		try {
			sender.sendPostMsg(dstMRN, data, 10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getMyMRN() { return myMRN; }
}
