import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import kr.ac.kaist.mms_client.MMSClientHandler;

/** 
File name : TS10_Test.java
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-22
*/
@FixMethodOrder(MethodSorters.DEFAULT)
public class TS10_Test {
	static TS10_server server;	
	public static final String srcMRN = "urn:mrn:imo:imo-no:ts-mms-07-client";
	public static final String dstMRN = "urn:mrn:imo:imo-no:ts-mms-07-server";
	public static final int PORT = 8907;
	
	@BeforeClass
	public static void testmain() throws Exception {
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
		TS10_client client = new TS10_client(src);
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
			server = new TS10_server(mrn, port);
		} catch(Exception e) {
			assertTrue("run Server make Exception", false);
		}
	
	}
	public void sendMessageForError(String src, String dst, String message, String expectedCode) throws Exception {
		TS10_client client = new TS10_client(src);
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
		runServer(dstMRN, PORT);
		sendMessage(srcMRN, dstMRN, "123", "OK");
	}
}

