

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;

/**
 * @author Niya Jaison | UTA ID : 1001562701 | Net ID:nxj2701
 * References:	https://docs.oracle.com/javase/tutorial/rmi/client.html
 * 				http://www.javaprogrammingforums.com/whats-wrong-my-code/6223-overwriting-txt-file.html
 * The Student class takes input from the UI( the student name, course) and send the request as a message to MQS, so that the advisor can process it.
 */

public class StudentProcess extends JFrame implements Runnable{
	public StudentProcess() {
		//createUI();
	}

	private static final long serialVersionUID = 1L;
	private static String studentName;
	private static String courseCode;
	private static MQSInterface mqsInterface;

	/** Component for UI*/
	public static JTextField cCodeTxF;
	private JButton sendButton;
	private JLabel coCodeLb;
	private JLabel stNameLb;
	private Component verticalStrut;
	private Component verticalStrut_1;
	public static JTextField stNameTxF;
	private JLabel lblNewLabel;

	/**
	 * Author: Niya Jaison | UTA ID : 1001562701 
	 * The function is used for creating the UI of the Student Process part in the Online Advising Application
	 * Includes: Initializing the member variables for UI creation, adding the components to the frame
	 *           adding listeners to the required components
	 */

public void createUI() {
		Box box = Box.createVerticalBox();
		getContentPane().add(box, BorderLayout.NORTH);


		stNameLb = new JLabel("Student Name");
		//stNameLb.setEnabled(false);
		stNameLb.setForeground(Color.BLUE);
		stNameLb.setVerticalAlignment(SwingConstants.TOP);
		box.add(stNameLb);

		stNameTxF = new JTextField();
		box.add(stNameTxF);
		stNameTxF.setColumns(20);

		verticalStrut = Box.createVerticalStrut(20);
		box.add(verticalStrut);

		coCodeLb = new JLabel("Course Code");
		coCodeLb.setForeground(Color.BLUE);
		box.add(coCodeLb);
		//coCodeLb.setEnabled(false);

		cCodeTxF = new JTextField();
		cCodeTxF.setColumns(20);
		box.add(cCodeTxF);

		verticalStrut_1 = Box.createVerticalStrut(20);
		box.add(verticalStrut_1);

		sendButton = new JButton("Sent To Advisor");
		box.add(sendButton);
		
		lblNewLabel = new JLabel("");
		box.add(lblNewLabel);

		setTitle("Student Process");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setSize(450, 300);
		setLocationRelativeTo(null);
		setVisible(true); 
		
		
		/**
		 *Author: Niya Jaison | UTA ID : 1001562701
		 *Adding an ActionListner to the Send button which will call the message to send the request for approval as a message to the MQS.
		 */
		sendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
					sendApprovalRequest();/**Calling a user defined message to send the request for approval as a message to the MQS.*/
				
				
			}
		});

	}
/**
 * @author Niya Jaison | UTA ID : 1001562701 
 * Input : void
 * Output :  void
 * Function: The main method is used for accessing the RMI feature of the program and to start the Thread.
 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		try {
			String advisingSerevrUrl="rmi://localhost:1045/OnlineAdvising";
			mqsInterface=(MQSInterface) Naming.lookup(advisingSerevrUrl);/**Inspects the RMI Registry running in the  mentioned url*/

			Thread studentThread = new Thread( new StudentProcess());/**Creating a thread for each of the student process*/
			studentThread.start();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @author Niya Jaison | UTA ID : 1001562701 
	 * Input : void
	 * Output :  void
	 * Function: The function is used to send the request for approval as a message to the MQS.
	 * 			The message is drafted in the format :key: "<timestamp>-<Student name>-<Course code> and message: <decision=null>-<flag=null>
	 */
	
	public static void sendApprovalRequest() {
		try {
			System.out.println(stNameTxF.getText());
			String keyForMap =(System.nanoTime()+"-"+stNameTxF.getText()+"-"+cCodeTxF.getText());
			mqsInterface.messageListUpdate(keyForMap, "null-null");/**Calling the update function defined in the interface for updating the message in the MQS*/
			stNameTxF.setText("");
			cCodeTxF.setText("");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

	}
	public static void readApprovalRequest() {
		try {
			Map<String,String> messageSet=new HashMap<String,String>();
			messageSet=mqsInterface.messageListRead("noticed");
			if(messageSet.isEmpty()) {

			}
			else {
				for(String key:messageSet.keySet()) {
					StringTokenizer keyString=new StringTokenizer(key, "-");
					String SName = keyString.nextToken();
					String Code=keyString.nextToken();
					if(SName.equals(studentName)&&Code.equals(courseCode)) {
						System.out.println("Student Name\t: "+SName);
						System.out.println("Course Code\t: "+Code);
						System.out.println("Decision\t: "+messageSet.get(key));
					}

				}
			}

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		createUI();/**calling the user defined function for creating the UI */

	}


}
