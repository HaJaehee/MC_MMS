package kr.ac.kaist.seamless_roaming;

import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;

class DupInfoRefCntAndChannelBean { 
	private int refCnt;
	private MRH_MessageInputChannel.ChannelBean bean;
	DupInfoRefCntAndChannelBean(MRH_MessageInputChannel.ChannelBean bean) {
		refCnt = 0;
		this.bean = bean;
	}
	public int getRefCnt() {
		return refCnt;
	}
	public void setRefCnt(int refCnt) {
		this.refCnt = refCnt;
	}
	public MRH_MessageInputChannel.ChannelBean getBean() {
		return bean;
	}
}
