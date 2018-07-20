package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : SessionList.java
	SessionList stores session information in list structure.
	SessionList support "synchronized" methods for multi-thread safety.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-07-20
Version : 0.7.2
*/
/* -------------------------------------------------------- */
import java.util.ArrayList;

public class  SessionList<SessionAndThr> extends ArrayList<SessionAndThr>{

	@Override
	public void add(int arg0, SessionAndThr arg1) {
		
		synchronized(this) {
			super.add(arg0, (SessionAndThr) arg1);
		}
	}

	@Override
	public boolean add(SessionAndThr arg0) {
		
		synchronized(this) {
			return super.add((SessionAndThr) arg0);
		}
	}

	@Override
	public void clear() {
		
		synchronized(this) {
			super.clear();
		}
	}

	@Override
	public SessionAndThr remove(int arg0) {
		
		synchronized(this) {
			return super.remove(arg0);
		}
	}

	@Override
	public boolean remove(Object arg0) {
	
		synchronized(this) {
			return super.remove(arg0);
		}
	}

	@Override
	public int size() {
		synchronized(this) {
			return super.size();
		}
	}

	@Override
	public SessionAndThr get(int index) {
		synchronized (this) {
			return super.get(index);
		}
	}
	
	
	
}
