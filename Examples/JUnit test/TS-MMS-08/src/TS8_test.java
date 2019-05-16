import static org.junit.Assert.assertTrue;
/** 
File name : TS8_test.java
	Dropping duplicate long polling request test 
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2019-05-10
*/

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

public class TS8_test {
	static TS8_client client;
	static TS8_server server;
	static int offset;
	
	@BeforeClass 
	public static void setupForClass() throws Exception {
		client = new TS8_client();
		server = new TS8_server();
		offset = 4;
	}
	
	@Test
	public void test01() throws IOException, InterruptedException {		
		int expected = 0;
		int actual = 0;				
				
		server.sendContent(actual);	
		//Thread.sleep(1000);
		
		System.out.println("response : " +server.getResponse());
		//expected = client.pollingReqeust();	
		client.singleThreadStart(); /// Do not 
		Thread.sleep(10000);	
		//assertTrue(expected==(actual)+offset);		
	}
	
	@Test
	public void test02() throws IOException, InterruptedException {		
		int expected = 0;
		int actual = 0;				
				
		server.sendContent(actual);	
		//Thread.sleep(1000);
		
		System.out.println("response : " +server.getResponse());
		//expected = client.pollingReqeust();	
		client.multipleThreadStart();
		Thread.sleep(10000);	
		//assertTrue(expected==(actual)+offset);		
	}
}
