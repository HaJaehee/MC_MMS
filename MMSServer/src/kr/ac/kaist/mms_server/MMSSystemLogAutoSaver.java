package kr.ac.kaist.mms_server;

/* -------------------------------------------------------- */
/** 
File name : MMSSystemLogAutoSaver.java
	
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-27
Version : 0.5.3

Rev. history : 2017-05-02
Version : 0.5.4
	Fixed bugs
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr) 
*/
/* -------------------------------------------------------- */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kr.ac.kaist.message_relaying.MessageRelayingHandler;

public class MMSSystemLogAutoSaver extends Thread {
	private String TAG = "[MMSSystemLogAutoSaver] ";
	
	public MMSSystemLogAutoSaver() {
		// TODO Auto-generated constructor stub
		if (MMSConfiguration.AUTO_SAVE_SYSTEM_LOG) {
			MMSConfiguration.SYSTEM_LOGGING = true;
			this.start();
		} else {
			MMSConfiguration.SYSTEM_LOGGING = false;
		}
	}
	
	public void run() {
		super.run();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO: handle exception
			if(MMSConfiguration.CONSOLE_LOGGING){
				System.out.print(TAG);
				e.printStackTrace();
			}
			if(MMSConfiguration.SYSTEM_LOGGING){
				MMSLog.systemLog.append(TAG+"InterruptedException\n");
			}
		}
		while (true) {
			if (MMSConfiguration.AUTO_SAVE_SYSTEM_LOG) {
				try {
					MMSConfiguration.SYSTEM_LOGGING = true;
					
		    		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		    		String logfile = "./sys_log_"+timeStamp+".txt";
		    		BufferedWriter wr;
					
		    		if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Saving system log");
		    		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Saving system log\n");
		    		
					wr = new BufferedWriter(new FileWriter(logfile));
		    		wr.write(MMSLog.systemLog.toString());
		    		wr.flush();
		    		wr.close();
		    		
		    		MMSLog.systemLog.setLength(0);
		    		if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"System log saved");
		    		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"System log saved\n");
		    		Thread.sleep(MMSConfiguration.AUTO_SAVE_SYSTEM_LOG_INTERVAL);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					if(MMSConfiguration.CONSOLE_LOGGING){
						System.out.print(TAG);
						e.printStackTrace();
					}
					if(MMSConfiguration.SYSTEM_LOGGING){
						MMSLog.systemLog.append(TAG+"UnknownHostException\n");
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if(MMSConfiguration.CONSOLE_LOGGING){
						System.out.print(TAG);
						e.printStackTrace();
					}
					if(MMSConfiguration.SYSTEM_LOGGING){
						MMSLog.systemLog.append(TAG+"IOException\n");
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					if(MMSConfiguration.CONSOLE_LOGGING){
						System.out.print(TAG);
						e.printStackTrace();
					}
					if(MMSConfiguration.SYSTEM_LOGGING){
						MMSLog.systemLog.append(TAG+"InterruptedException\n");
					}
					if (!MMSConfiguration.AUTO_SAVE_SYSTEM_LOG){
						MMSConfiguration.SYSTEM_LOGGING = false;
						MMSLog.systemLog.setLength(0);
						break;
					} else {
						break;
					}
				} 
			} else {
				MMSConfiguration.SYSTEM_LOGGING = false;
				MMSLog.systemLog.setLength(0);
				break;
			}
		}
	}
}
