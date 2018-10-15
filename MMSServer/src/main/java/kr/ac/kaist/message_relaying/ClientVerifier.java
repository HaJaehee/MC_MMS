package kr.ac.kaist.message_relaying;
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
*/
/* -------------------------------------------------------- */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.etri.pkilib.server.ServerPKILibrary;
import net.etri.pkilib.tool.ByteConverter;

public class ClientVerifier {
	private static final Logger logger = LoggerFactory.getLogger(ClientVerifier.class);
	private ByteConverter byteConverter = null;
	private byte[] signedData = null;
	private ServerPKILibrary serverPKILib = null;
	
	public boolean verifyClient (String srcMRN, String hexSignedData) {
		
		boolean verifyResult = false;
		
		serverPKILib = ServerPKILibrary.getInstance();
		
		byteConverter = ByteConverter.getInstance();
		signedData = byteConverter.hexToByteArray(hexSignedData);
		verifyResult = serverPKILib.verifySignedData(signedData);
		if(verifyResult && serverPKILib.getSubjectMRN(signedData).equals(srcMRN)) {
			return true;
		}
		
		return false;
	}
}
