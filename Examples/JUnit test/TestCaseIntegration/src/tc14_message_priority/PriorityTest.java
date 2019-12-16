package tc14_message_priority;
import static org.junit.Assert.*;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import kr.ac.kaist.mms_client.MMSClientHandler;
import tc_base.MMSTestBase;

/**
File name : PriorityTest.java
Author : Jin Jeong (jungst0001@kaist.ac.kr) 
Creation Date : 2019-09-17

Rev. history : 2019-09-17
Version : 0.9.5
    Add priority test.
    
    Modifier : Yunho Choi (choiking10@kaist.ac.kr)


Rev. history : 2019-09-23
Version : 0.9.5
	Add error code check testcase.
	
	Modifier : Yunho Choi (choiking10@kaist.ac.kr)
 */

@FixMethodOrder(MethodSorters.DEFAULT)
public class PriorityTest extends MMSTestBase {
	final static String clientMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	final static String providerMRN = "urn:mrn:imo:imo-no:ts-mms-14-server";
	final static String mmsMRN = "urn:mrn:smart-navi:device:mms1";
	
	static PollingClient client;
	static MessageProvider server;
	
	@BeforeClass 
	public static void setupForClass() throws Exception {
		client = new PollingClient(clientMRN, mmsMRN, providerMRN);
		server = new MessageProvider(providerMRN, clientMRN);
	}
	
	@After
	public void resetCallback() {
		server.setSenderCallbackMethod(server.getBasicCallback());
	}
	
	public void sendMessageWithPriority(String[] priority, String[] payload, String[] expected) throws IOException, InterruptedException {		
		assertTrue("All length of parameters must be same.", priority.length == payload.length);
		assertTrue("All length of parameters must be same.", expected.length == payload.length);
		
		int length = priority.length;
		
		for(int i =0 ; i < length; i++) {
			System.out.println(String.format("send payload[%s] as priority [%s]",payload[i], priority[i]));
			server.sendContent(priority[i], payload[i]);
		}
		
		verification(expected);
	}
	
	public void verification(String[] expected) throws IOException, InterruptedException {
		client.pollingReqeust();	
		assertTrue(server.getResponse() == 200);	
		
		String ArrayToString = Arrays.deepToString(expected);
		String payloadString = client.getPayload();
		assertEquals(String.format("realval [%s]\nexpected[%s]", payloadString, ArrayToString), 
				payloadString, ArrayToString);
		System.out.println(String.format("realval [%s]\nexpected[%s]", payloadString, ArrayToString));
	}
	
	@Test
	public void test01() throws IOException, InterruptedException {
		String[] priority = new String[] {"1", "0", "3", "4", "5"};
		String[] payload = new String[] {"4", "5", "3", "2", "1"};
		String[] expected = new String[] {"1", "2", "3", "4", "5"};
		
		System.out.printf("pri:%s\npay:%s\nexp:%s\n",
				Arrays.toString(priority),
				Arrays.toString(payload),
				Arrays.toString(expected));
		
		sendMessageWithPriority(priority, payload, expected);
		
	}

	@Test
	public void test02() throws IOException, InterruptedException {
		String[] alphabat = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"};
		String[] shuffle = alphabat.clone();
		List<String> tmplist = Arrays.asList(shuffle);
		Collections.shuffle(tmplist);
		shuffle = (String[]) tmplist.toArray();
		String[] priority = new String[alphabat.length];
		
		for(int i = 0; i < shuffle.length; i++) {
			priority[i] = "" + (int)('k' - shuffle[i].charAt(0));
		}

		System.out.printf("pri:%s\npay:%s\nexp:%s\n",
				Arrays.toString(priority),
				Arrays.toString(shuffle),
				Arrays.toString(alphabat));
		
		sendMessageWithPriority(
				priority,
				shuffle,
				alphabat
		);
	}

	@Test
	public void testSendWrongPriority1() throws IOException, InterruptedException {
		server.setSenderCallbackMethod(new MMSClientHandler.ResponseCallback() {
			
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				assertNotEquals(headerField.get("Response-code"), null);
				
				int code = Integer.parseInt(headerField.get("Response-code").get(0));
				System.out.println("response message: "+message);
				assertEquals(getErrorCode(message), "10016");
			}
		});
		server.sendContent("a", "wrong");
	}

	@Test
	public void testSendWrongPriority2() throws IOException, InterruptedException {
		server.setSenderCallbackMethod(new MMSClientHandler.ResponseCallback() {
			
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				assertNotEquals(headerField.get("Response-code"), null);
				
				int code = Integer.parseInt(headerField.get("Response-code").get(0));
				System.out.println("response message: "+message);
				assertEquals(getErrorCode(message), "10016");
			}
		});
		server.sendContent("11", "wrong");
	}
	
	public String getErrorCode(String s) {
		return s.substring(1, 6);
	}
}
