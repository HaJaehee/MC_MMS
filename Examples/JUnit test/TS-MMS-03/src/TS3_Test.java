import static org.junit.Assert.*;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.DEFAULT)
public class TS3_Test {
	static TS3_client client;
	static TS3_server server;
	static int offset;
	
	@BeforeClass 
	public static void setupForClass() {
		client = new TS3_client();
		server = new TS3_server();
		offset = 4;
	}
	
	@Test 
	public void test01() {
		int expected = 0;
		int actual = 0;
		
		server.sendContent(actual);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		expected = client.pollingReqeust();
		
		assertTrue(server.getResponse() == 200);
		assertTrue(expected==(actual)+offset);
	}
	
	@Test 
	public void test02() {
		int expected = 0;
		int actual = 170; //170 byte
		
		server.sendContent(actual);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		expected = client.pollingReqeust();
		
		assertTrue(server.getResponse() == 200);
		assertTrue(expected==(actual + offset));
	}
	
	@Test 
	public void test03() {
		int expected = 0;
		int actual = 3072; //3Kbyte
		
		server.sendContent(actual);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		expected = client.pollingReqeust();
		
		assertTrue(server.getResponse() == 200);
		assertTrue(expected==(actual + offset));
	}
	
	@Test 
	public void test04() {
		int expected = 0;
		int actual = 204800; //200Kbyte
		
		server.sendContent(actual);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		expected = client.pollingReqeust();
		
		assertTrue(server.getResponse() == 200);
		assertTrue(expected==(actual + offset));
	}
	
	@Test 
	public void test05() {
		int expected = 0;
		int actual = 512000; // 500Kbyte
		
		server.sendContent(actual);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		expected = client.pollingReqeust();
		
		assertTrue(server.getResponse() == 200);
		assertTrue(expected==(actual + offset));
	}
	
	@Test 
	public void test06() {
		int expected = 0;
		int actual = 2097152; // 2Mbyte
		
		server.sendContent(actual);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		expected = client.pollingReqeust();
		
		assertTrue(server.getResponse() == 200);
		assertTrue(expected==(actual + offset));
	}
	
	@Test 
	public void test07() {
		int expected = 0;
		int actual = 7340032; // 7Mbyte
		
		server.sendContent(actual);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		expected = client.pollingReqeust();
		
		assertTrue(server.getResponse() == 200);
		assertTrue(expected==(actual + offset));
	}
	
	@Test 
	public void test08() {
		int expected = 0;
		int actual = 10485760; // 10Mbyte
		
		server.sendContent(actual);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		expected = client.pollingReqeust();
		
		assertTrue(server.getResponse() == 200);
		assertTrue(expected==(actual + offset));
	}
	
	@Test 
	public void test09() {
		int expected = 0;
		int actual = 20971520; // 20Mbyte
		
		server.sendContent(actual);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		expected = client.pollingReqeust();
		
		assertTrue(server.getResponse() == 200);
		assertTrue(expected==(actual + offset));
	}
	
	@Test 
	public void test10() {
		int expected = 0;
		int actual = 31457280; // 30Mbyte
		
		server.sendContent(actual);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		expected = client.pollingReqeust();
		
		assertTrue(server.getResponse() == 200);
		assertTrue(expected==(actual + offset));
	}
	
	@Test 
	public void test11() {
		int expected = 0;
		int actual = 41943040; // 40Mbyte
		
		server.sendContent(actual);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		expected = client.pollingReqeust();
		
		assertTrue(server.getResponse() == 200);
		assertTrue(expected==(actual + offset));
	}
	
	@Test 
	public void test12() {
		int expected = 0;
		int actual = 52428800; // 50Mbyte
		
		server.sendContent(actual);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(server.getResponse() == 413);
		
//		expected = client.pollingReqeust();
//		assertTrue(expected==(actual + offset));
		
	}

}
