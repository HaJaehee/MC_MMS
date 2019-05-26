package kr.ac.kaist.message_relaying.polling_auth;

/** 
File name : PollingSessionManagerCode.java
	This class is used for polling message authentication to be more faster.
	When if a polling message is come and the message was already authenticated before,
	This class makes the message not be checked by trusted third party such as MIR and 
	the message is considered as a authenticated message.
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-05-21
Version : 0.9.1

*/

@Deprecated
public enum PollingSessionManagerCode {
	OK(0),
	ADDED(1), 
	FAIL(-1), 
	CONTAINED(2),
	DELETED(3),
	REFLESHED(4);
	
	private int code;
	
	PollingSessionManagerCode(int code) {
		this.code = code;
	}
	
	public int getCode(){
		return code;
	}
}