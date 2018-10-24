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
		int port = 8903;

		MMSConfiguration.MMS_URL="211.43.202.193:8088";
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
		String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<SV10_Accident:DataSet xmlns:SV10_Accident=\"http://www.iho.int/SV10_Accident/gml/1.0\"\r\n" + 
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
				"	xmlns:gml=\"http://www.opengis.net/gml/3.2\"\r\n" + 
				"	xmlns:S100=\"http://www.iho.int/s100gml/1.0\"\r\n" + 
				"	xmlns:xlink=\"http://www.w3.org/1999/xlink\" gml:id=\"SV10_Accident,1.2\">\r\n" + 
				"	<gml:boundedBy>\r\n" + 
				"		<gml:Envelope srsName=\"EPSG:4326\">\r\n" + 
				"			<gml:lowerCorner>34.897167 127.694950</gml:lowerCorner>\r\n" + 
				"			<gml:upperCorner>34.903017 127.755617</gml:upperCorner>\r\n" + 
				"		</gml:Envelope>\r\n" + 
				"	</gml:boundedBy>\r\n" + 
				"	<DatasetIdentificationInformation>\r\n" + 
				"		<S100:encodingSpecification>S-100</S100:encodingSpecification>\r\n" + 
				"		<S100:encodingSpecificationEdition>1.2</S100:encodingSpecificationEdition>\r\n" + 
				"		<S100:productIdentifier>S-100</S100:productIdentifier>\r\n" + 
				"		<S100:productEdition>SV10</S100:productEdition>\r\n" + 
				"		<S100:applicationProfile>e-Navi</S100:applicationProfile>\r\n" + 
				"		<S100:datasetFileIdentifier>e-Navi</S100:datasetFileIdentifier>\r\n" + 
				"		<S100:datasetTitle>Sample dataset for SV10</S100:datasetTitle>\r\n" + 
				"		<S100:datasetReferenceDate>2017-11-13</S100:datasetReferenceDate>\r\n" + 
				"		<S100:datasetLanguage>en</S100:datasetLanguage>\r\n" + 
				"		<S100:datasetAbstract>e-Navi</S100:datasetAbstract>\r\n" + 
				"		<S100:datasetTopicCategory>e-Navi</S100:datasetTopicCategory>\r\n" + 
				"	</DatasetIdentificationInformation>\r\n" + 
				"	<member>\r\n" + 
				"		<SV10_Accident:Accident gml:id=\"M.0001\">\r\n" + 
				"			<accidentAssociation gml:id=\"a.0001\" xlink:href=\"#M.0002\" xlink:role=\"cause\"/>\r\n" + 
				"			<accidentAssociation gml:id=\"a.0003\" xlink:href=\"#M.0003\" xlink:role=\"cause\"/>\r\n" + 
				"			<issueDate>20170912T050000</issueDate>\r\n" + 
				"			<accidentDate>20170911T020000</accidentDate>\r\n" + 
				"			<mmsi>111222333</mmsi>\r\n" + 
				"			<accidentType>Unknow</accidentType>\r\n" + 
				"			<geometry>\r\n" + 
				"				<S100:pointProperty>\r\n" + 
				"					<S100:Point gml:id=\"PT.0001\">\r\n" + 
				"						<gml:pos>34.900683 127.754100</gml:pos>\r\n" + 
				"					</S100:Point>\r\n" + 
				"				</S100:pointProperty>\r\n" + 
				"			</geometry>\r\n" + 
				"		</SV10_Accident:Accident>\r\n" + 
				"	</member>\r\n" + 
				"	<member>\r\n" + 
				"		<SV10_Accident:VesselRoutePrediction gml:id=\"M.0002\">\r\n" + 
				"			<accidentAssociation gml:id=\"a.0002\" xlink:href=\"#M.0001\" xlink:role=\"effect\"/>\r\n" + 
				"			<predictTime>20170912T120000</predictTime>\r\n" + 
				"			<vesselRouteHdg>180.000000</vesselRouteHdg>\r\n" + 
				"			<geometry>\r\n" + 
				"				<S100:pointProperty>\r\n" + 
				"					<S100:Point gml:id=\"PT.0002\">\r\n" + 
				"						<gml:pos>34.898967 127.755150</gml:pos>\r\n" + 
				"					</S100:Point>\r\n" + 
				"				</S100:pointProperty>\r\n" + 
				"			</geometry>\r\n" + 
				"		</SV10_Accident:VesselRoutePrediction>\r\n" + 
				"	</member>\r\n" + 
				"	<member>\r\n" + 
				"		<SV10_Accident:VesselRoutePrediction gml:id=\"M.0003\">\r\n" + 
				"			<accidentAssociation gml:id=\"a.0004\" xlink:href=\"#M.0001\" xlink:role=\"effect\"/>\r\n" + 
				"			<predictTime>20170912T180000</predictTime>\r\n" + 
				"			<vesselRouteHdg>180.000000</vesselRouteHdg>\r\n" + 
				"			<geometry>\r\n" + 
				"				<S100:pointProperty>\r\n" + 
				"					<S100:Point gml:id=\"PT.0003\">\r\n" + 
				"						<gml:pos>34.897167 127.755617</gml:pos>\r\n" + 
				"					</S100:Point>\r\n" + 
				"				</S100:pointProperty>\r\n" + 
				"			</geometry>\r\n" + 
				"		</SV10_Accident:VesselRoutePrediction>\r\n" + 
				"	</member>\r\n" + 
				"	<member>\r\n" + 
				"		<SV10_Accident:Accident gml:id=\"M.0004\">\r\n" + 
				"			<issueDate>20170912T120000</issueDate>\r\n" + 
				"			<accidentDate>20170912T050000</accidentDate>\r\n" + 
				"			<mmsi>222333444</mmsi>\r\n" + 
				"			<accidentType>Grounding</accidentType>\r\n" + 
				"			<geometry>\r\n" + 
				"				<S100:pointProperty>\r\n" + 
				"					<S100:Point gml:id=\"PT.0004\">\r\n" + 
				"						<gml:pos>34.903017 127.694950</gml:pos>\r\n" + 
				"					</S100:Point>\r\n" + 
				"				</S100:pointProperty>\r\n" + 
				"			</geometry>\r\n" + 
				"		</SV10_Accident:Accident>\r\n" + 
				"	</member>\r\n" + 
				"</SV10_Accident:DataSet>";
		
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
		
		sender.sendPostMsg(dstMRN, message);

		
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
		
		sender.sendPostMsg(dstMRN, message);
	}
}
