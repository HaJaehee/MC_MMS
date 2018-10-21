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
File name : SP_GEO.java
	Service Provider sends messages through geocasting.
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Version : 0.8.0
Creation Date : 2018-10-21
*/
/* -------------------------------------------------------- */

public class SP_GEO {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:mcp:service:instance:sp-geo";
		int port = 9965;

		MMSConfiguration.MMS_URL="192.168.202.193:8088";
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
		
		String dstMRN = "*";
		
		/* For geocasting-cirdcle */
		Map<String, List<String>> headerfield = new HashMap<String, List<String>>();
		List<String> geoType = new ArrayList<String>(); 
		geoType.add("circle");
		headerfield.put("geocasting",geoType);
		List<String> latValue = new ArrayList<String>();
		latValue.add("33.862177");
		headerfield.put("lat", latValue);
		List<String> longValue = new ArrayList<String>();
		longValue.add("126.348151");
		headerfield.put("long", longValue);
		List<String> radiusValue = new ArrayList<String>(); 
		radiusValue.add("30.0");
		headerfield.put("radius",radiusValue);
		sender.setMsgHeader(headerfield);
		
		sender.sendPostMsg(dstMRN, "Hello, this message is sent as geocasting circle");

		
		/* For geocasting-polygon */
		headerfield = new HashMap<String, List<String>>(); // Header field example. You are able to remove this code.
		geoType = new ArrayList<String>(); 
		geoType.add("polygon");
		headerfield.put("geocasting",geoType);
		latValue = new ArrayList<String>();
		latValue.add("33.562177");
		latValue.add("33.559825");
		latValue.add("33.385769");
		latValue.add("33.390355");
		headerfield.put("Lat", latValue);
		longValue = new ArrayList<String>();
		longValue.add("126.848151");
		longValue.add("127.092597");
		longValue.add("127.077491");
		longValue.add("126.878363");
		headerfield.put("Long", longValue);
		sender.setMsgHeader(headerfield);
		
		sender.sendPostMsg(dstMRN, "Hello, this message is sent as geocasting polygon");
	}
}
