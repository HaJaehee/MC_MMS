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

Rev. history: 2019-05-27
Version : 0.9.1
	Modified for requiring MMS keystore in MMS.conf.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history: 2019-05-29
Version : 0.9.1
	Added MMS configuration querying api.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-07
Version : 0.9.2
	Made logs neat.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-14
Version : 0.9.2
	Added RABBIT_MQ_CONN_POOL.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2019-06-18
Version : 0.9.2
	Added ErrorCode.
Modifier : Jaehee ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.SystemUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.util.ContextInitializer;


public class MMSConfiguration {
	private static final String TAG = " ";
	
	
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
	private static String RABBIT_MQ_MANAGING_HOST = null;
	private static int RABBIT_MQ_MANAGING_PORT = 0;
	private static String RABBIT_MQ_MANAGING_PROTOCOL = null;
	private static String RABBIT_MQ_USER = null;
	private static String RABBIT_MQ_PASSWD = null;
	private static int RABBIT_MQ_CONN_POOL = 0;
	
	private static String KEYSTORE = null;
	
	private static Map<String, String> MMS_CONFIGURATION = null;
	
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

		if (SystemUtils.IS_OS_WINDOWS) {
			System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "./MMS-configuration/logback-Windows.xml");
		}
		else if (SystemUtils.IS_OS_LINUX) {
			System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "./MMS-configuration/logback-Linux.xml");
		}
		this.logger = LoggerFactory.getLogger(MMSConfiguration.class);
		logger.error("Now setting MMS configuration.");
		MMS_CONFIGURATION = new HashMap<String, String>();
		
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
		
		Option rabbit_mq_mng_host = new Option ("mqmnghost", "rabbit_mq_managing_host", true, "Set the host of the Rabbit MQ management server.");
		rabbit_mq_mng_host.setRequired(false);
		options.addOption(rabbit_mq_mng_host);
		
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
		
		Option rabbit_mq_conn_pool = new Option ("mqconnpool", "rabbit_mq_conn_pool", true, "Set the size of Rabbit MQ connection pool.");
		rabbit_mq_conn_pool.setRequired(false);
		options.addOption(rabbit_mq_conn_pool);
		
		
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
					+ " [-mqmnghost rabbit_mq_managing_host]"
					+ " [-mqmngport rabbit_mq_managing_port]"
					+ " [-mqmngproto rabbit_mq_managing_protocol]"
					+ " [-mquser rabbit_mq_user]"
					+ " [-mqpasswd rabbit_mq_passwd]"
					+ " [-mqconnpool rabbit_mq_conn_pool]"
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
			
			if (RABBIT_MQ_MANAGING_HOST == null) {
				RABBIT_MQ_MANAGING_HOST = cmd.getOptionValue("rabbit_mq_managing_host");
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
			
			if (RABBIT_MQ_CONN_POOL == 0) {
				RABBIT_MQ_CONN_POOL = getOptionValueInteger(cmd, "rabbit_mq_conn_pool");
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
			logger.error(TAG+e.getClass()+ErrorCode.CONFIGURATION_ERROR.toString()+e.getLocalizedMessage());
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
		
		if (RABBIT_MQ_MANAGING_HOST == null) {
			String s = System.getenv("ENV_RABBIT_MQ_MANAGING_HOST");
			if (s != null) {
				RABBIT_MQ_MANAGING_HOST = s; 
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
		
		if (RABBIT_MQ_CONN_POOL == 0) {
			String s = System.getenv("ENV_RABBIT_MQ_CONN_POOL");
			if (s != null) {
				RABBIT_MQ_CONN_POOL = Integer.parseInt(s);
			}
		}
			
		if (MMS_MRN == null) {
			String s = System.getenv("ENV_MMS_MRN");
			if (s != null) {
				MMS_MRN = s;
				if (!MMS_MRN.startsWith("urn:mrn:")) {
					logger.error(TAG+ErrorCode.CONFIGURATION_ERROR.toString()+"Invalid MRN for MMS.");
					try {
						throw new IOException();
					} catch (IOException e) {
						logger.warn(e.getClass().getName()+ErrorCode.CONFIGURATION_ERROR.toString()+"Invalid MRN for MMS."+e.getMessage()+" "+e.getStackTrace()[0]+".");
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
			if (f.exists()) {
				logger.warn(TAG+"Found recource [./MMS-configuration/MMS.conf].");
				logger.warn(TAG+"Configuration items that are set by CLI arguments are ignored from resource [./MMS-configuration/MMS.conf].");
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
				if (RABBIT_MQ_MANAGING_HOST == null) {
					RABBIT_MQ_MANAGING_HOST = (String) jobj.get("RABBIT_MQ_MANAGING_HOST");
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
				if (RABBIT_MQ_CONN_POOL == 0) {
					if (jobj.get("RABBIT_MQ_CONN_POOL") != null) {
						RABBIT_MQ_CONN_POOL = getConfValueInteger(jobj, "RABBIT_MQ_CONN_POOL");
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
		}
		catch (IOException | org.json.simple.parser.ParseException e) {
			logger.error(TAG+e.getClass()+ErrorCode.CONFIGURATION_ERROR.toString()+e.getLocalizedMessage());
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
				logger.warn(TAG+e.getClass()+ErrorCode.CONFIGURATION_ERROR.toString()+e.getLocalizedMessage());
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
			
			if (RABBIT_MQ_MANAGING_HOST == null) {
				RABBIT_MQ_MANAGING_HOST = "localhost"; //Default is String "localhost".
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
			
			if (RABBIT_MQ_CONN_POOL == 0) {
				RABBIT_MQ_CONN_POOL = 250; //Default is 250.
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
			
			alertAndSetMmsConf("MMS_MRN",MMS_MRN);
			alertAndSetMmsConf("HTTP_PORT",HTTP_PORT);
			alertAndSetMmsConf("HTTPS_ENABLED",HTTPS_ENABLED[1]);
			if (HTTPS_ENABLED[1]) {
				alertAndSetMmsConf("HTTPS_PORT",HTTPS_PORT);
			}
			alertAndSetMmsConf("MNS_HOST",MNS_HOST);
			alertAndSetMmsConf("MNS_PORT",MNS_PORT);
			alertAndSetMmsConf("RABBIT_MQ_HOST",RABBIT_MQ_HOST);
			alertAndSetMmsConf("RABBIT_MQ_PORT",RABBIT_MQ_PORT);
			alertAndSetMmsConf("RABBIT_MQ_MANAGING_HOST",RABBIT_MQ_MANAGING_HOST);
			alertAndSetMmsConf("RABBIT_MQ_MANAGING_PORT",RABBIT_MQ_MANAGING_PORT);
			alertAndSetMmsConf("RABBIT_MQ_MANAGING_PROTOCOL",RABBIT_MQ_MANAGING_PROTOCOL);
			alertAndSetMmsConf("RABBIT_MQ_USER",RABBIT_MQ_USER);
			alertAndSetMmsConf("RABBIT_MQ_PASSWD",RABBIT_MQ_PASSWD);
			alertAndSetMmsConf("RABBIT_MQ_CONN_POOL", RABBIT_MQ_CONN_POOL);
			alertAndSetMmsConf("WEB_LOG_PROVIDING",WEB_LOG_PROVIDING[1]);
			alertAndSetMmsConf("WEB_MANAGING",WEB_MANAGING[1]);
			alertAndSetMmsConf("MAX_BRIEF_LOG_LIST_SIZE",MAX_BRIEF_LOG_LIST_SIZE);
			alertAndSetMmsConf("MAX_CONTENT_SIZE",MAX_CONTENT_SIZE+"bytes");
			alertAndSetMmsConf("WAITING_MESSAGE_TIMEOUT",WAITING_MESSAGE_TIMEOUT+"ms");
			if (HTTPS_ENABLED[1]) {
				alertAndSetMmsConf("KEYSTORE",KEYSTORE.substring(0, 50)+"......");
			}
			
		}
	}
	
	private void alertAndSetMmsConf (String arg1, boolean arg2) {
		logger.warn(TAG+arg1+"="+arg2);
		MMS_CONFIGURATION.put(arg1,arg2+"");
	}
	

	private void alertAndSetMmsConf (String arg1, int arg2) {
		logger.warn(TAG+arg1+"="+arg2);
		MMS_CONFIGURATION.put(arg1,arg2+"");
	}
	
	private void alertAndSetMmsConf (String arg1, String arg2) {
		logger.warn(TAG+arg1+"="+arg2);
		MMS_CONFIGURATION.put(arg1,arg2);
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
	
	public static String getRabbitMqManagingHost() {
		return RABBIT_MQ_MANAGING_HOST;
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
	
	public static Map<String, String> getMmsConfiguration() {
		return MMS_CONFIGURATION;
	}
	
	public static int getRabbitMqConnPool() {
		return RABBIT_MQ_CONN_POOL;
	}
	
	private int getOptionValueInteger (CommandLine cmd, String opt) throws IOException {
		String val = cmd.getOptionValue(opt);
		if (val != null){
			try {
				Integer i = Integer.parseInt(val);
				return i;
			}
			catch (NumberFormatException e) {
				logger.error(TAG+e.getClass()+ErrorCode.CONFIGURATION_ERROR.toString()+e.getLocalizedMessage());
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
				logger.error(TAG+e.getClass()+ErrorCode.CONFIGURATION_ERROR.toString()+e.getLocalizedMessage());
				throw new IOException();
			}
		}
		return 0;
	}
}
