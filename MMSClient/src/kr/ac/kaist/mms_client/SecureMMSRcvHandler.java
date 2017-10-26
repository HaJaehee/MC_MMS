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
	
Rev. history : 2017-05-02
Version : 0.5.4
	Added setting response header
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
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

import sun.misc.BASE64Encoder;

class SecureMMSRcvHandler {
	HttpsServer server = null;
	SSLContext sslContext = null;
	
	HttpsReqHandler hrh = null;
	//OONI
	SecureFileReqHandler frh = null;
	//OONI

	private String TAG = "[SecureMMSRcvHandler] ";
	private static final String USER_AGENT = "MMSClient/0.6.0";
	private String clientMRN = null;
	
	SecureMMSRcvHandler(int port, String jksDirectory, String jksPassword) throws Exception{
		httpsServerConfigure(port, jksDirectory, jksPassword);
		hrh = new HttpsReqHandler();
        server.createContext("/", hrh);
        if(MMSConfiguration.LOGGING)System.out.println(TAG+"Context \"/\" is created");
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
        if(MMSConfiguration.LOGGING)System.out.println(TAG+"Context \""+context+"\" is created");
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
        if(MMSConfiguration.LOGGING)System.out.println(TAG+"Context \""+fileDirectory+fileName+"\" is created");
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
			System.out.println(TAG+"Server is not created!");
			return;			
		}
		if (hrh == null) {
			hrh = new HttpsReqHandler();
		}
		if (!context.startsWith("/")){
			context = "/" + context;
		}
        server.createContext(context, hrh);
        if(MMSConfiguration.LOGGING)System.out.println(TAG+"Context \""+context+"\" is added");
	}
	
	void addFileContext (String fileDirectory, String fileName) {
		if (server == null) {
			System.out.println(TAG+"Server is not created!");
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
        if(MMSConfiguration.LOGGING)System.out.println(TAG+"Context \""+fileDirectory+fileName+"\" is added");
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
            
            String receivedData = new String( _out.toByteArray(), Charset.forName("UTF-8")).trim();
            
            String message = receivedData;
            inH.put("Http-method", httpMethods);
            inH.put("Uri", uris);
            
            String response = this.processRequest(inH, message);
            Map<String,List<String>> myHdr = setResponseHeader();
            Map<String,List<String>> resHdr = t.getResponseHeaders();
            
    		if (myHdr != null) {
    			if(MMSConfiguration.LOGGING)System.out.println(TAG+"set headerfield[");
    			for (Iterator keys = myHdr.keySet().iterator() ; keys.hasNext() ;) {
    				String key = (String) keys.next();
    				ArrayList<String> value = (ArrayList<String>) myHdr.get(key);
    				if(MMSConfiguration.LOGGING)System.out.println(key+":"+value);
    				resHdr.put(key, value);
    			}
    			if(MMSConfiguration.LOGGING)System.out.println("]");
    			
    		} 
           
            t.sendResponseHeaders(setResponseCode(), response.length());
            OutputStream os = t.getResponseBody();
            BufferedWriter wr = new BufferedWriter(
    				new OutputStreamWriter(os,Charset.forName("UTF-8")));
            wr.write(response);
            wr.flush();
            wr.close();
            os.close();
        }
        
        private String processRequest(Map<String,List<String>> headerField, String message) {
    		return this.myReqCallback.respondToClient(headerField, message);
    	}
        
        private int setResponseCode() {
        	return this.myReqCallback.setResponseCode();
        }
        
        private Map<String,List<String>> setResponseHeader(){
        	return this.myReqCallback.setResponseHeader();
        }
    }
    //OONI
    class SecureFileReqHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	URI uri = t.getRequestURI();
        	String fileName = uri.toString();
        	if(MMSConfiguration.LOGGING)System.out.println(TAG+"File request: "+fileName);
        	
            fileName = System.getProperty("user.dir")+fileName.trim();
            File file = new File (fileName);
            BASE64Encoder base64Encoder = new BASE64Encoder();
            InputStream in = new FileInputStream(file);

            ByteArrayOutputStream byteOutStream=new ByteArrayOutputStream();

            int len=0;

            byte[] buf = new byte[1024];

            while((len=in.read(buf)) != -1){
            	byteOutStream.write(buf, 0, len);
            }

            byte fileArray[]=byteOutStream.toByteArray();
            byte encodeBytes[]=base64Encoder.encodeBuffer(fileArray).getBytes(); 
            
            in.close();
            byteOutStream.close();
            // ok, we are ready to send the response.
            t.sendResponseHeaders(200, encodeBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(encodeBytes,0,encodeBytes.length);
            os.flush();
            os.close();
        }
    }
    //OONI end
}
