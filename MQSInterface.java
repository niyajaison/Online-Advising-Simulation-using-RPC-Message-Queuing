

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * @author Niya Jaison | UTA ID : 1001562701 | Net ID:nxj2701
 * References:	https://docs.oracle.com/javase/tutorial/rmi/designing.html
 * The MQSInterface is an interface for defining the messages that is to be defined for the MQS.
 */
public interface MQSInterface extends Remote{
	
	public void messageListUpdate(String studentName, String message) throws RemoteException;
	public Map<String,String> messageListRead(String decisionStatus) throws RemoteException;
}
