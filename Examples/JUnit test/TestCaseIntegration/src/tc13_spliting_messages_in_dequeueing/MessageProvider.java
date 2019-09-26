package tc13_spliting_messages_in_dequeueing;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;

/**
File name : MessageProvider.java
Author : Jin Jeong (jungst0001@kaist.ac.kr) 
Creation Date : 2019-09-16


Rev. history : 2019-09-17
Version : 0.9.5
	Add Constructor parameter for MRN.
	Create 'SendFixedSizeMessage' method in order to send fixed size message.
	Add Assertion for contents mismatch.
	
	Modifier : Yunho Choi (choiking10@kaist.ac.kr)
 */

public class MessageProvider {
	private int response = 0;
	private String myMRN;
	private String dstMRN;
	private MMSClientHandler myHandler = null;

	public MessageProvider(String myMRN, String dstMRN) {
		this.myMRN = myMRN;
		this.dstMRN = dstMRN;
		
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
	
	public void sendFixedSizeMessage(int length) {
		char[] charStr = new char[length];
		Arrays.fill(charStr,  'A');
		String data = new String(charStr);
		
		try {
            assertTrue(
                    String.format("The length of the data[%d] is different from the length expected[%d].", data.length(), length), 
                    data.length() == length);

			myHandler.sendPostMsg(dstMRN, data, 10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendContent(String FileName, long content) throws IOException {
		File file = new File(FileName);

		System.out.println(FileName);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufReader = new BufferedReader(fileReader);

		String data = ""; // createDataSize(actual);
		data = bufReader.readLine();
		if(data==null)
			data="";
		try {
            assertTrue(
                    String.format("The length of the data[%d] is different from the length expected[%d].", data.length(), content), 
                    data.length() == content);

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

	public void terminateServer() {
		myHandler.terminateServer();
	}
}
