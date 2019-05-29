/**
 * File name : TS11_test.java 
 * 		For testing MMS restful API.
 * Author : Jaehee Ha (jaehee.ha@kaist.ac.kr) 
 * Creation Date : 2019-05-22
 */

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class TS11_test {
	static TS11_client client;

	static int offset;
	
	static List<String> response = null;
	
	@BeforeClass 
	public static void setupForClass() throws Exception {
		client = new TS11_client();

		offset = 4;
	}
	
	@Test
	public void test01() throws IOException, InterruptedException {		

		String params = "mms-running=y";
		
		client.apiTest(params);
		
		Thread.sleep(1000);	
		
		assertTrue(client.jobj.containsKey("mms-running"));
	}
	
	@Test
	public void test02() throws IOException, InterruptedException {		

		String params = "client-session-ids=y";
		
		client.apiTest(params);
		
		Thread.sleep(1000);	
		
		assertTrue(client.jobj.containsKey("client-session-ids"));
	}
	
	@Test
	public void test03() throws IOException, InterruptedException {		

		String params = "realtime-log-users=y";
		
		client.apiTest(params);
		
		Thread.sleep(1000);	
		
		assertTrue(client.jobj.containsKey("realtime-log-users"));
	}
	
	@Test
	public void test04() throws IOException, InterruptedException {		

		String params = "mrns-being-debugged=y";
		
		client.apiTest(params);
		
		Thread.sleep(1000);	
		
		assertTrue(client.jobj.containsKey("mrns-being-debugged"));
	}
	
	@Test
	public void test05() throws IOException, InterruptedException {		

		String params = "msg-queue-count=y";
		
		client.apiTest(params);
		
		Thread.sleep(1000);	
		
		assertTrue(client.jobj.containsKey("msg-queue-count"));
	}
	
	@Test
	public void test06() throws IOException, InterruptedException {		

		String params = "relay-req-count-for=5";
		
		client.apiTest(params);
		
		Thread.sleep(1000);	
		
		assertTrue(client.jobj.containsKey("relay-req-count-for"));
	}
	
	@Test
	public void test07() throws IOException, InterruptedException {		

		String params = "polling-req-count-for=5";
		
		client.apiTest(params);
		
		Thread.sleep(1000);	
		
		assertTrue(client.jobj.containsKey("polling-req-count-for"));
	}
	
	@Test
	public void test08() throws IOException, InterruptedException {		

		String params = "msg-queue-counting=y";
		
		client.apiTest(params);
		
		Thread.sleep(1000);	
		
		assertTrue(client.jobj.containsKey("error"));
	}
	
}
