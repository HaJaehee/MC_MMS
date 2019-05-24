import java.io.IOException;
import java.sql.Timestamp;
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
	private String myMRN;
	
	public MsgSenderThread(String aMyMRN, Map<String, List<String>> aHeaderfield, String aDstMRN, String aMessage) {
		// TODO Auto-generated constructor stub
		super();
		myMRN = aMyMRN;
		headerfield = aHeaderfield;
		dstMRN = aDstMRN;
		message = aMessage;
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
					QueuingMain.printStampMessage("queueing message complete.");
				}
				
			});

			
			//sender.sendPostMsg(dstMRN, message);
			sender.sendPostMsg(dstMRN, message);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
