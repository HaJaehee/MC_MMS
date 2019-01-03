import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

/** 
File name : TS4_Test.java
	message sequence test
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-10-13
*/

public class TS4_test {
	static TS4_client client;
	static TS4_server server;

	@BeforeClass
	public static void testmain() throws Exception {
		client = new TS4_client();
		server = new TS4_server();

	}

	@Test
	public void test01() throws Exception { // short delay
		server.ArrayReset();
		int delay = 500;

		ArrayList<Integer> seqArray = new ArrayList();
		int seqNum = 0;		
		int checker =0;

		client.SendRandomSequence(delay);
		
		int size = server.getArraySize();
		seqArray = server.getSeqnum();
		
		System.out.println("size : " +size);
		for (int i = 0; i < size; i++) {

			seqNum = seqArray.get(i);			
			assertTrue(seqNum>=checker);
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
		
		int size = server.getArraySize();
		seqArray = server.getSeqnum();
		
		System.out.println("size : " +size);
		for (int i = 0; i < size; i++) {

			seqNum = seqArray.get(i);			
			assertTrue(seqNum>=checker);
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
		
		int size = server.getArraySize();
		seqArray = server.getSeqnum();
		
		System.out.println("size : " +size);
		for (int i = 0; i < size; i++) {

			seqNum = seqArray.get(i);			
			assertTrue(seqNum>=checker);
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
		
		int size = server.getArraySize();
		seqArray = server.getSeqnum();
		
		System.out.println("size : " +size);
		for (int i = 0; i < size; i++) {

			seqNum = seqArray.get(i);			
			assertTrue(seqNum>=checker);
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
		
		int size = server.getArraySize();
		seqArray = server.getSeqnum();
		
		System.out.println("size : " +size);
		for (int i = 0; i < size; i++) {

			seqNum = seqArray.get(i);			
			assertTrue(seqNum>=checker);
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
		
		int size = server.getArraySize();
		seqArray = server.getSeqnum();
		
		System.out.println("size : " +size);
		for (int i = 0; i < size; i++) {

			seqNum = seqArray.get(i);			
			assertTrue(seqNum>=checker);
			checker= seqNum;
			System.out.println("seqNum : " + seqNum);
			System.out.println("checker : " +checker);
		}
		server.ArrayReset();
	}
}
