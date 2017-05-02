package kr.ac.kaist.mms_server;

/* -------------------------------------------------------- */
/** 
File name : MMSConfiguration.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-04-27
Version : 0.5.2
	Added AUTO_SAVE_STATUS, SAVE_STATUS_INTERVAL
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr) 

Rev. history : 2017-04-29
Version : 0.5.3
	Added SYSTEM_LOGGING, AUTO_SAVE_SYSTEM_LOG, SAVE_SYSTEM_LOG_INTERVAL
	Changed LOGGING to CONSOLE_LOGGING
	Changed WEB_LOG_PROVIDING to WEB_LOG_PROVIDING
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr) 
*/
/* -------------------------------------------------------- */

public class MMSConfiguration {
	private String TAG = "[MMSConfiguration] ";
	public static final boolean CONSOLE_LOGGING = true;
	public static final boolean SYSTEM_LOGGING = true;
	public static final boolean WEB_LOG_PROVIDING = true;
	public static final boolean EMPTY_QUEUE = true;
	public static final boolean EMPTY_MNS_DUMMY = true;
	public static final boolean REMOVE_ENTRY_MNS_DUMMY = true;
	public static final int HTTP_PORT = 8088;
	public static final int HTTPS_PORT = 444;
	public static final int UDP_PORT = 8089;
	public static final String MMS_MRN = "urn:mrn:smart-navi:device:mms1";
	public static final boolean AUTO_SAVE_STATUS = true;
	public static final long SAVE_STATUS_INTERVAL = 300000;
	public static final boolean AUTO_SAVE_SYSTEM_LOG = true;
	public static final long SAVE_SYSTEM_LOG_INTERVAL = 180000; 
}
