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

public class TS8_test {
	static TS8_client client;
	static TS8_server server;
	static int offset;
	
	static List<String> response = null;
	
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
		boolean testPass = false;
		response = new ArrayList<String>();
				
		server.sendContent(actual);	
		
		client.singleThreadStart(); /// Do not 
		Thread.sleep(10000);	
		
		for (String s : response) {
			expected = response.size();
			System.out.println("Message : "+ s);
			if (s.equals("aa")) {
				testPass = true;
				actual++;
			}
		}
		
		assertTrue(testPass && expected == actual);		
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
		
		client.multipleThreadStart();
		Thread.sleep(10000);
		
		for (String s : response) {
			expectedError = response.size()-1;
			System.out.println("Message : "+ s);
			if (s.equals("aa")) {
				testPass = true;
				actual++;
			}
			else if (s.equals("[10011] The long polling request is already received. Duplicate request is not accepted.")) {
				actualError++;
			}
		}
		
		assertTrue(testPass && expected == actual && expectedError == actualError);			
	}
}
