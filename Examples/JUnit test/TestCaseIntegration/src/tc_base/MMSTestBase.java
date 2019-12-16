package tc_base;

import org.junit.BeforeClass;

import kr.ac.kaist.mms_client.MMSConfiguration;

public class MMSTestBase {
	public static String MMS_HOST = "127.0.0.1";
//	public static String MMS_HOST = "mms-kaist.com";
	//public static String MMS_HOST = "mms.smartnav.org";
	public static String MMS_URL = MMS_HOST + ":" + MMSConfiguration.MMS_PORT;
	@BeforeClass
	public static void configuration() {
		MMSConfiguration.MMS_URL = MMS_URL;	
		MMSConfiguration.DEBUG = false;
		System.out.println("MMS URL configuration");
		System.out.println();
		System.out.println("THIS MESSAGE MUST BE PRINTED BEFORE CLASS.");
	}
	public static void changeHost(String host) {
		changeURL(host, MMSConfiguration.MMS_PORT);
	}
	public static void changePort(int port) {
		changeURL(MMS_HOST, port);
	}
	public static void changeURL(String host, int port) {
		MMSConfiguration.MMS_URL = getURL(host, port);
	}
	public static String getURL(String host, int port) {
		return host+":"+port;
	}
}
