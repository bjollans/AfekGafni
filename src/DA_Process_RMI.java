import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;
 
public interface DA_Process_RMI extends Remote{
 
	public int startCandidate() throws RemoteException;
	public int startOrdinary(int level, UUID id) throws RemoteException;
	public boolean isReady();

}

