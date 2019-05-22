/**
 * File name : TS11_test.java 
 * 		For testing MMS restful API.
 * Author : Jaehee Ha (jaehee.ha@kaist.ac.kr) 
 * Creation Date : 2019-05-22
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class TS11_test {
	static TS11_client client;

	static int offset;
	
	static List<String> response = null;
	
	@BeforeClass 
	public static void setupForClass() throws Exception {
		client = new TS11_client();

		offset = 4;
	}
	
	@Test
	public void test01() throws IOException, InterruptedException {		

		response = new ArrayList<String>();
				
		client.apiTest();
		Thread.sleep(10000);	
		
		//assertTrue(!client.getLongchecker()&&client.getNormalchecker());
	}
	
	@Test
	public void test02() throws IOException, InterruptedException {		

		response = new ArrayList<String>();
	
		
		Thread.sleep(10000);
		
		//assertTrue(client.getLongchecker()&&!client.getNormalchecker());
		
			
	}
}
