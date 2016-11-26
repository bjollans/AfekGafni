import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class DA_Process extends UnicastRemoteObject implements DA_Process_RMI{
	
	private final UUID id = UUID.randomUUID();
	private UUID ownerID;
	private int ownerLevel = -1;
	private int level;
	
	private static final long serialVersionUID = 6384248030531941625L;
	public int number; 
	private DA_Process_RMI[] rp;
	public static final int FACTOR = 1;
	private int[] vectorClock = new int[3];
	private static final String NAMING = "proc";
	private boolean elected = false;
	private boolean ready= true;
	private int receivedSafeMsgs = 0;
	
	private ArrayList<DA_Process_RMI> e = new ArrayList<DA_Process_RMI>();
	private ArrayList<DA_Process_RMI> e2 = new ArrayList<DA_Process_RMI>();
	private ArrayList<DA_Process_RMI> eRest = new ArrayList<DA_Process_RMI>();
	private int k=0;
	private int acksReceived =0;
	private int emptyAcksReceived =0;
	private ArrayList<Node> requestsReceived = new ArrayList<Node>();
	
	private boolean[] remoteOrdinariesReady;

	protected DA_Process(int n) throws RemoteException{
		super();
		this.number = n;
		e = new ArrayList<DA_Process_RMI>(Arrays.asList(rp));
	}

	public void createProcesses(ArrayList<String> addresses) throws RemoteException{
		try {
			rp = new DA_Process_RMI[addresses.size()];
			remoteOrdinariesReady = new boolean[addresses.size()];
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

	public void requestElection(Node node) throws RemoteException{
		requestsReceived.add(node);
		if(requestsReceived.size()>=rp.length){
			startOrdinary(requestsReceived);
		}
	}
	
	public void startCandidate() throws RemoteException{
		level ++;
		if(level%2==0){
			if(e.size() ==0){
				elected = true;
				return;
			}
			else{
				k=Math.max((int)Math.pow(2, level/2), e.size());
				for(int i=0; i<k; i++){
					e2.add(e.remove(0));
				}
				Node node = new Node(number, id, level);
				for(DA_Process_RMI proc: e2){
					proc.requestElection(node);
				}
				for(DA_Process_RMI proc: e){
					proc.requestElection(null);
				}
			}
		}
	}

	public void acknowledge(int acknowledgement) throws RemoteException{
		if(acknowledgement == 1){
			acksReceived++;
		}
		else{
			emptyAcksReceived++;
		}
		if(acksReceived >= rp.length){
			if(acksReceived<k)
				System.out.println("NOT ELECTED");
			else{
				level++;
				acksReceived = 0;
				emptyAcksReceived = 0;
				startCandidate();
			}
		}
	}
	
	public void startOrdinary(ArrayList<Node> candidates) throws RemoteException{
		//calculate maximum of messages that came in
		int maxLevel =-1;
		UUID maxId = UUID.randomUUID();
		int maxLink =-1;
		for(Node n: candidates){
			UUID id = n.getId();
			int level = n.getLevel();
			int link = n.getIndexInProcArray();
			if(level > maxLevel){
				maxLevel = level;
				maxId = id;
				maxLink = link;
			}
			else if(level== maxLevel){
				if(idToLong(id)>idToLong(maxId)){
					maxLevel = level;
					maxId = id;
					maxLink = link;
				}
			}
		}
		if(maxLevel > ownerLevel || idToLong(maxId)>idToLong(ownerID)){
			//set new owner and send acknowledgement
			ownerLevel = maxLevel;
			ownerID = maxId;
			rp[maxLink].acknowledge(1);
		}
		else{
			//send an empty message to max process of this round
			rp[maxLink].acknowledge(-1);
		}
		//Send empties to everybody else
		for(int i =0; i<rp.length; i++){
			if(i!=maxLink)
				rp[i].acknowledge(-1);
		}
	}

	private long idToLong(UUID id){
		if(id == null) return Long.MIN_VALUE;
		return id.getLeastSignificantBits();
	}
	
	private void setVectorClock(int [] newVector) {
		this.vectorClock = newVector;
	}
	
	private int[] getVectorClock() {
		return vectorClock;
	}
	
}
