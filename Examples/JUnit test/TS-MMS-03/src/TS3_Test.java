import static org.junit.Assert.*;


import java.io.IOException;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/** 
File name : TS3_Test.java
	Polling request message function for the purpose of testing MMS
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-10-13

Rev. history : 2019-05-17
Version : 0.9.1
	Change the version from JUnit3 to JUnit4.
	Add JSON library and MIR API.
	Add certificate for version compatability.
	
	** And this test is succeeded
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

@FixMethodOrder(MethodSorters.DEFAULT)
public class TS3_Test {
	static TS3_client client;
	static TS3_server server;
	static int offset;
	
	@BeforeClass 
	public static void setupForClass() throws Exception {
		client = new TS3_client();
		server = new TS3_server();
		offset = 4;
	}
	
	@Test
	public void test01() throws IOException, InterruptedException {		
		int expected = 0;
		int actual = 0;				
				
		server.sendContent("file0B.txt",actual);	
		//Thread.sleep(1000);
		
		System.out.println("response : " +server.getResponse());
		expected = client.pollingReqeust();		
		assertTrue(server.getResponse() == 200);		
		assertTrue(expected==(actual)+offset);		
	}
	
	@Test
	public void test02() throws IOException {
		int expected = 0;
		int actual = 170;				
				
		server.sendContent("file170B.txt",actual);	
		expected = client.pollingReqeust();		
		System.out.println("expected : " +expected);
		assertTrue(server.getResponse() == 200);		
		assertTrue(expected==(actual)+offset);					
	}
	@Test
	public void test03() throws IOException {
		int expected = 0;
		int actual = 3*1024;				
				
		server.sendContent("file3KB.txt",actual);	
		expected = client.pollingReqeust();		
		assertTrue(server.getResponse() == 200);		
		assertTrue(expected==(actual)+offset);			
	}
	@Test
	public void test04() throws IOException {
		int expected = 0;
		int actual = 200*1024;				
				
		server.sendContent("file200KB.txt",actual);	
		expected = client.pollingReqeust();		
		assertTrue(server.getResponse() == 200);		
		assertTrue(expected==(actual)+offset);				
	}
	@Test
	public void test05() throws IOException {
		int expected = 0;
		int actual = 500*1024;				
				
		server.sendContent("file500KB.txt",actual);	
		expected = client.pollingReqeust();		
		assertTrue(server.getResponse() == 200);		
		assertTrue(expected==(actual)+offset);		
	}
	@Test
	public void test06() throws IOException {
		int expected = 0;
		int actual = 2*1024*1024;				
				
		server.sendContent("file2MB.txt",actual);	
		expected = client.pollingReqeust();		
		assertTrue(server.getResponse() == 200);		
		assertTrue(expected==(actual)+offset);		
	}
	@Test
	public void test07() throws IOException {
		int expected = 0;
		int actual = 7*1024*1024;				
				
		server.sendContent("file7MB.txt",actual);	
		expected = client.pollingReqeust();		
		assertTrue(server.getResponse() == 200);		
		assertTrue(expected==(actual)+offset);		
	}
	@Test
	public void test08() throws IOException {
		int expected = 0;
		int actual = 10*1024*1024;				
				
		server.sendContent("file10MB.txt",actual);	
		expected = client.pollingReqeust();		
		assertTrue(server.getResponse() == 200);		
		assertTrue(expected==(actual)+offset);	
	}
	@Test
	public void test09() throws IOException {
		int expected = 0;
		int actual = 20*1024*1024;				
				
		server.sendContent("file20MB.txt",actual);	
		expected = client.pollingReqeust();		
		assertTrue(server.getResponse() == 200);		
		assertTrue(expected==(actual)+offset);		
	}
	@Test
	public void test10() throws IOException {
		int expected = 0;
		int actual = 30*1024*1024;				
				
		server.sendContent("file30MB.txt",actual);	
		expected = client.pollingReqeust();		
		assertTrue(server.getResponse() == 200);		
		assertTrue(expected==(actual)+offset);		
	}
	@Test
	public void test11() throws IOException {
		int expected = 0;
		int actual = 40*1024*1024;				
				
		server.sendContent("file40MB.txt",actual);	
		expected = client.pollingReqeust();		
		assertTrue(server.getResponse() == 200);		
		assertTrue(expected==(actual)+offset);	
	}
	@Test
	public void test12() throws IOException {
		int expected = 0;
		int actual = 50*1024*1024;				
				
		server.sendContent("file50MB.txt",actual);			
		
		assertTrue(server.getResponse() == 413);
		
		
	}
	

}
