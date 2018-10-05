package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : ClientVerifier.java
	It verifies a client. 
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-10-05
Version : 0.8.0
*/
/* -------------------------------------------------------- */

import net.etri.pkilib.server.ServerPKILibrary;
import net.etri.pkilib.tool.ByteConverter;

public class ClientVerifier {
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
