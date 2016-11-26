import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;

public class DA_Process extends UnicastRemoteObject implements DA_Process_RMI{
	
	private final UUID id = UUID.randomUUID();
	private UUID ownerID;
	private Node node;
	private int ordinaryLevel = -1;
	private int candidateLevel = -1;
	private static final long serialVersionUID = 6384248030531941625L;
	public int number; 
	private DA_Process_RMI[] rp;
	public static final int FACTOR = 1;
	private int[] vectorClock = new int[3];
	private static final String NAMING = "proc";
	private boolean elected = false;
	private boolean ready= true;
	private static final int EMPTYMSG = -1;
	private boolean isCandidate = false;
	private boolean isShutdown = false;
	
	private boolean candidateReady= true;
	private boolean localOrdinaryReady= true;
	
	private int receivedSafeMsgs = 0;
	
	private boolean[] remoteOrdinariesReady;

	protected DA_Process(int n, boolean isCandidate) throws RemoteException{
		super();
		this.number = n;
		this.isCandidate = isCandidate;
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
	
	private boolean isOtherPartReady(){
		//check if other part is readz, the two parts are candidate and ordinary part
		//if other part is not ready, set own part to ready and return false
		return false; //or true
	}
	
	private void synchronize() throws RemoteException{
		System.out.println("Synchronizing");
		for(DA_Process_RMI process: rp){
			process.receiveSafeMessage();
		}
		while(receivedSafeMsgs < rp.length){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		receivedSafeMsgs -= rp.length;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void receiveSafeMessage() throws RemoteException{
		receivedSafeMsgs++;
	}
	
	public boolean isReady() throws RemoteException{
		return ready;
	}
	
	public void shutdown() throws RemoteException{
		isShutdown = true;
	}
	
	private void shutdownAll() throws RemoteException{
		for(DA_Process_RMI proc: rp){
			proc.shutdown();
		}
		shutdown();
	}
	
	public int startCandidate() throws RemoteException {
		System.out.println("\n");
		System.out.println("START CANDIDATE PROCESS");

		ArrayList<DA_Process_RMI> finishedOrdinaries = new ArrayList<DA_Process_RMI>();
		ArrayList<DA_Process_RMI> e = new ArrayList<DA_Process_RMI>(java.util.Arrays.asList(rp));
		ArrayList<DA_Process_RMI> e2 = new ArrayList<DA_Process_RMI>();

		int k =0;
		int acks =0;
		while(!isShutdown){
			synchronize();
			ordinaryLevel++;
			if(isCandidate){
				candidateLevel++;
				System.out.println("candidateLevel: "+candidateLevel);
				if(candidateLevel%2==0){
					if(e.size()<1){
						elected = true;
						isCandidate = false;
						System.out.println("IS ELECTED");
						shutdownAll();
					}
					else{
						acks = 0;
						k = Math.min((int)Math.pow(2,candidateLevel/2),e.size());
						for(int i =0; i<k; i++){
							e2.add(e.remove(0));
						}
						for(DA_Process_RMI proc: e2){
							int ackVal = proc.startOrdinary(candidateLevel, id);
							System.out.println("ackVal: "+ackVal);
							if(ackVal==1){
								acks++;
							}
						}		
					}
				}
				else{
					System.out.println("acks: "+acks+" k: "+k);
					if(acks<k){
						isCandidate = false;
						System.out.println("IS NOT ELECTED");
					}
				}
			}
			for(int i=0; i < e.size(); i++){
				//Send empty messages.
				e.get(i).startOrdinary(EMPTYMSG,id);
			}
			for(int i =0; i < finishedOrdinaries.size(); i++){
				//Send empty messages.
				finishedOrdinaries.get(i).startOrdinary(EMPTYMSG,id);
			}
			int e2Size = e2.size();
			for(int i=0; i < e2Size;i++){
				//Empty e2.
				finishedOrdinaries.add(e2.remove(0));
			}
		}
		return 0;
	}

	public int startOrdinary(int candidateLevel, UUID candidateID) throws RemoteException {
		//System.out.println("\nSTART ORDINARY PROCESS");
		
		System.out.println("candidate: "+candidateLevel+" ownLevel: "+ordinaryLevel);
		if (candidateLevel<ordinaryLevel){
			return -1;
		}else{
			System.out.println("candId: "+idToLong(candidateID)+" ownId: "+idToLong(ownerID));
			if(idToLong(candidateID)<idToLong(ownerID)){
				return -1;
			}
			else{
				ordinaryLevel = candidateLevel;
				ownerID=candidateID;
			}
		}
		ordinaryLevel++;
		return 1;
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
