import com.kaist.ServiceConsumer.MMSClientHandler;

public class SC2 {
	public static void main(String args[]) throws Exception{
		String myMRN;
		int port;
		//myMRN = args[0];
		myMRN = "urn:mrn:imo:imo-no:1000007";
		//port = Integer.parseInt(args[1]);
		port = 8903;
		MMSClientHandler mh = new MMSClientHandler(myMRN);
		mh.setPort(port);
		//Request Callback from the request message
		mh.setReqCallBack(new MMSClientHandler.reqCallBack() {
			@Override
			public String callbackMethod(String message) {
				System.out.println(message);
				return "OK";
			}
		});
		String a = mh.sendMSG("urn:mrn:smart-navi:device:tm-server", "hi hi hello");
		
	}
}
