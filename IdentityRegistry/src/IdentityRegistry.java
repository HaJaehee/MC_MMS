import com.kaist.MMSClient.MMSClientHandler;
import com.kaist.MMSClient.MMSConfiguration;

public class IdentityRegistry{
	
	public static void main(String args[]) throws Exception{
		String myMRN;
		int port;
		//myMRN = args[0];
		myMRN = "urn:mrn:smart-navi:device:mir1";
		//port = Integer.parseInt(args[1]);
		port = 8904;
		
		//MMSConfiguration.MMSURL="127.0.0.1:8088";
		//MMSConfiguration.CMURL="127.0.0.1";
		
		MMSClientHandler mh = new MMSClientHandler(myMRN);
		mh.setMIR(port);
		//Request Callback from the request message
		mh.setReqCallBack(new MMSClientHandler.reqCallBack() {
			@Override
			public String callbackMethod(String message) {
				System.out.println(message);
				return "OK";
			}
		});
		//String response = mh.sendMSG("mrn:kor:123124", "hello");
		//System.out.println("response:" + response);
	}
	
		
		
}
