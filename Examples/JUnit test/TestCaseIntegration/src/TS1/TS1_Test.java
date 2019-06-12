package TS1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/** 
File name : TS1_Test.java
	Relaying message function for the purpose of testing MMS
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-10-13

Rev. history : 2019-05-17
Version : 0.9.1
	Change the version from JUnit3 to JUnit4.
	Running this test case with version 0.9.1 and the test case is succeeded.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/
@FixMethodOrder(MethodSorters.DEFAULT)
public class TS1_Test {
	static TS1_client client;
	static TS1_server server;	
	
	@BeforeClass
	public static void testmain() throws Exception {	
		client = new TS1_client();
		server = new TS1_server();
		
	}

	@AfterClass
	public static void afterClass() {
		server.terminateServer();
	}
	
	public int sendContentLength(String FileName, int actual_content_length) throws IOException {
		
		client.sendContentLength(FileName,actual_content_length);
		int expected_content_length = TS1_server.getContentLength();
		return expected_content_length;
	}
	
	@Test
	public void test01() throws IOException {
		int expected_content_length=0;
		int actual_content_length=0;	
				
		expected_content_length = sendContentLength("file0B.txt",actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	
	@Test
	public void test02() throws IOException {
		int expected_content_length=0;
		int actual_content_length=170;		
		expected_content_length = sendContentLength("file170B.txt",actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test03() throws IOException {
		int expected_content_length=0;
		int actual_content_length=3*1024;		
		expected_content_length = sendContentLength("file3KB.txt",actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test04() throws IOException {
		int expected_content_length=0;
		int actual_content_length=200*1024;		
		expected_content_length = sendContentLength("file200KB.txt",actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test05() throws IOException {
		int expected_content_length=0;
		int actual_content_length=500*1024;		
		expected_content_length = sendContentLength("file500KB.txt",actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test06() throws IOException {
		int expected_content_length=0;
		int actual_content_length=2*1024*1024;		
		expected_content_length = sendContentLength("file2MB.txt",actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test07() throws IOException {
		int expected_content_length=0;
		int actual_content_length=7*1024*1024;		
		expected_content_length = sendContentLength("file7MB.txt",actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test08() throws IOException {
		int expected_content_length=0;
		int actual_content_length=10*1024*1024;		
		expected_content_length = sendContentLength("file10MB.txt",actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test09() throws IOException {
		int expected_content_length=0;
		int actual_content_length=20*1024*1024;		
		expected_content_length = sendContentLength("file20MB.txt",actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test10() throws IOException {
		int expected_content_length=0;
		int actual_content_length=30*1024*1024;		
		expected_content_length = sendContentLength("file30MB.txt",actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test11() throws IOException {
		int expected_content_length=0;
		int actual_content_length=40*1024*1024;		
		expected_content_length = sendContentLength("file40MB.txt",actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test12() throws IOException {
		int expected_content_length=0;
		int actual_content_length=50*1024*1024;		
		expected_content_length = sendContentLength("file50MB.txt",actual_content_length);	
		assertTrue(client.getResponse()==413);		
		
	}
}
