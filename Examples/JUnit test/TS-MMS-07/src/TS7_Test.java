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
File name : TS7_Test.java
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-02
*/
@FixMethodOrder(MethodSorters.DEFAULT)
public class TS7_Test {
	static TS7_server server;	
	public static final String srcMRN = "urn:mrn:imo:imo-no:ts7-mms-01-client";
	public static final String dstMRN = "urn:mrn:imo:imo-no:ts7-mms-01-server";
	public static final int PORT = 8907;
	
	@BeforeClass
	public static void testmain() throws Exception {
		server = new TS7_server(dstMRN, 8907);
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
	public void sendMessageForError(String src, String dst, String message, String expectedCode) throws Exception {
		TS7_client client = new TS7_client(src);
		client.sendMessage(dst, message, new MMSClientHandler.ResponseCallback() {		
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generatedX method stub
				
				assertTrue(isErrorCode(message));
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
	@Test
	public void testNullMRN() throws Exception {
		sendMessageForError("", "", "123", "10004");
	}
	@Test
	public void testNULLSRCMRN() throws Exception {
		sendMessageForError("", dstMRN, "123", "10002");
	}
	@Test
	public void testNULLDSTMRN() throws Exception {
		sendMessageForError(srcMRN, "", "123", "10003");
	}

	
	

}

