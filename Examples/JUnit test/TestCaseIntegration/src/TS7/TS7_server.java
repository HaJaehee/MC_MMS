package TS7;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/** 
File name : TS7_server.java
	This test server is for testing whether MMS give a error message properly or not.
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-02
*/

public class TS7_server {
	MMSClientHandler server;
	public TS7_server(String myMRN, int port) throws Exception {
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
