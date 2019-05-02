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
	WRONG_PARAM("10004", "Null MRNs."),
	NULL_MRN("10005", "Wrong parameter");
	
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
		return String.format("[%5s] %s", code, message).getBytes();
	}

	public byte[] getUTF8Bytes() {
		return String.format("[%5s] %s", code, message).getBytes(Charset.forName("UTF-8"));
	}
}
