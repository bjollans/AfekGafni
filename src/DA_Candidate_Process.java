import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;

public class DA_Process extends UnicastRemoteObject implements DA_Process_RMI{
	
	private final UUID id = UUID.randomUUID();
	private int level = -1;
	private static final long serialVersionUID = 6384248030531941625L;
	public int number; 
	private DA_Process_RMI[] rp;
	public static final int FACTOR = 1;
	private int[] vectorClock = new int[3];
	private static final String NAMING = "proc";

	protected DA_Process(int n){
		super();
		number = n;
	}

	public void createProcesses(String[] ipAddresses) throws RemoteException{
		try {
			rp = new DA_Process_RMI[ipAddresses.length];
			for(int i=0; i<rp.length;i++){
				int procNum = i;
				if(procNum >=number) procNum++;
				rp[i]=(DA_Process_RMI)Naming.lookup("rmi://"+ipAddresses[i]+"/"+NAMING+index);
			}
		} catch (MalformedURLException mue){
			System.out.println("Your URL is malformed!");
		} catch (Exception e) {
			long time = System.currentTimeMillis();
			while(System.currentTimeMillis()-time <5000){}
			System.out.println("polling...");
			createProcesses(ipAddresses);
		}
	}

	public int sendMessage(String message) throws RemoteException {
		System.out.println("\n");
		System.out.println("SENDING MESSAGE");
		
		return false;
	}
	
	private int[] getVectorClock() {
		return vectorClock;
	}

	public boolean receiveMessage(int level, UUID id) throws RemoteException {
		long foreignId = idToLong(id);
		long ownId = idToLong(this.id);
		return false;
	}

	public long idToLong(UUID id){
		return id.getLeastSignificantBits();
	}

	private void setVectorClock(int [] newVector) {
		this.vectorClock = newVector;
		System.out.println("New global vector clock " + vectorToString(vectorClock));


	}
	
}
