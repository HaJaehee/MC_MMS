import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/** 
File name : TS10_client.java
	Relaying message function for the purpose of testing MMS
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-02
*/

public class TS10_client {
	//public static void main(String[] args) throws Exception{	
	private MMSClientHandler sender ;
	
	public TS10_client(String myMRN) throws Exception {		
		sender = new MMSClientHandler(myMRN);
	}
	public void sendMessage(String dstMRN, String data, MMSClientHandler.ResponseCallback callback) {
		sender.setSender(callback);
		
		try {
			sender.sendPostMsg(dstMRN, data);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
