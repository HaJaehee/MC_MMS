package kr.ac.kaist.message_casting;
/* -------------------------------------------------------- */
/** 
File name : geolocationInfo.java
	It contains geolocation information for polygon area.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-07-27
Version : 0.7.2

Rev. history: 2019-04-12
Version : 0.8.2
	Modified for coding rule conformity.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
**/
/* -------------------------------------------------------- */

public class GeolocationPolygonInfo {

	private float[] geoLatList = null;
	private float[] geoLongList = null;
	
	public float[] getGeoLatList() {
		float[] ret = null;
		if (this.geoLatList != null) {
			ret = new float[this.geoLatList.length];
			for (int i = 0; i < this.geoLatList.length ; i++) {
				ret[i] = this.geoLatList[i];
			}
		}
		return ret;
	}
	public void setGeoLatList(float[] geoLatList) {
		this.geoLatList = new float[geoLatList.length];
		for (int i = 0; i < geoLatList.length ; i++) {
			this.geoLatList[i] = geoLatList[i];
		}
	}
	public float[] getGeoLongList() {
		float[] ret = null;
		if (this.geoLongList != null) {
			ret = new float[this.geoLongList.length];
			for (int i = 0; i < this.geoLongList.length ; i++) {
				ret[i] = this.geoLongList[i];
			}
		}
		return ret;
	}
	public void setGeoLongList(float[] geoLongList) {
		this.geoLongList = new float[geoLongList.length];
		for (int i = 0; i < geoLongList.length ; i++) {
			this.geoLongList[i] = geoLongList[i];
		}
	}
	
}
