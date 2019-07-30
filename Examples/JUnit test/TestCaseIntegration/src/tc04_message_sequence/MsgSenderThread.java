package tc04_message_sequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;

public class MsgSenderThread extends Thread{
	
	private String dstMRN;
	private String message;
	private Map<String, List<String>> headerfield;
	private int seqNum;
	private String myMRN;
	
	public MsgSenderThread(String aMyMRN, Map<String, List<String>> aHeaderfield, String aDstMRN, String aMessage, int aSeqNum) {
		// TODO Auto-generated constructor stub
		super();
		myMRN = aMyMRN;
		headerfield = aHeaderfield;
		dstMRN = aDstMRN;
		message = aMessage;
		seqNum = aSeqNum;
	}
	
	@Override
	public void run() {
		try {
			//Service Consumer which can only send message
			MMSClientHandler sender = new MMSClientHandler(myMRN);
			sender.setMsgHeader(headerfield); 
			//System.out.println(this.message);
			// Sender example.
			sender.setSender(new MMSClientHandler.ResponseCallback (){
				// callbackMethod is called when the response message arrives which is related to request message.
				@Override
				public void callbackMethod(Map<String, List<String>> headerField, String message) { // headerField and message of the response message.
					// TODO Auto-generated method stub
					Iterator<String> iter = headerField.keySet().iterator();
					while (iter.hasNext()){
						String key = iter.next();
						//System.out.println(key+":"+headerField.get(key).toString());// Print the matched header field and the header contents.
					}
					System.out.println(message);
				}
				
			});

			
			//sender.sendPostMsg(dstMRN, message);
			sender.sendPostMsg(dstMRN, message, seqNum, 10000);

		}
		catch (Exception e) {
			System.out.println("Message out of order is dropped.");
		}
	}
	
	
}
