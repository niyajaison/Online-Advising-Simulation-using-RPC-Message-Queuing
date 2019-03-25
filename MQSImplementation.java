

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyStore.Entry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Niya Jaison | UTA ID : 1001562701 | Net ID:nxj2701
 * References:	https://docs.oracle.com/javase/tutorial/rmi/implementing.html
 * The MQSImplemetation class implements the logic for the methods defined in the interface.
 */
public class MQSImplementation extends UnicastRemoteObject implements MQSInterface{

	private static final long serialVersionUID = 1L;
	public static Map<String,String> messageSet=new ConcurrentHashMap<String,String>();
	static MessageQueuingServer mqsServer;

	protected MQSImplementation(Map<String,String> messageFromFile,MessageQueuingServer mqs) throws RemoteException {
		super();
		messageSet=messageFromFile;
		mqsServer=mqs;
		//createUI();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Author: Niya Jaison	| UTA ID: 1001562701	
	 * Function: The function is the implementation of the messageListUpdate() function in the interface
	 * 			 This function is used to update the messages that is being stored in the MQS.
	 * Input	:  The 2 string	to create key and value for the queue(map)
	 */
	@Override
	public void messageListUpdate(String studentIdentifier, String updatedMessage) throws RemoteException {
		// TODO Auto-generated method stub
		if(updatedMessage.equals("notified")) {
			MessageQueuingServer.displayMessageList(messageSet);	
		}
		else {
			if(messageSet.containsKey(studentIdentifier)) {/**StudentName + courseCode+randomNum*/
				messageSet.replace(studentIdentifier, updatedMessage);
			}
			else {
				messageSet.put(studentIdentifier, updatedMessage);
			}
			System.out.println("here");
			
			mqsServer.displayMessageList(messageSet);
		}
		
		
	}
	
	/**
	 * Author: Niya Jaison	| UTA ID: 1001562701	
	 * Function: The function is the implementation of the messageListRead() function in the interface
	 * 			 This function is used to read the messages that is being stored in the MQS.
	 * Input	:  The read is based on the flag that is set in the message
	 */
	@Override
	public Map<String,String> messageListRead(String decisionStatus) throws RemoteException {
		// TODO Auto-generated method stub
		//System.out.println("in MessageListRead-"+messageSet.isEmpty());
		Map<String,String> unreadMessages=new ConcurrentHashMap<String,String>();/**Map for storing the unread messages*/
		/*if(decisionStatus.equals("all")) {// for displaying the current messages in list
			for(String key : messageSet.keySet() ) {
				
					unreadMessages.put(key,messageSet.get(key));
				}
				
			}
		else {*/
		for(String key : messageSet.keySet() ) {
				String messageServer=messageSet.get(key);
				StringTokenizer presentMessage = new StringTokenizer(messageServer, "-");
				presentMessage.nextToken();
				String messageBody =presentMessage.nextToken();
				/**The below code detects whether the message is unread and if it is not read by the client program the 
				 * the message is saved in to a new Map to be send to the client and the same is removed from the MQS.
				 * For an advisor: The decision status will be null
				 * For a Notification process: The decision is either Rejected or Approved.*/
				if(messageBody.equals(decisionStatus)) {
					unreadMessages.put(key, messageServer);
					
					messageSet.remove(key,messageSet.get(key));
				}
		}
				
			
			/*Iterator it=messageSet.keySet().iterator();
			while(it.hasNext()) {
				System.out.println("it.toString() value"+messageSet.get(it));
				StringTokenizer presentMessage = new StringTokenizer(it.toString(), ",");
				presentMessage.nextToken();
				String messageBody =presentMessage.nextToken();
				if(messageBody.equals(decisionStatus)) {
					unreadMessages.put(key, messageBody);
					
					messageSet.remove(key,messageSet.get(key));
				}
					
			}*/
			
		
		return unreadMessages;
	}
	
	

}
