package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : MMSConfiguration.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03
Version : 0.3.01


Rev. history : 2017-06-27
Version : 0.5.8
	Variables lat (latitude) and lon (longitude) are added.
Modifier : Jaehyun Park (jae519@kaist.ac.kr)


Rev. history : 2018-06-28
Version : 0.7.1
	Replaced LOGGING to DEBUG.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-19
Version : 0.7.2
	Updated to 0.7.2.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

*/
/* -------------------------------------------------------- */


/**
 * It is an object that contains values for setting MMSClientHandler and SecureMMSClientHandler.
 * @version 0.9.5
 * @see MMSClientHandler
 * @see SecureMMSClientHandler
 */
public class MMSConfiguration {
	private String TAG = "[MMSConfiguration] ";
	public static String MMS_HOST = "127.0.0.1";
	public static int MMS_PORT = 8088; //HTTPS port : 444, HTTP port : 8088
	public static int MMS_SECURE_PORT = 444; //HTTPS port : 444, HTTP port : 8088
	public static String MNS_HOST = "127.0.0.1";	
	public static int MNS_PORT = 8588;
	public static String MMS_URL = MMS_HOST+":"+MMS_PORT;
	public static boolean DEBUG = false;
	public static final int LOC_UPDATE_INTERVAL = 5000;
	public static float lat = (float)0.0;
	public static float lon = (float)0.0;
	public static final String USER_AGENT = "MMSClient/0.9.1";
}
