package tc_base;

import org.junit.BeforeClass;

import kr.ac.kaist.mms_client.MMSConfiguration;

public class MMSTestBase {
	public static String MMS_URL = "mms-kaist.com:8088";
	@BeforeClass
	public static void configuration() {
		MMSConfiguration.MMS_URL = MMS_URL;	
		MMSConfiguration.DEBUG = false;
		System.out.println("MMS URL configuration");
		System.out.println();
		System.out.println("THIS MESSAGE MUST BE PRINTED BEFORE CLASS.");
	}
}
