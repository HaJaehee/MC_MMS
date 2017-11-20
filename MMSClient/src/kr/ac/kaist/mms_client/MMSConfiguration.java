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

*/
/* -------------------------------------------------------- */


/**
 * It is an object that contains values for setting MMSClientHandler and SecureMMSClientHandler.
 * @version 0.6.1
 * @see MMSClientHandler
 * @see SecureMMSClientHandler
 */
public class MMSConfiguration {
	private String TAG = "[MMSConfiguration] ";
	public static String MMS_HOST = "127.0.0.1";
	public static String MNS_HOST = "127.0.0.1";
	public static int MMS_PORT = 444; //HTTPS port : 444, HTTP port : 8088
	public static String MMS_URL = MMS_HOST+":"+MMS_PORT;
	public static boolean LOGGING = true;
	public static final int LOC_UPDATE_INTERVAL = 5000;
	public static float lat = (float)0.0;
	public static float lon = (float)0.0;
}
