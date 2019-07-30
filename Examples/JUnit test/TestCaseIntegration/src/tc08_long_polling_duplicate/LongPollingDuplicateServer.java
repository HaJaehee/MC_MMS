package tc08_long_polling_duplicate;

/** 
File name : TS8_server.java
	Dropping duplicate long polling request test 
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2019-05-10


 * Rev. history : 2019-05-17
 * Version : 0.9.1
 *		Added assert statements.
 * Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
 * 
 
Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS8_Server to LongPollingDuplicateServer
	Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

import java.io.IOException;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;




public class LongPollingDuplicateServer {
	private int response = 0;
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-08-server";
	private String dstMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	private MMSClientHandler myHandler = null;

	public LongPollingDuplicateServer() {

		try {
			myHandler = new MMSClientHandler(myMRN);

			myHandler.setSender(new MMSClientHandler.ResponseCallback() {

				@Override
				public void callbackMethod(Map<String, List<String>> headerField, String message) {
					// TODO Auto-generated method stub				
				}

			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getResponse() {
		return response;
	}

	public void sendContent(int content) throws IOException {
		

		String data = "aa"; // createDataSize(actual);				
		try {
			myHandler.sendPostMsg(dstMRN, data, 10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*String data = createDataSize(content);
		try {
			myHandler.sendPostMsg(dstMRN, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}


}

