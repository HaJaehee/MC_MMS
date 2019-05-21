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

Rev. history: 2019-05-21
Version : 0.9.1
	Added function of saving and restoring session count list.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

**/
/* -------------------------------------------------------- */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.mms_server.MMSLog;

public class SessionManager {
	private String TAG = "[SessionManager] ";
	
	private static Logger logger = null;
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
		this.logger = LoggerFactory.getLogger(SessionManager.class);
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
			try { // Wait for initializing other threads.
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// Do nothing.
			}
			
			File f = new File("./session-count.csv");
			long fileLines = 0;
			try {
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String line;

				while ((line=br.readLine()) != null) {
					fileLines++;
				}
				
				br.close();
				fr.close();
			} catch (ArrayIndexOutOfBoundsException | NumberFormatException | IOException e1) {
				logger.warn("File session-count.csv is not found or there is a problem when reading the file.");  
				logger.warn(e1.getClass().getName()+" "+e1.getStackTrace()[0]+".");
	    			for (int i = 1 ; i < e1.getStackTrace().length && i < 4 ; i++) {
	    				logger.warn(e1.getStackTrace()[i]+".");
	    			}
			}
			
			fileLines -= 12*60*24;

			try {
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String line;

				while ((line=br.readLine()) != null) {
					if (fileLines > 0) {
						fileLines--;
						continue;
					}
					if (line.equals("")) {
						break;
					}
					String[] timeAndSessionCountAndPollingSessionCount = line.split(",");
					//print
					
					long time = Long.parseLong(timeAndSessionCountAndPollingSessionCount[0]);
					long sessionCount = Long.parseLong(timeAndSessionCountAndPollingSessionCount[1]);
					long pollingSessionCount = Long.parseLong(timeAndSessionCountAndPollingSessionCount[2]);
					
					SessionCountForFiveSecs scffs = new SessionCountForFiveSecs(time);
					scffs.setSessionCount(sessionCount);
					scffs.setPollingSessionCount(pollingSessionCount);
					sessionCountList.add(0,scffs);
				}
				br.close();
				fr.close();
			} catch (ArrayIndexOutOfBoundsException | NumberFormatException | IOException e1) {
				logger.warn("File session-count.csv is not found or there is a problem when reading the file.");  
				logger.warn(e1.getClass().getName()+" "+e1.getStackTrace()[0]+".");
	    			for (int i = 1 ; i < e1.getStackTrace().length && i < 4 ; i++) {
	    				logger.warn(e1.getStackTrace()[i]+".");
	    			}
			}
			
			while (System.currentTimeMillis() % 5000 > 100 ) { // Avoid busy waiting.
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// Do nothing.
				}
			}
			
			FileWriter fw = null;
			BufferedWriter bw = null;
			PrintWriter pw = null;
			
			
				while (true) { // Start tik tok.
					try {
						long curTimeMillis = System.currentTimeMillis();
						long correction = 0;
						
						if (curTimeMillis % 5000 < 100 ) {
							correction = curTimeMillis % 5000; // Session counting list saves the number of sessions for every 5 seconds.
							
							
						
							
							long lastTime = 0;
							if (sessionCountList.size() > 0) {
								lastTime = sessionCountList.get(0).getCurTimeInMillis();
							}
							
						
							while (curTimeMillis - lastTime > 10000 && curTimeMillis - lastTime < 1000*60*60*24) { // More than 10 seconds, less than 24 hours.
								sessionCountList.add(0,new SessionCountForFiveSecs(lastTime+5000)); // Add time slots with 0 session count.
								lastTime += 5000;
							}
							
							
							
							for (int i = sessionCountList.size()-(12*60*24) ; i >= 0 ; i--) { // Session counts are saved for 24 hours.
								sessionCountList.remove(sessionCountList.size()-1);
							}
							
							fw = new FileWriter(f, true);
							bw = new BufferedWriter(fw);
							pw = new PrintWriter(bw);
							if (sessionCountList.size() > 0) {
								pw.println(sessionCountList.get(0).getCurTimeInMillis()+","
										+sessionCountList.get(0).getSessionCount()+","
										+sessionCountList.get(0).getPollingSessionCount());
							}
							pw.close();
							bw.close();
							fw.close();
							
							SessionCountForFiveSecs curCount = new SessionCountForFiveSecs(curTimeMillis);
							sessionCountList.add(0, curCount);
			
							// print
							for (int i = 0 ; i < sessionCountList.size() ; i++) {
								SimpleDateFormat dayTime = new SimpleDateFormat("hh:mm:ss:SSS");
								System.out.print(dayTime.format(sessionCountList.get(i).getCurTimeInMillis())+"  ");
							}
							System.out.println();
							
							try {
								Thread.sleep(5000 - correction);
							} catch (InterruptedException e) {
								// Do nothing.
							}
							
						}
					}
				catch (IOException e1) {
					logger.warn("File session-count.csv is not found or there is a problem when writing the file.");  
					logger.warn(e1.getClass().getName()+" "+e1.getStackTrace()[0]+".");
		    			for (int i = 1 ; i < e1.getStackTrace().length && i < 4 ; i++) {
		    				logger.warn(e1.getStackTrace()[i]+".");
		    			}
				} 
				finally {
					if (pw != null) {
						pw.close();
					}
					if (bw != null) {
						try {
							bw.close();
						} catch (IOException e) {
							logger.warn("Failed to close BufferedWriter.");  
							logger.warn(e.getClass().getName()+" "+e.getStackTrace()[0]+".");
				    			for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
				    				logger.warn(e.getStackTrace()[i]+".");
				    			}
						}
					}
					if (fw != null) {
						try {
							fw.close();
						} catch (IOException e) {
							logger.warn("Failed to close FileWriter.");  
							logger.warn(e.getClass().getName()+" "+e.getStackTrace()[0]+".");
				    			for (int i = 1 ; i < e.getStackTrace().length && i < 4 ; i++) {
				    				logger.warn(e.getStackTrace()[i]+".");
				    			}
						}
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
