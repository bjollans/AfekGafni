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
	private boolean elected = false;

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
	
	public int startCandidate() throws RemoteException {
		System.out.println("\n");
		System.out.println("START CANDIDATE PROCESS");
		ArrayList<DA_Process_RMI> e = new ArrayList<DA_Process_RMI(Arrays.asList(rp));
		while(true){
			if(level%2==0){
				elected = true;
				return 0;
			}
			else{
				int k = Math.min(Math.pow(2,level/2),e.size())
				ArrayList<DA_Process_RMI> e2 = 
			}
		}
		
		return 0;
	}

	public int startOrdinary(int level, UUID id) throws RemoteException {
		System.out.println("\n");
		System.out.println("START ORDINARY PROCESS");
		
		return 0;
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
