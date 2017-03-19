package kr.ac.kaist.mms_server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import one.utils.jre.OneUtilsJre;

public class JKSDecoder {
	public static void main(String[] args) {
		final File original = new File (System.getProperty("user.dir")+"/mmskeystore.jks");
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
