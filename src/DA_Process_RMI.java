import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;
 
public interface DA_Process_RMI extends Remote{
 
	public int sendID(int level, UUID id) throws RemoteException;
	public int startCandidate() throws RemoteException;

}

