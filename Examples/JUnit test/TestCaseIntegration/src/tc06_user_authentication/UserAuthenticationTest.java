package tc06_user_authentication;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.BindException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.etri.pkilib.client.ClientPKILibrary;
import net.etri.pkilib.tool.ByteConverter;
import tc_base.MMSTestBase;

/** 
File name : TS6_Test.java
	Polling message authentication tests.
	Every case will be performed with a normal polling message.
	When measuring total elapsed time, a total time in the end of result must be deducted by n * 1 second such that n is the number of cases.
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-04-16
Version : 0.8.2

Rev. history : 2019-05-26
Version : 0.9.1
	Error message is changed.
	One testcase is deleted.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

# This below case is a successful case.
case 1: Client sends a polling message, formatted by JSON, with service MRN and certificate.

# These below cases are failed cases.
case 2: Client sends a polling message, not formatted by JSON (it means a previous formatted message), with service MRN and certificate.
case 3: Client sends a polling message, formatted by JSON, with only service MRN.
case 4: Client sends a polling message, formatted by JSON, with only certificate.
case 5: Client sends a polling message, formatted by JSON, with service MRN and certificate, 
		but the source MRN has a problem that the source MRN does not match a MRN described in the certificate.
		
Rev. history : 2019-05-17
Version : 0.9.1
	Modify output of the test cases because of adding error code.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS6_Test to UserAuthenticationTest
	Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

public class UserAuthenticationTest extends MMSTestBase {
	public final static String serverMRN = "urn:mrn:imo:imo-no:ts-mms-06-server";
	public final static String clientMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	public final static String server_message = "Hello, polling client!";
	private static UserAuthenticationServer server;
	private static UserAuthenticationClient client;
	private static PollingRequestContents contentsBuilder;
	
	public String getSignedData(boolean isActive) {
		ClientPKILibrary clientPKILib = ClientPKILibrary.getInstance();
		ByteConverter byteConverter = ByteConverter.getInstance();
		
		//===== dummy content =====
		byte[] content = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
		
		String signedData = null;
		
		if (isActive) {
			//===== active certificate =====
			String privateKeyPath_active = "certs/PrivateKey_POUL_LOWENORN_active.pem";
			String certPath_active = "certs/Certificate_POUL_LOWENORN_active.pem";
			
			byte[] signedData_active = clientPKILib.generateSignedData(content, privateKeyPath_active, certPath_active);
			String hexSignedData_active = byteConverter.byteArrToHexString(signedData_active);
			signedData = hexSignedData_active;
		}
		else {
			//===== revoked certificate =====
			String privateKeyPath_revoked = "certs/PrivateKey_POUL_LOWENORN_revoked.pem";
			String certPath_revoked = "certs/Certificate_POUL_LOWENORN_revoked.pem";
			
			byte[] signedData_revoked = clientPKILib.generateSignedData(content, privateKeyPath_revoked, certPath_revoked);
			String hexSignedData_revoked = byteConverter.byteArrToHexString(signedData_revoked);
			signedData = hexSignedData_revoked;
		}
		
		return signedData;
	}
	
	@BeforeClass
	public static void initializeClass() {
		server = new UserAuthenticationServer();
		client = new UserAuthenticationClient();
//		client = new TS6_Client(true);
		contentsBuilder = new PollingRequestContents(null, null);
		contentsBuilder.setServiceMRN(server.getMyMRN());
	}

	@AfterClass
	public static void afterClass() {
		server.terminateServer();
	}
	
	@Test
	public void test01() throws IOException, InterruptedException { 
		UserAuthenticationClient.sentMessage = null;
		contentsBuilder.setCertificate(null);
		contentsBuilder.setServiceMRN(null);
		
		server.sendMessage(server_message);

		String signedData = getSignedData(true);
		contentsBuilder.setServiceMRN(serverMRN);
		contentsBuilder.setCertificate(signedData);
		client.sendPollingMessage(contentsBuilder.toString());

		System.out.println(UserAuthenticationClient.sentMessage);
		assertTrue(UserAuthenticationClient.sentMessage.equals(server_message));
	}
	
	@Test
	public void test02() throws IOException, InterruptedException {	
		UserAuthenticationClient.sentMessage = null;
		contentsBuilder.setCertificate(null);
		contentsBuilder.setServiceMRN(null);
		
		server.sendMessage(server_message);
		Thread.sleep(1000);

		String message = null;
		String signedData = getSignedData(true);
		message = serverMRN + "\n" + signedData;
		client.sendPollingMessage(message);

		System.out.println(message);
		
//		assertTrue(TS6_Client.sentMessage.equals(server_message));	
		assertTrue(UserAuthenticationClient.sentMessage.equals("[10009] The message is not formatted by JSON."));
	}
	
	/**
	 * expected value: 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void test03() throws IOException, InterruptedException {		
		UserAuthenticationClient.sentMessage = null;
		contentsBuilder.setCertificate(null);
		contentsBuilder.setServiceMRN(null);
		
		server.sendMessage(server_message);

		contentsBuilder.setServiceMRN(server.getMyMRN());
		client.sendPollingMessage(contentsBuilder.toString());

//		assertTrue(TS6_Client.sentMessage.equals(server_message));	
		assertTrue(UserAuthenticationClient.sentMessage.equals("[10007] The certificate is not included."));
	}
	
	@Test
	public void test04() throws IOException, InterruptedException {		
		UserAuthenticationClient.sentMessage = null;
		contentsBuilder.setCertificate(null);
		contentsBuilder.setServiceMRN(null);
		
		server.sendMessage(server_message);

		String signedData = getSignedData(true);
		contentsBuilder.setCertificate(signedData);
		client.sendPollingMessage(contentsBuilder.toString());
		assertTrue(UserAuthenticationClient.sentMessage.equals("[10008] The service MRN is not included."));	
	}
	
	@Test
	public void test05() throws IOException, InterruptedException {		
		UserAuthenticationClient.sentMessage = null;
		contentsBuilder.setCertificate(null);
		contentsBuilder.setServiceMRN(null);
		
		server.sendMessage(server_message);

		UserAuthenticationClient theClient = new UserAuthenticationClient(UserAuthenticationTest.clientMRN + "-attacker");
		String signedData = getSignedData(true);
		contentsBuilder.setServiceMRN(serverMRN);
		contentsBuilder.setCertificate(signedData);
		
		theClient.sendPollingMessage(contentsBuilder.toString());

//		System.out.println(TS6_Client.sentMessage);
		assertTrue(UserAuthenticationClient.sentMessage.equals("[10012] Authentication is failed."));
	}
}
