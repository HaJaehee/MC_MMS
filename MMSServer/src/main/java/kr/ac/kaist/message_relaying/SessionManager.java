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

Rev. history: 2019-05-24
Version : 0.9.1
	Fixed session count bugs.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-03
Version : 0.9.3
	Added resetSessionInfo().
	Added multi-thread safety.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-09
Version : 0.9.3
	Revised for coding rule conformity.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-07-11
Version : 0.9.3
	Added GC suggestion.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
		
		@Override
		public synchronized void run() {
			try { // Wait for initializing other threads.
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// Do nothing.
				System.out.println(e.getMessage());
			}
			
			File f = new File("./session-count.csv");
			
			FileWriter fw = null;
			BufferedWriter bw = null;
			PrintWriter pw = null;
			
			
			synchronized (f) {
				long fileLines = 0;
				FileReader fr = null;
				BufferedReader br = null;
				if (f.exists()) {

					try {
						fr = new FileReader(f);
						br = new BufferedReader(fr);
						String line;
		
						while ((line=br.readLine()) != null) {
							fileLines++;
						}
						
						br.close();
						fr.close();
						
					} //TODO: logger 사용 변경 
					catch (ArrayIndexOutOfBoundsException | NumberFormatException | IOException e1) {
						MMSLog.getInstance().warnException(logger, "", "File session-count.csv is not found or there is a problem when reading the file.", e1, 5);
					}
					finally {
						if (fr != null) {
							try {
								fr.close();
							} catch (IOException e) {
								MMSLog.getInstance().warnException(logger, "","Failed to close BufferedWriter.", e, 5);
							}
							fr = null;
						}
						if (br != null) {
							try {
								br.close();
							} catch (IOException e) {
								MMSLog.getInstance().warnException(logger, "","Failed to close BufferedWriter.", e, 5);
			
							}
							bw = null;
						}
					}
				}
					
				fileLines -= 12*60*24;
				long lineCount = fileLines;
				
				if (f.exists()) {
					try {
						fr = new FileReader(f);
						br = new BufferedReader(fr);
						String line;
						long curTimeMillis = System.currentTimeMillis();
						
						while ((line=br.readLine()) != null) {
							if (lineCount > 0) { // More than 24 hours,
								lineCount--;
								continue; // ignore.
							}
							
							
							if (line.equals("")) {
								break;
							}
							String[] timeAndSessionCountAndPollingSessionCount = line.split(",");
							//print
							
							long time = Long.parseLong(timeAndSessionCountAndPollingSessionCount[0]);
							long isOverflow  = curTimeMillis - time;
							long aDayTime = 1000*60*60*24;
							if (isOverflow > 0 && isOverflow > aDayTime ) {  // More than 24 hours,
								continue; // ignore.
							}
							
							long sessionCount = Long.parseLong(timeAndSessionCountAndPollingSessionCount[1]);
							long pollingSessionCount = Long.parseLong(timeAndSessionCountAndPollingSessionCount[2]);
							
							
							SessionCountForFiveSecs scffs = new SessionCountForFiveSecs(time);
							scffs.setSessionCount(sessionCount);
							scffs.setPollingSessionCount(pollingSessionCount);
							synchronized(sessionCountList) {
								sessionCountList.add(0,scffs);
							}
						}
						br.close();
						fr.close();
					} 
					catch (ArrayIndexOutOfBoundsException | NumberFormatException | IOException e1) {
						MMSLog mmsLog = MMSLog.getInstance();
						mmsLog.warnException(logger, "", "File session-count.csv is not found or there is a problem when reading the file.", e1, 5);
	
					}
					finally {
						if (fr != null) {
							try {
								fr.close();
							} catch (IOException e) {
								MMSLog.getInstance().warnException(logger, "","Failed to close BufferedWriter.", e, 5);
							}
							fr = null;
						}
						if (br != null) {
							try {
								br.close();
							} catch (IOException e) {
								MMSLog.getInstance().warnException(logger, "","Failed to close BufferedWriter.", e, 5);
			
							}
							bw = null;
						}
					}
				}
					
				
				/*
				// Rewrite session-count.csv if the file has more than 24 hours content.
				if (fileLines > 0 && sessionCountList.size() > 0) { 
					try {
						if (f.exists()) {
							f.delete();
						}
						
						fw = new FileWriter(f, true);
						bw = new BufferedWriter(fw);
						pw = new PrintWriter(bw);
						
						for (int i = sessionCountList.size()-1 ; i >=0 ; i--) {
							pw.println(sessionCountList.get(i).getCurTimeInMillis()+","
									+sessionCountList.get(i).getSessionCount()+","
									+sessionCountList.get(i).getPollingSessionCount());
						}
						pw.close();
						bw.close();
						fw.close();
					}
					
					catch (IOException e1) {
						logger.warn("File session-count.csv is not found or there is a problem when writing the file.");  
						logger.warn(e1.getClass().getName()+" "+e1.getStackTrace()[0]+".");
			    			for (int i = 1 ; i < e1.getStackTrace().length && i < 4 ; i++) {
			    				logger.warn(e1.getStackTrace()[i]+".");
			    			}
					} 
				}*/
				
			}
			System.gc();
			
			while (System.currentTimeMillis() % 5000 > 100 ) { // Avoid busy waiting.
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// Do nothing.
					System.out.println(e.getMessage());
				}
			}
			
			int gcSuggestion = 0;
			
			boolean escapeLoop = false;
			while (!escapeLoop) { // Start tik tok.
			
				gcSuggestion++;
				if (gcSuggestion > 100) {
					System.gc();
					gcSuggestion = 0;
				}
				long curTimeMillis = System.currentTimeMillis();
				long correction = 0;
				
				if (curTimeMillis % 5000 < 100 ) {
					correction = curTimeMillis % 5000; // Session counting list saves the number of sessions for every 5 seconds.
					synchronized(sessionCountList) {
						long lastTime = 0;
						if (sessionCountList.size() > 0) {
							lastTime = sessionCountList.get(0).getCurTimeInMillis();
						}
						
					
						while (curTimeMillis - lastTime > 10000 && curTimeMillis - lastTime < 1000*60*60*24) { // More than 10 seconds, less than 24 hours.
							sessionCountList.add(0,new SessionCountForFiveSecs(lastTime+5000)); // Add time slots having 0 session count.
							lastTime += 5000;
						}

						for (int i = sessionCountList.size()-(12*60*24) ; i >= 0 ; i--) { // Session counts are saved for 24 hours.
							sessionCountList.remove(sessionCountList.size()-1);
						}
						
						try {
							synchronized (f) {
								if (!f.exists()) {
									f.createNewFile();
								}
								if (f.exists()) {
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
								}
							}
						}
						catch (IOException e){
							escapeLoop = true;
							MMSLog.getInstance().warnException(logger, "","File session-count.csv is not found or there is a problem when writing the file.", e, 5);

						}
						finally {
							if (pw != null) {
								pw.close();
								pw = null;
							}
							if (bw != null) {
								try {
									bw.close();
								} catch (IOException e) {
									MMSLog mmsLog = MMSLog.getInstance();
									mmsLog.warnException(logger, "","Failed to close BufferedWriter.", e, 5);
				
								}
								bw = null;
							}
							if (fw != null) {
								try {
									fw.close();
								} catch (IOException e) {
									MMSLog mmsLog = MMSLog.getInstance();
									mmsLog.warnException(logger, "","Failed to close FileWriter.", e, 5);
								
								}
								fw = null;
							}
						}
						
						SessionCountForFiveSecs curCount = new SessionCountForFiveSecs(curTimeMillis);
						sessionCountList.add(0, curCount);
					}
	
					/*
					// print
					for (int i = 0 ; i < sessionCountList.size() ; i++) {
						SimpleDateFormat dayTime = new SimpleDateFormat("hh:mm:ss:SSS");
						System.out.print(dayTime.format(sessionCountList.get(i).getCurTimeInMillis())+"  ");
					}
					System.out.println();
					*/
					
					try {
						Thread.sleep(5000 - correction);
					} catch (InterruptedException e) {
						// Do nothing.
						System.out.println(e.getMessage());
					}
				}			
			} 
		}
	}
	
	public static void putSessionInfo(String sessionId, String type) {
		synchronized(sessionInfo) {
			sessionInfo.put(sessionId, type);
		}
	}
	
	public static String getSessionType(String sessionId) {
		synchronized(sessionInfo) {
			return sessionInfo.get(sessionId);
		}
	}
	
	public static void removeSessionInfo(String sessionId) {
		synchronized(sessionInfo) {
			sessionInfo.remove(sessionId);
		}
	}
	
	public static Set<String> getSessionIDs() {
		synchronized(sessionInfo) {
			return sessionInfo.keySet();
		}
	}
	
	public static boolean isSessionInfoEmpty() {
		synchronized(sessionInfo) {
			return sessionInfo.isEmpty();
		}
	}
	
	public static int getSessionInfoSize() {
		synchronized(sessionInfo) {
			return sessionInfo.size();
		}
	}
	
	public static void resetSessionInfo() {
		synchronized(sessionInfo) {
			sessionInfo = new HashMap<>();
		}
	}

	public static void putItemToMapSrcDstPairAndSessionInfo(String srcDstPair) {
		synchronized(mapSrcDstPairAndSessionInfo) {
			mapSrcDstPairAndSessionInfo.put(srcDstPair, new SessionList<SessionIdAndThr>());
		}
	}

	public static SessionList<SessionIdAndThr> getItemFromMapSrcDstPairAndSessionInfo(String srcDstPair) {
		synchronized(mapSrcDstPairAndSessionInfo) {
			return mapSrcDstPairAndSessionInfo.get(srcDstPair);
		}
	}
	
	public static Double getNumFromMapSrcDstPairAndLastSeqNum(String srcDstPair) {
		synchronized(mapSrcDstPairAndLastSeqNum) {
			return mapSrcDstPairAndLastSeqNum.get(srcDstPair);
		}
	}

	public static void resetNumInMapSrcDstPairAndLastSeqNum(String srcDstPair) {
		synchronized(mapSrcDstPairAndLastSeqNum) {
			mapSrcDstPairAndLastSeqNum.put(srcDstPair, -1.0);
		}
	}
	
	public static void setNumInMapSrcDstPairAndLastSeqNum(String srcDstPair, Double num) {
		synchronized(mapSrcDstPairAndLastSeqNum) {
			mapSrcDstPairAndLastSeqNum.put(srcDstPair, num);
		}
	}
	
	public static void incSessionCount() {
		synchronized(sessionCountList) {
			sessionCountList.get(0).incSessionCount();
		}
	}
	
	public static void incPollingSessionCount() {
		synchronized(sessionCountList) {
			sessionCountList.get(0).incPollingSessionCount();
		}
	}
	
	public static int getSessionCount(int minutes) {
		synchronized(sessionCountList) {
			int countListSize = sessionCountList.size(); // Session counting list saves the number of sessions for every 5 seconds.
			int relayReqCount = 0;
			for (int i = 0 ; i < countListSize && i < minutes*12 ; i++) { // Adding count up for x minutes.
				relayReqCount += sessionCountList.get(i).getSessionCount() // Total session counts.
						- sessionCountList.get(i).getPollingSessionCount();// Subtract polling session counts from total session counts.
			}
			return relayReqCount;
		}
	}
	
	public static int getPollingSessionCount(int minutes) {
		synchronized(sessionCountList) {
			int countListSize = sessionCountList.size(); // Session counting list saves the number of sessions for every 5 seconds.
			int pollingReqCount = 0;
			for (int i = 0 ; i < countListSize && i < minutes*12 ; i++) { // Adding count up for x minutes.
				pollingReqCount += sessionCountList.get(i).getPollingSessionCount(); // Polling session counts.
			}
			return pollingReqCount;
		}
	}
	
	public static int getSessionCountListSize() {
		synchronized(sessionCountList) {
			return sessionCountList.size();
		}
	}
}
