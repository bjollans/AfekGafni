import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;

public class DA_Process extends UnicastRemoteObject implements DA_Process_RMI{
	
	private final UUID id = UUID.randomUUID();
	private UUID ownerID = id;
	private Node node;
	private int level = -1;
	private static final long serialVersionUID = 6384248030531941625L;
	public int number; 
	private DA_Process_RMI[] rp;
	public static final int FACTOR = 1;
	private int[] vectorClock = new int[3];
	private static final String NAMING = "proc";
	private boolean ready= true;

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
	
	private void synchronize(){
		for(DA_Process_RMI process: rp){
			while(!process.isReady()){
				long time = System.currentTimeMillis();
				while(System.currentTimeMillis()-time <1000){}
			}
		}
	}
	
	public boolean isReady(){
		return ready;
	}
	
	public int startCandidate() throws RemoteException {
		System.out.println("\n");
		System.out.println("START CANDIDATE PROCESS");
		
		return 0;
	}

	public int startOrdinary(int candidateLevel, UUID candidateID) throws RemoteException {
		System.out.println("\n");
		System.out.println("START ORDINARY PROCESS");
		
		if (candidateLevel<level){
			return 0;
		}else{
			if(idToLong(candidateID)<idToLong(ownerID)){
				return 0;
			}
			else{
				level = candidateLevel;
				ownerID=candidateID;
			}
		}
		
		return 1;
	}

	private long idToLong(UUID id){
		return id.getLeastSignificantBits();
	}
	
	private void setVectorClock(int [] newVector) {
		this.vectorClock = newVector;
	}
	
	private int[] getVectorClock() {
		return vectorClock;
	}
	
}
