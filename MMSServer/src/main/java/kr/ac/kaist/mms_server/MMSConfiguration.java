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

			if (HTTPS_ENABLED[1]) {
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
			
			/*if (LOG_LEVEL == null) {
				if (jobj.get("LOG_LEVEL") != null){
					LOG_LEVEL = (String) jobj.get("LOG_LEVEL");
					
				}
			}
			
			if (!LOG_FILE_OUT[0]) {
				LOG_FILE_OUT = getConfValueBoolean(jobj, "LOG_FILE_OUT");
			}
			
			if (!LOG_CONSOLE_OUT[0]) {
				LOG_CONSOLE_OUT = getConfValueBoolean(jobj, "LOG_CONSOLE_OUT");
			}*/
			
			
			fr.close();
			fr = null;
		}
		catch (FileNotFoundException e) {
			logger.error(TAG+e.getClass()+" "+e.getLocalizedMessage());
		}
		catch (IOException e) {
			logger.error(TAG+e.getClass()+" "+e.getLocalizedMessage());
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			sc.close();
			isError = true;
		}
		catch (org.json.simple.parser.ParseException e) {
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
			
			if (HTTPS_PORT == 0) {
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
