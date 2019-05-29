import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/** 
File name : TS7_Test.java
	This test is for testing whether MMS give a error message properly or not.
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-02
*/
@FixMethodOrder(MethodSorters.DEFAULT)
public class TS7_Test {
	static TS7_server server;	

	// WARN: you have to change your dstMRN(mms-07-server) at MNS. At the website,
	// Add MNS entry having MRN=[urn:mrn:imo:imo-no:ts-mms-07-server], IP=[your-ip], PortNumber=[8907], Model=[2] and ADD!
	public static final String srcMRN = "urn:mrn:imo:imo-no:ts-mms-07-client";
	public static final String dstMRN = "urn:mrn:imo:imo-no:ts-mms-07-server";
	public static final int PORT = 8907;
	
	@Before
	public void before() {
		MMSConfiguration.MMS_URL = "mms-kaist.com:8088";
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
	public void sendMessage(String src, String dst, String message, String expectedMessage) throws Exception {
		TS7_client client = new TS7_client(src);
		client.sendMessage(dst, message, new MMSClientHandler.ResponseCallback() {		
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generatedX method stub
				assertEquals(message, expectedMessage);
			}
		});
	}
	public static void runServer(String mrn, int port) {
		try {
			server = new TS7_server(mrn, port);
		} catch(Exception e) {
			assertTrue("run Server make Exception", false);
		}
	
	}
	public void sendMessageForError(String src, String dst, String message, String expectedCode) throws Exception {
		TS7_client client = new TS7_client(src);
		client.sendMessage(dst, message, new MMSClientHandler.ResponseCallback() {		
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
		sendMessage(srcMRN, dstMRN, "123", "OK");
	}
	@Test
	public void testUnknownSrcMRN1() throws Exception {
		sendMessageForError("1234", "123", "123", "10001");
	}
	@Test
	public void testUnknownSrcMRN2() throws Exception {
		sendMessageForError(srcMRN, "123", "123", "10001");
	}

}

