package kr.ac.kaist.message_relaying.polling_auth;

import java.nio.charset.Charset;

@Deprecated
public class ClientVerifierTest extends ClientVerifier {
	public ClientVerifierTest(String sessionId) {
		super(sessionId);
		
	}

	private long start_time;
	private long end_time;
	
	@Override
	public boolean verifyClient(String srcMRN, String hexSignedData) {
		// TODO Auto-generated method stub
		boolean result = false;
		
		start_time = System.currentTimeMillis();
		result = super.verifyClient(srcMRN, hexSignedData);
		end_time = System.currentTimeMillis();
		
		return result;
	}
	
	public long getVerificationTime() {
		return end_time - start_time;
	}
	
	public byte[] verificationTimeJSONString(){
		String msg = "[\""+getVerificationTime()+"\"]";
		return msg.getBytes(Charset.forName("UTF-8"));
	}
}
