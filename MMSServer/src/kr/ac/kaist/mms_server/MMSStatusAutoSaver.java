package kr.ac.kaist.mms_server;

/* -------------------------------------------------------- */
/** 
File name : MMSStatusAutoSaver.java
	
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-27
Version : 0.5.2

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

public class MMSStatusAutoSaver extends Thread{
	private String TAG = "[MMSStatusAutoSaver] ";
	
	public MMSStatusAutoSaver() {
		// TODO Auto-generated constructor stub
		if (MMSConfiguration.AUTO_SAVE_STATUS) {
			this.start();
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
			if (MMSConfiguration.AUTO_SAVE_STATUS) {
				do {
					try{
						MMSLog.dumpMNS();
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						try {
							if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"MNS Dummy is not running");
							if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"MNS Dummy is not running\n");
							Thread.sleep(10000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							if(MMSConfiguration.CONSOLE_LOGGING){
								System.out.print(TAG);
								e1.printStackTrace();
							}
							if(MMSConfiguration.SYSTEM_LOGGING){
								MMSLog.systemLog.append(TAG+"InterruptedException\n");
							}
						}
					}
				} while (MMSConfiguration.AUTO_SAVE_STATUS);
			} else {
				break;
			}
			while (true) {
				if (MMSConfiguration.AUTO_SAVE_STATUS) {
					try {
						String status = MMSLog.getStatusForSAS().replaceAll("<br/>","\n");
						
			    		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			    		String logfile = "./auto_save_"+timeStamp+".txt";
			    		BufferedWriter wr;
						
						wr = new BufferedWriter(new FileWriter(logfile));
			    		if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Saving status");
			    		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Saving status\n");
			    		
			    		wr.write(status);
			    		wr.flush();
			    		wr.close();

						
			    		MMSLog.queueLogForSAS.setLength(0);
			    		if(MMSConfiguration.CONSOLE_LOGGING)System.out.println(TAG+"Status saved");
			    		if(MMSConfiguration.SYSTEM_LOGGING)MMSLog.systemLog.append(TAG+"Status saved\n");
			    		Thread.sleep(MMSConfiguration.AUTO_SAVE_STATUS_INTERVAL);
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
						if(!MMSConfiguration.AUTO_SAVE_STATUS){
							MMSLog.queueLogForSAS.setLength(0);
							break;
						} else {
							break;
						}
					}
				} else {
					MMSLog.queueLogForSAS.setLength(0);
					break;
				}
			}
		}
	}
}
