import java.rmi.Remote;
import java.rmi.RemoteException;
 
public interface DA_Process_RMI extends Remote{
 
	public boolean sendMessage(String message) throws RemoteException;
	public boolean receiveMessage(String message, int[] timeVector) throws RemoteException;

}

