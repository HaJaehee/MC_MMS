package com.kaist.IdentityRegistry;

public class IdentityRegistry{
	
	public static void main(String args[]) throws Exception{
		String myMRN;
		int port;
		//myMRN = args[0];
		myMRN = "mrn:kor:123125";
		//port = Integer.parseInt(args[1]);
		port = 8903;
		MMSClientHandler mh = new MMSClientHandler(myMRN, port);
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
