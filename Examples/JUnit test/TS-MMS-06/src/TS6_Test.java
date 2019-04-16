import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import net.etri.pkilib.client.ClientPKILibrary;
import net.etri.pkilib.tool.ByteConverter;

/** 
File name : TS6_Test.java
	Polling message authentication tests.
	Every case will be performed with a normal polling message.
	When measuring total elapsed time, a total time in the end of result must be deducted by n * 1 second such that n is the number of cases.
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-04-16

# This below case is a successful case.
case 1: Client sends a polling message, formatted by JSON, with service MRN and certificate.

# These below cases are failed cases.
case 2: Client sends a polling message, not formatted by JSON (it means a previous formatted message), with service MRN and certificate.
case 3: Client sends a polling message, formatted by JSON, with only service MRN.
case 4: Client sends a polling message, formatted by JSON, with only certificate.
case 5: Client sends a polling message, formatted by JSON, with service MRN and certificate, but the service MRN has a problem such as not existed MRN.
case 6: Client sends a polling message, formatted by JSON, with service MRN and certificate, but the certificate has been revoked.
case 7: Client sends a polling message, formatted by JSON, with service MRN and certificate, 
		but the source MRN has a problem that the source MRN does not match a MRN described in the certificate.
*/

public class TS6_Test {
	public final static String MMS_URL = "127.0.0.1:8088";
	public final static String serverMRN = "urn:mrn:imo:imo-no:ts-mms-06-server";
	public final static String clientMRN = "";
	private TS6_Server server;
	private TS6_Client client;
	private PollingRequestContents contentsBuilder;
	
	public String getSignedData(boolean isActive) {
		ClientPKILibrary clientPKILib = ClientPKILibrary.getInstance();
		ByteConverter byteConverter = ByteConverter.getInstance();
		
		//===== dummy content =====
		byte[] content = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
		
		String signedData = null;
		
		if (isActive) {
			//===== active certificate =====
			String privateKeyPath_active = "PrivateKey_POUL_LOWENORN_active.pem";
			String certPath_active = "Certificate_POUL_LOWENORN_active.pem";
			
			byte[] signedData_active = clientPKILib.generateSignedData(content, privateKeyPath_active, certPath_active);
			String hexSignedData_active = byteConverter.byteArrToHexString(signedData_active);
			signedData = hexSignedData_active;
		}
		else {
			//===== revoked certificate =====
			String privateKeyPath_revoked = "PrivateKey_POUL_LOWENORN_revoked.pem";
			String certPath_revoked = "Certificate_POUL_LOWENORN_revoked.pem";
			
			byte[] signedData_revoked = clientPKILib.generateSignedData(content, privateKeyPath_revoked, certPath_revoked);
			String hexSignedData_revoked = byteConverter.byteArrToHexString(signedData_revoked);
			signedData = hexSignedData_revoked;
		}
		
		return signedData;
	}
	
	@BeforeClass
	public void initializeClass() {
		server = new TS6_Server();
		client = new TS6_Client();
		contentsBuilder = new PollingRequestContents(null, null);
		contentsBuilder.setServiceMRN(server.getMyMRN());
	}
	
	@Test
	public void test01() throws IOException, InterruptedException { 
		TS6_Client.sentMessage = null;
		contentsBuilder.setCertificate(null);
		contentsBuilder.setServiceMRN(null);
		
		server.sendMessage();
		Thread.sleep(1000);

		String signedData = getSignedData(true);
		contentsBuilder.setCertificate(signedData);
		client.sendPollingMessage(contentsBuilder.toString());

		assertTrue(TS6_Client.sentMessage.equals("200"));
	}
	
	@Test
	public void test02() throws IOException, InterruptedException {		
		TS6_Client.sentMessage = null;
		contentsBuilder.setCertificate(null);
		contentsBuilder.setServiceMRN(null);
		
		server.sendMessage();
		Thread.sleep(1000);

		String message = null;
		String signedData = getSignedData(true);
		message = server.getMyMRN() + ":" + signedData;
		client.sendPollingMessage(message);

		assertTrue(TS6_Client.sentMessage.equals("200"));	
	}
	
	/**
	 * expected value: 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void test03() throws IOException, InterruptedException {		
		TS6_Client.sentMessage = null;
		contentsBuilder.setCertificate(null);
		contentsBuilder.setServiceMRN(null);
		
		server.sendMessage();
		Thread.sleep(1000);

		contentsBuilder.setServiceMRN(server.getMyMRN());
		client.sendPollingMessage(contentsBuilder.toString());

		assertTrue(TS6_Client.sentMessage.equals("200"));	
	}
	
	@Test
	public void test04() throws IOException, InterruptedException {		
		TS6_Client.sentMessage = null;
		contentsBuilder.setCertificate(null);
		contentsBuilder.setServiceMRN(null);
		
		server.sendMessage();
		Thread.sleep(1000);

		String signedData = getSignedData(true);
		contentsBuilder.setCertificate(signedData);
		client.sendPollingMessage(contentsBuilder.toString());

		assertTrue(TS6_Client.sentMessage.equals("INVALID MESSAGE"));	
	}
	
	@Test
	public void test05() throws IOException, InterruptedException {		
		TS6_Client.sentMessage = null;
		contentsBuilder.setCertificate(null);
		contentsBuilder.setServiceMRN(null);
		
		server.sendMessage();
		Thread.sleep(1000);

		String signedData = getSignedData(true);
		contentsBuilder.setServiceMRN(server.getMyMRN() + "invalid");
		contentsBuilder.setCertificate(signedData);
		client.sendPollingMessage(contentsBuilder.toString());

		assertTrue(TS6_Client.sentMessage.equals("Invalid MRN"));	
	}
	
	@Test
	public void test06() throws IOException, InterruptedException {		
		TS6_Client.sentMessage = null;
		contentsBuilder.setCertificate(null);
		contentsBuilder.setServiceMRN(null);
		
		server.sendMessage();
		Thread.sleep(1000);

		String signedData = getSignedData(false);
		contentsBuilder.setCertificate(signedData);
		client.sendPollingMessage(contentsBuilder.toString());

		assertTrue(TS6_Client.sentMessage.equals("200"));
	}
	
	@Test
	public void test07() throws IOException, InterruptedException {		
		TS6_Client.sentMessage = null;
		contentsBuilder.setCertificate(null);
		contentsBuilder.setServiceMRN(null);
		
		server.sendMessage();
		Thread.sleep(1000);

		String signedData = getSignedData(true);
		contentsBuilder.setCertificate(signedData);
		
		TS6_Client theClient = new TS6_Client(TS6_Test.clientMRN + "-attacker");
		theClient.sendPollingMessage(contentsBuilder.toString());

		assertTrue(TS6_Client.sentMessage.equals("Authentication Failed"));
	}
}
