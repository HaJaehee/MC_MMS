package kr.ac.kaist.mms_server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;

public class ErrorResponseException extends MMSBaseException {
	private static final Logger logger = LoggerFactory.getLogger(ErrorResponseException.class);

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
	public void replyToSender(MRH_MessageInputChannel.ChannelBean bean) {
		try {
			bean.getOutputChannel().replyToSender(bean.getCtx(), message, realtimeLog, responseCode);
		} catch (IOException e) {
			MMSLog.getInstance().infoException(logger, "", ErrorCode.CLIENT_DISCONNECTED.toString(), e, 5);
		}
	}
}
