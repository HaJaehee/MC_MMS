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

Rev. history : 2018-07-18
Version : 0.7.2
	Added handling input messages by reordering policy.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-03-09
Version : 0.8.1
	MMS Client is able to choose its polling method.
	Removed locator registering function.
	Duplicated polling requests are not allowed.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-06
Version : 0.9.0
	Added sessionCountList.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-07
Version : 0.9.0
	Modified for coding conventions.
	Added SessionCounter.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-10
Version : 0.9.0
	Fixed bugs related to session count list.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SessionManager {
	private String TAG = "[SessionManager] ";
	
	// TODO: Youngjin Kim must inspect this following code.
	/* sessionInfo: If client is a polling client, value is "p".
	If client is a long polling client, value is "lp".
	Otherwise value is "".*/
	private static HashMap<String, String> sessionInfo = null; //This is used for saving session information which is polling, long-polling, relaying or the others.
	private static HashMap<String, SessionList<SessionIdAndThr>> mapSrcDstPairAndSessionInfo = null; //This is used for handling input messages by reordering policy.
	private static HashMap<String, Double> mapSrcDstPairAndLastSeqNum = null; //This is used for handling last sequence numbers of sessions.
	private static ArrayList<SessionCountForFiveSecs> sessionCountList = null; //This saves the number of sessions for every five seconds.
	
	private SessionCounter sessionCounter = null;
	
	private SessionManager () {
		sessionInfo = new HashMap<>(); 
		mapSrcDstPairAndSessionInfo = new HashMap<>(); //This is used for handling input messages by reordering policy.
		mapSrcDstPairAndLastSeqNum = new HashMap<>(); //This is used for handling last sequence numbers of sessions.
		sessionCountList = new ArrayList<SessionCountForFiveSecs>(); //This saves the number of sessions for every five seconds.
		sessionCounter = new SessionCounter();
		sessionCounter.start();
	}
	
	public static SessionManager getInstance() { //double check synchronization.
		return LazyHolder.INSTANCE;
	}
	
	private static class LazyHolder {
		private static final SessionManager INSTANCE = new SessionManager();
	}
	
	private class SessionCounter extends Thread {
		
		SessionCounter () {
			super();
		}
		
		//TODO
		@Override
		public void run() {
			try { // Wait initialization of other threads.
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// Do nothing.
			}
			
			while (System.currentTimeMillis() % 5000 > 100 ) { // Avoid busy waiting.
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// Do nothing.
				}
			}
			
			while (true) { // Start tik tok.
				
				long curTimeMillis = System.currentTimeMillis();
				long correction = 0;
				
				if (curTimeMillis % 5000 < 100 ) {
					correction = curTimeMillis % 5000; // Session counting list saves the number of sessions for every 5 seconds.
					for (int i = sessionCountList.size()-(12*60*24) ; i >= 0 ; i--) { // Session counts are saved for 24 hours.
						sessionCountList.remove(sessionCountList.size()-1);
					}
					
					SessionCountForFiveSecs curCount = new SessionCountForFiveSecs(curTimeMillis);
					sessionCountList.add(0, curCount);
					
					/*// print
					for (int i = 0 ; i < sessionCountList.size() ; i++) {
						SimpleDateFormat dayTime = new SimpleDateFormat("hh:mm:ss:SSS");
						System.out.print(dayTime.format(sessionCountList.get(i).getCurTimeInMillis())+"  ");
					}
					System.out.println();*/
					
					try {
						Thread.sleep(5000 - correction);
					} catch (InterruptedException e) {
						// Do nothing.
					}
					
				}
			}
		}
	}
	
	public static HashMap<String, String> getSessionInfo() {
		return sessionInfo;
	}

	public static HashMap<String, SessionList<SessionIdAndThr>> getMapSrcDstPairAndSessionInfo() {
		return mapSrcDstPairAndSessionInfo;
	}

	public static HashMap<String, Double> getMapSrcDstPairAndLastSeqNum() {
		return mapSrcDstPairAndLastSeqNum;
	}

	public static ArrayList<SessionCountForFiveSecs> getSessionCountList() {
		return sessionCountList;
	}
}
