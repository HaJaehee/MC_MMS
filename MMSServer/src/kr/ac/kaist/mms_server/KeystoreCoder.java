package kr.ac.kaist.mms_server;
/* -------------------------------------------------------- */
/** 
File name : KeystoreCoder.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-04-25
Version : 0.5.0
*/
/* -------------------------------------------------------- */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import one.utils.jre.OneUtilsJre;

public class KeystoreCoder {
	
	public static void main(String[] args) {
		 final File original = new File(System.getProperty("user.dir")+"/mykeystore.jks");
		    
		 try {
			System.out.println(Base64Coder.encode(OneUtilsJre.toByteArray(new FileInputStream(original))));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
   
}
