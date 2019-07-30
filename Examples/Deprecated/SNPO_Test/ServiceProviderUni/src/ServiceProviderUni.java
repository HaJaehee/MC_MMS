import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : ServiceProviderUni.java
	Service Provider only forwards messages to SC having urn:mrn:mcl:vessel:dma:poul-lowenorn
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Haeun Kim (hukim@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2016-12-03

Rev. history : 2017-02-01
Version : 0.3.01
	Added header field features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-20 
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-05-02
Version : 0.5.4
	Added setting response header
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-11-21
Version : 0.7.0
	Compatible with MMS Client beta-0.7.0.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-10-21
Version : 0.8.0
	Created for SNPO test.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class ServiceProviderUni {
	static MMSClientHandler sender;
	static MMSClientHandler server;
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:mcp:service:instance:sp-uni";
		int port = 8902;


		MMSConfiguration.MMS_URL="localhost:8088";
//		MMSConfiguration.MMS_URL="211.43.202.193:8088";
		MMSConfiguration.DEBUG = false; // If you are debugging client, set this variable true.
		
		server = new MMSClientHandler(myMRN);
		sender = new MMSClientHandler(myMRN);
		sender.setSender(new MMSClientHandler.ResponseCallback() {
			//Response Callback from the request message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, String message) {
				// TODO Auto-generated method stub
				System.out.println(message);
			}
		});
		
		server.setServerPort(port, new MMSClientHandler.RequestCallback() {
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

					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							//it only forwards messages to sc having urn:mrn:mcl:vessel:dma:poul-lowenorn
							
							String dstMRN = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
							
							String resMsg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
									"<SV30_Route:DataSet xmlns:SV30_Route=\"http://www.iho.int/SV30_Route/gml/1.0\"\r\n" + 
									"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
									"	xmlns:gml=\"http://www.opengis.net/gml/3.2\"\r\n" + 
									"	xmlns:S100=\"http://www.iho.int/s100gml/1.0\"\r\n" + 
									"	xmlns:xlink=\"http://www.w3.org/1999/xlink\" gml:id=\"SV30_Route,1.2\">\r\n" + 
									"	<gml:boundedBy>\r\n" + 
									"		<gml:Envelope srsName=\"EPSG:4326\">\r\n" + 
									"			<gml:lowerCorner>34.871900 127.686667</gml:lowerCorner>\r\n" + 
									"			<gml:upperCorner>34.944830 127.765900</gml:upperCorner>\r\n" + 
									"		</gml:Envelope>\r\n" + 
									"	</gml:boundedBy>\r\n" + 
									"	<DatasetIdentificationInformation>\r\n" + 
									"		<S100:encodingSpecification>S-100</S100:encodingSpecification>\r\n" + 
									"		<S100:encodingSpecificationEdition>1.2</S100:encodingSpecificationEdition>\r\n" + 
									"		<S100:productIdentifier>S-100</S100:productIdentifier>\r\n" + 
									"		<S100:productEdition>SV30</S100:productEdition>\r\n" + 
									"		<S100:applicationProfile>e-Navi</S100:applicationProfile>\r\n" + 
									"		<S100:datasetFileIdentifier>e-Navi</S100:datasetFileIdentifier>\r\n" + 
									"		<S100:datasetTitle>Sample dataset for SV30</S100:datasetTitle>\r\n" + 
									"		<S100:datasetReferenceDate>2017-11-13</S100:datasetReferenceDate>\r\n" + 
									"		<S100:datasetLanguage>en</S100:datasetLanguage>\r\n" + 
									"		<S100:datasetAbstract>e-Navi</S100:datasetAbstract>\r\n" + 
									"		<S100:datasetTopicCategory>e-Navi</S100:datasetTopicCategory>\r\n" + 
									"	</DatasetIdentificationInformation>\r\n" + 
									"	<imember>\r\n" + 
									"		<SV30_Route:RouteInfo gml:id=\"IM.0001\">\r\n" + 
									"			<issueDate>20171013T120000</issueDate>\r\n" + 
									"			<vvd>001E</vvd>\r\n" + 
									"			<destinationDtg>34.000000</destinationDtg>\r\n" + 
									"			<destinationTtg>060000</destinationTtg>\r\n" + 
									"			<eta>20171013T180000</eta>\r\n" + 
									"		</SV30_Route:RouteInfo>\r\n" + 
									"	</imember>\r\n" + 
									"	<imember>\r\n" + 
									"		<SV30_Route:ShipSpec gml:id=\"IM.0002\">\r\n" + 
									"			<shipName>Test Vessel</shipName>\r\n" + 
									"			<shipId>1</shipId>\r\n" + 
									"			<mmsi>123123123</mmsi>\r\n" + 
									"			<imoNo>1234567</imoNo>\r\n" + 
									"			<callSign>A2C4E</callSign>\r\n" + 
									"			<netTonnage>4700.000000</netTonnage>\r\n" + 
									"			<grossTonnage>8400.000000</grossTonnage>\r\n" + 
									"			<length>120.000000</length>\r\n" + 
									"			<breadth>30.000000</breadth>\r\n" + 
									"			<depth>10.000000</depth>\r\n" + 
									"			<vsslTp>test</vsslTp>\r\n" + 
									"			<hullTp>test</hullTp>\r\n" + 
									"			<shipSvrType>Routine</shipSvrType>\r\n" + 
									"			<inOutPortType>OuterPort</inOutPortType>\r\n" + 
									"			<buildDate>20071013T110000</buildDate>\r\n" + 
									"			<ownerNationality>S.Korea</ownerNationality>\r\n" + 
									"			<owner>e-Nav</owner>\r\n" + 
									"			<shipOperator>e-Nav</shipOperator>\r\n" + 
									"			<operatorFlag>S.Korea</operatorFlag>\r\n" + 
									"			<previousCallsign>1B3D5</previousCallsign>\r\n" + 
									"			<vesselFlag>S.Korea</vesselFlag>\r\n" + 
									"			<mainEngHp>6500.000000</mainEngHp>\r\n" + 
									"			<regstPort>Busan</regstPort>\r\n" + 
									"		</SV30_Route:ShipSpec>\r\n" + 
									"	</imember>\r\n" + 
									"	<imember>\r\n" + 
									"		<SV30_Route:ShipCondi gml:id=\"IM.0003\">\r\n" + 
									"			<displacement>5300.000000</displacement>\r\n" + 
									"			<draftFwd>4.500000</draftFwd>\r\n" + 
									"			<draftAft>6.200000</draftAft>\r\n" + 
									"		</SV30_Route:ShipCondi>\r\n" + 
									"	</imember>\r\n" + 
									"	<member>\r\n" + 
									"		<SV30_Route:Route gml:id=\"M.0001\">\r\n" + 
									"			<routeService gml:id=\"a.0001\" xlink:href=\"#M.0002\" xlink:role=\"use\"/>\r\n" + 
									"			<routeService gml:id=\"a.0005\" xlink:href=\"#M.0004\" xlink:role=\"use\"/>\r\n" + 
									"			<routeService gml:id=\"a.0009\" xlink:href=\"#M.0006\" xlink:role=\"use\"/>\r\n" + 
									"			<routeService gml:id=\"a.0013\" xlink:href=\"#M.0008\" xlink:role=\"use\"/>\r\n" + 
									"			<routeService gml:id=\"a.0017\" xlink:href=\"#M.0010\" xlink:role=\"use\"/>\r\n" + 
									"			<routeService gml:id=\"a.0021\" xlink:href=\"#M.0012\" xlink:role=\"use\"/>\r\n" + 
									"			<routeAdditionalInformation gml:id=\"a.0023\" xlink:href=\"#IM.0001\" xlink:role=\"role\"/>\r\n" + 
									"			<shipInformation gml:id=\"a.0024\" xlink:href=\"#IM.0002\" xlink:role=\"role\"/>\r\n" + 
									"			<shipInformation gml:id=\"a.0025\" xlink:href=\"#IM.0003\" xlink:role=\"role\"/>\r\n" + 
									"			<startLat>34.912550</startLat>\r\n" + 
									"			<startLong>127.686667</startLong>\r\n" + 
									"			<endLat>34.871900</endLat>\r\n" + 
									"			<endLong>127.765900</endLong>\r\n" + 
									"			<waypointCount>6</waypointCount>\r\n" + 
									"			<routeType>SafeRoute</routeType>\r\n" + 
									"		</SV30_Route:Route>\r\n" + 
									"	</member>\r\n" + 
									"	<member>\r\n" + 
									"		<SV30_Route:Waypoint gml:id=\"M.0002\">\r\n" + 
									"			<routeService gml:id=\"a.0002\" xlink:href=\"#M.0001\" xlink:role=\"usedby\"/>\r\n" + 
									"			<routeLine gml:id=\"a.0003\" xlink:href=\"#M.0003\" xlink:role=\"consistsOf\"/>\r\n" + 
									"			<waypointNo>0</waypointNo>\r\n" + 
									"			<nextWpDtg>1.200000</nextWpDtg>\r\n" + 
									"			<nextWpTtg>011200</nextWpTtg>\r\n" + 
									"			<wpCategory>Start</wpCategory>\r\n" + 
									"			<geometry>\r\n" + 
									"				<S100:pointProperty>\r\n" + 
									"					<S100:Point gml:id=\"PT.0001\">\r\n" + 
									"						<gml:pos>34.912550 127.686667</gml:pos>\r\n" + 
									"					</S100:Point>\r\n" + 
									"				</S100:pointProperty>\r\n" + 
									"			</geometry>\r\n" + 
									"		</SV30_Route:Waypoint>\r\n" + 
									"	</member>\r\n" + 
									"	<member>\r\n" + 
									"		<SV30_Route:Leg gml:id=\"M.0003\">\r\n" + 
									"			<routeLine gml:id=\"a.0004\" xlink:href=\"#M.0002\" xlink:role=\"consists\"/>\r\n" + 
									"			<plannedCourse>109</plannedCourse>\r\n" + 
									"			<plannedSpeed>4.000000</plannedSpeed>\r\n" + 
									"			<geometry>\r\n" + 
									"				<S100:curveProperty>\r\n" + 
									"					<S100:Curve gml:id=\"C.0001\">\r\n" + 
									"						<gml:segments>\r\n" + 
									"							<gml:LineStringSegment>\r\n" + 
									"								<gml:posList>34.912550 127.686667 34.944830 127.716050</gml:posList>\r\n" + 
									"							</gml:LineStringSegment>\r\n" + 
									"						</gml:segments>\r\n" + 
									"					</S100:Curve>\r\n" + 
									"				</S100:curveProperty>\r\n" + 
									"			</geometry>\r\n" + 
									"		</SV30_Route:Leg>\r\n" + 
									"	</member>\r\n" + 
									"	<member>\r\n" + 
									"		<SV30_Route:Waypoint gml:id=\"M.0004\">\r\n" + 
									"			<routeService gml:id=\"a.0006\" xlink:href=\"#M.0001\" xlink:role=\"usedby\"/>\r\n" + 
									"			<routeLine gml:id=\"a.0007\" xlink:href=\"#M.0005\" xlink:role=\"consistsOf\"/>\r\n" + 
									"			<waypointNo>1</waypointNo>\r\n" + 
									"			<nextWpDtg>2.300000</nextWpDtg>\r\n" + 
									"			<nextWpTtg>021800</nextWpTtg>\r\n" + 
									"			<wpCategory>Waypoint</wpCategory>\r\n" + 
									"			<geometry>\r\n" + 
									"				<S100:pointProperty>\r\n" + 
									"					<S100:Point gml:id=\"PT.0002\">\r\n" + 
									"						<gml:pos>34.944830 127.716050</gml:pos>\r\n" + 
									"					</S100:Point>\r\n" + 
									"				</S100:pointProperty>\r\n" + 
									"			</geometry>\r\n" + 
									"		</SV30_Route:Waypoint>\r\n" + 
									"	</member>\r\n" + 
									"	<member>\r\n" + 
									"		<SV30_Route:Leg gml:id=\"M.0005\">\r\n" + 
									"			<routeLine gml:id=\"a.0008\" xlink:href=\"#M.0004\" xlink:role=\"consists\"/>\r\n" + 
									"			<plannedCourse>103</plannedCourse>\r\n" + 
									"			<plannedSpeed>5.000000</plannedSpeed>\r\n" + 
									"			<geometry>\r\n" + 
									"				<S100:curveProperty>\r\n" + 
									"					<S100:Curve gml:id=\"C.0002\">\r\n" + 
									"						<gml:segments>\r\n" + 
									"							<gml:LineStringSegment>\r\n" + 
									"								<gml:posList>34.944830 127.716050 34.898467 127.742533</gml:posList>\r\n" + 
									"							</gml:LineStringSegment>\r\n" + 
									"						</gml:segments>\r\n" + 
									"					</S100:Curve>\r\n" + 
									"				</S100:curveProperty>\r\n" + 
									"			</geometry>\r\n" + 
									"		</SV30_Route:Leg>\r\n" + 
									"	</member>\r\n" + 
									"	<member>\r\n" + 
									"		<SV30_Route:Waypoint gml:id=\"M.0006\">\r\n" + 
									"			<routeService gml:id=\"a.0010\" xlink:href=\"#M.0001\" xlink:role=\"usedby\"/>\r\n" + 
									"			<routeLine gml:id=\"a.0011\" xlink:href=\"#M.0007\" xlink:role=\"consistsOf\"/>\r\n" + 
									"			<waypointNo>2</waypointNo>\r\n" + 
									"			<nextWpDtg>3.400000</nextWpDtg>\r\n" + 
									"			<nextWpTtg>032400</nextWpTtg>\r\n" + 
									"			<wpCategory>Waypoint</wpCategory>\r\n" + 
									"			<geometry>\r\n" + 
									"				<S100:pointProperty>\r\n" + 
									"					<S100:Point gml:id=\"PT.0003\">\r\n" + 
									"						<gml:pos>34.898467 127.742533</gml:pos>\r\n" + 
									"					</S100:Point>\r\n" + 
									"				</S100:pointProperty>\r\n" + 
									"			</geometry>\r\n" + 
									"		</SV30_Route:Waypoint>\r\n" + 
									"	</member>\r\n" + 
									"	<member>\r\n" + 
									"		<SV30_Route:Leg gml:id=\"M.0007\">\r\n" + 
									"			<routeLine gml:id=\"a.0012\" xlink:href=\"#M.0006\" xlink:role=\"consists\"/>\r\n" + 
									"			<plannedCourse>139</plannedCourse>\r\n" + 
									"			<plannedSpeed>4.000000</plannedSpeed>\r\n" + 
									"			<geometry>\r\n" + 
									"				<S100:curveProperty>\r\n" + 
									"					<S100:Curve gml:id=\"C.0003\">\r\n" + 
									"						<gml:segments>\r\n" + 
									"							<gml:LineStringSegment>\r\n" + 
									"								<gml:posList>34.898467 127.742533 34.889083 127.753525</gml:posList>\r\n" + 
									"							</gml:LineStringSegment>\r\n" + 
									"						</gml:segments>\r\n" + 
									"					</S100:Curve>\r\n" + 
									"				</S100:curveProperty>\r\n" + 
									"			</geometry>\r\n" + 
									"		</SV30_Route:Leg>\r\n" + 
									"	</member>\r\n" + 
									"	<member>\r\n" + 
									"		<SV30_Route:Waypoint gml:id=\"M.0008\">\r\n" + 
									"			<routeService gml:id=\"a.0014\" xlink:href=\"#M.0001\" xlink:role=\"usedby\"/>\r\n" + 
									"			<routeLine gml:id=\"a.0015\" xlink:href=\"#M.0009\" xlink:role=\"consistsOf\"/>\r\n" + 
									"			<waypointNo>3</waypointNo>\r\n" + 
									"			<nextWpDtg>4.500000</nextWpDtg>\r\n" + 
									"			<nextWpTtg>043000</nextWpTtg>\r\n" + 
									"			<wpCategory>Waypoint</wpCategory>\r\n" + 
									"			<geometry>\r\n" + 
									"				<S100:pointProperty>\r\n" + 
									"					<S100:Point gml:id=\"PT.0004\">\r\n" + 
									"						<gml:pos>34.889083 127.753525</gml:pos>\r\n" + 
									"					</S100:Point>\r\n" + 
									"				</S100:pointProperty>\r\n" + 
									"			</geometry>\r\n" + 
									"		</SV30_Route:Waypoint>\r\n" + 
									"	</member>\r\n" + 
									"	<member>\r\n" + 
									"		<SV30_Route:Leg gml:id=\"M.0009\">\r\n" + 
									"			<routeLine gml:id=\"a.0016\" xlink:href=\"#M.0008\" xlink:role=\"consists\"/>\r\n" + 
									"			<plannedCourse>163</plannedCourse>\r\n" + 
									"			<plannedSpeed>5.000000</plannedSpeed>\r\n" + 
									"			<geometry>\r\n" + 
									"				<S100:curveProperty>\r\n" + 
									"					<S100:Curve gml:id=\"C.0004\">\r\n" + 
									"						<gml:segments>\r\n" + 
									"							<gml:LineStringSegment>\r\n" + 
									"								<gml:posList>34.889083 127.753525 34.877983 127.757833</gml:posList>\r\n" + 
									"							</gml:LineStringSegment>\r\n" + 
									"						</gml:segments>\r\n" + 
									"					</S100:Curve>\r\n" + 
									"				</S100:curveProperty>\r\n" + 
									"			</geometry>\r\n" + 
									"		</SV30_Route:Leg>\r\n" + 
									"	</member>\r\n" + 
									"	<member>\r\n" + 
									"		<SV30_Route:Waypoint gml:id=\"M.0010\">\r\n" + 
									"			<routeService gml:id=\"a.0018\" xlink:href=\"#M.0001\" xlink:role=\"usedby\"/>\r\n" + 
									"			<routeLine gml:id=\"a.0019\" xlink:href=\"#M.0011\" xlink:role=\"consistsOf\"/>\r\n" + 
									"			<waypointNo>4</waypointNo>\r\n" + 
									"			<nextWpDtg>5.600000</nextWpDtg>\r\n" + 
									"			<nextWpTtg>053600</nextWpTtg>\r\n" + 
									"			<wpCategory>Waypoint</wpCategory>\r\n" + 
									"			<geometry>\r\n" + 
									"				<S100:pointProperty>\r\n" + 
									"					<S100:Point gml:id=\"PT.0005\">\r\n" + 
									"						<gml:pos>34.877983 127.757833</gml:pos>\r\n" + 
									"					</S100:Point>\r\n" + 
									"				</S100:pointProperty>\r\n" + 
									"			</geometry>\r\n" + 
									"		</SV30_Route:Waypoint>\r\n" + 
									"	</member>\r\n" + 
									"	<member>\r\n" + 
									"		<SV30_Route:Leg gml:id=\"M.0011\">\r\n" + 
									"			<routeLine gml:id=\"a.0020\" xlink:href=\"#M.0010\" xlink:role=\"consists\"/>\r\n" + 
									"			<plannedCourse>129</plannedCourse>\r\n" + 
									"			<plannedSpeed>4.000000</plannedSpeed>\r\n" + 
									"			<geometry>\r\n" + 
									"				<S100:curveProperty>\r\n" + 
									"					<S100:Curve gml:id=\"C.0005\">\r\n" + 
									"						<gml:segments>\r\n" + 
									"							<gml:LineStringSegment>\r\n" + 
									"								<gml:posList>34.877983 127.757833 34.871900 127.765900</gml:posList>\r\n" + 
									"							</gml:LineStringSegment>\r\n" + 
									"						</gml:segments>\r\n" + 
									"					</S100:Curve>\r\n" + 
									"				</S100:curveProperty>\r\n" + 
									"			</geometry>\r\n" + 
									"		</SV30_Route:Leg>\r\n" + 
									"	</member>\r\n" + 
									"	<member>\r\n" + 
									"		<SV30_Route:Waypoint gml:id=\"M.0012\">\r\n" + 
									"			<routeService gml:id=\"a.0022\" xlink:href=\"#M.0001\" xlink:role=\"usedby\"/>\r\n" + 
									"			<waypointNo>0</waypointNo>\r\n" + 
									"			<nextWpDtg>0.000000</nextWpDtg>\r\n" + 
									"			<nextWpTtg>000000</nextWpTtg>\r\n" + 
									"			<wpCategory>End</wpCategory>\r\n" + 
									"			<geometry>\r\n" + 
									"				<S100:pointProperty>\r\n" + 
									"					<S100:Point gml:id=\"PT.0006\">\r\n" + 
									"						<gml:pos>34.871900 127.765900</gml:pos>\r\n" + 
									"					</S100:Point>\r\n" + 
									"				</S100:pointProperty>\r\n" + 
									"			</geometry>\r\n" + 
									"		</SV30_Route:Waypoint>\r\n" + 
									"	</member>\r\n" + 
									"</SV30_Route:DataSet>\r\n" + 
									"";
							try {
								sender.sendPostMsg(dstMRN, resMsg);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						}
					}).start();
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
		}); //server has a context '/forwarding'
		/* It is not same with:
		 * server.setPort(port); //It sets default context as '/'
		 * server.addContext("/forwarding"); //Finally server has two context '/' and '/forwarding'
		 */

	}
}
