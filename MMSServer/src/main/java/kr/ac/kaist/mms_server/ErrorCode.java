package kr.ac.kaist.mms_server;

/* -------------------------------------------------------- */
/** 
File name : ErrorCode.java
	Define of Error code.
Author : Yunho Choi (choiking10@kaist.ac.kr)
Creation Date : 2019-05-02
Version : 0.9.0

Rev. history : 2019-05-17
Version : 0.9.1
	Add error codes related to polling authentication message.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2019-05-26
Version : 0.9.1
	Make error code to be general.
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)


Rev. history : 2019-05-27
Version : 0.9.1
	Simplified logger.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-14
Version : 0.9.2
	Added error cases for sequential relaying message.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */


import java.nio.charset.Charset;

public enum ErrorCode {
	UNKNOWN_ERR("99999", "INVALID MESSAGE."),
	
	UNKNOWN_MRN("10001", "No Device having that MRN."),
	NULL_SRC_MRN("10002", "Null source MRN."),
	NULL_DST_MRN("10003", "Null destination MRN."),
	NULL_MRN("10004", "Null MRNs."),
	WRONG_PARAM("10005", "Wrong parameter."),
	NOT_EXIST_REALTIME_LOG_CONSUMER("10006", "The ID does not exist in realtime log service consumer IDs."),

	NULL_CERTIFICATE("10006", "The certificate is not included."),
	NULL_SVC_MRN("10007", "The service MRN is not included."),
	
	@Deprecated
	AUTHENTICATION_FAIL_REVOKED("10008", "It is failed to verify the client. The certificate has been revoked."),
	JSON_FORMAT_ERR("10009", "The message is not formatted by JSON."),
	
	@Deprecated
	AUTHENTICATION_FAIL_NOTMATCHING("10010", "It is failed to verify the client. The source MRN is not equal to the certificate's."),
	
	DUPLICATE_LONG_POLLING("10011", "The long polling request is already received. Duplicate request is not accepted."),

	AUTHENTICATE_FAIL("10012", "Authentication is failed."),
	
	// Numbers greater than 10000 and lower than 20000 are internal error codes.
	// Number 19XXX is related to the MMS monitoring service including dump MNS.
	MONITORING_CONNECTION_ERR("19001", "Failed to connect to MMS monitoring service."),
	DUMPMNS_LOGGING_ERR("19002", "Failed to dump MNS."),

	// Number 18XXX is related to the relaying functions.
	SEQUENTIAL_RELAYING_EXCEPTION_ERR("18001", "Exception error occured in sequentailly relaying function."),
	SEQUENTIAL_RELAYING_INITIALIZATION_ERR("18002", "Initialization problem occured in sequentailly relaying function."),
	SEQUENCE_NUMBER_IS_DUPLICATED("18003","Sequence number of message is duplicated."),
	SEQUENCE_NUMBER_IS_OUT_OF_ORDERED("18004","Sequence number of message is out of ordered."),
	SEQUENTIAL_RELAYING_NULL_POINTER_EXCEPTION("18005", "Null pointer exception error occured in sequentailly relaying function."),
	SEQUENTIAL_RELAYING_LIST_EMTPY("18006", "Empty message list error occured in sequentailly relaying function.")
	;
	
	
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
	public byte[] getJSONFormattedBytes() {
		String msg = "[\""+toString()+"\"]";
		return msg.getBytes();
	}

	public byte[] getJSONFormattedUTF8Bytes() {
		String msg = "[\""+toString()+"\"]";
		return msg.getBytes(Charset.forName("UTF-8"));
	}
	public byte[] getBytes(String message) {
		return toString().getBytes();
	}

	public byte[] getUTF8Bytes(String message) {
		return toString().getBytes(Charset.forName("UTF-8"));
	}
	public String toString() {
		return String.format("[%5s] %s", code, message);
	}
}
