package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : geolocationInfo.java
	It contains geolocation information for polygon area.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-07-27
Version : 0.7.2
**/
/* -------------------------------------------------------- */

public class GeolocationPolygonInfo {

	private float[] geoLatList = null;
	private float[] geoLongList = null;
	
	public float[] getGeoLatList() {
		return geoLatList;
	}
	public void setGeoLatList(float[] geoLatList) {
		this.geoLatList = geoLatList;
	}
	public float[] getGeoLongList() {
		return geoLongList;
	}
	public void setGeoLongList(float[] geoLongList) {
		this.geoLongList = geoLongList;
	}
	
}
