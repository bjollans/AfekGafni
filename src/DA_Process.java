import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;

public class DA_Process extends UnicastRemoteObject implements DA_Process_RMI{
	
	private final UUID id = UUID.randomUUID();
	private Node node;
	private int level = -1;
	private static final long serialVersionUID = 6384248030531941625L;
	public int number; 
	private DA_Process_RMI[] rp;
	public static final int FACTOR = 1;
	private int[] vectorClock = new int[3];
	private static final String NAMING = "proc";

	protected DA_Process(int n) throws RemoteException{
		super();
		this.number = n;
	}

	public void createProcesses(ArrayList<String> addresses) throws RemoteException{
		try {
			rp = new DA_Process_RMI[addresses.size()];
			for(int i=0; i<rp.length;i++){
				rp[i]=(DA_Process_RMI)Naming.lookup(addresses.get(i));
			}
		} catch (MalformedURLException mue){
			System.out.println("Your URL is malformed!");
		} catch (Exception e) {
			long time = System.currentTimeMillis();
			while(System.currentTimeMillis()-time <5000){}
			System.out.println("polling...");
			createProcesses(addresses);
		}
	}

	public int sendID(String message) throws RemoteException {
		System.out.println("\n");
		System.out.println("SENDING ID to nodes");
		
		return 0;
	}

	public boolean receiveACK(int level, Node foreignNode) throws RemoteException {
		System.out.println("\n");
		System.out.println("RECEIVED ACK: process captured");
		return false;
	}
	public int receiveMessage(String message) throws RemoteException {
		System.out.println("\n");
		System.out.println("RECEIVING ID from Candidate");
		
		return 0;
	}
	
	public boolean sendACK(int level, Node foreignNode) throws RemoteException {
		System.out.println("SEND ACK to Candidate");
		return false;
	}

	private void setVectorClock(int [] newVector) {
		this.vectorClock = newVector;
	}
	
	private int[] getVectorClock() {
		return vectorClock;
	}
	
}
