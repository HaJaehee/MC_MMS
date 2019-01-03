package kr.ac.kaist.message_casting;
/* -------------------------------------------------------- */
/** 
File name : GeolocationCircleInfo.java
	It contains geolocation information for circle area.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-07-27
Version : 0.7.2
**/
/* -------------------------------------------------------- */

public class GeolocationCircleInfo {
	private float geoLat = 0;
	private float geoLong = 0;
	private float geoRadius = 0;
	
	public GeolocationCircleInfo() {
		
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
