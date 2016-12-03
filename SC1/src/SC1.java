import com.kaist.ServiceConsumer.MMSClientHandler;

public class SC1 {
	public static void main(String args[]) throws Exception{
		String myMRN;
		int port;
		//myMRN = args[0];
		myMRN = "urn:mrn:imo:imo-no:0100006";
		
		//Service Consumer can be HTTP server and listen to port 'port'. 
		//port = Integer.parseInt(args[1]);
		/*
		port = 8904;
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
		*/

		//Service Consumer cannot be HTTP server and should poll from MMS. 
		MMSClientHandler ph = new MMSClientHandler(myMRN);
		ph.setPolling("urn:mrn:smart-navi:device:mms1",1000);
		//Request Callback from the request message
		ph.setReqCallBack(new MMSClientHandler.reqCallBack() {
			@Override
			public String callbackMethod(String message) {
				System.out.println(message);
				return "OK";
			}
		});
	}
}
