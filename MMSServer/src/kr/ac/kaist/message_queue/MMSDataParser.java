package kr.ac.kaist.message_queue;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MMSDataParser {
	private static final String TAG = "MMSDataParser";
	private JSONParser parser;
	
	public MMSDataParser () {
		parser = new JSONParser();
	}
	
	public ArrayList<MMSData> processParsing(String payload) {
		ArrayList<MMSData> arrayList = new ArrayList<MMSData>();
		
		try{
			JSONObject jsonPayload = (JSONObject) parser.parse(payload);
			JSONArray jsonArray =  (JSONArray) jsonPayload.get("payload");
			
			for(int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				
				MMSData data = new MMSData(Integer.parseInt((String) jsonObject.get("seq")),
						(String) jsonObject.get("srcMRN"), (String) jsonObject.get("data"));
				
				arrayList.add(data);
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return arrayList;
	}
}
