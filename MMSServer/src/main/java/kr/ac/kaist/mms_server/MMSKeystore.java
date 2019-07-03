package kr.ac.kaist.mms_server;
/* -------------------------------------------------------- */
/** 
File name : MMSKeystore.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-25
Version : 0.5.0

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */
public class MMSKeystore {
	private String TAG = "[MMSKeystore:";
	// mms-kaist.com 190402
	public final static String data = MMSConfiguration.getKeystore();
}
