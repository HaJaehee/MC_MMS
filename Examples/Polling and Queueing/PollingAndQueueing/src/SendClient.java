import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;

import kr.ac.kaist.mms_client.MMSClientHandler;

/** 
File name : TS4_client.java
	message sequence test
Author : Youngjin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-10-13
*/
public class SendClient {
	private String myMRN;
	private String dstMRN;
	private MMSClientHandler sender;
	private int response = 0;
	String message = "\"hello\"";
	Map<String, List<String>> headerfield = new HashMap<String, List<String>>();

	public SendClient(String svcMRN, String dstMRN) throws Exception {
		this.myMRN = svcMRN;
		this.dstMRN = dstMRN;
		sender = new MMSClientHandler(myMRN);
		sender.setSender(new MMSClientHandler.ResponseCallback() {

			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				if (headerField.get("Response-code") != null) {
					int code = Integer.parseInt(headerField.get("Response-code").get(0));
					response = code;
				}

			}
		});

	}

	public void Send(int delay) throws Exception {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				new MsgSenderThread(myMRN, headerfield, dstMRN, message).start();
			}
		}, delay, delay);
	}
}
