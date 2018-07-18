package kr.ac.kaist.message_relaying;

import java.io.IOException;

public class MessageOrderException extends IOException {
	public MessageOrderException (String message) {
		super (message);
	}
}
