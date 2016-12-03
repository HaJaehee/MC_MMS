import java.util.Scanner;

import com.kaist.ServiceConsumer.MMSClientHandler;

public class SC2 {
	public static void main(String args[]) throws Exception{
		String myMRN;
		//myMRN = args[0];
		myMRN = "urn:mrn:imo:imo-no:1000007";

		//Service Consumer which can only send message
		MMSClientHandler mh = new MMSClientHandler(myMRN);
		//Request Callback from the request message
		mh.setReqCallBack(new MMSClientHandler.reqCallBack() {
			@Override
			public String callbackMethod(String message) {
				System.out.println(message);
				return "OK";
			}
		});
		for (int i = 0; i < 10;i++){
			String a = mh.sendMSG("urn:mrn:smart-navi:device:tm-server", "hi hi hello " + i);
			Thread.sleep(100);
		}
	}
}
