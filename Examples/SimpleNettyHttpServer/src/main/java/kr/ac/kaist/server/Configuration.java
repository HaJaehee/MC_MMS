package kr.ac.kaist.server;

import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.cli.*;

public class Configuration {
	private static final String TAG = "[MMSConfiguration] ";
	
	
	private static boolean IS_MMS_CONF_SET = false;
	
	private static int HTTP_PORT = 0;

	private static int MAX_CONTENT_SIZE = 0;


	public Configuration (String[] args) {
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
		
		
		Option http_port = new Option ("p","http_port", true, "Set the HTTP port number of this MMS.");
		http_port.setRequired(false);
		options.addOption(http_port);
		
		Option max_content_size = new Option ("mc", "max_content_size", true, "Set the maximum content size of this MMS. The unit of size is Kilo Bytes.");
		max_content_size.setRequired(false);
		options.addOption(max_content_size);
		

		
		CommandLineParser clParser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;
		
		try {
			cmd = clParser.parse(options, args);
			
			String usage = "java -jar SimpleHttpServer.jar"
					+ " [-h help]"
					+ " [-mc max_content_size]"
					+ " [-p http_port]";
					
			
			if (cmd.hasOption("help")) {
				formatter.printHelp(120, usage, "", options, "");
				Scanner sc = new Scanner(System.in);
				sc.nextLine();
				System.exit(0);
			}
			

			if (HTTP_PORT == 0) {
				HTTP_PORT = getOptionValueInteger(cmd, "http_port");
			}

			
			if (MAX_CONTENT_SIZE == 0) {
				MAX_CONTENT_SIZE = 1024*getOptionValueInteger(cmd, "max_content_size");
			}

		}
		catch (org.apache.commons.cli.ParseException e){
			formatter.printHelp("MMS options", options);
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			System.exit(1);
		}
		catch (IOException e) {
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			System.exit(1);
		}
		


		if (HTTP_PORT == 0) {
			String s = System.getenv("ENV_HTTP_PORT");
			if (s != null)
				HTTP_PORT = Integer.parseInt(s);
		}
		

		
		if (MAX_CONTENT_SIZE == 0) {
			String s = System.getenv("ENV_MAX_CONTENT_SIZE");
			if (s != null)
				MAX_CONTENT_SIZE = 1024*Integer.parseInt(s);
		}

		
		
		

			
			
		if (HTTP_PORT == 0) {
			HTTP_PORT = 8090; //Default is integer 8090.
		}
		
		
		
		if (MAX_CONTENT_SIZE == 0) {
			MAX_CONTENT_SIZE = 40*1024*1024; //Default is 40MB.
		}
			
		
	}


	public static int HTTP_PORT() {
		return HTTP_PORT;
	}


	public static int MAX_CONTENT_SIZE() {
		return MAX_CONTENT_SIZE;
	}
	
	private int getOptionValueInteger (CommandLine cmd, String opt) throws IOException {
		String val = cmd.getOptionValue(opt);
		if (val != null){
			try {
				Integer i = Integer.parseInt(val);
				return i;
			}
			catch (NumberFormatException e) {
				throw new IOException();
			}
		}
		return 0;
	}


}
