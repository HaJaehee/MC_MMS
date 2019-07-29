package tc12_https;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import kr.ac.kaist.mms_client.SecureMMSClientHandler;
import net.etri.pkilib.client.ClientPKILibrary;
import net.etri.pkilib.tool.ByteConverter;
import tc_base.MMSTestBase;

/** 
File name : MMSHttpsTest.java
	This test is to test HTTPS scenario. 
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-07-24
*/

@FixMethodOrder(MethodSorters.DEFAULT)
public class MMSHttpsTest extends MMSTestBase {
	// WARN: you have to change your dstMRN(mms-11-server) at MNS. At the website,
	// Add MNS entry having MRN=[urn:mrn:imo:imo-no:ts-mms-10-server], IP=[your-ip], PortNumber=[8907], Model=[2] and ADD!
	public static final String JKS_PATH = "jks/testkey.jks";
	public static final String JKS_PASSWORD = "mmsclient";
	
	public static final String SC1_DUMMY_MRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn"; 
	public static final String SC2_DUMMY_MRN = "urn:mrn:imo:imo-no:ts-mms-12-client";
	public static final String SP_DUMMY_MRN = "urn:mrn:imo:imo-no:ts-mms-12-server";
	public static final String MMS_MRN = "urn:mrn:smart-navi:device:mms1";
	public static final int PORT = 8907;
	
	public static final String FOWARDING_CONTEXT = "/forwarding";
	public static final String MESSAGE_PREFIX = "¾È³ç hi hello";
	
	
	
	SecureMMSClientHandler server;	
	SecureMMSClientHandler serverSender;
	SecureMMSClientHandler SC1Sender;
	SecureMMSClientHandler SC2Sender;

	Thread serverThread;
	Thread SC1Thread;
	Thread SC2Thread;

	private String sendingMessages = "";
	private String receivingMessages = "";
	
	@Before
	public void before() {
		changePort(444);
	}
	
	@After
	public void after() {
		server.terminateServer();
		SC1Sender.stopPolling();
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
	
	public void runServer() {
		System.out.println("\n\n==== run server ====\n\n");
		try {
			server = new SecureMMSClientHandler(SP_DUMMY_MRN);
			serverSender = new SecureMMSClientHandler(SP_DUMMY_MRN);
			serverSender.setSender(new SecureMMSClientHandler.ResponseCallback() {
				//Response Callback from the request message
				@Override
				public void callbackMethod(Map<String, List<String>> headerField, String message) {
					// TODO Auto-generated method stub
					System.out.println(message);
				}
			});
			
			// ==== run server ----
			server.setServerPort(PORT, FOWARDING_CONTEXT, JKS_PATH, JKS_PASSWORD, 
					new SecureMMSClientHandler.RequestCallback() {
				
				@Override
				public int setResponseCode() {
					// TODO Auto-generated method stub
					return 200;
				}
				
				//it is called when client receives a message
				@Override
				public String respondToClient(Map<String,List<String>> headerField, String message) {
					try {
						Iterator<String> iter = headerField.keySet().iterator();
						while (iter.hasNext()){
							String key = iter.next();
							System.out.println(key+":"+headerField.get(key).get(0));
						}
						System.out.println(message);

						//it only forwards messages to SC1_DUMMY
						serverSender.sendPostMsg(SC1_DUMMY_MRN, message, 10000);

					} catch (Exception e) {
						e.printStackTrace();
					}
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
	
	public void runSC1() throws Exception {
		System.out.println("\n\n==== start SC1 ====\n\n");
		SC1Sender = new SecureMMSClientHandler(SC1_DUMMY_MRN);

		ClientPKILibrary clientPKILib = ClientPKILibrary.getInstance();
		ByteConverter byteConverter = ByteConverter.getInstance();

		// ===== dummy content =====
		byte[] content = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };

		// ===== active certificate =====
		String privateKeyPath_active = "certs/PrivateKey_POUL_LOWENORN_active.pem";
		String certPath_active = "certs/Certificate_POUL_LOWENORN_active.pem";

		byte[] signedData_active = clientPKILib.generateSignedData(content, privateKeyPath_active, certPath_active);
		String hex = byteConverter.byteArrToHexString(signedData_active);
		
		// ==== start polling ----
		SC1Sender.startPolling(MMS_MRN, SP_DUMMY_MRN, hex, 0, 10000, new SecureMMSClientHandler.PollingResponseCallback() {
			//Response Callback from the polling message
			//it is called when client receives a message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
				// TODO Auto-generated method stub
				for (String s : messages) {
					System.out.println(s);
					receivingMessages += s + "\n";
				}
			}
		});
	}
	
	public void runSC2() throws Exception {
		System.out.println("\n\n==== start SC2 ====\n\n");
		
		SC2Sender = new SecureMMSClientHandler(SC2_DUMMY_MRN);
		
		//Service Consumer is able to set its HTTP header field
		Map<String, List<String>> headerfield = new HashMap<String, List<String>>(); // Header field example. You are able to remove this code.
		List<String> valueList = new ArrayList<String>();
		valueList.add("1234567890");
		headerfield.put("AccessToken",valueList);
		SC2Sender.setMsgHeader(headerfield);
		// Header field example ends.
		
		// Sender example.
		SC2Sender.setSender(new SecureMMSClientHandler.ResponseCallback() {
			// callbackMethod is called when the response message arrives which is related to request message.
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) { // headerField and message of the response message.
				// TODO Auto-generated method stub
				Iterator<String> iter = headerField.keySet().iterator();
				while (iter.hasNext()){
					String key = iter.next();
					System.out.println(key+":"+headerField.get(key).toString());// Print the matched header field and the header contents.
				}
				System.out.println(message);
				System.out.println("\n==== send Complete ====\n");
			}
		});

		// ==== start sending ====
		for (int i = 0; i < 10; i++){
			String message = MESSAGE_PREFIX+ " " + i;
			SC2Sender.sendPostMsg(SP_DUMMY_MRN, FOWARDING_CONTEXT, message, 10000);
			sendingMessages += message + "\n";
		}
	}
	
	@Test
	public void testScenario() throws Exception {
		runServer();
		runSC2();
		runSC1();
		
		// === sleep to wait pooling ===
		Thread.sleep(10000);
		
		System.out.println("sending");
		System.out.println(sendingMessages);
		
		System.out.println("receiving");
		System.out.println(receivingMessages);
		
		assertEquals(sendingMessages, receivingMessages);
	}
}

