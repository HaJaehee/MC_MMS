import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.DEFAULT)
public class TS1_Test {
	static TS1_client client;
	static TS1_server server;
	
	@BeforeClass
	public static void testmain() throws Exception {	
		client = new TS1_client();
		server = new TS1_server();
		
	}
	public int sendContentLength(int actual_content_length) {
		
		client.sendContentLength(actual_content_length);
		int expected_content_length = TS1_server.getContentLength();
		return expected_content_length;
	}
	
	@Test
	public void test01() {
		int expected_content_length=0;
		int actual_content_length=0;
		
		expected_content_length = sendContentLength(actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	
	@Test
	public void test02() {
		int expected_content_length=0;
		int actual_content_length=170;		
		expected_content_length = sendContentLength(actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test03() {
		int expected_content_length=0;
		int actual_content_length=3*1024;		
		expected_content_length = sendContentLength(actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test04() {
		int expected_content_length=0;
		int actual_content_length=200*1024;		
		expected_content_length = sendContentLength(actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test05() {
		int expected_content_length=0;
		int actual_content_length=500*1024;		
		expected_content_length = sendContentLength(actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test06() {
		int expected_content_length=0;
		int actual_content_length=2*1024*1024;		
		expected_content_length = sendContentLength(actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test07() {
		int expected_content_length=0;
		int actual_content_length=7*1024*1024;		
		expected_content_length = sendContentLength(actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test08() {
		int expected_content_length=0;
		int actual_content_length=10*1024*1024;		
		expected_content_length = sendContentLength(actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test09() {
		int expected_content_length=0;
		int actual_content_length=20*1024*1024;		
		expected_content_length = sendContentLength(actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test10() {
		int expected_content_length=0;
		int actual_content_length=30*1024*1024;		
		expected_content_length = sendContentLength(actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test11() {
		int expected_content_length=0;
		int actual_content_length=40*1024*1024;		
		expected_content_length = sendContentLength(actual_content_length);	
		assertEquals(expected_content_length,actual_content_length);		
	}
	@Test
	public void test12() {
		int expected_content_length=0;
		int actual_content_length=50*1024*1024;		
		expected_content_length = sendContentLength(actual_content_length);	
		assertTrue(client.getResponse()==413);		
		
	}
}
