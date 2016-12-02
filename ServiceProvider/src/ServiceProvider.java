import com.kaist.ServiceConsumer.MMSClientHandler;

public class ServiceProvider {
	public static void main(String args[]) throws Exception{
		String myMRN;
		int port;
		myMRN = "urn:mrn:smart-navi:device:tm-server";
		port = 8902;
		MMSClientHandler mh = new MMSClientHandler(myMRN);
		mh.setPort(port);
		mh.setReqCallBack(new MMSClientHandler.reqCallBack() {
			@Override
			public String callbackMethod(String message) {
				try {
					mh.sendMSG("urn:mrn:imo:imo-no:0100006", message);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "OK";
			}
		});
	}
}
