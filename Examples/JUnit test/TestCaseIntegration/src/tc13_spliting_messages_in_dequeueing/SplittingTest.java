package tc13_spliting_messages_in_dequeueing;
import static org.junit.Assert.*;


import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import tc_base.MMSTestBase;

/**
File name : SplittingTest.java
Author : Jin Jeong (jungst0001@kaist.ac.kr) 
Creation Date : 2019-09-16
 
Rev. history : 2019-09-17
Version : 0.9.5
	Create 'repeatTest', 'verification' method. This method will be used to test message splitting function.
	Create Testcase 01-06 using repeatTest method.
	
	Modifier : Yunho Choi (choiking10@kaist.ac.kr)
 */

@FixMethodOrder(MethodSorters.DEFAULT)
public class SplittingTest extends MMSTestBase {
	final static String clientMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	final static String providerMRN = "urn:mrn:imo:imo-no:ts-mms-13-server";
	final static String mmsMRN = "urn:mrn:smart-navi:device:mms1";

	final static int KB = 1024;
	final static int MB = 1024*1024;
	final static int LIMITED = 40 * MB;
	final static int OFFSET = 3;
	final static int START_OFFSET = 1;
	final static int SLEEP_TIME = 1;
	
	static PollingClient client;
	static MessageProvider server;
	
	@BeforeClass 
	public static void setupForClass() throws Exception {
		client = new PollingClient(clientMRN, mmsMRN, providerMRN);
		server = new MessageProvider(providerMRN, clientMRN);
	}

	public void repeatTest(int repeated, int perSize, int limited)  throws IOException, InterruptedException {	
		int[] sizeArray = new int[repeated];
		Arrays.fill(sizeArray,  perSize);
		repeatTest(sizeArray, limited);
	}
	
	public void repeatTest(int[] sizeArray, int limited) throws IOException, InterruptedException {	
		for(int i = 0; i < sizeArray.length; i++) {
			server.sendFixedSizeMessage(sizeArray[i]);
			Thread.sleep(SLEEP_TIME);
			
			System.out.println(String.format("Send %d-messages", i));
		}
		
		verification(sizeArray, limited);
	}
	
	public void verification(int[] sizeArray, int limited) throws IOException, InterruptedException {	
		int received = 0;

		// simulation of MMS Server
		int consumption = START_OFFSET;
		
		
		for(int i = 0; i < sizeArray.length; i++) {
			if (consumption + sizeArray[i] <= limited) {
				consumption += sizeArray[i];
				consumption += OFFSET; // @TODO when offset insert in size?
			} else {
				received = client.pollingReqeust();	
				assertTrue(String.format("received=%d, predicted=%d", received,  consumption), received==consumption);
				Thread.sleep(SLEEP_TIME);
				
				System.out.println(String.format("received=%d, predicted=%d", received,  consumption));
				
				consumption = START_OFFSET + sizeArray[i] + OFFSET;
			}
			
			if (i+1 == sizeArray.length) {
				// polling start
				received = client.pollingReqeust();	
				assertTrue(String.format("received=%d, predicted=%d", received,  consumption), received==consumption);	
				System.out.println(String.format("received=%d, predicted=%d", received,  consumption));
				Thread.sleep(SLEEP_TIME);
			}
		}
		
		received = client.pollingReqeust();	
		Thread.sleep(SLEEP_TIME);
		
		assertTrue(String.format("received=%d, predicted=%d", received,  0), received == 0);	
		System.out.println(String.format("received=%d, predicted=%d all clear", received,  0));
	}
	
	@Test
	public void test01() throws IOException, InterruptedException {		
		repeatTest(1, 10 * MB, LIMITED);
	}

	@Test
	public void test02() throws IOException, InterruptedException {		
		repeatTest(2, 10 * MB, LIMITED);
	}

	@Test
	public void test03() throws IOException, InterruptedException {		
		repeatTest(3, 10 * MB, LIMITED);
	}

	@Test
	public void test04() throws IOException, InterruptedException {		
		repeatTest(4, 10 * MB, LIMITED);
	}

	@Test
	public void test05() throws IOException, InterruptedException {		
		repeatTest(10, 10 * MB, LIMITED);
	}
	
	@Test
	public void test06() throws IOException, InterruptedException {		
		repeatTest(
				new int[] {10 * MB, 20 * MB, 500 * KB, 300 * KB, 5 * MB, 4 * MB, 10 * MB, 10 * MB, 10 * MB, 30 * MB},
				LIMITED);
	}
}
