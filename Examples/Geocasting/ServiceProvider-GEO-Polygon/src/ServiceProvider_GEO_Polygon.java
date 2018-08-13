import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : ServiceProvider_GEO_Polygon.java
	Service Provider sends messages through geocasting.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Version : 0.7.2
Creation Date : 2018-07-27
*/
/* -------------------------------------------------------- */

public class ServiceProvider_GEO_Polygon {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:smart-navi:device:geo-server";
		int port = 8912;

		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		MMSConfiguration.DEBUG=true;
		
		//MMSClientHandler server = new MMSClientHandler(myMRN);
		MMSClientHandler sender = new MMSClientHandler(myMRN);
		sender.setSender(new MMSClientHandler.ResponseCallback() {
			//Response Callback from the request message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				System.out.println(message);
			}
		});
		
		
		// It is able to set its HTTP header field
		Map<String, List<String>> headerfield = new HashMap<String, List<String>>(); // Header field example. You are able to remove this code.
		List<String> geoType = new ArrayList<String>(); 
		geoType.add("polygon");
		headerfield.put("geocasting",geoType);
		List<String> latValue = new ArrayList<String>();
		latValue.add("1");
		latValue.add("2");
		latValue.add("3");
		latValue.add("4");
		latValue.add("1");
		headerfield.put("lat", latValue);
		List<String> longValue = new ArrayList<String>();
		longValue.add("9");
		longValue.add("7");
		longValue.add("5");
		longValue.add("3");
		longValue.add("1");
		headerfield.put("long", longValue);
		sender.setMsgHeader(headerfield);
		// Header field example ends.
		
		String dstMRN = "*";
		sender.sendPostMsg(dstMRN, "Hello Geocast");
		/*server.setServerPort(port, "/forwarding", new MMSClientHandler.RequestCallback() {
			//Request Callback from the request message
			//it is called when client receives a message
			
			@Override
			public int setResponseCode() {
				// TODO Auto-generated method stub
				return 200;
			}
			
			@Override
			public String respondToClient(Map<String,List<String>> headerField, String message) {
				try {
					Iterator<String> iter = headerField.keySet().iterator();
					while (iter.hasNext()){
						String key = iter.next();
						System.out.println(key+":"+headerField.get(key).toString());
					}
					System.out.println(message);

					//it only forwards messages to sc having urn:mrn:mcs:casting:geocast:smart:lat-1-long-1-radius-3
					//sender.sendPostMsg("urn:mrn:mcs:casting:geocast:smart:lat-1-long-1-radius-3", message);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "OK";
			}

			@Override
			public Map<String, List<String>> setResponseHeader() {
				// TODO Auto-generated method stub
				return null;
			}
			
		});*/ //server has a context '/forwarding'
		/* It is not same with:
		 * server.setPort(port); //It sets default context as '/'
		 * server.addContext("/forwarding"); //Finally server has two context '/' and '/forwarding'
		 */

	}
}
