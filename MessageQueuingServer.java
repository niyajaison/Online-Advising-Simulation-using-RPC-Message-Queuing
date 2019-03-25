

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.Box;

import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JTextArea;

/**
 * @author Niya Jaison | UTA ID : 1001562701 | Net ID:nxj2701
 * References:    https://docs.oracle.com/javase/tutorial/rmi/client.html
 *                 http://www.javaprogrammingforums.com/whats-wrong-my-code/6223-overwriting-txt-file.html
 * The MessageQueuingServer class displays the messages that is present in the MQS . The class also implements the logic for reading from the file on starting the MQS and logic for writing
 * the pending messages(to be processed by advisor and the messages yet to be notified) in to the file when MQS server is closed.
 * The UI of the MessageQueuingServer is updated everytime a read or a write action is performed by any of the remaining processes.
 */

public class MessageQueuingServer extends JFrame {
    
    private static final long serialVersionUID = 8809328569784621172L;
    static Map<String,String> messageSet=new ConcurrentHashMap<String,String>();/**The collection used for storing the message*/
    /**Member variables for the UI component*/
    public static JTextArea msgTxArea;
    static MQSInterface mqsInstance;
    private JLabel msgLb;
    private Component verticalStrut;
    final static String SEPERATOR="\n***********************************\n";
    
    /**
     * @author Niya Jaison | UTA ID : 1001562701
     * Constructor MessageQueuingServer() is for Calling the function to build the UI for the advisor process of the Online Advising Simulation application.
     */
    
    public MessageQueuingServer() {
        //setAlwaysOnTop(true);
        createUI();
    }
    
    
    /**
     * @author Niya Jaison | UTA ID : 1001562701
     * The method createUI() is for creating the UI of the Message Queuing Server part in the Online Advising Simulation Application
     * Includes: Initializing the member variables, adding the components to the frame
     */
    public void createUI() {
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        Box box = Box.createVerticalBox();
        getContentPane().add(box);
        
        
        msgLb = new JLabel("Messages in the MQS");
        msgLb.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        msgLb.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        //msgLb.setHorizontalAlignment(SwingConstants.CENTER);
        //stNameLb.setEnabled(false);
        msgLb.setForeground(Color.BLUE);
        //msgLb.setVerticalAlignment(SwingConstants.TOP);
        box.add(msgLb);
        
        verticalStrut = Box.createVerticalStrut(20);
        box.add(verticalStrut);
        
        msgTxArea = new JTextArea();
        msgTxArea.setTabSize(0);
        msgTxArea.setLineWrap(true);
        //msgTxArea.setTabSize(1);
        msgTxArea.setRows(10);
        msgTxArea.setColumns(30);
        box.add(msgTxArea);
        
        setTitle("Message Queuing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(450, 300);
        setLocationRelativeTo(null);
        setVisible(true);
        
        
    }
    /**
     * @author Niya Jaison | UTA ID : 1001562701
     * Input : void
     * Output :  void
     * Function: Displays the messages pending the MQS queue list in the UI
     */
    public static void displayMessageList() {
        msgTxArea.setText("");
        if(messageSet.isEmpty()) {/**The if loop processes the condition that in case of no message is present i the MQS queue , display "No message" in the MQS UI*/
            msgTxArea.append("****No Message in the Queue****");
        }
        else {/**Else loop satisfies the condition that if any message is present in the server queue, display the same in the UI(to be processed by advisor and the messages yet to be notified) */
            for(String Key:messageSet.keySet()) {
                
                /**
                 * The below set of code is used to display the messages in the Notification process UI in the below format:
                 * Student Name : <Student name>
                 * Course Code  : <Course code>
                 * Decision : Approved/Rejected/Pending From Advisor
                 * Note: A StringTokenizer is used to iterate over the key and message to retrieve the UI message in the mentioned format
                 */
                StringTokenizer keyToken=new StringTokenizer(Key, "-");
                StringTokenizer msgToken = new StringTokenizer(messageSet.get(Key), "-");
                keyToken.nextToken();
                String msg="Student: "+keyToken.nextToken()+"    Course Code: "+keyToken.nextToken();
                String tempStr =msgToken.nextToken();
                if(tempStr.equals("null")) {
                    msg+="\nDecision : Pending From Advisor\n"+SEPERATOR;
                }
                else {
                    msg+="\nDecision : "+tempStr+" by Advisor\n"+SEPERATOR;
                }
                
                msgTxArea.append(msg);
            }
        }
        
    }
    /**
     * @author Niya Jaison | UTA ID : 1001562701
     * Input : void
     * Output :  void
     * Function: Displays the messages pending the MQS queue list in the UI
     */
    public static void displayMessageList(Map<String,String> messageUpdated) {
        messageSet=messageUpdated;
        msgTxArea.setText("");
        if(messageUpdated.isEmpty()) {/**The if loop processes the condition that in case of no message is present i the MQS queue , display "No message" in the MQS UI*/
            msgTxArea.append("****No Message in the Queue****");
        }
        else {/**Else loop satisfies the condition that if any message is present in the server queue, display the same in the UI(to be processed by advisor and the messages yet to be notified) */
            /**
             * The below set of code is used to display the messages in the Notification process UI in the below format:
             * Student Name : <Student name>
             * Course Code  : <Course code>
             * Decision : Approved/Rejected/Pending From Advisor
             * Note: A StringTokenizer is used to iterate over the key and message to retrieve the UI message in the mentioned format
             */
            for(String Key:messageUpdated.keySet()) {
                StringTokenizer keyToken=new StringTokenizer(Key, "-");
                StringTokenizer msgToken = new StringTokenizer(messageSet.get(Key), "-");
                keyToken.nextToken();
                String msg="\nStudent: "+keyToken.nextToken()+"    Course Code: "+keyToken.nextToken();
                String tempStr =msgToken.nextToken();
                if(tempStr.equals("null")) {
                    msg+="\nDecision : Pending From Advisor"+SEPERATOR;
                }
                else {
                    msg+="\nDecision : "+tempStr+" by Advisor"+SEPERATOR;
                }
                
                msgTxArea.append(msg);
            }
        }
        
    }
    
    /**
     * @author Niya Jaison | UTA ID : 1001562701
     * The main method is used for accessing the RMI feature of the program.Like creating the registry, binding the url to the interface implementation
     */
    
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1045);/**creating the registry*/
            /*URL url = getClass().getResource("client.policy");
             System.setProperty("java.security.policy", url.toString()); */
            /*System.setProperty("java.security.policy","file:./local.policy");
             if (System.getSecurityManager() == null) {
             System.setSecurityManager(new SecurityManager());
             }*/
            MessageQueuingServer MQSServer=new MessageQueuingServer();
            readDataFromFile();/**The user defined method for reading the data from file, when the class starts*/
            displayMessageList();/**Calling the user defined method for displaying the messages in the MQS UI*/
            mqsInstance = new MQSImplementation(messageSet,MQSServer);
            Naming.bind("rmi://localhost:1045/OnlineAdvising", mqsInstance);/**binding the url to the interface implementation*/
            
            /**
             *@author  Niya Jaison | UTA ID : 1001562701
             * Input : WindowEvent
             * Output : void
             * Function: Calls the user defined method for writing the content to the file system on closing the server.
             */
            MQSServer.addWindowListener(new WindowListener() {
                
                @Override
                public void windowOpened(WindowEvent e) {
                    
                }
                
                @Override
                public void windowIconified(WindowEvent e) {}
                
                public void windowDeiconified(WindowEvent e) {}
                
                @Override
                public void windowDeactivated(WindowEvent e) {}
                
                @Override
                public void windowClosing(WindowEvent e) {
                    writeDataToFile();
                }
                
                @Override
                public void windowClosed(WindowEvent e) {
                    // TODO Auto-generated method stub
                    
                    writeDataToFile();
                }
                
                @Override
                public void windowActivated(WindowEvent e) {}
            });
            
        }catch (RemoteException re) {
            // TODO Auto-generated catch block
            System.out.println(re.getMessage()+"\n*************************************\n");
            re.printStackTrace();
        } catch (MalformedURLException mue) {
            // TODO Auto-generated catch block
            mue.printStackTrace();
        } catch (Exception abe) {
            // TODO Auto-generated catch block
            abe.printStackTrace();
        }
    }
    
    /**
     * @author Niya Jaison | UTA ID : 1001562701
     * Input : void
     * Output :  void
     * Function: Read the data stored in the file system when the MQS is opened
     */
    public static void readDataFromFile() {
        try {
            Scanner msgFile = new Scanner(new File("MessageStorageFile.txt"));/**uses a scanner for accessing the .txt file*/
            while((msgFile.hasNextLine())) {/**iterate till the end of the text file*/
                //= file_scanner.nextLine();
                /**The below set of code is used to read the msg from the system line by line and then load it in to the queue(map) as key and value*/
                /**The message retrieved from the file is stored in the format of key: "<timestamp>-<Student name>-<Course code> and message: <Decison>-<flag>*/
                String msgLineFromFile=msgFile.nextLine().toString();
                System.out.println(msgLineFromFile);
                StringTokenizer fileStringToken = new StringTokenizer(msgLineFromFile, "-");
                String keyForMap= fileStringToken.nextToken()+"-"+fileStringToken.nextToken()+"-"+fileStringToken.nextToken();
                String dataForMap = fileStringToken.nextToken()+"-"+fileStringToken.nextToken();
                messageSet.put(keyForMap, dataForMap);/**Adding the message read into the map one by one.*/
            }
            msgFile.close();/**closing the file*/
        }
        catch (Exception e) {
            
            e.printStackTrace();
        }
        
    }
    
    /**
     * @author Niya Jaison | UTA ID : 1001562701
     * Input : void
     * Output :  void
     * Function: Write the pending messages to the file system when the MQS is closed.
     */
    public static void  writeDataToFile() {
        try{
            // Create file
            FileWriter fstream = new FileWriter("MessageStorageFile.txt",false);/**Uses a fileWriter operator for overwriting the content in the file with the messages pending in the queue*/
            BufferedWriter out = new BufferedWriter(fstream);
            for(String key:messageSet.keySet()) {
                String msgToFile=key+"-"+messageSet.get(key)+"\n";
                out.write(msgToFile);/**Writing the message in to the file*/
            }
            
            //Close the output stream
            out.close();/**closing the writer*/
            fstream.close();/**closing the file*/
        }catch (Exception e){//Catch exception if any
            e.printStackTrace();
        }
        
    }
    
}

