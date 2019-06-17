package tc09_choose_polling_type;


/** 
File name : TS9_test.java

 * Client Type decide junit test 
 * of testing MMS Author : Youngjin Kim (jcdad3000@kaist.ac.kr) Creation Date :
 * 2019-05-20

Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS9_Test to ChoosePollingTypeTest
	Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import tc_base.MMSTestBase;

public class ChoosePollingTypeTest extends MMSTestBase {
	static ChoosePollingTypeClient client;
	static ChoosePollingTypeServer server;
	static int offset;
	
	static List<String> response = null;
	
	@BeforeClass 
	public static void setupForClass() throws Exception {
		client = new ChoosePollingTypeClient();
		server = new ChoosePollingTypeServer();
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
