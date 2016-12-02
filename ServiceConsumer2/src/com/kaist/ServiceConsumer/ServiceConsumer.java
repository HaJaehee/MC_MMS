package com.kaist.ServiceConsumer;

public class ServiceConsumer{
	
	public static void main(String args[]) throws Exception{
		String myMRN;
		int port;
		//myMRN = args[0];
		myMRN = "mrn:kor:123124";
		//port = Integer.parseInt(args[1]);
		port = 8902;
		MMSClientHandler mh = new MMSClientHandler(myMRN, port);
		//Request Callback from the request message
		mh.setReqCallBack(new MMSClientHandler.reqCallBack() {
			@Override
			public String callbackMethod(String message) {
				System.out.println(message);
				return "OK";
			}
		});
		/*
		("mrn:kor:123123", "127.0.0.1:8901"); // SC
	    ("mrn:kor:123124", "127.0.0.1:8902"); // SP
	    ("mrn:kor:123125", "127.0.0.1:8903"); // MIR
	    ("mrn:kor:123126", "127.0.0.1:8904"); // MSR
	    */
		/*
		String response = mh.sendMSG("mrn:kor:123124", "hello, SP");
		System.out.println("response from SP :" + response);
		response = mh.sendMSG("mrn:kor:123125", "hello, MIR");
		System.out.println("response from MIR :" + response);
		response = mh.sendMSG("mrn:kor:123126", "hello, MSR");
		System.out.println("response from MSR :" + response);
		*/
	}		
}