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
	From this version, this class reads system arguments and configurations from "MMS-configuration/MMS.conf" file.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-08-20
Version : 0.7.3
	From this version, this class reads environment argument.
Modifier : Jaehyun Park (jae519@kaist.ac.kr)

Rev. history : 2018-08-21
Version : 0.7.3
	Updated vague.
Modifier : Jaehyun Park (jae519@kaist.ac.kr)

Rev. history : 2018-09-21
Version : 0.8.0
	Updated Checking initialized variables in MMSConfiguration.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-09-21
Version : 0.8.0
	Updated RABBIT_MQ_HOST variable in MMSConfiguration.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-01-03
Version : 0.8.0
	Removed log level, console out, file out options from MMSConfiguration.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)


Rev. history : 2019-01-29
Version : 0.8.1
	Specify external logback.xml configuration file.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	
Rev. history: 2019-04-12
Version : 0.8.2
	Modified for coding rule conformity.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	
Rev. history: 2019-05-06
Version : 0.9.0
	Modified for coding conventions.
	Added Rabbit MQ port number, username and password configurations.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-09
Version : 0.9.0
	Added configuration of port number of Rabbit MQ management server.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-22
Version : 0.9.1
	Added RABBIT_MQ_MANAGING_PROTOCOL.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.cli.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.util.ContextInitializer;


public class MMSConfiguration {
	private static final String TAG = "[MMSConfiguration] ";
	
	
	private static boolean IS_MMS_CONF_SET = false;
	
	private static boolean[] WEB_LOG_PROVIDING = {false, false}; //{isSet, value}
	private static boolean[] WEB_MANAGING = {false, false}; //{isSet, value}
	
	private static boolean[] HTTPS_ENABLED = {false, false}; //{isSet, value}
	
	private static int HTTP_PORT = 0;
	private static int HTTPS_PORT = 0;
	private static String MNS_HOST = null;
	private static int MNS_PORT = 8588; //0;
	
	private static String MMS_MRN = null;
	
	private static int MAX_CONTENT_SIZE = 0;
	private static int WAITING_MESSAGE_TIMEOUT = 0;
	
	private static int MAX_BRIEF_LOG_LIST_SIZE = 0;

	
	private static String RABBIT_MQ_HOST = null;
	private static int RABBIT_MQ_PORT = 0;
	private static int RABBIT_MQ_MANAGING_PORT = 0;
	private static String RABBIT_MQ_MANAGING_PROTOCOL = null;
	private static String RABBIT_MQ_USER = null;
	private static String RABBIT_MQ_PASSWD = null;
	
	private static String KEYSTORE = null;
	
	@Deprecated
	private static final boolean POLLING_AUTH_SESSION = false;
	@Deprecated
	private static final boolean POLLING_AUTH_PERF = false;
	
	
	private static Logger logger = null;

	public MMSConfiguration (String[] args) {
		if (!IS_MMS_CONF_SET) {
			ConfigureMMSSettings (args);
		}
		IS_MMS_CONF_SET = true;
	}
	
	private void ConfigureMMSSettings (String[] args) {
		
		System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "./MMS-configuration/logback.xml");
		this.logger = LoggerFactory.getLogger(MMSLog.class);
		logger.error("Now setting MMS configuration.");
		
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
		
		Option https_enabled = new Option ("https","https", false, "Set the HTTPS enabled.");
		https_enabled.setRequired(false);
		options.addOption(https_enabled);
		
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
		
		Option max_content_size = new Option ("mc", "max_content_size", true, "Set the maximum content size of this MMS. The unit of size is Kilo Bytes.");
		max_content_size.setRequired(false);
		options.addOption(max_content_size);
		
		Option waiting_message_timeout = new Option ("t", "waiting_message_timeout", true, "Set the waiting message timeout of this MMS, when using sequence number in a message.");
		waiting_message_timeout.setRequired(false);
		options.addOption(waiting_message_timeout);
		
		Option max_brief_log_list_size = new Option ("mls", "max_brief_log_list_size", true, "Set the maximum list size of the brief log in the MMS status.");
		max_brief_log_list_size.setRequired(false);
		options.addOption(max_brief_log_list_size);
		
		Option rabbit_mq_host = new Option ("mqhost", "rabbit_mq_host", true, "Set the host of the Rabbit MQ server.");
		rabbit_mq_host.setRequired(false);
		options.addOption(rabbit_mq_host);
		
		Option rabbit_mq_port = new Option ("mqport", "rabbit_mq_port", true, "Set the port number of the Rabbit MQ server.");
		rabbit_mq_port.setRequired(false);
		options.addOption(rabbit_mq_port);
		
		Option rabbit_mq_managing_port = new Option ("mqmngport", "rabbit_mq_managing_port", true, "Set the port number of the Rabbit MQ management server.");
		rabbit_mq_managing_port.setRequired(false);
		options.addOption(rabbit_mq_managing_port);
		
		Option rabbit_mq_managing_protocol = new Option ("mqmngproto", "rabbit_mq_managing_protocol", true, "Set the protocol of the Rabbit MQ management server.");
		rabbit_mq_managing_protocol.setRequired(false);
		options.addOption(rabbit_mq_managing_protocol);
		
		Option rabbit_mq_user = new Option ("mquser", "rabbit_mq_user", true, "Set the username of the Rabbit MQ server.");
		rabbit_mq_user.setRequired(false);
		options.addOption(rabbit_mq_user);
		
		Option rabbit_mq_passwd = new Option ("mqpasswd", "rabbit_mq_passwd", true, "Set the password of the Rabbit MQ server.");
		rabbit_mq_passwd.setRequired(false);
		options.addOption(rabbit_mq_passwd);
		
		
		
		CommandLineParser clParser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;
		
		try {
			cmd = clParser.parse(options, args);
			
			String usage = "java -cp MC_MMS.jar kr.ac.kaist.mms_server.MMSServer"
					+ " [-h help]"
					+ " [-https https]"
					+ " [-mc max_content_size]"
					+ " [-mls max_brief_log_list_size]"
					+ " [-mns mns_host]"
					+ " [-mnsp mns_port]"
					+ " [-mrn mms_mrn]"
					+ " [-mqhost rabbit_mq_host]"
					+ " [-mqport rabbit_mq_port]"
					+ " [-mqmngport rabbit_mq_managing_port]"
					+ " [-mqmngproto rabbit_mq_managing_protocol]"
					+ " [-mquser rabbit_mq_user]"
					+ " [-mqpasswd rabbit_mq_passwd]"
					+ " [-p http_port]"
					+ " [-sp https_port]"
					+ " [-t waiting_message_timeout]"
					+ " [-wl web_log_providing]"
					+ " [-wm web_managing]";
					
					
			
			if (cmd.hasOption("help")) {
				formatter.printHelp(120, usage, "", options, "");
				Scanner sc = new Scanner(System.in);
				sc.nextLine();
				System.exit(0);
			}
			
			if (WEB_LOG_PROVIDING[0] == false) {
				WEB_LOG_PROVIDING = getOptionValueBoolean(cmd, "web_log_providing");
			}
			if (WEB_MANAGING[0] == false) {
				WEB_MANAGING = getOptionValueBoolean(cmd, "web_managing");
			}
			if (HTTP_PORT == 0) {
				HTTP_PORT = getOptionValueInteger(cmd, "http_port");
			}
			
			if (HTTPS_ENABLED[0] == false) {
				HTTPS_ENABLED = getOptionValueBoolean(cmd, "https");
			}
			if (HTTPS_ENABLED[1] && HTTPS_PORT == 0) {
				HTTPS_PORT = getOptionValueInteger(cmd, "https_port");
			}
			if (MNS_HOST == null) {
				MNS_HOST = cmd.getOptionValue("mns_host");
			}
			if (MNS_PORT == 0) {
				MNS_PORT = getOptionValueInteger(cmd, "mns_port");
			}
			
			if (RABBIT_MQ_HOST == null) {
				RABBIT_MQ_HOST = cmd.getOptionValue("rabbit_mq_host");
			}
			
			if (RABBIT_MQ_PORT == 0) {
				RABBIT_MQ_PORT = getOptionValueInteger(cmd, "rabbit_mq_port");
			}
			
			if (RABBIT_MQ_MANAGING_PORT == 0) {
				RABBIT_MQ_MANAGING_PORT = getOptionValueInteger(cmd, "rabbit_mq_managing_port");
			}
			
			if (RABBIT_MQ_MANAGING_PROTOCOL == null) {
				RABBIT_MQ_MANAGING_PROTOCOL = cmd.getOptionValue("rabbit_mq_managing_protocol");
			}
			
			if (RABBIT_MQ_USER == null) {
				RABBIT_MQ_USER = cmd.getOptionValue("rabbit_mq_user");
			}
			
			if (RABBIT_MQ_PASSWD == null) {
				RABBIT_MQ_PASSWD = cmd.getOptionValue("rabbit_mq_passwd");
			}
			
			if (MMS_MRN == null) {
				String val = cmd.getOptionValue("mms_mrn");
				if (val != null) {
					MMS_MRN = val;
					if (!MMS_MRN.startsWith("urn:mrn:")) {
						logger.error(TAG+"Invalid MRN for MMS.");
						throw new IOException();
					}
				}
			}
			
			if (MAX_CONTENT_SIZE == 0) {
				MAX_CONTENT_SIZE = 1024*getOptionValueInteger(cmd, "max_content_size");
			}
			if (WAITING_MESSAGE_TIMEOUT == 0) {
				WAITING_MESSAGE_TIMEOUT = getOptionValueInteger(cmd, "waiting_message_timeout");
			}
			if (MAX_BRIEF_LOG_LIST_SIZE == 0) {
				MAX_BRIEF_LOG_LIST_SIZE = getOptionValueInteger(cmd, "max_brief_log_list_size");
			}


		}
		catch (org.apache.commons.cli.ParseException | IOException e){
			logger.error(TAG+e.getClass()+" "+e.getLocalizedMessage());
			formatter.printHelp("MMS options", options);
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			sc.close();
			System.exit(1);
		}


		if (HTTP_PORT == 0) {
			String s = System.getenv("ENV_HTTP_PORT");
			if (s != null) {
				HTTP_PORT = Integer.parseInt(s);
			}
		}
		

		if (HTTPS_ENABLED[0] == false) {
			String s = System.getenv("ENV_HTTPS_ENABLED");
			if (s != null) {
				HTTPS_ENABLED[1] = Boolean.parseBoolean(s);
				HTTPS_ENABLED[0] = true;
			}
		}
		
		if (HTTPS_ENABLED[1] && HTTPS_PORT == 0) {
			String s = System.getenv("ENV_HTTPS_PORT");
			if (s != null) {
				HTTPS_PORT = Integer.parseInt(s);
			}
		}

		
		if (MNS_HOST == null) {
			String s = System.getenv("ENV_MNS_HOST");
			if (s != null) {
				MNS_HOST = s;
			}
		}
		
		if (MNS_PORT == 0) {
			String s = System.getenv("ENV_MNS_PORT");
			if (s != null) {
				MNS_PORT = Integer.parseInt(s); 
			}
		}
		
		if (RABBIT_MQ_HOST == null) {
			String s = System.getenv("ENV_RABBIT_MQ_HOST");
			if (s != null) {
				RABBIT_MQ_HOST = s;
			}
		}
		
		if (RABBIT_MQ_PORT == 0) {
			String s = System.getenv("ENV_RABBIT_MQ_PORT");
			if (s != null) {
				RABBIT_MQ_PORT = Integer.parseInt(s); 
			}
		}
		
		if (RABBIT_MQ_MANAGING_PORT == 0) {
			String s = System.getenv("ENV_RABBIT_MQ_MANAGING_PORT");
			if (s != null) {
				RABBIT_MQ_MANAGING_PORT = Integer.parseInt(s); 
			}
		}
		
		if (RABBIT_MQ_MANAGING_PROTOCOL == null) {
			String s = System.getenv("ENV_RABBIT_MQ_MANAGING_PROTOCOL");
			if (s != null) {
				RABBIT_MQ_MANAGING_PROTOCOL = s; 
			}
		}
		
		if (RABBIT_MQ_USER == null) {
			String s = System.getenv("ENV_RABBIT_MQ_USER");
			if (s != null) {
				RABBIT_MQ_USER = s;
			}
		}
		
		if (RABBIT_MQ_PASSWD == null) {
			String s = System.getenv("ENV_RABBIT_MQ_PASSWD");
			if (s != null) {
				RABBIT_MQ_PASSWD = s;
			}
		}
			
		if (MMS_MRN == null) {
			String s = System.getenv("ENV_MMS_MRN");
			if (s != null) {
				MMS_MRN = s;
				if (!MMS_MRN.startsWith("urn:mrn:")) {
					logger.error(TAG+"Invalid MRN for MMS.");
					try {
						throw new IOException();
					} catch (IOException e) {
						logger.warn(e.getClass().getName()+" "+e.getMessage()+" "+e.getStackTrace()[0]+".");
						for (int i = 1 ; i < e.getStackTrace().length && i < 5 ; i++) {
							logger.warn(e.getStackTrace()[i]+".");
						}
					}
				}
			}
		}
		
		if (MAX_CONTENT_SIZE == 0) {
			String s = System.getenv("ENV_MAX_CONTENT_SIZE");
			if (s != null)
				MAX_CONTENT_SIZE = 1024*Integer.parseInt(s);
		}

		if (WAITING_MESSAGE_TIMEOUT == 0) {
			String s = System.getenv("ENV_WAITING_MESSAGE_TIMEOUT");
			if (s != null)
				WAITING_MESSAGE_TIMEOUT = Integer.parseInt(s);
		}
		
		if (MAX_BRIEF_LOG_LIST_SIZE == 0) {
			String s = System.getenv("ENV_MAX_BRIEF_LOG_LIST_SIZE");
			if (s != null)
				MAX_BRIEF_LOG_LIST_SIZE = Integer.parseInt(s);
		}		
		
		
		
		
		
		JSONParser parser = new JSONParser();
		FileReader fr = null;
		boolean isError = false;
		try {
			File f = new File("./MMS-configuration/MMS.conf");
			fr = new FileReader(f);
			JSONObject jobj = new JSONObject();
			jobj = (JSONObject) parser.parse(fr);
			
			if (!WEB_LOG_PROVIDING[0]) {
				WEB_LOG_PROVIDING = getConfValueBoolean(jobj, "WEB_LOG_RROVIDING");
			}
			
			if (!WEB_MANAGING[0]) {
				WEB_MANAGING = getConfValueBoolean(jobj, "WEB_MANAGING");
			}

			if (HTTP_PORT == 0) {
				HTTP_PORT = getConfValueInteger(jobj, "HTTP_PORT");
			}
			
			if (HTTPS_ENABLED[0] == false) {
				HTTPS_ENABLED = getConfValueBoolean(jobj, "HTTPS_ENABLED");
			}
			
			if (HTTPS_ENABLED[1] && HTTPS_PORT == 0) {
				HTTPS_PORT = getConfValueInteger(jobj, "HTTPS_PORT");
			}

			if (HTTPS_ENABLED[1] && KEYSTORE == null) {
				KEYSTORE = (String) jobj.get("KEYSTORE");
			}
			
			if (MNS_HOST == null) {
				if (jobj.get("MNS_HOST") != null){
					MNS_HOST = (String) jobj.get("MNS_HOST");
				}
			}
			
			if (RABBIT_MQ_HOST == null) {
				if (jobj.get("RABBIT_MQ_HOST") != null){
					RABBIT_MQ_HOST = (String) jobj.get("RABBIT_MQ_HOST");
				}
			}
			if (RABBIT_MQ_PORT == 0) {
				if (jobj.get("RABBIT_MQ_PORT") != null){
					RABBIT_MQ_PORT = getConfValueInteger(jobj, "RABBIT_MQ_PORT");
				}
			}
			if (RABBIT_MQ_MANAGING_PORT == 0) {
				if (jobj.get("RABBIT_MQ_MANAGING_PORT") != null){
					RABBIT_MQ_MANAGING_PORT = getConfValueInteger(jobj, "RABBIT_MQ_MANAGING_PORT");
				}
			}
			if (RABBIT_MQ_MANAGING_PROTOCOL == null) {
				if (jobj.get("RABBIT_MQ_MANAGING_PROTOCOL") != null){
					RABBIT_MQ_MANAGING_PROTOCOL =  (String) jobj.get("RABBIT_MQ_MANAGING_PROTOCOL");
				}
			}
			if (RABBIT_MQ_USER == null) {
				if (jobj.get("RABBIT_MQ_USER") != null){
					RABBIT_MQ_USER = (String) jobj.get("RABBIT_MQ_USER");
				}
			}
			if (RABBIT_MQ_PASSWD == null) {
				if (jobj.get("RABBIT_MQ_PASSWD") != null){
					RABBIT_MQ_PASSWD = (String) jobj.get("RABBIT_MQ_PASSWD");
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
			
			
			
			
			fr.close();
			fr = null;
		}
		catch (IOException | org.json.simple.parser.ParseException e) {
			logger.error(TAG+e.getClass()+" "+e.getLocalizedMessage());
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			sc.close();
			isError = true;
		}
		finally {
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				logger.warn(TAG+e.getClass()+" "+e.getLocalizedMessage());
			}
			
			if (isError) {
				System.exit(1);
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
			
			if (HTTPS_ENABLED[1] && HTTPS_PORT == 0) {
				HTTPS_PORT = 444; //Default is integer 444.
			}
			
			if (MNS_PORT == 0) {
				MNS_PORT = 8588; //Default is integer 8588.
			}
			
			if (MNS_HOST == null) {
				MNS_HOST = "localhost"; //Default is String "localhost".
			}
			
			if (RABBIT_MQ_HOST == null) {
				RABBIT_MQ_HOST = "localhost"; //Default is String "localhost".
			}
			
			if (RABBIT_MQ_PORT == 0) {
				RABBIT_MQ_PORT = 5672; //Default is integer 5672.
			}
			
			if (RABBIT_MQ_MANAGING_PORT == 0) {
				RABBIT_MQ_MANAGING_PORT = 15672; //Default is integer 15672.
			}
			
			if (RABBIT_MQ_MANAGING_PROTOCOL == null) {
				RABBIT_MQ_MANAGING_PROTOCOL = "http"; //Default is http.
			}
			
			if (RABBIT_MQ_USER == null) {
				RABBIT_MQ_USER = "guest"; //Default is String "guest".
			}
			
			if (RABBIT_MQ_PASSWD == null) {
				RABBIT_MQ_PASSWD = "guest"; //Default is String "guest".
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
			
			if (HTTPS_ENABLED[1] && KEYSTORE == null) {
				KEYSTORE = "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";
			}
			
			
			logger.warn(TAG+"MMS_MRN="+MMS_MRN);
			logger.warn(TAG+"HTTP_PORT="+HTTP_PORT);
			logger.warn(TAG+"HTTPS_ENABLED="+HTTPS_ENABLED[1]);
			if (HTTPS_ENABLED[1]) {
				logger.warn(TAG+"HTTPS_PORT="+HTTPS_PORT);
			}
			logger.warn(TAG+"MNS_HOST="+MNS_HOST);
			logger.warn(TAG+"MNS_PORT="+MNS_PORT);
			logger.warn(TAG+"RABBIT_MQ_HOST="+RABBIT_MQ_HOST);
			logger.warn(TAG+"RABBIT_MQ_PORT="+RABBIT_MQ_PORT);
			logger.warn(TAG+"RABBIT_MQ_MANAGING_PORT="+RABBIT_MQ_MANAGING_PORT);
			logger.warn(TAG+"RABBIT_MQ_MANAGING_PROTOCOL="+RABBIT_MQ_MANAGING_PROTOCOL);
			logger.warn(TAG+"RABBIT_MQ_USER="+RABBIT_MQ_USER);
			logger.warn(TAG+"RABBIT_MQ_PASSWD="+RABBIT_MQ_PASSWD);
			logger.warn(TAG+"WEB_LOG_PROVIDING="+WEB_LOG_PROVIDING[1]);
			logger.warn(TAG+"WEB_MANAGING="+WEB_MANAGING[1]);
			logger.warn(TAG+"MAX_BRIEF_LOG_LIST_SIZE="+MAX_BRIEF_LOG_LIST_SIZE);
			logger.warn(TAG+"MAX_CONTENT_SIZE="+MAX_CONTENT_SIZE+"bytes");
			logger.warn(TAG+"WAITING_MESSAGE_TIMEOUT="+WAITING_MESSAGE_TIMEOUT+"ms");
			if (HTTPS_ENABLED[1]) {
				logger.warn(TAG+"KEYSTORE="+KEYSTORE);
			}
			
		}
	}
	
	@Deprecated
	public static boolean isPollingSessionOn() {
		return POLLING_AUTH_SESSION;
	}
	@Deprecated
	public static boolean isPollingTest() {
		return POLLING_AUTH_PERF;
	}

	public static boolean isWebLogProviding() {
		return WEB_LOG_PROVIDING[1];
	}

	public static boolean isWebManaging() {
		return WEB_MANAGING[1];
	}

	public static int getHttpPort() {
		return HTTP_PORT;
	}

	public static boolean isHttpsEnabled () {
		return HTTPS_ENABLED[1];
	}
	public static int getHttpsPort() {
		return HTTPS_PORT;
	}

	public static String getMnsHost() {
		return MNS_HOST;
	}

	public static int getMnsPort() {
		return MNS_PORT;
	}

	public static String getMmsMrn() {
		return MMS_MRN;
	}

	public static int getMaxContentSize() {
		return MAX_CONTENT_SIZE;
	}

	public static int getWaitingMessageTimeout() {
		return WAITING_MESSAGE_TIMEOUT;
	}

	public static int getMaxBriefLogListSize() {
		return MAX_BRIEF_LOG_LIST_SIZE;
	}
	
	public static String getRabbitMqHost() {
		return RABBIT_MQ_HOST;
	}
	
	public static int getRabbitMqPort() {
		return RABBIT_MQ_PORT;
	}
	
	public static int getRabbitMqManagingPort() {
		return RABBIT_MQ_MANAGING_PORT;
	}
	
	public static String getRabbitMqManagingProtocol() {
		return RABBIT_MQ_MANAGING_PROTOCOL;
	}
	
	public static String getRabbitMqUser() {
		return RABBIT_MQ_USER;
	}
	
	public static String getRabbitMqPasswd() {
		return RABBIT_MQ_PASSWD;
	}
	
	public static String getKeystore() {
		return KEYSTORE;
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
