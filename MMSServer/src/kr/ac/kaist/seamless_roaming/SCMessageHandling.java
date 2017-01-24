package kr.ac.kaist.seamless_roaming;

import java.io.UnsupportedEncodingException;

import io.netty.handler.codec.http.FullHttpRequest;
import kr.ac.kaist.message_queue.MMSQueue;

public class SCMessageHandling {

	void putSCMessage(String dstMRN, FullHttpRequest req){
		try {
			MMSQueue.putMessage(dstMRN, req);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
