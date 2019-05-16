package kr.ac.kaist.mms_server;

/* -------------------------------------------------------- */
/** 
File name : ErrorCode.java
	Define of Error code.
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-02
Version : 0.9.0
*/
import java.nio.charset.Charset;

public enum ErrorCode {
	UNKNOWN_ERR("99999", "INVALID MESSAGE."),
	
	UNKNOWN_MRN("10001", "No Device having that MRN."),
	NULL_SRC_MRN("10002", "Null source MRN."),
	NULL_DST_MRN("10003", "Null destination MRN."),
	NULL_MRN("10004", "Null MRNs."),
	WRONG_PARAM("10005", "Wrong parameter"),
	
	// Numbers greater than 10000 and lower than 20000 are internal error codes.
	// Number 19XXX is related to the MMS monitoring service including dump MNS.
	MONITORING_CONNECTION_ERR("19001", "Failed to connect to MMS monitoring service."),
	DUMPMNS_LOGGING_ERR("19002", "Failed to connect to dump MNS."),
	
	// Number 18XXX is related to the relaying functions.
	SEQUENTIAL_RELAYING_EXCEPTION_ERR("18001", "Exception error occured in sequentailly relaying function."),
	SEQUENTIAL_RELAYING_INITIALIZATION_ERR("18002", "Initialization problem occured in sequentailly relaying function.");
	
	
	private String code;
	private String message;
	
	ErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	public byte[] getBytes() {
		return getBytes(this.message);
	}

	public byte[] getUTF8Bytes() {
		return getUTF8Bytes(this.message);
	}
	public byte[] getBytes(String message) {
		return String.format("[%5s] %s", code, message).getBytes();
	}

	public byte[] getUTF8Bytes(String message) {
		return String.format("[%5s] %s", code, message).getBytes(Charset.forName("UTF-8"));
	}
}
