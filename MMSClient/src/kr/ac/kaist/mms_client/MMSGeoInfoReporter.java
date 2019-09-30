package kr.ac.kaist.mms_client;
/** 
File name : MMSGeoInfoReporter.java
	Processing Geo-information.
Author : Jaehyun Park (jae519@kaist.ac.kr)
Creation Date : 2017-06-27
Version : 0.6.0

Rev. history : 2018-04-23
Version : 0.7.1
	Removed IMPROPER_CHECK_FOR_UNUSUAL_OR_EXCEPTIONAL_CONDITION, EXPOSURE_OF_SYSTEM_DATA hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-27
Version : 0.7.2
	Revised for more stable socket communication with MNS.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
**/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * It is an object that processes geo-information and registers it to MMS.
 * It is being developed, please do not use it.
 * @version 0.9.5
 * @see MMSClientHandler
 * @see SecureMMSClientHandler
 */
public class MMSGeoInfoReporter {
	GeoInfoReporter gr = null;
	//HJH
	private String TAG = "[MMSGeoInfoReporter] ";
	private String clientMRN = null;
	
	MMSGeoInfoReporter(String clientMRN, String svcMRN, int interval, int clientPort, int msgType) throws IOException{
		gr = new GeoInfoReporter(clientMRN, svcMRN, interval, clientPort, msgType);
		if(MMSConfiguration.DEBUG) {System.out.println(TAG+"Geocasting Information Reporter is created");}
	}
	
	//HJH
    class GeoInfoReporter extends Thread{
		private int interval = 0;
		private String clientMRN = null;
		private String dstMRN = null;
		private String svcMRN = null;
		private int clientPort = 0;
		private int clientModel = 0;
		private Map<String,List<String>> headerField = null;
		MMSClientHandler.PollingResponseCallback myCallback = null;
		

		GeoInfoReporter (String clientMRN, String svcMRN, int interval, int clientPort, int clientModel){
    		this.interval = interval;
    		this.clientMRN = clientMRN;
    		this.svcMRN = svcMRN;
    		this.clientPort = clientPort;
    		this.clientModel = clientModel;
    	}
    	public void run(){
    		while (true){
    			try{
	    			Thread.sleep(interval);
	    			Report();
    			} catch (InterruptedException e){
    				System.out.println(TAG+" Exception: "+ e.getLocalizedMessage());
					if(MMSConfiguration.DEBUG){e.printStackTrace();}
    			} catch (Exception e){
    				System.out.println(TAG+" Exception: "+ e.getLocalizedMessage());
					if(MMSConfiguration.DEBUG){e.printStackTrace();}
    			}
    		}
    	}
    	String geoLocationBuilder(){
    		//should be added some geo-location realted functions.
    		//float lat = (float) 1.1;
    		//float lon = (float) 2.2;
    		return "lat-" + MMSConfiguration.lat + "-long-" + MMSConfiguration.lon;
    	}
		void Report() throws Exception {
			
			Socket MNSSocket = null;
			PrintWriter pw = null;	
			InputStreamReader isr = null;
			BufferedReader br = null;
			String queryReply = null;
	    	try{
		    	//String modifiedSentence;

		    	MNSSocket = new Socket(MMSConfiguration.MNS_HOST, MMSConfiguration.MNS_PORT);
		    	MNSSocket.setSoTimeout(5000);
		    	pw = new PrintWriter(MNSSocket.getOutputStream());
		    	isr = new InputStreamReader(MNSSocket.getInputStream());
		    	br = new BufferedReader(isr);
		    	String inputLine = null;
				StringBuffer response = new StringBuffer();
				
			    pw.println("Geo-location-Update:"+MNSSocket.getLocalSocketAddress()+","+ clientMRN +","+clientPort+",1,"+geoLocationBuilder());
			    pw.flush();
			    if (!MNSSocket.isOutputShutdown()) {
			    	MNSSocket.shutdownOutput();
			    }
			   
			    
		    	while ((inputLine = br.readLine()) != null) {
		    		response.append(inputLine);
		    	}
			    
		    	
		    	queryReply = response.toString();
		    	

	    	} catch (UnknownHostException e) {
	    		System.out.println(TAG+" Exception: "+ e.getLocalizedMessage());
				if(MMSConfiguration.DEBUG){e.printStackTrace();}
			} catch (IOException e) {
				System.out.println(TAG+" Exception: "+ e.getLocalizedMessage());
				if(MMSConfiguration.DEBUG){e.printStackTrace();}
			} finally {
	    		if (pw != null) {
	    			pw.close();
	    		}
				if (isr != null) {
					try {
						isr.close();
					} catch (IOException e) {
						System.out.println(TAG+" Exception: "+ e.getLocalizedMessage());
						if(MMSConfiguration.DEBUG){e.printStackTrace();}
					}
				}
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						System.out.println(TAG+" Exception: "+ e.getLocalizedMessage());
						if(MMSConfiguration.DEBUG){e.printStackTrace();}
					}
				}
	    		if (MNSSocket != null) {
	    			try {
						MNSSocket.close();
					} catch (IOException e) {
						System.out.println(TAG+" Exception: "+ e.getLocalizedMessage());
						if(MMSConfiguration.DEBUG){e.printStackTrace();}
					}
	    		}
			}
		}
		
	}
    //HJH end	
}
