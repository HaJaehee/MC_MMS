package kr.ac.kaist.mms_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.ac.kaist.mms_client.MMSPollHandler.PollHandler;
/** 
File name : MMSGeoInfoReporter.java
	Processing Geo-information 
Author : Jaehyun Park (jae519@kaist.ac.kr)
	
Creation Date : 2017-06-27
**/
public class MMSGeoInfoReporter {
	GeoInfoReporter gr = null;
	//HJH
	private static final String USER_AGENT = "MMSClient/0.5.8";
	private String TAG = "[MMSGeoInfoReporter] ";
	private String clientMRN = null;
	
	MMSGeoInfoReporter(String clientMRN, String svcMRN, int interval, int clientPort, int msgType) throws IOException{
		gr = new GeoInfoReporter(clientMRN, svcMRN, interval, clientPort, msgType);
		if(MMSConfiguration.LOGGING)System.out.println(TAG+"Geocasting Information Reporter is created");
	}
	
	//PJH
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
    			} 
    			catch (Exception e){
					System.out.print(TAG);
					e.printStackTrace();
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
			try{
		    	Socket MNSSocket = new Socket(MMSConfiguration.MNS_HOST, 1004);
		    	
		    	BufferedWriter outToMNS = new BufferedWriter(
							new OutputStreamWriter(MNSSocket.getOutputStream(),Charset.forName("UTF-8")));
		    	
		    	outToMNS.write("Geo-location-Update:"+MNSSocket.getLocalSocketAddress()+","+ clientMRN +","+clientPort+",1,"+geoLocationBuilder());
		    	outToMNS.flush();
		    	outToMNS.close();
		    	MNSSocket.close();
		    	
	    	}
	    	catch (Exception e) {
	    		System.out.println("Error during geo-location update");
				return;
			}
		}
	
		
	}
    //PJH end	
}
