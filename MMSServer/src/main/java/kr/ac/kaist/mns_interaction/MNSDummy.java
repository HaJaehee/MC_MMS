package kr.ac.kaist.mns_interaction;
/* -------------------------------------------------------- */
/** 
File name : MNSDummy.java
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-06-23
	Added Geo-location Update.
Modifier : Jaehyun Park (jae519@kaist.ac.kr)

Rev. history : 2017-02-01
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-27
Version : 0.5.8
	Added geocasting related features
Modifier : Jaehyun Park (jae519@kaist.ac.kr)


Rev. history : 2017-09-26
Version : 0.6.0
	Added adding mrn entry case 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-09-29
Version : 0.6.0
	MRNtoIPs are printed into sorted by key(MRN) form .
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-04-23
Version : 0.7.1
	Removed INTEGER_OVERFLOW hazard.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-06-06
Version : 0.7.1
	Revised interfaces.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-03
Version : 0.7.2
	Removed console prints.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-10
Version : 0.7.2
	Fixed insecure codes.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-07-27
Version : 0.7.2
	Added geocasting features which cast message to circle or polygon area.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
**/
/* -------------------------------------------------------- */

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.ac.kaist.mms_server.MMSConfiguration;

public class MNSDummy {
	private static int UNICASTING = 1;
	private static int GEOCASTING = 2;
	private static int GROUPCASTING = 3;


	//private static final Logger logger = LoggerFactory.getLogger(MNSDummy.class);
	//All MRN to IP Mapping is in hashmap 
	private static HashMap<String, String> MRNtoIP = new HashMap<String, String>();
	//	private static HashMap<String, String> IPtoMRN = new HashMap<String, String>();

	public static void main(String argv[]) throws Exception
	{
		ServerSocket Sock = new ServerSocket(MMSConfiguration.MNS_PORT());
		System.out.println("Listen:"+MMSConfiguration.MNS_PORT());

		//logger.error("MNSDummy started.");
		//       -------------Put MRN --> IP Information -------------
		//		 MRN table structure:           IP_Address:PortNumber:Model
		//       (Geo-location added version)   IP_Address:PortNumber:Model:Geo-location
		//
		//
		//       MRNtoIP.put("urn:mrn:imo:imo-no:1000001","223.39.131.117:0:1");
		//       MRNtoIP.put("urn:mrn:imo:imo-no:1000001-SCSession","118.220.143.130:0:1");
		//       MRNtoIP.put("urn:mrn:imo:imo-no:1000001-kaist","172.25.0.11:0:1");
		//       MRNtoIP.put("urn:mrn:imo:imo-no:1000001-pjh","143.248.55.117:0:1");
		//       MRNtoIP.put("urn:mrn:imo:imo-no:1000001-test171024","219.249.186.19:0:1");
		//       MRNtoIP.put("urn:mrn:imo:imo-no:1000002-kaist","172.25.0.11:0:1");
		//       MRNtoIP.put("urn:mrn:imo:imo-no:1000005","218.39.202.78:8906:2");
		//       MRNtoIP.put("urn:mrn:smart-navi:client:sv40","106.240.253.98:0:1");
		//       MRNtoIP.put("urn:mrn:smart-navi:device:msc1-20170914","175.244.145.136:0:1");
		//       MRNtoIP.put("urn:mrn:smart-navi:device:msr1-20170914","221.162.236.234:8982:2");
		//       MRNtoIP.put("urn:mrn:smart-navi:device:tm-server","223.39.131.117:8902:2");
		//       MRNtoIP.put("urn:mrn:smart-navi:device:tm-server-kaist","143.248.57.72:8902:2");
		//       MRNtoIP.put("urn:mrn:smart-navi:device:tm-server-middle-test171024","223.39.131.117:20001:2");
		//       MRNtoIP.put("urn:mrn:smart-navi:device:tm-server-pjh","143.248.55.117:8902:2");
		//       MRNtoIP.put("urn:mrn:smart-navi:s:kjesv40","112.162.241.161:0:1");
		//       MRNtoIP.put("urn:mrn:smart-navi:s:sv40","1.220.41.11:0:1");
		//       MRNtoIP.put("urn:mrn:smart-navi:service:kjesv40","1.220.41.11:8902:2");
		//       MRNtoIP.put("urn:mrn:smart-navi:service:sv40","183.103.51.133:8902:2");
		//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:NXDDS","106.248.228.114:7088:2");
		//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:NXRESULT","106.248.228.114:7090:2");
		//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S10","203.250.182.94:7088:2");
		//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S11","203.250.182.94:7088:2");
		//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S20","203.250.182.94:7088:2");
		//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S30","203.250.182.94:7088:2");
		//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S40","1.220.41.11:8902:2");
		//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S51","219.249.186.19:0:1");
		//       MRNtoIP.put("urn:mrn:smart:service:instance:mof:S52","203.250.182.94:7088:2");
		//       MRNtoIP.put("urn:mrn:smart:vessel:imo-no:mof:tmp100fors10","203.250.182.94:0:1");
		//       MRNtoIP.put("urn:mrn:smart:vessel:imo-no:mof:tmp100fors11","203.250.182.94:7080:2");
		//       MRNtoIP.put("urn:mrn:smart:vessel:imo-no:mof:tmp101fors10","118.220.143.130:0:1");
		//       MRNtoIP.put("urn:mrn:smart:vessel:imo-no:mof:tmp200fors20","1.1.1.1:0:1");
		//       MRNtoIP.put("urn:mrn:smart:vessel:imo-no:mof:tmp300fors30","1.1.1.1:0:1");
		//       MRNtoIP.put("urn:mrn:smart:vessel:imo-no:mof:tmp400fors40","112.186.26.198:0:1");
		//       MRNtoIP.put("urn:mrn:smart:vessel:imo-no:mof:tmp510fors51","218.39.202.78:20001:2");
		//       MRNtoIP.put("urn:mrn:smart:vessel:imo-no:mof:tmp520fors52","119.203.5.157:0:1");



		//-----------------------------------------------------

		while(true)
		{

			Socket connectionSocket = Sock.accept();


			//logger.debug("Packet incomming.");

			InputStreamReader in = new InputStreamReader(connectionSocket.getInputStream());
			BufferedReader br = new BufferedReader(in);
			PrintWriter pw = new PrintWriter(connectionSocket.getOutputStream());

			String inputLine;
			StringBuffer buf = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				buf.append(inputLine.trim());
			}
			if (!connectionSocket.isInputShutdown()) {
				connectionSocket.shutdownInput();
			}
			String data = buf.toString();
			// newly designed interfaces
			if (data.startsWith("{")) {
				try {
					String dataToReply = "";

					JSONParser queryParser = new JSONParser();

					JSONObject query = (JSONObject) queryParser.parse(data);

					if (query.get("unicasting") != null) {
						JSONObject unicastingQuery = (JSONObject) query.get("unicasting");
						String srcMRN = unicastingQuery.get("srcMRN").toString();
						String dstMRN = unicastingQuery.get("dstMRN").toString();
						String IPAddr = unicastingQuery.get("IPAddr").toString();

						String dstInfo = (String)MRNtoIP.get(dstMRN);
						if (dstInfo != null) {
							String splittedDstInfo[] = dstInfo.split(":");
							if (splittedDstInfo[2].equals("1")) { //polling model
								JSONObject connTypePolling = new JSONObject();
								connTypePolling.put("connType", "polling");
								connTypePolling.put("dstMRN", dstMRN);
								connTypePolling.put("netType", "LTE-M");
								dataToReply = connTypePolling.toJSONString();
							}
							else if (splittedDstInfo[2].equals("2")) { //push model
								JSONObject connTypePush = new JSONObject();
								connTypePush.put("connType", "push");
								connTypePush.put("dstMRN", dstMRN);
								connTypePush.put("IPAddr", splittedDstInfo[0]);
								connTypePush.put("portNum", splittedDstInfo[1]);
								dataToReply = connTypePush.toJSONString();
							}
						}
						else {
							dataToReply = "No";
						}


					} 
					else if (query.get("geocasting_circle") != null) {
						JSONObject geocastingQuery = (JSONObject) query.get("geocasting_circle");
						String srcMRN = geocastingQuery.get("srcMRN").toString();
						String dstMRN = geocastingQuery.get("dstMRN").toString();
						String geoLat = geocastingQuery.get("lat").toString();
						String geoLong = geocastingQuery.get("long").toString();
						String geoRadius = geocastingQuery.get("radius").toString();

						float lat = Float.parseFloat(geoLat); 
						float lon = Float.parseFloat(geoLat);
						float rad = Float.parseFloat(geoRadius);

						if ( 20000 >= rad && 90 >= Math.abs(lat) && 180 >= Math.abs(lon)) {
							Set<String> keys = MRNtoIP.keySet();

							Iterator<String> keysIter = keys.iterator();
							// MRN lists are returned by json format.
							JSONArray objList = new JSONArray();


							if (keysIter.hasNext()){
								do{
									String key = keysIter.next();
									String value = MRNtoIP.get(key);
									String[] parsedVal = value.split(":");
									if (parsedVal.length == 4){ // Geo-information exists.
										String[] curGeoMRN = parsedVal[3].split("-");
										float curLat = Float.parseFloat(curGeoMRN[1]); 
										float curLong = Float.parseFloat(curGeoMRN[3]);


										if (((lat-curLat)*(lat-curLat) + (lon-curLong)*(lon-curLong)) < rad * rad){
											JSONObject item = new JSONObject();
											item.put("dstMRN", key);
											item.put("netType", "LTE-M");
											if (parsedVal[2].equals("1")) {
												item.put("connType", "polling");
											}
											else if (parsedVal[1].equals("2")) {
												item.put("connType", "push");
											}
											objList.add(item);
										}
									}


								}while(keysIter.hasNext());
							}
							dataToReply = objList.toJSONString();
						}
					}
					else if (query.get("geocasting_polygon") != null) {
						JSONObject geocastingQuery = (JSONObject) query.get("geocasting_polygon");
						String srcMRN = geocastingQuery.get("srcMRN").toString();
						String dstMRN = geocastingQuery.get("dstMRN").toString();
						String geoLat = geocastingQuery.get("lat").toString();
						String geoLong = geocastingQuery.get("long").toString();
						
						System.out.println("Geocating polygon, srcMRN="+srcMRN+", dstMRN="+dstMRN+", geoLat="+geoLat+", geoLong="+geoLong);
						dataToReply = "[{\"exception\":\"absent MRN\"}]";
					}
					pw.println(dataToReply);
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						pw.close();
					}
					if (br != null) {
						br.close();						
					}
					if (in != null) {
						in.close();
					}
					if (connectionSocket != null) {
						connectionSocket.close();
					}
				}
			}
			//logger.debug(data);

			String dataToReply = "MNSDummy-Reply:";

			if (data.regionMatches(0, "MRN-Request:", 0, 12)){

				data = data.substring(12);



				//loggerdebug("MNSDummy:data=" + data);
				if (!data.regionMatches(0, "urn:mrn:mcs:casting:geocast:smart:",0,34)){
					try {
						if (MRNtoIP.containsKey(data)) {
							dataToReply += MRNtoIP.get(data);
						}
						else {
							//loggerdebug("No MRN to IP Mapping.");
							dataToReply = "No";
						}
						//loggerdebug(dataToReply);

						pw.println(dataToReply);
						pw.flush();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						if (pw != null) {
							pw.close();
						}
						if (br != null) {
							br.close();						
						}
						if (in != null) {
							in.close();
						}
						if (connectionSocket != null) {
							connectionSocket.close();
						}
					}
				}
				else { // if geocasting (urn:mrn:mcs:casting:geocasting:smart:-)


					String geoMRN = data.substring(34);
					String[] parsedGeoMRN = geoMRN.split("-");
					//loggerinfo("Geocasting MRN="+geoMRN+".");
					float lat = Float.parseFloat(parsedGeoMRN[1]); 
					float lon = Float.parseFloat(parsedGeoMRN[3]);
					float rad = Float.parseFloat(parsedGeoMRN[5]);

					if ( 20000 <= rad && 90 >= Math.abs(lat) && 180 >= Math.abs(lon)) {
						try {
							Set<String> keys = MRNtoIP.keySet();

							Iterator<String> keysIter = keys.iterator();
							// MRN lists are returned by json format.
							// {"poll":[{"mrn":"urn:mrn:-"},{"mrn":"urn:mrn:-"},{"mrn":"urn:mrn:-"},....]}
							JSONArray objlist = new JSONArray();


							if (keysIter.hasNext()){
								do{
									String key = keysIter.next();
									String value = MRNtoIP.get(key);
									String[] parsedVal = value.split(":");
									if (parsedVal.length == 4){ // Geo-information exists.
										String[] curGeoMRN = parsedVal[3].split("-");
										float curLat = Float.parseFloat(curGeoMRN[1]); 
										float curLong = Float.parseFloat(curGeoMRN[3]);


										if (((lat-curLat)*(lat-curLat) + (lon-curLong)*(lon-curLong)) < rad * rad){
											JSONObject item = new JSONObject();
											item.put("dstMRN", key);
											objlist.add(item);
										}
									}


								} while(keysIter.hasNext());
							}
							JSONObject dstMRNs = new JSONObject();
							dstMRNs.put("poll", objlist);

							pw.println("MNSDummy-Reply:" + dstMRNs.toString());
							pw.flush();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						finally {
							if (pw != null) {
								pw.close();
							}
							if (br != null) {
								br.close();						
							}
							if (in != null) {
								in.close();
							}
							if (connectionSocket != null) {
								connectionSocket.close();
							}
						}
					} 
					else {
						try {
							JSONArray objlist = new JSONArray();
							JSONObject dstMRNs = new JSONObject();
							dstMRNs.put("poll", objlist);

							pw.println("MNSDummy-Reply:" + dstMRNs.toString());
							pw.flush();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						finally {
							if (pw != null) {
								pw.close();
							}
							if (br != null) {
								br.close();						
							}
							if (in != null) {
								in.close();
							}
							if (connectionSocket != null) {
								connectionSocket.close();
							}
						}
					}
				}
			} 
			else if (data.regionMatches(0, "Location-Update:", 0, 16)){
				try {
					data = data.substring(16);

					//loggerinfo("MNSDummy:data=" + data);
					String[] data_sub = data.split(",");
					
					if (MRNtoIP.get(data_sub[1]) == null || MRNtoIP.get(data_sub[1]).split(":").length == 3 ) {
						// data_sub = IP_address, MRN, Port
						MRNtoIP.put(data_sub[1], data_sub[0] + ":" + data_sub[2] + ":" + data_sub[3]);
					}

					pw.println("OK");
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						pw.close();
					}
					if (br != null) {
						br.close();						
					}
					if (in != null) {
						in.close();
					}
					if (connectionSocket != null) {
						connectionSocket.close();
					}
				}

			} 
			else if (data.regionMatches(0, "Dump-MNS:", 0, 9)){
				try {
					if (!MRNtoIP.isEmpty()){
						SortedSet<String> keys = new TreeSet<String>(MRNtoIP.keySet());
						for (String key : keys) {
							String value = MRNtoIP.get(key);
							dataToReply = dataToReply + key + "," + value + "<br/>";
						}
					}
					else{
						//loggerdebug("No MRN to IP Mapping.");
						dataToReply = "No";
					}

					pw.println(dataToReply);
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						try {
							pw.close();
						}
						catch (Exception e) {

						}
					}
					if (br != null) {
						try {
							br.close();	
						}
						catch (IOException e) {

						}
					}
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {

						}
					}
					if (connectionSocket != null) {
						try {
							connectionSocket.close();
						}
						catch (IOException e) {

						}
					}
				}

			}
			else if (MMSConfiguration.WEB_MANAGING() && data.equals("Empty-MNS:")){
				try {
					MRNtoIP.clear();
					//loggerwarn("MNSDummy:EMPTY.");
					pw.println("");
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						try {
							pw.close();
						}
						catch (Exception e) {

						}
					}
					if (br != null) {
						try {
							br.close();	
						}
						catch (IOException e) {

						}
					}
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {

						}
					}
					if (connectionSocket != null) {
						try {
							connectionSocket.close();
						}
						catch (IOException e) {

						}
					}
				}

			}
			else if (MMSConfiguration.WEB_MANAGING() && data.regionMatches(0, "Remove-Entry:", 0, 13)){
				try {
					String mrn = data.substring(13);
					MRNtoIP.remove(mrn);
					//loggerwarn("MNSDummy:REMOVE="+mrn+".");
					pw.println("");
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						try {
							pw.close();
						}
						catch (Exception e) {

						}
					}
					if (br != null) {
						try {
							br.close();	
						}
						catch (IOException e) {

						}
					}
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {

						}
					}
					if (connectionSocket != null) {
						try {
							connectionSocket.close();
						}
						catch (IOException e) {

						}
					}
				}
			}
			else if (MMSConfiguration.WEB_MANAGING() && data.regionMatches(0, "Add-Entry:", 0, 10)){
				try {
					String[] params = data.substring(10).split(",");
					String mrn = params[0];
					String locator = params[1] +":"+ params[2] +":"+ params[3];
					MRNtoIP.put(mrn, locator);
					//loggerwarn("MNSDummy:ADD="+mrn+".");

					//Geo-location update function.  
					pw.println("");
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						try {
							pw.close();
						}
						catch (Exception e) {

						}
					}
					if (br != null) {
						try {
							br.close();	
						}
						catch (IOException e) {

						}
					}
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {

						}
					}
					if (connectionSocket != null) {
						try {
							connectionSocket.close();
						}
						catch (IOException e) {

						}
					}
				}
			}
			else if (data.regionMatches(0, "Geo-location-Update:", 0, 20)){
				try {
					//data format: Geo-location-update:
					String[] data_sub = data.split(",");
					//loggerdebug("MNSDummy:Geolocationupdate "+data_sub[1]);
					MRNtoIP.put(data_sub[1], "127.0.0.1" + ":" + data_sub[2] + ":" + data_sub[3] + ":" + data_sub[4]);
					pw.println("");
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						try {
							pw.close();
						}
						catch (Exception e) {
							
						}
					}
					if (br != null) {
						try {
							br.close();	
						}
						catch (IOException e) {
							
						}
					}
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {
							
						}
					}
					if (connectionSocket != null) {
						try {
							connectionSocket.close();
						}
						catch (IOException e) {
							
						}
					}
				}
			} 
			else if(data.regionMatches(0, "IP-Request:", 0, 11)){
				try {
					String address = data.substring(11).split(",")[0];
					String[] parseAddress = address.split(":");
					String mrn = null;
					for(String value : MRNtoIP.keySet()){
						String[] parseValue = MRNtoIP.get(value).split(":");
						if(parseAddress[0].equals(parseValue[0]) 
								&& parseAddress[1].equals(parseValue[1])){
							mrn = value;
							break;
						}
					}

					if(mrn == null){
						dataToReply += "Unregistered MRN in MNS";
					} 
					else {
						dataToReply += mrn;
					}

					pw.println(dataToReply);
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						try {
							pw.close();
						}
						catch (Exception e) {

						}
					}
					if (br != null) {
						try {
							br.close();	
						}
						catch (IOException e) {

						}
					}
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {

						}
					}
					if (connectionSocket != null) {
						try {
							connectionSocket.close();
						}
						catch (IOException e) {

						}
					}
				}
			}
		}
	}
}
