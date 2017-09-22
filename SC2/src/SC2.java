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
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-18
Version : 0.5.6
	Changed the variable Map<String,String> headerField to Map<String,List<String>>
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class SC2 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:1000002";
		//myMRN = args[0];

		MMSConfiguration.MMS_URL="127.0.0.1:8088";

		//Service Consumer which can only send message
		MMSClientHandler sender = new MMSClientHandler(myMRN);
		
		//Service Consumer is able to set he's HTTP header field
		Map<String, List<String>> headerfield = new HashMap<String, List<String>>();
		List<String> valueList = new ArrayList<String>();
		valueList.add("1234567890");
		headerfield.put("AccessToken",valueList);
		sender.setMsgHeader(headerfield);
		
		sender.setSender(new MMSClientHandler.ResponseCallback (){
			//Response Callback from the request message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				Iterator<String> iter = headerField.keySet().iterator();
				while (iter.hasNext()){
					String key = iter.next();
					System.out.println(key+":"+headerField.get(key).toString());// Print the matched header field and the header contents.
				}
				System.out.println(message);
			}
			
		});
		
		
		for (int i = 0; i < 10;i++){
			sender.sendPostMsg("urn:mrn:smart-navi:device:tm-server", "/forwarding", "¾È³ç hi \"hello\" " + i);
			//Thread.sleep(100);
		}

		/*
		for (int i = 0; i < 10;i++){
			sender.sendPostMsg("urn:mrn:imo:imo-no:1000005", "¾È³ç hi hello " + i);
			//Thread.sleep(100);
		}*/
	}
}
