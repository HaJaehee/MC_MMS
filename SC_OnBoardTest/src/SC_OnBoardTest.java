import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/* -------------------------------------------------------- */
/** 
File name : SC_OnBoardTest.java
	Service Consumer used in onBoardTest 
Author : Jaehyun Park (jae519@kaist.ac.kr)
Creation Date : 2017-11-14

*/
/* -------------------------------------------------------- */

public class SC_OnBoardTest extends JFrame {
	
	JButton button_Enable = new JButton("Enable Communication");
	JButton button_Disable = new JButton("Disable Communication");
	Rectangle rect, rect2;
	
	static String myMRN;
	static String dstMRN;
	static String svcMRN;
	static int pollInterval;
	
	
	static MMSClientHandler polling;
	
	
	public SC_OnBoardTest(){
		
		super("");
		
		
		button_Enable.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (polling != null){
					try {
						polling.startPolling(dstMRN, svcMRN, pollInterval,new MMSClientHandler.PollingResponseCallback() {
							//Response Callback from the polling message
							//it is called when client receives a message
							@Override
							public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
								// TODO Auto-generated method stub
								for (String s : messages) {
									System.out.println(s);
								}
							}
						});
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				
			}
		});
		
		button_Disable.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (polling != null){
					polling.stopPolling();
				}
				
			}
		});
		
		this.setLayout(null);
		//프레임에 컴포넌트 추가
		rect = new Rectangle(10, 10, 220, 100);
		rect2 = new Rectangle(10, 140, 220, 100);
		button_Enable.setBounds(rect);
		button_Disable.setBounds(rect2);
		this.add(button_Enable);
		this.add(button_Disable);
			
		//프레임 크기 지정
		this.setSize(300, 300);
				
		//프레임 보이기
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}
	
	public static void main(String args[]) throws Exception{
		myMRN = "urn:mrn:smart:vessel:imo-no:mof:onBoardVessel";
		//myMRN = args[0];
		
		MMSConfiguration.LOGGING = false;
		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		
		//Service Consumer cannot be HTTP server and should poll from MMS. 
		polling = new MMSClientHandler(myMRN);
		
		pollInterval = 1000;
				
		dstMRN = "urn:mrn:smart:service:instance:mof:mms1";
		svcMRN = "urn:mrn:smart:service:instance:mof:tm-server";
		polling.startPolling(dstMRN, svcMRN, pollInterval, new MMSClientHandler.PollingResponseCallback() {
			//Response Callback from the polling message
			//it is called when client receives a message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
				// TODO Auto-generated method stub
				for (String s : messages) {
					System.out.println(s);
				}
			}
		});
		
		
		new SC_OnBoardTest();
	}
}
