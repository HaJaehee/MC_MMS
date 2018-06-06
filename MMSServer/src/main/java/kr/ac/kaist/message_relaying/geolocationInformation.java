package kr.ac.kaist.message_relaying;
/* -------------------------------------------------------- */
/** 
File name : geolocationInformation.java
	It contains geolocation information.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-06-06
Version : 0.7.1
**/
/* -------------------------------------------------------- */

public class geolocationInformation {
	private float geoLat = 0;
	private float geoLong = 0;
	private float geoRadius = 0;
	
	public geolocationInformation() {
		// TODO Auto-generated constructor stub
		geoLat = 0;
		geoLong = 0;
		geoRadius = 0;
	}
	
	public float getGeoLat() {
		return geoLat;
	}
	public void setGeoLat(float geoLat) {
		this.geoLat = geoLat;
	}
	public float getGeoLong() {
		return geoLong;
	}
	public void setGeoLong(float geoLong) {
		this.geoLong = geoLong;
	}
	public float getGeoRadius() {
		return geoRadius;
	}
	public void setGeoRadius(float geoRadius) {
		this.geoRadius = geoRadius;
	}

}
