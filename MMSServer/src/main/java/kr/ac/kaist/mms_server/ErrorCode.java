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

Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */


import java.nio.charset.Charset;

public enum ErrorCode {
	UNKNOWN_ERR("99999", "INVALID MESSAGE."),
	
	// Number 10XXX is related to message format and authentication.
	UNKNOWN_MRN("10001", "No Device having that MRN."),
	NULL_SRC_MRN("10002", "Null source MRN."),
	NULL_DST_MRN("10003", "Null destination MRN."),
	NULL_MRN("10004", "Null MRNs."),
	WRONG_PARAM("10005", "Wrong parameter."),
	NOT_EXIST_REALTIME_LOG_CONSUMER("10006", "The ID does not exist in realtime log service consumer IDs."),
	NULL_CERTIFICATE("10007", "The certificate is not included."),
	NULL_SVC_MRN("10008", "The service MRN is not included."),
	@Deprecated
	AUTHENTICATION_FAIL_REVOKED("10008", "It is failed to verify the client. The certificate has been revoked."),
	JSON_FORMAT_ERROR("10009", "The message is not formatted by JSON."),
	@Deprecated
	AUTHENTICATION_FAIL_NOTMATCHING("10010", "It is failed to verify the client. The source MRN is not equal to the certificate's."),
	DUPLICATE_POLLING("10011", "The polling request is already received. Duplicate request is not accepted."),
	AUTHENTICATE_FAIL("10012", "Authentication is failed."),
	MESSAGE_PARSING_ERROR("10013", "Message parsing error."),
	
	// Numbers greater than 10000 and lower than 20000 are internal error codes.
	// Number 11XXX is related to configuration of MMS.
	CONFIGURATION_ERROR("11001","MMS Configuration error."),

	// Number 18XXX is related to the relaying functions.
	SEQUENTIAL_RELAYING_EXCEPTION_ERR("18001", "Exception error occured in sequentailly relaying function."),
	SEQUENTIAL_RELAYING_INITIALIZATION_ERR("18002", "Initialization problem occured in sequentailly relaying function."),
	SEQUENCE_NUMBER_IS_DUPLICATED("18003","Sequence number of message is duplicated."),
	SEQUENCE_NUMBER_IS_OUT_OF_ORDER("18004","Sequence number of message is out of order."),
	SEQUENTIAL_RELAYING_NULL_POINTER_EXCEPTION("18005", "Null pointer exception error occured in sequentailly relaying function."),
	SEQUENTIAL_RELAYING_LIST_EMTPY("18006", "Empty message list error occured in sequentailly relaying function."),
	MESSAGE_RELAYING_FAIL_UNREACHABLE("18007","Destination host is unrecheable."),
	MESSAGE_RELAYING_FAIL_DISCONNECT("18008","Destination host is disconnected."),
	CLIENT_DISCONNECTED("18009","MMS Client is disconnected."),
	POLLING_CLIENT_DISCONNECTED("18010","MMS Polling Client is disconnected."),
	LONG_POLLING_CLIENT_DISCONNECTED("18011","MMS Long Polling Client is disconnected."),
	SEQUENCE_NUMBER_IS_NEGATIVE("18012","Sequence number of message is negative."),
	WRONG_GEOCASTING_INFO("18013","Failed to parse geolocation info."),
	
	
	// Numbers greater than 20000 and lower than 30000 are external error codes.
	// Number 20XXX is related to interface between MMS Server and Rabbit MQ.
	RABBITMQ_CHANNEL_CLOSE_ERROR("20001","Rabbit MQ channel close error."),
	RABBITMQ_CHANNEL_OPEN_ERROR("20002","Rabbit MQ channel open error."),
	RABBITMQ_CONNECTION_CLOSE_ERROR("20003","Rabbit MQ connection close error."),
	RABBITMQ_CONNECTION_OPEN_ERROR("20004","Rabbit MQ connection open error."),
	RABBITMQ_MANAGEMENT_CONNECTION_OPEN_ERROR("20005","Rabbit MQ management plugin connection open error."),
	
	// Number 21XXX is related to interface between MMS Server and MNS.
	MNS_CONNECTION_CLOSE_ERROR("21001","MNS connection close error."),
	MNS_CONNECTION_OPEN_ERROR("21002","MNS connection open error."),
	MNS_WRONG_FORMAT_ERROR("21003","MNS response message is wrong."),
	@Deprecated
	DUMPMNS_LOGGING_ERROR("21004", "Failed to dump MNS."),
	
	// Number 22XXX is related to the MMS monitoring service including dump MNS.
	MONITORING_CONNECTION_ERR("22001", "Connection to MMS monitoring service error."),
	

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
