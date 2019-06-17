package tc04_message_sequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kr.ac.kaist.mms_client.MMSClientHandler;

/** 
File name : MessageSequenceServer.java
	message sequence test
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-10-13

Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS4_Test to MessageSequenceServer
	Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

public class MessageSequenceServer {
	String myMRN = "urn:mrn:imo:imo-no:ts-mms-04-server";
	String dstMRN = "urn:mrn:imo:imo-no:ts-mms-04-client";
	private MMSClientHandler myHandler = null;
	ArrayList<Integer> seqNum = new ArrayList();

	public MessageSequenceServer() throws NullPointerException, IOException {

		myHandler = new MMSClientHandler(myMRN);
		int port = 8907;

		myHandler.setServerPort(port, new MMSClientHandler.RequestCallback() {

			@Override
			public Map<String, List<String>> setResponseHeader() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int setResponseCode() {
				// TODO Auto-generated method stub
				return 200;
			}

			@Override
			public String respondToClient(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				Iterator<String> iter = headerField.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next();

					if (key.equals("Seqnum")) {
						// System.out.println(key+":"+headerField.get(key).toString());
						String tmp = headerField.get(key).toString();
						String tmp2 = tmp.substring(1, 2);
						seqNum.add(Integer.parseInt(tmp2));
						// System.out.println("subString1 :" + tmp2 );

					}
				}
				System.out.println("arrivin server : "+message);
				return "OK";
			}
		});

	}
	
	public void terminateServer() {
		myHandler.terminateServer();
	}

	public ArrayList getSeqnum() {
		return seqNum;
	}

	public int getArraySize() {
		return seqNum.size();
	}
	
	public void ArrayReset() {
		seqNum.clear();
	}

}
