package kr.ac.kaist.seamless_roaming;
/* -------------------------------------------------------- */
/** 
File name : DupInfoRefCntAndChannelBean.java
	It has a reference count and a channel bean.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2019-09-25
Version : 0.9.5

**/
/* -------------------------------------------------------- */

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
