package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : SessionManager.java
	SessionManager saves session information.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-05-06
Version : 0.5.5

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-03
Version : 0.7.2
	Added handling input messages by FIFO scheduling.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */
import java.util.HashMap;
import java.util.List;



public class SessionManager {
	private String TAG = "[SessionManager] ";
	

	public static HashMap<String, String> sessionInfo = new HashMap<>(); //If client is a polling client, value is "p" otherwise ""
	public static HashMap<String, List<SessionIdAndThr>> sessionWatingRes = new HashMap<>(); //This is used for handling input messages by FIFO scheduling.

	
	
}
