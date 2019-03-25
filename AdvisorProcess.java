package online_Advising_Simulation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;

/**
 * @author Niya Jaison | UTA ID : 1001562701 | Net ID:nxj2701
 * References:	https://docs.oracle.com/javase/tutorial/rmi/client.html
 * The Advisor class approves or disapproves the requests from the Student Process based on a random probability.
 * The Advisor process always contact the MQS server to check for messages after sleeping foe 3 seconds
 */

public class AdvisorProcess extends JFrame implements Runnable{

	private static final long serialVersionUID = 1L;
	private static MQSInterface mqsInterface;
	private static String approvalStatus;
	
	/**Components for UI creation*/
	private JLabel msgToLb;
	private JLabel msgRdLb;
	private Component verticalStrut;
	private Component verticalStrut_1;
	public static JTextArea msgReadTxA;
	public static JTextArea msgToTxA;
	final static String SEPERATOR="\n***********************************\n";
	
	/**
	 * @author Niya Jaison | UTA ID : 1001562701 
	 * Constructor AdvisorProcess() is for Calling the function to build the UI for the advisor process of the Online Advising Simulation application.
	 */
	public AdvisorProcess() {
		// TODO Auto-generated constructor stub
		createUI();
	}
	
	/**
	 * @author Niya Jaison | UTA ID : 1001562701 
	 * The method createUI() is for creating the UI of the advisor part in the Online Advising Simulation Application
	 * Includes: Initializing the member variables, adding the components to the frame
	 */
	
	public void createUI() {
		Box box = Box.createVerticalBox();
		getContentPane().add(box, BorderLayout.NORTH);


		msgRdLb = new JLabel(" Message Read from MQS");
		msgRdLb.setHorizontalAlignment(SwingConstants.LEFT);
		//stNameLb.setEnabled(false);
		msgRdLb.setForeground(Color.BLUE);
		box.add(msgRdLb);
		
		msgReadTxA = new JTextArea();
		msgReadTxA.setColumns(30);
		msgReadTxA.setRows(5);
		box.add(msgReadTxA);

		verticalStrut = Box.createVerticalStrut(5);
		box.add(verticalStrut);

		msgToLb = new JLabel(" Message Updated to MQS");
		msgToLb.setHorizontalAlignment(SwingConstants.LEFT);
		msgToLb.setForeground(Color.BLUE);
		box.add(msgToLb);
		
		msgToTxA = new JTextArea();
		msgToTxA.setRows(5);
		msgToTxA.setTabSize(10);
		msgToTxA.setColumns(30);
		box.add(msgToTxA);

		verticalStrut_1 = Box.createVerticalStrut(5);
		box.add(verticalStrut_1);

		setTitle("Advisor Process");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true); 
		setSize(450, 300);
		

	}
	
	/**
	 * @author Niya Jaison | UTA ID : 1001562701 
	 * The main method is used for accessing the RMI feature of the program and to start the Thread.
	 */
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		try {
			String advisingSerevrUrl="rmi://localhost:1045/OnlineAdvising";
			mqsInterface=(MQSInterface)Naming.lookup(advisingSerevrUrl);/**Inspects the RMI Registry running in the  mentioned url*/
			Thread advisorThread = new Thread(new AdvisorProcess());/**Creating a thread for each of the advisor process*/
			advisorThread.start();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(true) {
				synchronized (mqsInterface) {
					readApprovalRequest();/**calling the user defined function for reading the messages from the MQS*/
					Thread.sleep(3000);/**The thread for Notification processes sleeps for 3 seconds and then reads again.*/
				}


			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	/**
	 * @author Niya Jaison | UTA ID : 1001562701 
	 * Input : void
	 * Output :  void
	 * Function: Calls the function in defined in the interface in-order to write the message back to MQS.
	 */
	public static void sendApprovalRequest(String keyForMap) {
		try {
			mqsInterface.messageListUpdate(keyForMap, approvalStatus);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

	}
	
	/**
	 * @author Niya Jaison | UTA ID : 1001562701 
	 * Input : void
	 * Output :  void
	 * Function: Read the pending approvals from the MQS and displays the same in the UI and Randomly approves or reject the request from students
	 */
	
	public static void readApprovalRequest() {
		Random approvalGenerator = new Random();
		try {
			Map<String,String> messageSet=new HashMap<String,String>();/**Creating a temporary map to store the result coming from the MQS server*/
			messageSet=mqsInterface.messageListRead("null");/**calling the interface method which is used to read the message list pending on the MQS to be processed by the advisor.*/
			msgReadTxA.setText("");
			msgToTxA.setText("");
			if(messageSet.isEmpty()) {/**The if loop processes the condition that in case of no message to be notified , display "No message" in the Advisor Process UI*/
				//System.out.println("****No Request pending for Approval****");
				msgReadTxA.setText("****No Request pending for Decision****");
			}
			else {/**Else loop satisfies the condition that if message is pending to be processed by advisor, display the same in the UI* and make the decision based on Random probability*/
				for(String key:messageSet.keySet()) {
					/**The message retrieved here will be in the format of key: "<timestamp>-<Student name>-<Course code> and message: <null>-<null>*/
				//	System.out.println("Message found");
					StringTokenizer keyToken=new StringTokenizer(key, "-");
					//StringTokenizer msgToken = new StringTokenizer(messageSet.get(Key), "-");
					keyToken.nextToken();
					String stName=keyToken.nextToken();
					String cCode=keyToken.nextToken();
					/**
					 * The below set of code is used to display the messages in the Advisor process UI in the below format:
					 * Student Name : <Student name>
					 * Course Code  : <Course code> 
					 * Message : Decision Pending 
					 * Note: A StringTokenizer is used to iterate over the key and message to retrieve the UI message in the mentioned format
					 */
					String msg="\nStudent: "+stName+"     Course Code: "+cCode;
					msgReadTxA.append(msg+" \n"+"Message: Decision Pending\n");
					/**The below set of code is used to perform the random probability approval/rejection and Display the message in a new Text Area after the decision is made*/
					if(approvalGenerator.nextBoolean()== true){
						approvalStatus= "Approved-Updated";
						msgToTxA.append(msg+" \n "+"Decision Made: Approved"+SEPERATOR);
					}
					else {
						approvalStatus= "Rejected-Updated";
						msgToTxA.append(msg+"\n"+"Decision Made: Rejected"+SEPERATOR);
					}
					
					sendApprovalRequest(System.nanoTime()+"-"+stName+"-"+cCode);/**calling a user defined function to send back the updated message to the MQS server so that it can be accessed by the notification process*/
				}

			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

