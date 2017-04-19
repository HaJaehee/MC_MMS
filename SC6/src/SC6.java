import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : SC6.java
	Service Provider can be HTTP server and listen to port 'port'.
	SP can send text-based files
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-02-14
Version : 0.3.01
*/
/* -------------------------------------------------------- */

public class SC6 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:1000006";
		//myMRN = args[0];
		
		//Service Provider can be HTTP server and listen to port 'port'. 
		//port = Integer.parseInt(args[1]);
		int port = 8907;
		//SP can send text-based files
		String fileDirectory = "/get/";
		String fileName = "test.xml";
		
		MMSConfiguration.MMS_URL="127.0.0.1:8088";

		MMSClientHandler ch = new MMSClientHandler(myMRN);
		ch.setPort(port,fileDirectory,fileName); //ch has a context '/get/test.xml'
		/* It is not same with: 
		* ch.setPort(port); //It sets default context as '/'
		* ch.addFileContext(fileDirectory, fileName); //Finally ch has two context '/' and '/get/test.xml'
	    */
	}
}
