package kr.ac.kaist.mms_server;
/* -------------------------------------------------------- */
/** 
File name : MMSConfiguration.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-04-27
Version : 0.5.2
	Added AUTO_SAVE_STATUS, SAVE_STATUS_INTERVAL
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr) 

Rev. history : 2017-04-29
Version : 0.5.3
	Added SYSTEM_LOGGING, AUTO_SAVE_SYSTEM_LOG, SAVE_SYSTEM_LOG_INTERVAL
	Changed LOGGING to CONSOLE_LOGGING
	Changed WEB_LOG_PROVIDING to WEB_LOG_PROVIDING
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr) 

Rev. history : 2017-06-17
Version : 0.5.6
	Added normal polling function
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-07-24
Version : 0.5.9
	Updated max http contents size
Modifier : Jin Jeong (jungst0001@kaist.ac.kr)

Rev. history : 2018-07-11
Version : 0.7.2
	Added MNS_HOST and MNS_PORT.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-08-13
Version : 0.7.3
	From this version, this class reads system arguments and configurations from "MMS configuration/MMS.conf" file.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import org.apache.commons.cli.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MMSConfiguration {
	private static final String TAG = "[MMSConfiguration] ";
	private static final Logger logger = LoggerFactory.getLogger(MMSLog.class);
	
	private static boolean IS_MMS_CONF_SET = false;
	
	private static boolean[] WEB_LOG_PROVIDING = {false, false}; //{isSet, value}
	private static boolean[] WEB_MANAGING = {false, false}; //{isSet, value}
	
	private static int HTTP_PORT;
	private static int HTTPS_PORT;
	private static String MNS_HOST;
	private static int MNS_PORT = 8588;
	
	private static String MMS_MRN;
	
	private static int MAX_CONTENT_SIZE;
	private static int WAITING_MESSAGE_TIMEOUT;
	
	private static int MAX_BRIEF_LOG_LIST_SIZE;
	
	public MMSConfiguration (String[] args) {
		if (!IS_MMS_CONF_SET) {
			ConfigureMMSSettings (args);
		}
		IS_MMS_CONF_SET = true;
	}
	
	private void ConfigureMMSSettings (String[] args) {
		
		Options options = new Options();
		
		Option help = new Option("h","help", false, "Print a help message and exit.");
		help.setRequired(false);
		options.addOption(help);
		
		Option web_log_providing = new Option("wl","web_log_providing", true, "Enable this MMS provide web logging if the argument is [true], otherwise disable if [false]. Default is [true].");
		web_log_providing.setRequired(false);
		options.addOption(web_log_providing);
		
		Option web_managing = new Option("wm","web_managing", true, "Enable this MMS provide web management if the argument is [true], otherwise disable if [false]. Default is [true].");
		web_managing.setRequired(false);
		options.addOption(web_managing);
		
		Option http_port = new Option ("p","http_port", true, "Set the HTTP port number of this MMS.");
		http_port.setRequired(false);
		options.addOption(http_port);
		
		Option https_port = new Option ("sp","https_port", true, "Set the HTTPS port number of this MMS.");
		https_port.setRequired(false);
		options.addOption(https_port);
		
		Option mns_host = new Option ("mns","mns_host", true, "Set the host of the Maritime Name System server.");
		mns_host.setRequired(false);
		options.addOption(mns_host);
		
		Option mns_port = new Option ("mnsp","mns_port", true, "Set the port number of the Maritime Name System server.");
		mns_port.setRequired(false);
		options.addOption(mns_port);
		
		Option mms_mrn = new Option ("mrn","mms_mrn", true, "Set the Maritime Resource Name of this MMS.");
		mms_mrn.setRequired(false);
		options.addOption(mms_mrn);
		
		Option max_content_size = new Option ("c", "max_content_size", true, "Set the maximum content size of this MMS. The unit of size is Kilo Bytes.");
		max_content_size.setRequired(false);
		options.addOption(max_content_size);
		
		Option waiting_message_timeout = new Option ("t", "waiting_message_timeout", true, "Set the waiting message timeout of this MMS, when using sequence number in a message.");
		waiting_message_timeout.setRequired(false);
		options.addOption(waiting_message_timeout);
		
		Option max_brief_log_list_size = new Option ("mls", "max_brief_log_list_size", true, "Set the maximum list size of the brief log in the MMS status.");
		max_brief_log_list_size.setRequired(false);
		options.addOption(max_brief_log_list_size);
		
		CommandLineParser clParser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;
		
		try {
			cmd = clParser.parse(options, args);
			
			String usage = "java -jar ST20_MMSServer.jar "
					+ "[-c max_content_size] "
					+ "[-h] "
					+ "[-mls max_brief_log_list_size] "
					+ "[-mns mns_host] "
					+ "[-mnsp mns_port] "
					+ "[-mrn mms_mrn] "
					+ "[-p http_port] "
					+ "[-sp https_port] "
					+ "[-t waiting_message_timeout] "
					+ "[-wl web_log_providing] "
					+ "[-wm web_managing] ";
			
			if (cmd.hasOption("help")) {
				formatter.printHelp(usage, options);
				System.exit(0);
			}
			
			WEB_LOG_PROVIDING = getOptionValueBoolean(cmd, "web_log_providing");
			WEB_MANAGING = getOptionValueBoolean(cmd, "web_managing");
			HTTP_PORT = getOptionValueInteger(cmd, "http_port");
			HTTPS_PORT = getOptionValueInteger(cmd, "https_port");
			MNS_HOST = cmd.getOptionValue("mns_nost");
			MNS_PORT = getOptionValueInteger(cmd, "mns_port");

			String val = cmd.getOptionValue("mms_mrn");
			if (val != null) {
				MMS_MRN = val;
				if (!MMS_MRN.startsWith("urn:mrn:")) {
					logger.error(TAG+"Invalid MRN for MMS.");
					throw new IOException();
				}
			}
			
			MAX_CONTENT_SIZE = getOptionValueInteger(cmd, "max_content_size");
			WAITING_MESSAGE_TIMEOUT = getOptionValueInteger(cmd, "waiting_message_timeout");
			MAX_BRIEF_LOG_LIST_SIZE = getOptionValueInteger(cmd, "max_brief_log_list_size");
		}
		catch (org.apache.commons.cli.ParseException e){
			logger.error(TAG+e.getClass()+" "+e.getLocalizedMessage());
			formatter.printHelp("MMS options", options);
			
			System.exit(1);
		}
		catch (IOException e) {
			logger.error(TAG+e.getClass()+" "+e.getLocalizedMessage());
			
			System.exit(1);
		}
		
		JSONParser parser = new JSONParser();
		FileReader fr = null;
		try {
			File f = new File("./MMS configuration/MMS.conf");
			fr = new FileReader(f);
			JSONObject jobj = new JSONObject();
			jobj = (JSONObject) parser.parse(fr);
			
			if (Arrays.equals(WEB_LOG_PROVIDING, new boolean[] {false, false})) {
				WEB_LOG_PROVIDING = getConfValueBoolean(jobj, "WEB_LOG_RROVIDING");
			}
			
			if (Arrays.equals(WEB_MANAGING, new boolean[] {false, false})) {
				WEB_MANAGING = getConfValueBoolean(jobj, "WEB_MANAGING");
			}

			if (HTTP_PORT == 0) {
				HTTP_PORT = getConfValueInteger(jobj, "HTTP_PORT");
			}
			
			if (HTTPS_PORT == 0) {
				HTTPS_PORT = getConfValueInteger(jobj, "HTTPS_PORT");
			}
			
			if (MNS_HOST == null) {
				if (jobj.get("MNS_HOST") != null){
					MNS_HOST = (String) jobj.get("MNS_HOST");
				}
			}
			
			if (MNS_PORT == 0) {
				MNS_PORT = getConfValueInteger(jobj, "MNS_PORT");
			}
				
			if (MMS_MRN == null) {
				MMS_MRN = (String)jobj.get("MMS_MRN");
				if (!MMS_MRN.startsWith("urn:mrn:")) {
					logger.error(TAG+"Invalid MRN for MMS.");
					throw new IOException();
				}
			}
			
			if (MAX_CONTENT_SIZE == 0) {
				MAX_CONTENT_SIZE = 1024*getConfValueInteger(jobj, "MAX_CONTENT_SIZE");
			}

			if (WAITING_MESSAGE_TIMEOUT == 0) {
				WAITING_MESSAGE_TIMEOUT = getConfValueInteger(jobj, "WAITING_MESSAGE_TIMEOUT");
			}
			
			if (MAX_BRIEF_LOG_LIST_SIZE == 0) {
				MAX_BRIEF_LOG_LIST_SIZE = getConfValueInteger(jobj, "MAX_BRIEF_LOG_LIST_SIZE");
			}		
		}
		catch (FileNotFoundException e) {
			logger.error(TAG+e.getClass()+" "+e.getLocalizedMessage());
			//System.exit(2);
		}
		catch (IOException e) {
			logger.error(TAG+e.getClass()+" "+e.getLocalizedMessage());
			System.exit(3);
		}
		catch (org.json.simple.parser.ParseException e) {
			logger.error(TAG+e.getClass()+" "+e.getLocalizedMessage());
			System.exit(4);
		}
		finally {
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				logger.warn(TAG+e.getClass()+" "+e.getLocalizedMessage());
			}
			if (Arrays.equals(WEB_LOG_PROVIDING, new boolean[] {false, false})) {
				WEB_LOG_PROVIDING = new boolean[] {true, true};
			}
			
			if (Arrays.equals(WEB_MANAGING, new boolean[] {false, false})) {
				WEB_MANAGING = new boolean[] {true, true};
			}
			
			if (HTTP_PORT == 0) {
				HTTP_PORT = 8088; //Default is integer 8088.
			}
			
			if (HTTPS_PORT == 0) {
				HTTPS_PORT = 444; //Default is integer 444.
			}
			
			if (MNS_PORT == 0) {
				MNS_PORT = 8588; //Default is integer 8588.
			}
			
			if (MNS_HOST == null) {
				MNS_HOST = "127.0.0.1"; //Default is String "127.0.0.1".
			}
			
			if (MMS_MRN == null) {
				MMS_MRN = "urn:mrn:smart-navi:device:mms1"; //Default is String "urn:mrn:smart-navi:device:mms1".
			}
			
			if (MAX_CONTENT_SIZE == 0) {
				MAX_CONTENT_SIZE = 40*1024*1024; //Default is 40MB.
			}
				
			if (WAITING_MESSAGE_TIMEOUT == 0) {
				WAITING_MESSAGE_TIMEOUT = 3000; //Default is 3000ms.
			}
			
			if (MAX_BRIEF_LOG_LIST_SIZE == 0) {
				MAX_BRIEF_LOG_LIST_SIZE = 200; //Default is integer 200.
			}
			
			logger.warn(TAG+"WEB_LOG_PROVIDING="+WEB_LOG_PROVIDING[1]);
			logger.warn(TAG+"WEB_MANAGING="+WEB_MANAGING[1]);
			logger.warn(TAG+"MMS_MRN="+MMS_MRN);
			logger.warn(TAG+"HTTP_PORT="+HTTP_PORT);
			logger.warn(TAG+"HTTPS_PORT="+HTTPS_PORT);
			logger.warn(TAG+"MNS_HOST="+MNS_HOST);
			logger.warn(TAG+"MNS_PORT="+MNS_PORT);
			logger.warn(TAG+"MAX_CONTENT_SIZE="+MAX_CONTENT_SIZE+"bytes");
			logger.warn(TAG+"WAITING_MESSAGE_TIMEOUT="+WAITING_MESSAGE_TIMEOUT+"ms");
			logger.warn(TAG+"MAX_BRIEF_LOG_LIST_SIZE="+MAX_BRIEF_LOG_LIST_SIZE);
		}
	}

	public static boolean WEB_LOG_PROVIDING() {
		return WEB_LOG_PROVIDING[1];
	}

	public static boolean WEB_MANAGING() {
		return WEB_MANAGING[1];
	}

	public static int HTTP_PORT() {
		return HTTP_PORT;
	}

	public static int HTTPS_PORT() {
		return HTTPS_PORT;
	}

	public static String MNS_HOST() {
		return MNS_HOST;
	}

	public static int MNS_PORT() {
		return MNS_PORT;
	}

	public static String MMS_MRN() {
		return MMS_MRN;
	}

	public static int MAX_CONTENT_SIZE() {
		return MAX_CONTENT_SIZE;
	}

	public static int WAITING_MESSAGE_TIMEOUT() {
		return WAITING_MESSAGE_TIMEOUT;
	}

	public static int MAX_BRIEF_LOG_LIST_SIZE() {
		return MAX_BRIEF_LOG_LIST_SIZE;
	}
	
	private int getOptionValueInteger (CommandLine cmd, String opt) throws IOException {
		String val = cmd.getOptionValue(opt);
		if (val != null){
			try {
				Integer i = Integer.parseInt(val);
				return i;
			}
			catch (NumberFormatException e) {
				logger.error(TAG+e.getClass()+" "+e.getLocalizedMessage());
				throw new IOException();
			}
		}
		return 0;
	}

	private boolean[] getOptionValueBoolean (CommandLine cmd, String opt) throws IOException {
		String val = cmd.getOptionValue(opt);
		if (val != null) {
			val = val.toLowerCase();
			if (val.equals("true")) {
				return new boolean[] {true, true};
			}
			else if (val.equals("false")){
				return new boolean[] {true, false};
			}
			else {
				throw new IOException();
			}
		}
		return new boolean[] {false, false};
	}
	
	private boolean[] getConfValueBoolean (JSONObject jobj, String key) throws IOException {		
		
		if (jobj.get(key) != null){
			String str = (String) jobj.get(key);
			str = str.toLowerCase();
			if (str.equals("true")) {
				return new boolean[] {true, true};
			}
			else if (str.equals("false")){
				return new boolean[] {true, false};
			}
			else {
				throw new IOException();
			}
		}
		return new boolean[] {false, false};
	}
	
	private int getConfValueInteger (JSONObject jobj, String key) throws IOException {		
		if (jobj.get(key) != null){
			try {
				Integer i = Integer.parseInt((String)jobj.get(key));
				return i;
			}
			catch (NumberFormatException e) {
				logger.error(TAG+e.getClass()+" "+e.getLocalizedMessage());
				throw new IOException();
			}
		}
		return 0;
	}
}
