package tc14_message_priority;
import static org.junit.Assert.*;


import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import tc_base.MMSTestBase;

/**
 * File name : PriorityTest.java
 * Author : Jin Jeong (jungst0001@kaist.ac.kr) 
 * Creation Date : 2019-09-17
 */

@FixMethodOrder(MethodSorters.DEFAULT)
public class PriorityTest extends MMSTestBase {
	static PollingClient client;
	static MessageProvider server;
	static int offset;
	
	@BeforeClass 
	public static void setupForClass() throws Exception {
		client = new PollingClient();
		server = new MessageProvider();
		offset = 4;
	}

	@AfterClass
	public static void afterClass() {
		server.terminateServer();
	}
	
	@Test
	public void test01() throws IOException, InterruptedException {		
		int expected = 0;
		int actual = 20*1024*1024;	
		
		String[] message1 = new String[2];
		message1[0] = "1";
		message1[1] = "4";
		String[] message2 = new String[2];
		message2[0] = "0";
		message2[1] = "5";
		String[] message3 = new String[2];
		message3[0] = "3";
		message3[1] = "3";
		String[] message4 = new String[2];
		message4[0] = "4";
		message4[1] = "2";
		String[] message5 = new String[2];
		message5[0] = "5";
		message5[1] = "1";
														// priority: 1    0    3    4    5
		server.sendContent(message1[0], message1[1]);	//  payload: 4 -> 5 -> 3 -> 2 -> 1
		Thread.sleep(1000);								// exptected: 1, 2, 3, 4 ,5
		server.sendContent(message2[0], message2[1]);	
		Thread.sleep(1000);	
		server.sendContent(message3[0], message3[1]);	
		Thread.sleep(1000);	
		server.sendContent(message4[0], message4[1]);	
		Thread.sleep(1000);	
		server.sendContent(message5[0], message5[1]);	
//		Thread.sleep(1000);	
		
		Thread.sleep(10000);
		expected = client.pollingReqeust();	
//		assertTrue(server.getResponse() == 200);	
		Thread.sleep(1000);
		System.out.println(client.getPayload());
		assertTrue(expected==(actual*2)+offset);		
	}	

}
