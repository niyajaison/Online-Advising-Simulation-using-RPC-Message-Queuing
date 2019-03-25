

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * @author Niya Jaison | UTA ID : 1001562701 | Net ID:nxj2701
 * References:    https://docs.oracle.com/javase/tutorial/rmi/client.html
 *                 http://www.javaprogrammingforums.com/whats-wrong-my-code/6223-overwriting-txt-file.html
 * The Notification class displays the student name, course and the advisor decision.
 * The Notification process contact the MQS server to check for messages after sleeping for 7 seconds
 */

public class NotificationProcess extends JFrame implements Runnable{
    
    private static final long serialVersionUID = 1L;
    
    private static MQSInterface mqsInterface;
    //private static String updatedStatus;
    
    /**Components for UI creation*/
    private JLabel msgToNo;
    private Component verticalStrut_1;
    public static JTextArea msgToNotifyTxA;
    final static String SEPERATOR="\n***********************************\n";
    
    /**
     * @author Niya Jaison | UTA ID : 1001562701
     * Constructor NotificationProcess() is for calling the function to build the UI for the Notification process of the Online Advising Simulation application.
     */
    public NotificationProcess() {
        // TODO Auto-generated constructor stub
        createUI();
    }
    
    /**
     * @author Niya Jaison | UTA ID : 1001562701
     * The method createUI() is for creating the UI of the Notification part in the Online Advising Simulation Application
     * Includes: Initializing the member variables, adding the components to the frame
     */
    
    public void createUI() {
        Box box = Box.createVerticalBox();
        getContentPane().add(box, BorderLayout.NORTH);
        
        
        msgToNo = new JLabel(" Message Read from MQS to Notify");
        msgToNo.setHorizontalAlignment(SwingConstants.LEFT);
        //stNameLb.setEnabled(false);
        msgToNo.setForeground(Color.BLUE);
        box.add(msgToNo);
        
        msgToNotifyTxA = new JTextArea();
        msgToNotifyTxA.setColumns(30);
        msgToNotifyTxA.setRows(5);
        box.add(msgToNotifyTxA);
        
        verticalStrut_1 = Box.createVerticalStrut(5);
        box.add(verticalStrut_1);
        
        setTitle("Notification Process");
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
            String notifyServerUrl="rmi://localhost:1045/OnlineAdvising";
            mqsInterface=(MQSInterface)Naming.lookup(notifyServerUrl); /**Inspects the RMI Registry running in the  mentioned url*/
            Thread notifyThread = new Thread(new NotificationProcess());/**Creating a thread for each of the notification process*/
            notifyThread.start();
            
            
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
     * Function: Read the pending non-notified messages from the MQS and displays the same in the UI
     */
    public static void readApprovalRequest() {
        try {
            Map<String,String> messageSet=new HashMap<String,String>();/**Creating a temporary map to store the result coming from the MQS server*/
            msgToNotifyTxA.setText("");
            messageSet=mqsInterface.messageListRead("Updated");/**calling the interface method which is used to read the message list pending on the MQS to be notified.*/
            if(messageSet.isEmpty()) {/**The if loop processes the condition that in case of no message to be notified , display "No message" in the Notification Process UI*/
                //System.out.println("****No Request pending to be Notified****");
                
                msgToNotifyTxA.setText("****No Request pending to be Notified****");
            }
            else {/**Else loop satisfies the condition that if message is pending to be notified, display the same in the UI*/
                for(String key:messageSet.keySet()) {
                    /**The message is in the format of key: "<timestamp>-<Student name>-<Course code> and message: <Decision><flag to notify>*/
                    //System.out.println(messageSet.get(key));
                    /**
                     * The below set of code is used to display the messages in the Notification process UI in the below format:
                     * Student Name : <Student name>
                     * Course Code  : <Course code>
                     * Decision : Approved/Rejected
                     * Note: A StringTokenizer is used to iterate over the key and message to retrieve the UI message in the mentioned format
                     */
                    StringTokenizer keyString=new StringTokenizer(key, "-");
                    keyString.nextToken();
                    String SName = keyString.nextToken();
                    String Code=keyString.nextToken();
                    msgToNotifyTxA.append("\nStudent Name : "+SName);
                    msgToNotifyTxA.append("\nCourse Code : "+Code);
                    
                    if(messageSet.get(key).contains("Approved")) {
                        //    System.out.println("Decision\t: Approved");
                        msgToNotifyTxA.append("\nDecision : Approved"+SEPERATOR);
                    }
                    else if(messageSet.get(key).contains("Rejected")) {
                        //System.out.println("Decision : Rejected");
                        msgToNotifyTxA.append("\nDecision : Rejected"+SEPERATOR);
                    }
                    
                }
                
                mqsInterface.messageListUpdate("test", "notified");/**Call the user defined to delete the messages in the MQS server.*/
                
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            while(true) {
                readApprovalRequest();/**calling the user defined function for reading the messages from the MQS*/
                Thread.sleep(6000);/**The thread for Notification processes sleeps for 7 seconds and then reads again.*/
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }
}

