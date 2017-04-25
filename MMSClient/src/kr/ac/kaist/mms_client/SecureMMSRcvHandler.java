package kr.ac.kaist.mms_client;

/* -------------------------------------------------------- */
/** 
File name : SecureMMSRcvHandler.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-03-21
Version : 0.4.0

Rev. history : 2017-04-25
Version : 0.5.0
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
	Moved PollHandler class into MMSPollHandler.java
*/
/* -------------------------------------------------------- */

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

public class SecureMMSRcvHandler {
	HttpsServer server = null;
	SSLContext sslContext = null;
	
	HttpsReqHandler hrh = null;
	//OONI
	SecureFileReqHandler frh = null;
	//OONI

	private static final String USER_AGENT = "MMSClient/0.5.0";
	private String clientMRN = null;
	
	SecureMMSRcvHandler(int port, String jksDirectory, String jksPassword) throws Exception{
		httpsServerConfigure(port, jksDirectory, jksPassword);
		hrh = new HttpsReqHandler();
        server.createContext("/", hrh);
        if(MMSConfiguration.LOGGING)System.out.println("Context \"/\" is created");
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	SecureMMSRcvHandler(int port, String context, String jksDirectory, String jksPassword) throws Exception {
		httpsServerConfigure(port, jksDirectory, jksPassword);
		hrh = new HttpsReqHandler();
		if (!context.startsWith("/")){
			context = "/" + context;
		}
		
        server.createContext(context, hrh);
        if(MMSConfiguration.LOGGING)System.out.println("Context \""+context+"\" is created");
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	SecureMMSRcvHandler(int port, String fileDirectory, String fileName, String jksDirectory, String jksPassword) throws Exception {
		httpsServerConfigure(port, jksDirectory, jksPassword);
        //OONI
        frh = new SecureFileReqHandler();
        if (!fileDirectory.startsWith("/")){
        	fileDirectory = "/" + fileDirectory;
		}
        if(!fileDirectory.endsWith("/")&&!fileName.startsWith("/")){
        	fileName = "/" + fileName;
        }
        if(fileDirectory.endsWith("/")&&fileName.startsWith("/")){
        	fileName = fileName.substring(1);
        }
        server.createContext(fileDirectory+fileName, frh);
        if(MMSConfiguration.LOGGING)System.out.println("Context \""+fileDirectory+fileName+"\" is created");
        //OONI
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	void httpsServerConfigure (int port, String jksDirectory, String jksPassword) throws Exception{
		
		server = HttpsServer.create(new InetSocketAddress(port), 0);
		sslContext = SSLContext.getInstance( "TLS" );

		 // initialise the keystore
	    char[] jksPwCharArr = jksPassword.toCharArray ();
	    KeyStore ks = KeyStore.getInstance ( "JKS" );
	    //FileInputStream fis = new FileInputStream ( System.getProperty("user.dir")+"/testkey.jks" );
	    FileInputStream fis = new FileInputStream ( jksDirectory );
	    ks.load ( fis, jksPwCharArr );

	    // setup the key manager factory
	    KeyManagerFactory kmf = KeyManagerFactory.getInstance ( "SunX509" );
	    kmf.init ( ks, jksPwCharArr );

	    // setup the trust manager factory
	    TrustManagerFactory tmf = TrustManagerFactory.getInstance ( "SunX509" );
	    tmf.init ( ks );

	    // setup the HTTPS context and parameters
	    sslContext.init ( kmf.getKeyManagers (), tmf.getTrustManagers (), null );
	    server.setHttpsConfigurator ( new HttpsConfigurator( sslContext )
	    {
	        public void configure ( HttpsParameters params )
	        {
	            try
	            {
	                // initialise the SSL context
	                SSLContext c = SSLContext.getDefault ();
	                SSLEngine engine = c.createSSLEngine ();
	                params.setNeedClientAuth ( false );
	                params.setCipherSuites ( engine.getEnabledCipherSuites () );
	                params.setProtocols ( engine.getEnabledProtocols () );

	                // get the default parameters
	                SSLParameters defaultSSLParameters = c.getDefaultSSLParameters ();
	                params.setSSLParameters ( defaultSSLParameters );
	            }
	            catch ( Exception ex )
	            {
	                System.err.println( "Failed to create HTTPS port" );
	            }
	        }
	    } );
	    
	}
	
	void addContext (String context) {
		if (server == null) {
			System.out.println("Server is not created!");
			return;			
		}
		if (hrh == null) {
			hrh = new HttpsReqHandler();
		}
		if (!context.startsWith("/")){
			context = "/" + context;
		}
        server.createContext(context, hrh);
        if(MMSConfiguration.LOGGING)System.out.println("Context \""+context+"\" is added");
	}
	
	void addFileContext (String fileDirectory, String fileName) {
		if (server == null) {
			System.out.println("Server is not created!");
			return;
		}
		if (frh == null) {
			frh = new SecureFileReqHandler();
		}
        if (!fileDirectory.startsWith("/")){
        	fileDirectory = "/" + fileDirectory;
		}
        if(!fileDirectory.endsWith("/")&&!fileName.startsWith("/")){
        	fileName = "/" + fileName;
        }
        if(fileDirectory.endsWith("/")&&fileName.startsWith("/")){
        	fileName = fileName.substring(1);
        }
        server.createContext(fileDirectory+fileName, frh);
        if(MMSConfiguration.LOGGING)System.out.println("Context \""+fileDirectory+fileName+"\" is added");
	}
	
	class HttpsReqHandler implements HttpHandler {
    	SecureMMSClientHandler.RequestCallback myReqCallback;
    	
       	public void setRequestCallback(SecureMMSClientHandler.RequestCallback callback){
    		this.myReqCallback = callback;
    	}
    	
        @Override
        public void handle(HttpExchange t) throws IOException {
        	URI uri = t.getRequestURI();
        	List<String> uris = new ArrayList<String>();
        	uris.add(uri.toString());
        	String httpMethod = t.getRequestMethod();
        	List<String> httpMethods = new ArrayList<String>();
        	httpMethods.add(httpMethod);
        	
        	InputStream inB = t.getRequestBody();
        	Map<String,List<String>> inH = t.getRequestHeaders();
            ByteArrayOutputStream _out = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            int read = 0;
            while ((read = inB.read(buf)) != -1) {
                _out.write(buf, 0, read);
            }
            
            String receivedData = new String( buf, Charset.forName("UTF-8")).trim();

            String message = receivedData;
            inH.put("Http-method", httpMethods);
            inH.put("Uri", uris);
            
            String response = this.processRequest(inH, message);
            
           
            t.sendResponseHeaders(setResponseCode(), response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.flush();
            os.close();
        }
        
        private String processRequest(Map<String,List<String>> headerField, String message) {
    		return this.myReqCallback.respondToClient(headerField, message);
    	}
        
        private int setResponseCode() {
        	return this.myReqCallback.setResponseCode();
        }
    }
    //OONI
    class SecureFileReqHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	URI uri = t.getRequestURI();
        	String fileName = uri.toString();
        	if(MMSConfiguration.LOGGING)System.out.println("File request: "+fileName);
        	
            fileName = System.getProperty("user.dir")+fileName.trim();
            File file = new File (fileName);
            byte [] bytearray  = new byte [(int)file.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            bis.read(bytearray, 0, bytearray.length);
            
            // ok, we are ready to send the response.
            t.sendResponseHeaders(200, file.length());
            OutputStream os = t.getResponseBody();
            os.write(bytearray,0,bytearray.length);
            os.flush();
            os.close();
            
            bis.close();
        }
    }
    //OONI end
}
