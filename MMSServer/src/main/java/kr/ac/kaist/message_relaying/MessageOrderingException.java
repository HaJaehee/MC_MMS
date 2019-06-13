package kr.ac.kaist.message_relaying;

import java.io.IOException;

public class MessageOrderingException extends IOException {
	public MessageOrderingException (String message) {
		super (message);
	}
}
