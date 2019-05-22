import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/** 
File name : TS10_server.java
	Relaying message function for the purpose of testing MMS
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-22
*/

public class TS10_server {
	MMSClientHandler server;
	public TS10_server(String myMRN, int port) throws Exception {
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
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "OK";
			}
		});
	}
	public void stop() {
		server.terminateServer();
	}
}
