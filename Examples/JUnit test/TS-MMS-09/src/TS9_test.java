/** 
File name : TS8_test.java
	Dropping duplicate long polling request test 
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2019-05-10

 * Rev. history : 2019-05-17
 * Version : 0.9.1
 *		Added assert statements.
 * Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
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
