package tc10_long_waiting;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import kr.ac.kaist.mms_client.MMSClientHandler;
import tc_base.MMSTestBase;

/** 
File name : MMSLongResponseWaitingForSPTest.java
	This test is for testing long response waiting when SP does not response. 
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-22

Rev.history :2019-06-13
Version : 0.9.2
	Change the class name TS10_Test -> MMSLongResponseWaitingForSPTest
	
	** And this test is succeeded
Modifier : Yunho Choi (choiking10@kaist.ac.kr)

Rev.history :2019-07-22
Version : 0.9.4
	Change testcase to test multiple request.
	
	** And this test is succeeded
Modifier : Yunho Choi (choiking10@kaist.ac.kr)
*/

@FixMethodOrder(MethodSorters.DEFAULT)
public class MMSLongResponseWaitingForSPTest extends MMSTestBase {
	static MMSClientHandler server;	
	
	// WARN: you have to change your dstMRN(mms-10-server) at MNS. At the website,
	// Add MNS entry having MRN=[urn:mrn:imo:imo-no:ts-mms-10-server], IP=[your-ip], PortNumber=[8907], Model=[2] and ADD!
	public static final String srcMRN = "urn:mrn:imo:imo-no:ts-mms-10-client";
	public static final String dstMRN = "urn:mrn:imo:imo-no:ts-mms-10-server";
	public static final int PORT = 8907;
	
	@After
	public void after() {
		server.terminateServer();
	}
	
	public void sendMessage(String src, String dst, 
			String message, String expectedMessage, int timeout) throws Exception {
		MMSClientHandler client = new MMSClientHandler(src);
		client.setSender(new MMSClientHandler.ResponseCallback() {		
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generatedX method stub
				assertEquals(message, expectedMessage);
			}
		});
		
		client.sendPostMsg(dst, message, timeout);
	}
	
	public static void runServer(String mrn, int port, long sleepTime, boolean isError) {
		try {
			server = new MMSClientHandler(mrn);
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
						Thread.sleep(sleepTime);
						if(isError) {
							assertTrue("This code must cause an error.", false);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						if(!isError) {
							assertTrue("This code must not cause an error.", false);
						}
					}
					return "OK";
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	@Test
	public void testOK() throws Exception {
		runServer(dstMRN, PORT, 10, false);
		for (int i = 0; i < 100; i++) {
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						sendMessage(srcMRN, dstMRN, "123", "OK", 1000);
					} catch(Exception e) {
						assertTrue("this code have to unreachable", false);
					}
				}
			});
			t.start();
			Thread.sleep(50);
		}
		Thread.sleep(1000);
	}
	
	@Test
	public void testTimeout() throws Exception {
		runServer(dstMRN, PORT, 10000, true);
		for (int i = 0; i < 100; i++) {
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						sendMessage(srcMRN, dstMRN, "123", "OK", 1000);
						assertTrue("this code have to unreachable", false);
					} catch(Exception e) {
						
					}
				}
			});
			t.start();
			Thread.sleep(50);
		}
		Thread.sleep(1000);
	}
}

