package kr.ac.kaist.message_relaying.polling_auth;
/* -------------------------------------------------------- */
/** 
File name : ClientVerifier.java
	It verifies a client. 
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-10-05
Version : 0.8.0

Rev. history : 2018-10-15
Version : 0.8.0
	Resolved MAVEN dependency problems with library "net.etri.pkilib".
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-05-17
Version : 0.9.1
	Modified for classifying the reason why the authentication is failed.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-05-21
Version : 0.9.1
	Added session management of polling message authentication.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.ac.kaist.mms_server.MMSLog;
import net.etri.pkilib.server.ServerPKILibrary;
import net.etri.pkilib.tool.ByteConverter;

public class ClientVerifier {
	private static final Logger logger = LoggerFactory.getLogger(ClientVerifier.class);
	private ByteConverter byteConverter;
	private byte[] signedData;
	private ServerPKILibrary serverPKILib;
	private boolean isMatching;
	private boolean isVerified;
	private PollingSessionManager pManager;
	private final MMSLog mmsLog = MMSLog.getInstance();
	
	public ClientVerifier() {
		byteConverter = null;
		signedData = null;
		serverPKILib = null;
		isMatching = false;
		isVerified = false;
		pManager = new PollingSessionManager();
	}
	
	public boolean verifyClient (String srcMRN, String hexSignedData) {		
		PollingSessionManagerCode code = pManager.contains(srcMRN, hexSignedData);
		
		if (code == PollingSessionManagerCode.FAIL) {
//			mmsLog.addBriefLogForStatus("[ClientVerifier] Not exist session");
			authenticateUsingMIRAPI(srcMRN, hexSignedData);
			
			if(isVerified && isMatching) {
//				mmsLog.addBriefLogForStatus("[ClientVerifier] Succedd authenticaiton, add session");
				pManager.add(srcMRN, hexSignedData);
				
				return true;
			}
			
			return false;
			
		} else if (code == PollingSessionManagerCode.CONTAINED) {
//			mmsLog.addBriefLogForStatus("[ClientVerifier] Contained session");
			pManager.refresh(srcMRN, hexSignedData);
			isMatching = true;
			isVerified = true;
			
			return true;
		}
		
		return false;
	}
	
	private void authenticateUsingMIRAPI(String srcMRN, String hexSignedData) {
		serverPKILib = ServerPKILibrary.getInstance();
		
		byteConverter = ByteConverter.getInstance();
		signedData = byteConverter.hexToByteArray(hexSignedData);
		isVerified = serverPKILib.verifySignedData(signedData);
		isMatching = serverPKILib.getSubjectMRN(signedData).equals(srcMRN);
	}
	
	public boolean isMatching () {
		return isMatching;
	}
	
	public boolean isVerified () {
		return isVerified;
	}
}

