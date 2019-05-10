import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

public class TS8_server {
	private int response = 0;
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-06-server";
	private String dstMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
	private MMSClientHandler myHandler = null;

	public TS8_server() {
		//MMSConfiguration.MMS_URL = "143.248.55.83:8088";
		//MMSConfiguration.MMS_URL="mms.smartnav.org:8088";

		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		MMSConfiguration.DEBUG = false;

		try {
			myHandler = new MMSClientHandler(myMRN);

			myHandler.setSender(new MMSClientHandler.ResponseCallback() {

				@Override
				public void callbackMethod(Map<String, List<String>> headerField, String message) {
					// TODO Auto-generated method stub
					if (headerField.get("Response-code") != null) {
						int code = Integer.parseInt(headerField.get("Response-code").get(0));
						response = code;
						System.out.println("Response : " +response);
					}
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
			myHandler.sendPostMsg(dstMRN, data);
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

