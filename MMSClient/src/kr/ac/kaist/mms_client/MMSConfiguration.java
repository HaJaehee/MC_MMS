package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : MMSConfiguration.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03
Version : 0.3.01
*/
/* -------------------------------------------------------- */

public class MMSConfiguration {
	public static String MMS_HOST = "127.0.0.1";
	public static int MMS_PORT = 444;
	public static String MMS_URL = MMS_HOST+":"+MMS_PORT;
	public static final boolean LOGGING = true;
	public static final int LOC_UPDATE_INTERVAL = 5000;
}
