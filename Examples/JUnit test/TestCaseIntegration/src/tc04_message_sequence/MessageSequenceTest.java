package tc04_message_sequence;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import tc_base.MMSTestBase;

/** 
File name : MessageSequenceTest.java
	message sequence test
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-10-13

Rev. history : 2019-05-17
Version : 0.9.1
	Running this test case with version 0.9.1 and the test case is succeeded.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS4_Test to MessageSequenceTest
	Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

public class MessageSequenceTest extends MMSTestBase {
	static MessageSequenceClient client;
	static MessageSequenceServer server;

	@BeforeClass
	public static void testmain() throws Exception {
		client = new MessageSequenceClient();
		server = new MessageSequenceServer();
	}

	@AfterClass
	public static void afterClass() {
		server.terminateServer();
	}
	
	@Test
	public void test01() throws Exception { // short delay
		server.ArrayReset();
		int delay = 500;

		ArrayList<Integer> seqArray = new ArrayList();
		int seqNum = 0;		
		int checker =0;

		client.SendRandomSequence(delay);
		
		Thread.sleep(2000);
		
		int size = server.getArraySize();
		seqArray = server.getSeqnum();
		
		System.out.println("size : " +size);
		assertFalse(size == 0);
		for (int i = 0; i < size; i++) {

			seqNum = seqArray.get(i);			
			assertTrue(seqNum>=checker && size > 0);
			checker= seqNum;
			System.out.println("seqNum : " + seqNum);
			System.out.println("checker : " +checker);
		}
		
		
		server.ArrayReset();
	}

	@Test
	public void test02() throws Exception { // median delay
		int delay = 1000;

		ArrayList<Integer> seqArray = new ArrayList();
		int seqNum = 0;		
		int checker =0;

		client.SendRandomSequence(delay);
		
		Thread.sleep(2000);
		
		int size = server.getArraySize();
		seqArray = server.getSeqnum();
		
		System.out.println("size : " +size);
		assertFalse(size == 0);
		for (int i = 0; i < size; i++) {

			seqNum = seqArray.get(i);			
			assertTrue(seqNum>=checker && size > 0);
			checker= seqNum;
			System.out.println("seqNum : " + seqNum);
			System.out.println("checker : " +checker);
		}
		server.ArrayReset();
	}

	@Test
	public void test03() throws Exception { // long delay
		int delay = 4000;

		ArrayList<Integer> seqArray = new ArrayList();
		int seqNum = 0;		
		int checker =0;

		client.SendRandomSequence(delay);
		
		Thread.sleep(2000);
		
		int size = server.getArraySize();
		seqArray = server.getSeqnum();
		
		System.out.println("size : " +size);
		assertFalse(size == 0);
		for (int i = 0; i < size; i++) {

			seqNum = seqArray.get(i);			
			assertTrue(seqNum>=checker && size > 0);
			checker= seqNum;
			System.out.println("seqNum : " + seqNum);
			System.out.println("checker : " +checker);
		}
		server.ArrayReset();
	}
	@Test
	public void test04() throws Exception { // short delay
		int delay = 500;

		ArrayList<Integer> seqArray = new ArrayList();
		int seqNum = 0;		
		int checker =0;

		client.SendSortedSequence(delay);
		
		Thread.sleep(2000);
		
		int size = server.getArraySize();
		seqArray = server.getSeqnum();
		
		System.out.println("size : " +size);
		assertFalse(size == 0);
		for (int i = 0; i < size; i++) {

			seqNum = seqArray.get(i);			
			assertTrue(seqNum>=checker && size > 0);
			checker= seqNum;
			System.out.println("seqNum : " + seqNum);
			System.out.println("checker : " +checker);
		}
		server.ArrayReset();
	}
	@Test
	public void test05() throws Exception { // median delay
		int delay = 1000;

		ArrayList<Integer> seqArray = new ArrayList();
		int seqNum = 0;		
		int checker =0;

		client.SendSortedSequence(delay);
		
		Thread.sleep(2000);
		
		int size = server.getArraySize();
		seqArray = server.getSeqnum();
		
		System.out.println("size : " +size);
		assertFalse(size == 0);
		for (int i = 0; i < size; i++) {

			seqNum = seqArray.get(i);			
			assertTrue(seqNum>=checker && size > 0);
			checker= seqNum;
			System.out.println("seqNum : " + seqNum);
			System.out.println("checker : " +checker);
		}
		server.ArrayReset();
	}
	@Test
	public void test06() throws Exception { // long delay
		int delay = 4000;

		ArrayList<Integer> seqArray = new ArrayList();
		int seqNum = 0;		
		int checker =0;

		client.SendSortedSequence(delay);
		
		Thread.sleep(2000);
		
		int size = server.getArraySize();
		seqArray = server.getSeqnum();
		
		
		System.out.println("size : " +size);
		assertFalse(size == 0);
		for (int i = 0; i < size; i++) {

			seqNum = seqArray.get(i);			
			assertTrue(seqNum>=checker && size > 0);
			checker= seqNum;
			System.out.println("seqNum : " + seqNum);
			System.out.println("checker : " +checker);
		}
		server.ArrayReset();
	}
}
