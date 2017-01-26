import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

public class ServiceProvider {
	public static void main(String args[]) throws Exception{
		String myMRN;
		int port;
		myMRN = "urn:mrn:smart-navi:device:tm-server";
		port = 8902;
		
		MMSConfiguration.MMSURL="127.0.0.1:8088";
		MMSConfiguration.CMURL="127.0.0.1";
		
		MMSClientHandler mh = new MMSClientHandler(myMRN);
		mh.setMSP(port);
		mh.setReqCallBack(new MMSClientHandler.ReqCallBack() {
			@Override
			public String callbackMethod(String message) {
				try {
					mh.sendPostMsg("urn:mrn:imo:imo-no:0100006", message);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "OK";
			}
		});
	}
}
