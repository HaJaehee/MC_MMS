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

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-21
Version : 0.6.1
	Compatible with MMS Client beta-0.6.1.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	
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
		
		MMSConfiguration.MMS_URL = "127.0.0.1:8088";
		MMSConfiguration.LOGGING = false; // If you are debugging client, set this variable true.

		MMSClientHandler server = new MMSClientHandler(myMRN);
		server.setFileServerPort(port, fileDirectory, fileName); //server has a context '/get/test.xml'
		/* It is not same with: 
		* server.setPort(port); //It sets default context as '/'
		* server.addFileContext(fileDirectory, fileName); //Finally server has two context '/' and '/get/test.xml'
	    */
		fileName = "mc.png";
		server.addFileContext(fileDirectory, fileName);
		fileName = "pdf.pdf";
		server.addFileContext(fileDirectory, fileName);
		fileName = "korean_pdf.pdf";
		server.addFileContext(fileDirectory, fileName);
	}
}
