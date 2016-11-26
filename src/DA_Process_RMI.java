import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;
 
public interface DA_Process_RMI extends Remote{
 
	public void requestElection(Node node) throws RemoteException;
	public void acknowledge(int acknowledgement) throws RemoteException;
}

