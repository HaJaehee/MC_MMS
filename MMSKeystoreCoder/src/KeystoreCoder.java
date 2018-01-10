/* -------------------------------------------------------- */
/** 
File name : KeystoreCoder.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-25
Version : 0.5.0

Rev. history : 2017-04-29
Version : 0.5.3
	Added system log features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-06-19
Version : 0.5.7
	Applied LogBack framework in order to log events.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2018-01-10
Version : 0.7.1
	Replaced from mykeystore.jks to cert.jks.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import one.utils.jre.OneUtilsJre;

public class KeystoreCoder {
	//private static final Logger logger = LoggerFactory.getLogger(KeystoreCoder.class);

	public static void main(String[] args) {
		 final File original = new File(System.getProperty("user.dir")+"/cert.jks");
		    
		 try {
			System.out.println(Base64Coder.encode(OneUtilsJre.toByteArray(new FileInputStream(original))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.getMessage();
		}

	}
   
}
