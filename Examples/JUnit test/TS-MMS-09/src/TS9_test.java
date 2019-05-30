/** 
File name : TS9_test.java

 * Client Type decide junit test 
 * of testing MMS Author : Youngjin Kim (jcdad3000@kaist.ac.kr) Creation Date :
 * 2019-05-20

*/

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class TS9_test {
	static TS9_client client;
	static TS9_server server;
	static int offset;
	
	static List<String> response = null;
	
	@BeforeClass 
	public static void setupForClass() throws Exception {
		client = new TS9_client();
		server = new TS9_server();
		offset = 4;
	}
	
	@Test
	public void test01() throws IOException, InterruptedException {		
		int expected = 0;
		int actual = 0;		
		boolean testPass = false;
		response = new ArrayList<String>();
				
		server.sendContent(actual);				
		
		client.normalPollingTest();
		Thread.sleep(10000);	
		
		assertTrue(!client.getLongchecker()&&client.getNormalchecker());
	}
	
	@Test
	public void test02() throws IOException, InterruptedException {		
		int expected = 1;
		int actual = 0;		
		int expectedError = 0;
		int actualError = 0;
		boolean testPass = false;
		response = new ArrayList<String>();
				
		server.sendContent(actual);	
		
		client.longPollingTest();
		
		Thread.sleep(10000);
		
		assertTrue(client.getLongchecker()&&!client.getNormalchecker());
		
			
	}
}
