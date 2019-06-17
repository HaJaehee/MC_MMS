package tc02_relaying_variable_header;
import static org.junit.Assert.*;

import java.awt.List;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import tc_base.MMSTestBase;

/** 
File name : TS2_server.java
	Relaying message function for the purpose of testing MMS
Author : YoungJin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-09-13

Rev. history : 2019-05-17
Version : 0.9.1
	Change the version from JUnit3 to JUnit4.
	Change output because MMS return string is refined.
	Running this test case with version 0.9.1 and the test is succeeded.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS2_Test to MMSMessagewithVariableHeaderTest
	Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

public class MMSMessagewithVariableHeaderTest extends MMSTestBase {

	static MMSMessagewithVariableHeaderServer server;
	static MMSMessagewithVariableHeaderClient client;

	@BeforeClass
	public static void main() {
		try {
			server = new MMSMessagewithVariableHeaderServer();
			client = new MMSMessagewithVariableHeaderClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@AfterClass
	public static void afterClass() {
		server.terminateServer();
	}

	@Test
	public void test01() {
		int expected_content_length = 0;
		int actual_content_length = 0;
		String srcMRN = "urn:mrn:imo:imo-no:ts-mms-02-server";
		String dstMRN = "urn:mrn:imo:imo-no:ts-mms-02-client";
		String actual_dstmrn = null;
		String actual_srcmrn = null;

		client.insertHeader01(dstMRN, srcMRN);
		actual_dstmrn = server.getdstMRN();
		actual_srcmrn = server.getsrcMRN();

		dstMRN = "[" + dstMRN + "]";
		srcMRN = "[" + srcMRN + "]";
		System.out.println("dstMRN" + dstMRN);
		assertEquals(dstMRN, actual_dstmrn);
		assertEquals(srcMRN, actual_srcmrn);
	}

	@Test
	public void test02() {
		int expected_content_length = 0;
		int actual_content_length = 0;
		String srcMRN = "urn:mrn:imo:imo-no:ts-mms-02-serverAAAAAAAAAaa";
		String dstMRN = "urn:mrn:imo:imo-no:ts-mms-02-clientAAAAAAAAAaa";
		String actual_dstmrn = null;
		String actual_srcmrn = null;

		client.insertHeader01(dstMRN, srcMRN);
		actual_dstmrn = server.getdstMRN();
		actual_srcmrn = server.getsrcMRN();

		dstMRN = "[" + dstMRN + "]";
		srcMRN = "[" + srcMRN + "]";
		System.out.println("dstMRN" + dstMRN);
		assertEquals(dstMRN, actual_dstmrn);
		assertEquals(srcMRN, actual_srcmrn);
	}

	@Test
	public void test03() {
		int expected_content_length = 0;
		int actual_content_length = 0;
		String srcMRN = "urn:mrn:imo:imo-no:ts-mms-02-server";
		String dstMRN = "urn:mrn:imo:imo-no:ts-mms-02-client";
		String realm = "example";
		String consumer_key = "06ewg5q65d1a56s1fd56a1";
		String token = "ga1531g51a62eg31d23g1d23";
		String signature_method = "gads563g1a32ds1g23";
		String signature = "g45a6dg56a56g4a5s6g5";
		String timestamp = "137120000";
		String nonce = "57256a56sdg5sd6g3sdg";
		String version = "1.0";
		String msg = "Hello";

		String actual_dstmrn = null;
		String actual_srcmrn = null;
		String actual_realm = null;
		String actual_consumer_key = null;
		String actual_token = null;
		String actual_signature_method = null;
		String actual_signature = null;
		String actual_timestamp = null;
		String actual_nonce = null;
		String actual_version = null;
		String actual_msg = null;

		ArrayList headername = new ArrayList();

		headername.add("srcMRN");
		headername.add("dstMRN");
		headername.add("realm");
		headername.add("consumer_key");
		headername.add("token");
		headername.add("signature_method");
		headername.add("signature");
		headername.add("timestamp");
		headername.add("nonce");
		headername.add("version");
		headername.add("msg");

		ArrayList value = new ArrayList();

		value.add(srcMRN);
		value.add(dstMRN);
		value.add(realm);
		value.add(consumer_key);
		value.add(token);
		value.add(signature_method);
		value.add(signature);
		value.add(timestamp);
		value.add(nonce);
		value.add(version);
		value.add(msg);

		client.insertHeader03(headername, value, 11);
		// client.insertHeader02(dstMRN,
		// srcMRN,realm,consumer_key,token,signature_method,signature,timestamp,nonce,version,msg);
		actual_dstmrn = server.getdstMRN();
		actual_srcmrn = server.getsrcMRN();
		actual_realm = server.getrealm();
		actual_consumer_key = server.getconsumerkey();
		actual_token = server.gettoken();
		actual_signature_method = server.getsignaturemethod();
		actual_signature = server.getsignature();
		actual_timestamp = server.gettimestamp();
		actual_nonce = server.getnonce();
		actual_version = server.getversion();
		actual_msg = server.getmsg();

		dstMRN = "[" + dstMRN + "]";
		srcMRN = "[" + srcMRN + "]";
		realm = "[" + realm + "]";
		consumer_key = "[" + consumer_key + "]";
		token = "[" + token + "]";
		signature_method = "[" + signature_method + "]";
		signature = "[" + signature + "]";
		timestamp = "[" + timestamp + "]";
		nonce = "[" + nonce + "]";
		version = "[" + version + "]";
		msg = "[" + msg + "]";

		assertEquals(dstMRN, actual_dstmrn);
		assertEquals(srcMRN, actual_srcmrn);
		assertEquals(realm, actual_realm);
		assertEquals(consumer_key, actual_consumer_key);
		assertEquals(token, actual_token);
		assertEquals(signature_method, actual_signature_method);
		assertEquals(signature, actual_signature);
		assertEquals(timestamp, actual_timestamp);
		assertEquals(nonce, actual_nonce);
		assertEquals(version, actual_version);
		assertEquals(msg, actual_msg);

	}

	@Test
	public void test04() {
		int expected_content_length = 0;
		int actual_content_length = 0;
		String srcMRN = "urn:mrn:imo:imo-no:ts-mms-02-server";
		String dstMRN = "urn:mrn:imo:imo-no:ts-mms-02-client";
		String token = "fe5F165f6f121we231fa1df2a1sdf656we1a1sd56f5as6f56asd425v1sad23v1as56dv564ear56v151vr56as156v1ds1v56sd5v1as56dv156asd1v5asd5g445w6afg2dsa1v65asdf56asdf56sd56f4as651f26ds1v65asd1v56as1dv5a4sd5gv4a5v45cxv156sa4v5s5adv56sdgf458sa4df561as56f4asd56fs5v5sda4v56";
		String actual_dstmrn = null;
		String actual_srcmrn = null;
		String actual_token = null;

		ArrayList headername = new ArrayList();

		headername.add("srcMRN");
		headername.add("dstMRN");
		headername.add("Token");
		ArrayList value = new ArrayList();

		value.add(srcMRN);
		value.add(dstMRN);
		value.add(token);

		client.insertHeader03(headername, value, 3);

		actual_dstmrn = server.getdstMRN();
		actual_srcmrn = server.getsrcMRN();
		actual_token = server.gettoken();
		dstMRN = "[" + dstMRN + "]";
		srcMRN = "[" + srcMRN + "]";
		token = "[" + token + "]";

		assertEquals(dstMRN, actual_dstmrn);
		assertEquals(srcMRN, actual_srcmrn);
		assertEquals(token, actual_token);
	}
	@Test
	public void test05() {
		
		String tmp = client.sendmsg();
		
		System.out.println("tmp : "+tmp);
		
		
		
		assertTrue(tmp.equals("[10003] Null destination MRN."));
		
	}
}
