import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/* -------------------------------------------------------- */
/** 
File name : SC2.java
	Service Consumer which can only send messages
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03

Rev. history : 2017-02-01 - Second Issue
Version : 0.3.01

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-18
Version : 0.5.6
	Changed the variable Map<String,String> headerField to Map<String,List<String>>
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-21
Version : 0.7.0
	Compatible with MMS Client beta-0.7.0.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)	

Rev. history : 2018-07-19
Version : 0.7.2
	Added API; message sender guarantees message sequence .
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class SC2 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:1000002";
		//myMRN = args[0];

		MMSConfiguration.MMS_URL = "127.0.0.1:8088";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.
		
		//Service Consumer is able to set its HTTP header field
		Map<String, List<String>> headerfield = new HashMap<String, List<String>>(); // Header field example. You are able to remove this code.
		List<String> valueList = new ArrayList<String>(); 
		valueList.add("1234567890");
		headerfield.put("AccessToken",valueList);
		// Header field example ends.

		String dstMRN = "urn:mrn:smart-navi:service:message-sequence-sensitive-server";
		String message = "안녕 hi \"hello\" ";
		
		List<MsgSenderThread> thrList = new ArrayList<MsgSenderThread>();
		for (int i = 0; i < 10; i++) {
			thrList.add(new MsgSenderThread(myMRN, headerfield, dstMRN, message+i, i));
		}
		
		//Shuffle message sequence.
		int testNum = 2;
		if (testNum == 0) {
			thrList.get(0).start();
			Thread.sleep(4000);
			thrList.get(2).start();
			Thread.sleep(4000);
			thrList.get(1).start();
			Thread.sleep(4000);
			thrList.get(3).start();
			Thread.sleep(4000);
			thrList.get(5).start();
			Thread.sleep(4000);
			thrList.get(4).start();
			Thread.sleep(1000);
			thrList.get(8).start();
			Thread.sleep(1000);
			thrList.get(6).start();
			Thread.sleep(1000);
			thrList.get(7).start();
			Thread.sleep(1000);
			thrList.get(9).start();
		}
		else if (testNum == 1) { 
			thrList.get(0).start();
			Thread.sleep(1000);
			thrList.get(2).start();
			Thread.sleep(1000);
			thrList.get(1).start();
			Thread.sleep(1000);
			thrList.get(3).start();
			Thread.sleep(1000);
			thrList.get(5).start();
			Thread.sleep(1000);
			thrList.get(4).start();
			Thread.sleep(1000);
			thrList.get(8).start();
			Thread.sleep(1000);
			thrList.get(6).start();
			Thread.sleep(1000);
			thrList.get(7).start();
			Thread.sleep(1000);
			thrList.get(9).start();
		}
		else if (testNum == 2) {
			thrList.get(0).start();
			Thread.sleep(100);
			thrList.get(2).start();
			Thread.sleep(100);
			thrList.get(1).start();
			Thread.sleep(100);
			thrList.get(3).start();
			Thread.sleep(100);
			thrList.get(5).start();
			Thread.sleep(100);
			thrList.get(4).start();
			Thread.sleep(100);
			thrList.get(8).start();
			Thread.sleep(100);
			thrList.get(6).start();
			Thread.sleep(100);
			thrList.get(7).start();
			Thread.sleep(100);
			thrList.get(9).start();
		}
	}
}
