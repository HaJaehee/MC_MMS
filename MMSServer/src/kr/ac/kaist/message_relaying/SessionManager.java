package kr.ac.kaist.message_relaying;

import java.util.HashMap;

/* -------------------------------------------------------- */
/** 
File name : SessionManager.java
	SessionManager saves session information.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-05-06
Version : 0.5.5
*/
/* -------------------------------------------------------- */

public class SessionManager {
	private String TAG = "[SessionManager] ";
	
	public static HashMap<Integer, String> sessionInfo = new HashMap<>(); //If client is a polling client, value is "p" otherwise ""
	
	
}
