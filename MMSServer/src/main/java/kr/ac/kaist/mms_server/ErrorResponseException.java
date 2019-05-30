package kr.ac.kaist.mms_server;

import io.netty.channel.ChannelHandlerContext;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;

public class ErrorResponseException extends MMSBaseException {
	private byte[] message;
	private int responseCode;
	private boolean realtimeLog;
	public ErrorResponseException() {
		this(null, 400);
	}
	public ErrorResponseException(byte[] message, int responseCode, boolean realtimeLog) {
		this.message = message;
		this.responseCode = responseCode;
		this.realtimeLog = realtimeLog;
	}

	public ErrorResponseException(byte[] message, int responseCode) {
		this(message, responseCode, false);
	}
	public ErrorResponseException(StringBuffer message) {
		this(message.toString().getBytes(), 400);
	}
	public ErrorResponseException(String message) {
		this(message.getBytes());
	}
	public ErrorResponseException(byte[] message) {
		this(message, 400);
	}
	public ErrorResponseException(int responseCode) {
		this(null, responseCode);
	}
	public void replyToSender(MRH_MessageOutputChannel outputChannel, ChannelHandlerContext ctx) {
		outputChannel.replyToSender(ctx, message, realtimeLog, responseCode);
	}
}
