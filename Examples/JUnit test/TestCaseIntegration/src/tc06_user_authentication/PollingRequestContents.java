package tc06_user_authentication;

import org.json.simple.JSONObject;

/** 
File name : PollingRequestContents.java
	This class is in the MMSClientHandler.
Author : Jin Jeong (jungst0001@kaist.ac.kr)
Creation Date : 2019-04-16

*/

public class PollingRequestContents {
		private String svcMRN = null;
		private String certificate = null;
		
		PollingRequestContents (String serviceMRN, String certificate){
			this.svcMRN = serviceMRN;
			this.certificate = certificate;
		}
		
		private JSONObject makeJSONData(){
			JSONObject data = new JSONObject();
			
			if (this.svcMRN != null) {
				data.put("svcMRN", this.svcMRN);
			}
			
			if (this.certificate != null) {
				data.put("certificate", this.certificate);
			}
		
			return data;
		}
		
		@Override
		public String toString(){
			String contents = this.makeJSONData().toJSONString();
			
//			System.out.println("[Test Message] : \n" + contents);
			
			return contents;
		}
		
		public void setServiceMRN(String svcMRN) {
			this.svcMRN = svcMRN;
		}
		
		public void setCertificate(String certificate) {
			this.certificate = certificate;
		}
		
//		String getServiceMRN() {
//			return this.svcMRN;
//		}
//		
//		String getCertificate() {
//			return this.certificate;
//		}
	}