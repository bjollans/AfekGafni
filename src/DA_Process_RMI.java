import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface DA_Process_RMI extends Remote{

	public void requestElection(int level, int link, UUID id) throws RemoteException;
	public void acknowledge(int acknowledgement, int processNumber) throws RemoteException;
  public boolean isReady() throws RemoteException;
  public void shutdown() throws RemoteException;
  public int getProcessNumber() throws RemoteException;
}