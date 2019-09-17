package tc13_spliting_messages_in_dequeueing;
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
 * File name : SplittingTest.java
 * Author : Jin Jeong (jungst0001@kaist.ac.kr) 
 * Creation Date : 2019-09-16
 */

@FixMethodOrder(MethodSorters.DEFAULT)
public class SplittingTest extends MMSTestBase {
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
				
		server.sendContent("files/file20MB.txt",actual);	
		Thread.sleep(1000);
		server.sendContent("files/file20MB.txt",actual);	
		Thread.sleep(1000);
		server.sendContent("files/file20MB.txt",actual);	
		Thread.sleep(1000);
		
		System.out.println("response : " +server.getResponse());
		expected = client.pollingReqeust();	
		Thread.sleep(1000);
//		assertTrue(server.getResponse() == 200);		
		assertTrue(expected==(actual*2)+offset);		
	}
	
//	@Test
//	public void test02() throws IOException {
//		int expected = 0;
//		int actual = 170;				
//				
//		server.sendContent("files/file170B.txt",actual);	
//		expected = client.pollingReqeust();		
//		System.out.println("expected : " +expected);
//		assertTrue(server.getResponse() == 200);		
//		assertTrue(expected==(actual)+offset);					
//	}
//	@Test
//	public void test03() throws IOException {
//		int expected = 0;
//		int actual = 3*1024;				
//				
//		server.sendContent("files/file3KB.txt",actual);	
//		expected = client.pollingReqeust();		
//		assertTrue(server.getResponse() == 200);		
//		assertTrue(expected==(actual)+offset);			
//	}
//	@Test
//	public void test04() throws IOException {
//		int expected = 0;
//		int actual = 200*1024;				
//				
//		server.sendContent("files/file200KB.txt",actual);	
//		expected = client.pollingReqeust();		
//		assertTrue(server.getResponse() == 200);		
//		assertTrue(expected==(actual)+offset);				
//	}
//	@Test
//	public void test05() throws IOException {
//		int expected = 0;
//		int actual = 500*1024;				
//				
//		server.sendContent("files/file500KB.txt",actual);	
//		expected = client.pollingReqeust();		
//		assertTrue(server.getResponse() == 200);		
//		assertTrue(expected==(actual)+offset);		
//	}
//	@Test
//	public void test06() throws IOException {
//		int expected = 0;
//		int actual = 2*1024*1024;				
//				
//		server.sendContent("files/file2MB.txt",actual);	
//		expected = client.pollingReqeust();		
//		assertTrue(server.getResponse() == 200);		
//		assertTrue(expected==(actual)+offset);		
//	}
//	@Test
//	public void test07() throws IOException {
//		int expected = 0;
//		int actual = 7*1024*1024;				
//				
//		server.sendContent("files/file7MB.txt",actual);	
//		expected = client.pollingReqeust();		
//		assertTrue(server.getResponse() == 200);		
//		assertTrue(expected==(actual)+offset);		
//	}
//	@Test
//	public void test08() throws IOException {
//		int expected = 0;
//		int actual = 10*1024*1024;				
//				
//		server.sendContent("files/file10MB.txt",actual);	
//		expected = client.pollingReqeust();		
//		assertTrue(server.getResponse() == 200);		
//		assertTrue(expected==(actual)+offset);	
//	}
//	@Test
//	public void test09() throws IOException {
//		int expected = 0;
//		int actual = 20*1024*1024;				
//				
//		server.sendContent("files/file20MB.txt",actual);	
//		expected = client.pollingReqeust();		
//		assertTrue(server.getResponse() == 200);		
//		assertTrue(expected==(actual)+offset);		
//	}
//	@Test
//	public void test10() throws IOException {
//		int expected = 0;
//		int actual = 30*1024*1024;				
//				
//		server.sendContent("files/file30MB.txt",actual);	
//		expected = client.pollingReqeust();		
//		assertTrue(server.getResponse() == 200);		
//		assertTrue(expected==(actual)+offset);		
//	}
//	@Test
//	public void test11() throws IOException {
//		int expected = 0;
//		int actual = 40*1024*1024;				
//				
//		server.sendContent("files/file40MB.txt",actual);	
//		expected = client.pollingReqeust();		
//		assertTrue(server.getResponse() == 200);		
//		assertTrue(expected==(actual)+offset);	
//	}
//	@Test
//	public void test12() throws IOException {
//		int expected = 0;
//		int actual = 50*1024*1024;				
//				
//		server.sendContent("files/file50MB.txt",actual);			
//		
//		assertTrue(server.getResponse() == 413);
//		
//		
//	}
	

}
