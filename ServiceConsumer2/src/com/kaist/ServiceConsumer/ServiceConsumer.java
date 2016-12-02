package com.kaist.ServiceConsumer;

public class ServiceConsumer{
	
	public static void main(String args[]) throws Exception{
		String myMRN;
		int port;
		//myMRN = args[0];
		myMRN = "urn:mrn:imo:imo-no:0100006";
		//port = Integer.parseInt(args[1]);
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
		/*
		("urn:mrn:imo:imo-no:1000007", "127.0.0.1:8901"); // SC
		("urn:mrn:imo:imo-no:0100006", "127.0.0.1:8901"); // SC2
	    ("urn:mrn:smart-navi:device:tm-server", "127.0.0.1:8902"); // SP
	    ("urn:mrn:smart-navi:device:mir1", "127.0.0.1:8903"); // MIR
	    ("urn:mrn:smart-navi:device:msr1", "127.0.0.1:8904"); // MSR
	    ("urn:mrn:smart-navi:device:mms1", "127.0.0.1:8904"); // MMS
	    ("urn:mrn:smart-navi:device:cm1", "127.0.0.1:8904"); // CM
	    */
		//simple message exchange
		/*
		String response = mh.sendMSG("urn:mrn:smart-navi:device:tm-server", "hello, SP");
		System.out.println("response from SP :" + response);
		response = mh.sendMSG("urn:mrn:smart-navi:device:mir1", "hello, MIR");
		System.out.println("response from MIR :" + response);
		response = mh.sendMSG("urn:mrn:smart-navi:device:msr1", "hello, MSR");
		System.out.println("response from MSR :" + response);
		*/
		//file transferring
		/*
		String response = mh.requestFile("urn:mrn:smart-navi:device:tm-server", "test.xml");
	    System.out.println("response from SC :" + response);
	    response = mh.sendMSG("urn:mrn:smart-navi:device:tm-server", "hello, SC");
		System.out.println("response from MSR :" + response);
		*/
		

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