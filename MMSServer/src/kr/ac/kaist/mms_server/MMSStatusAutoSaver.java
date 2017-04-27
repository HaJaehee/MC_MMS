package kr.ac.kaist.mms_server;

/* -------------------------------------------------------- */
/** 
File name : MMSStatusAutoSaver.java
	
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-27
Version : 0.5.2
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
	private static final String TAG = "[MMSStatusAutoSaver] ";
	
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
			if(MMSConfiguration.LOGGING){
				System.out.print(TAG);
				e.printStackTrace();
			}
		}
		while (MMSConfiguration.AUTO_SAVE_STATUS) {
			do {
				try{
					MessageRelayingHandler.dumpMNS();
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					try {
						if(MMSConfiguration.LOGGING){System.out.println(TAG+"MNS Dummy is not running");}
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						if(MMSConfiguration.LOGGING){
							System.out.print(TAG);
							e1.printStackTrace();
						}
					}
				}
			} while (MMSConfiguration.AUTO_SAVE_STATUS);
			while (MMSConfiguration.AUTO_SAVE_STATUS) {
				try {
					String status = MMSLog.getStatus();
					
		    		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		    		String logfile = "./auto_save_"+timeStamp+".txt";
		    		BufferedWriter wr;
					
					wr = new BufferedWriter(new FileWriter(logfile));
					status = status.replaceAll("<br/>", "\n");
		    		wr.write(status);
		    		wr.flush();
		    		wr.close();
		    		if(MMSConfiguration.LOGGING){System.out.println(TAG+"Status saved");}
					
		    		Thread.sleep(MMSConfiguration.SAVE_STATUS_INTERVAL);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					if(MMSConfiguration.LOGGING){
						System.out.print(TAG);
						e.printStackTrace();
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if(MMSConfiguration.LOGGING){
						System.out.print(TAG);
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					if(MMSConfiguration.LOGGING){
						System.out.print(TAG);
						e.printStackTrace();
					}
				}
				
				
			}
		}
	}
}
