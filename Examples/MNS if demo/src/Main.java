import java.util.Random;

/* -------------------------------------------------------- */
/** 
File name : Main.java
	This class includes a main method.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2018-08-21

*/
/* -------------------------------------------------------- */

public class Main {
	
	private static MRNInformationQuerier MRNInfoQuerier;
	private static MIH_MessageOutputChannel messageOutput; 
	private static String SESSION_ID = "";
	
	public static void main(String[] args) {
		
		initializeModule();
		
		String reply = "";
		
		//Sends a geocasting circle message to MNSDummy.  
		Random r = new Random();
		reply = requestDstInfo("SC", "*", r.nextFloat()>0.5? -90*r.nextFloat():90*r.nextFloat(), r.nextFloat()>0.5? -180*r.nextFloat():180*r.nextFloat(), 200+r.nextFloat()*200);
		System.out.println("reply="+reply);
		
		//Sends a geocasting polygon message to MNSDummy.  
		float[] geoLat = new float[] {r.nextFloat()>0.5? -90*r.nextFloat():90*r.nextFloat(),r.nextFloat()>0.5? -90*r.nextFloat():90*r.nextFloat(),r.nextFloat()>0.5? -90*r.nextFloat():90*r.nextFloat(),r.nextFloat()>0.5? -90*r.nextFloat():90*r.nextFloat(),r.nextFloat()>0.5? -90*r.nextFloat():90*r.nextFloat()}; 
		float[] geoLong = new float[] {r.nextFloat()>0.5? -180*r.nextFloat():180*r.nextFloat(),r.nextFloat()>0.5? -180*r.nextFloat():180*r.nextFloat(),r.nextFloat()>0.5? -180*r.nextFloat():180*r.nextFloat(),r.nextFloat()>0.5? -180*r.nextFloat():180*r.nextFloat(),r.nextFloat()>0.5? -180*r.nextFloat():180*r.nextFloat()};
		reply = requestDstInfo("SC", "*", geoLat, geoLong);
		System.out.println("reply="+reply);
		
		//Sends a unicasting message to MNSDummy.
		reply = requestDstInfo("SC", "SP", "127.0.0.1");
		System.out.println("reply="+reply);
		
	}
	
	//Initializes MRNInformationQuerier and MIH_MessageOutputChannel classes.
	private static void initializeModule(){
		MRNInfoQuerier = new MRNInformationQuerier();
		messageOutput = new MIH_MessageOutputChannel(SESSION_ID);
	}
	
	//Builds geocasting message using MRNInfoQuerier and returns reply from messageOutput.
	public static String requestDstInfo(String srcMRN, String dstMRN, float geoLat, float geoLong, float geoRadius) {
		String msg = MRNInfoQuerier.buildQuery("geocasting_circle", srcMRN, dstMRN, geoLat, geoLong, geoRadius);
		return messageOutput.sendToMNS(msg);
	}
	
	//Builds geocasting message using MRNInfoQuerier and returns reply from messageOutput.
	public static String requestDstInfo(String srcMRN, String dstMRN, float[] geoLat, float[] geoLong) {
		String msg = MRNInfoQuerier.buildQuery("geocasting_polygon", srcMRN, dstMRN, geoLat, geoLong);
		return messageOutput.sendToMNS(msg);
	}
	
	//Builds unicasting message using MRNInfoQuerier and returns reply from messageOutput.
	public static String requestDstInfo(String srcMRN, String dstMRN, String srcIP){
		String msg = MRNInfoQuerier.buildQuery("unicasting", srcMRN, dstMRN, srcIP);
		return messageOutput.sendToMNS(msg);
	}
	
	
}
