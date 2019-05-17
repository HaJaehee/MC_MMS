import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
/** 
File name : TS5_test.java
	Validation of MMS polling client
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-10-16

Rev. history : 2019-05-17
Version : 0.9.1
	This test case is deprecated
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

@Deprecated
public class TS5_test {

	static TS5_client client;
	static TS5_server server;

	@BeforeClass
	public static void testmain() throws Exception {
		client = new TS5_client();
		server = new TS5_server();

	}

	@Test
	public void test01() throws Exception { // valid case
		client.RightMRN();
		client.ValidCertificatedCase();
		server.RightsrcMRN();

		Thread.sleep(3000);

		String message = client.getCheckMessage();

		System.out.println("sended message : " + message);
		
		assertTrue(message.equals("test"));
	}

	@Test
	public void test02() throws Exception {
		client.RightMRN();
		client.InValidCertificatedCase();
		server.RightsrcMRN();
		Thread.sleep(3000);

		String message = client.getCheckMessage();

		System.out.println("sended message : " + message);
		assertTrue(message.equals("It is failed to verify the client."));
	}

	@Test
	public void test03() throws Exception {
		client.WrongMRN();
		client.ValidCertificatedCase();
		server.WrongsrcMRN();
		Thread.sleep(3000);

		String message = client.getCheckMessage();

		System.out.println("sended message : " + message);
		assertTrue(message.equals("It is failed to verify the client."));
	}

	@Test
	public void test04() throws Exception {
		client.WrongMRN();
		client.InValidCertificatedCase();
		server.WrongsrcMRN();
		Thread.sleep(3000);
		String message = client.getCheckMessage();

		System.out.println("sended message : " + message);
		assertTrue(message.equals("It is failed to verify the client."));

	}
}
