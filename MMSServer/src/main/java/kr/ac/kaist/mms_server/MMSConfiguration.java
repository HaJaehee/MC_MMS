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

Rev. history : 2017-06-17
Version : 0.5.6
	Added normal polling function
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-07-24
Version : 0.5.9
	Updated max http contents size
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class MMSConfiguration {
	private String TAG = "[MMSConfiguration] ";
	//-------------------------------------------------------------------
	public static boolean WEB_LOG_PROVIDING = true;
	public static final boolean WEB_MANAGING = true;
	//-------------------------------------------------------------------
	public static final int HTTP_PORT = 8088;
	public static final int HTTPS_PORT = 444;
	//public static final int UDP_PORT = 8089;
	//-------------------------------------------------------------------
	public static final String MMS_MRN = "urn:mrn:smart-navi:device:mms1";
	//-------------------------------------------------------------------
	public static final int MAX_CONTENT_SIZE = 40 * 1024 * 1024; // 40MB
	//-------------------------------------------------------------------
	public static final int MAX_BRIEF_LOG_LIST_SIZE = 200;

}
