package tc07_general_errorcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;

/** 
File name : MMSGeneralErrorCodeServer.java
	This test server is for testing whether MMS give a error message properly or not.
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-02

Rev.history :2019-06-13
Version : 0.9.2
	Change the class name TS7_Server -> MMSGeneralErrorCodeServer
	
	** And this test is succeeded
Modifier : Yunho Choi (choiking10@kaist.ac.kr)
*/

public class MMSGeneralErrorCodeServer {
	MMSClientHandler server;
	public MMSGeneralErrorCodeServer(String myMRN, int port) throws Exception {
		server = new MMSClientHandler(myMRN);
		
		server.setServerPort(port, new MMSClientHandler.RequestCallback() {
			
			@Override
			public Map<String, List<String>> setResponseHeader() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int setResponseCode() {
				// TODO Auto-generated method stub
				return 200;
			}
			
			@Override
			public String respondToClient(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub														

				return "OK";
			}
		});
	}
	public void terminate() {
		server.terminateServer();
	}
}
