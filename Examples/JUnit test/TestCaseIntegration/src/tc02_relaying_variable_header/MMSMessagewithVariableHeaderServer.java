package tc02_relaying_variable_header;
/** 
File name : TS2_server.java
	Relaying message function for the purpose of testing MMS
Author : YoungJin Kim (jcdad3000@kaist.ac.kr)
Creation Date : 2018-09-13

Rev. history : 2019-05-17
Version : 0.9.1
	Running this test case with version 0.9.1 and 
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-06-13
Version : 0.9.2
	Change the class name from TS2_Test to MMSMessagewithVariableHeaderServer
	Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;

public class MMSMessagewithVariableHeaderServer {
	private int response = 0;
	private String myMRN = "urn:mrn:imo:imo-no:ts-mms-02-server";
	private String dstMRN = "urn:mrn:imo:imo-no:ts-mms-02-client";
	private MMSClientHandler myHandler = null;
	private static int content_length = 0;
	private static int length = -1;
	String Dstmrn= null;
	String Srcmrn=null;
	String Consumer_key =null;
	String Token=null;
	String Signature_method=null;
	String Signature=null;
	String Timestamp=null;
	String Nonce=null;
	String Version=null;
	String Msg=null;
	String Realm=null;
	
	public MMSMessagewithVariableHeaderServer() throws Exception {
		myHandler = new MMSClientHandler(myMRN);
		int port = 8907;
		
		myHandler.setServerPort(port, new MMSClientHandler.RequestCallback() {
			
			@Override
			public Map<String, List<String>> setResponseHeader() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int setResponseCode() {
				// TODO Auto-generated method stub
				return 200;
			}
			
			@Override
			public String respondToClient(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				
				compareHeaderfield(headerField);
				
				
				return "OK";			
			}
		});
	}
	
	public static int getContentLength() {
		return content_length;
	}
	
	public void compareHeaderfield(Map<String,List<String>> headerField) {
		try {
			Iterator<String> iter = headerField.keySet().iterator();
			while (iter.hasNext()){
				String key = iter.next();
				//System.out.println("key : "+key);
				if(key.equals("Dstmrn")) 
				Dstmrn=headerField.get(key).toString();				
			
				else if(key.equals("Srcmrn"))
					Srcmrn=headerField.get(key).toString();
				else if(key.equals("Consumer_key"))
					Consumer_key=headerField.get(key).toString();
				else if(key.equals("Token"))
					Token=headerField.get(key).toString();
				else if(key.equals("Signature_method"))
					Signature_method=headerField.get(key).toString();
				else if(key.equals("Signature"))
					Signature=headerField.get(key).toString();
				else if(key.equals("Timestamp"))
					Timestamp=headerField.get(key).toString();
				else if(key.equals("Nonce"))
					Nonce=headerField.get(key).toString();
				else if(key.equals("Version"))
					Version=headerField.get(key).toString();
				else if(key.equals("Msg"))
					Msg=headerField.get(key).toString();
				else if(key.equals("Realm"))
					Realm=headerField.get(key).toString();
				
				System.out.println(key+":"+headerField.get(key).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println();
		
	}
	public String getdstMRN() {		
		return Dstmrn;
	}
	public String getsrcMRN() {
		return Srcmrn;
	}
	public String getconsumerkey() {
		return Consumer_key;
	}
	public String gettoken() {
		return Token;
	}
	public String getsignaturemethod() {
		return Signature_method;
	}
	public String getsignature() {
		return Signature;
	}
	public String gettimestamp() {
		return Timestamp;
	}
	public String getnonce() {
		return Nonce;
	}
	public String getversion() {
		return Version;
	}
	public String getmsg() {
		return Msg;
	}
	public String getrealm() {
		return Realm;
	}
	public void terminateServer() {
		myHandler.terminateServer();
	}
}
