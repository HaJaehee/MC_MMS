package kr.ac.kaist.seamless_roaming;
/* -------------------------------------------------------- */
/** 
File name : PollingMethodRegDummy.java
	It is a dummy of a registry saving polling methods which are related to services.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-07-28
Version : 0.5.9

*/
/* -------------------------------------------------------- */

import java.util.HashMap;
import java.util.Map;

public class PollingMethodRegDummy {
	
	public static final int NORMAL_POLLING = 1;
	public static final int LONG_POLLING = 2;
	
	public static Map<String, Integer> pollingMethodReg = new HashMap<String, Integer>();
	
}
