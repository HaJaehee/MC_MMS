import kr.ac.kaist.mms_client.MMSConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kr.ac.kaist.mms_client.MMSClientHandler;

/** 
File name : TS4_server.java
	message sequence test
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-10-13
*/

public class TS4_server {
	String myMRN = "urn:mrn:imo:imo-no:ts-mms-04-server";
	String dstMRN = "urn:mrn:imo:imo-no:ts-mms-04-client";
	ArrayList<Integer> seqNum = new ArrayList();

	public TS4_server() throws NullPointerException, IOException {
		MMSConfiguration.MMS_URL = "mms-kaist.com:8088";

		MMSClientHandler server = new MMSClientHandler(myMRN);
		int port = 8902;

		server.setServerPort(port, new MMSClientHandler.RequestCallback() {

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
