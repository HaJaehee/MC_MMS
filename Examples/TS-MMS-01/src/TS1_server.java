import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

/** 
File name : TS1_server.java
	Relaying message function for the purpose of testing MMS
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2017-07-23
*/

public class TS1_server {
	public static void main(String[] args) throws Exception{
		String myMRN = "urn:mrn:imo:imo-no:ts-mms-01-server";

		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		
		MMSClientHandler server = new MMSClientHandler(myMRN);
		int port = 8907;
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
				try {
					Iterator<String> iter = headerField.keySet().iterator();
					while (iter.hasNext()){
						String key = iter.next();
						System.out.println(key+":"+headerField.get(key).toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println();
				
				return "OK";
			}
		});

//		fileServer.setFileServerPort(port, fileDirectory, fileName);
//		fileServer.addFileContext(fileDirectory, "fullpaper.txt");
//		fileServer.addFileContext(fileDirectory, "imagezip.txt");
//		fileServer.addFileContext(fileDirectory, "maxlogsize.txt");
//		fileServer.addFileContext(fileDirectory, "mp3file.txt");
//		fileServer.addFileContext(fileDirectory, "originalmedia.txt");
//		fileServer.addFileContext(fileDirectory, "pdfguide.txt");
//		fileServer.addFileContext(fileDirectory, "VDESbps.txt");
//		fileServer.addFileContext(fileDirectory, "VoLTEbps.txt");
//		
//		String data = "";
		
	}
}
