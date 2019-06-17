package tc07_general_errorcode;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import kr.ac.kaist.mms_client.MMSClientHandler;
import tc_base.MMSTestBase;

/** 
File name : MMSGeneralErrorCodeTest.java
	This test is for testing whether MMS give a error message properly or not.
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-02

Rev.history :2019-06-13
Version : 0.9.2
	Change the class name TS7_Test -> MMSGeneralErrorCodeTest
	
	** And this test is succeeded
Modifier : Yunho Choi (choiking10@kaist.ac.kr)
*/
@FixMethodOrder(MethodSorters.DEFAULT)
public class MMSGeneralErrorCodeTest extends MMSTestBase {
	static MMSGeneralErrorCodeServer server;	

	// WARN: you have to change your dstMRN(mms-07-server) at MNS. At the website,
	// Add MNS entry having MRN=[urn:mrn:imo:imo-no:ts-mms-07-server], IP=[your-ip], PortNumber=[8907], Model=[2] and ADD!
	public static final String srcMRN = "urn:mrn:imo:imo-no:ts-mms-07-client";
	public static final String dstMRN = "urn:mrn:imo:imo-no:ts-mms-07-server";
	public static final int PORT = 8907;
	
	@Before
	public void before() {
		runServer(dstMRN, PORT);
	}
	
	@After
	public void after() throws Exception {
		server.terminate();
	}

	public boolean isErrorCode(String s) {
		if(s.charAt(0) != '[') return false;
		if(s.charAt(6) != ']') return false;
		return true;
	}
	public String getErrorCode(String s) {
		return s.substring(1, 6);
	}
	public void sendMessage(String src, String dst, String loc, String message, String expectedMessage) throws Exception {
		MMSGeneralErrorCodeClient client = new MMSGeneralErrorCodeClient(src);
		client.sendMessage(dst, message, loc, new MMSClientHandler.ResponseCallback() {		
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generatedX method stub
				assertEquals(message, expectedMessage);
			}
		});
	}
	public static void runServer(String mrn, int port) {
		try {
			server = new MMSGeneralErrorCodeServer(mrn, port);
		} catch(Exception e) {
			assertTrue("run Server make Exception", false);
		}
	
	}
	public void sendMessageForError(String src, String dst, String loc, String message, String expectedCode) throws Exception {
		
		MMSGeneralErrorCodeClient client = new MMSGeneralErrorCodeClient(src);
		client.sendMessage(dst, loc, message, new MMSClientHandler.ResponseCallback() {		
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generatedX method stub

				assertTrue("result have to be error code.", isErrorCode(message));
				assertEquals(getErrorCode(message), expectedCode);
			}
		});
	}
	
	@Test
	public void testOK() throws Exception {
		sendMessage(srcMRN, dstMRN, "", "123", "OK");
	}
	@Test
	public void testNullSrcMRN() throws Exception {
		sendMessageForError(null, dstMRN, "", "123", "10002");
	}
	@Test
	public void testNullDstMRN() throws Exception {
		sendMessageForError(srcMRN, null, "", "123", "10003");
	}
	@Test
	public void testNullMRN() throws Exception {
		sendMessageForError(null, null, "", "123", "10004");
	}
}

