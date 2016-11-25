import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class DA_Candidate_Process extends UnicastRemoteObject implements DA_Process_RMI{
	
	private Node node;
	private int level = -1;
	private static final long serialVersionUID = 6384248030531941625L;
	public int number; 
	private DA_Process_RMI[] rp;
	public static final int FACTOR = 1;
	private int[] vectorClock = new int[3];
	private static final String NAMING = "proc";

	protected DA_Process(int n, Node node){
		super();
		this.number = n;
		this.node = node;
	}

	public void createProcesses(Node[] nodes) throws RemoteException{
		try {
			rp = new DA_Process_RMI[ipAddresses.length];
			for(int i=0; i<rp.length;i++){
				rp[i]=(DA_Process_RMI)Naming.lookup();
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

	public boolean receiveMessage(int level, Node foreignNode) throws RemoteException {
		//TODO
		return false;
	}

	private void setVectorClock(int [] newVector) {
		this.vectorClock = newVector;
		System.out.println("New global vector clock " + vectorToString(vectorClock));


	}
	
}
