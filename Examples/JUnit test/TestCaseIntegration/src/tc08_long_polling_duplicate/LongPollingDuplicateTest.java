package tc08_long_polling_duplicate;

/** 
package tc08_long_polling_duplicate;

File name : LongPollingDuplicateTest.java
	Dropping duplicated long polling request test 
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2019-05-10

Rev. history : 2019-05-17
Version : 0.9.1
	Added assert statements.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS8_Test to LongPollingDuplicateTest
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
	
Rev. history : 2019-06-20
Version : 0.9.2
	Revised test cases and fixed bugs.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-10-25
Version : 0.9.6
	Revised test cases and fixed bugs.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import tc_base.MMSTestBase;

public class LongPollingDuplicateTest extends MMSTestBase {
	static LongPollingDuplicateClient client;
	static LongPollingDuplicateServer server;
	static int offset;
	
	static List<String> response = null;
	
	@BeforeClass 
	public static void setupForClass() throws Exception {
		client = new LongPollingDuplicateClient();
		client.emptyTheQueue();
		Thread.sleep(5000);
		offset = 4;
	}
	
	@Test
	public void test01() throws IOException, InterruptedException {		
		int expected = 0;
		int actual = 0;		
		boolean testPass = false;
		response = new ArrayList<String>();
		client = new LongPollingDuplicateClient();
		server = new LongPollingDuplicateServer();
		
		client.singleThreadStart(); /// Do not 
		Thread.sleep(5000);	
		
		server.sendContent(actual);	
		Thread.sleep(2000);
		
		for (String s : response) {
			expected = response.size();
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
		client = new LongPollingDuplicateClient();
		server = new LongPollingDuplicateServer();
			
		client.multipleThreadStart();
		Thread.sleep(10000);
		
		server.sendContent(actual);	
		Thread.sleep(2000);
		
		for (String s : response) {
			expectedError = response.size()-1;
			if (s.equals("aa")) {
				testPass = true;
				actual++;
			}
			else if (s.equals("[10011] The long polling request is received. Duplicated request is not accepted. The prior request is discarded.")) {
				actualError++;
			}
		}
		assertTrue(testPass && expected == actual && expectedError == actualError);			
	}
}
