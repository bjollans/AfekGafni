import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DA_Process extends UnicastRemoteObject implements DA_Process_RMI{

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	private final UUID id = UUID.randomUUID();
	private UUID ownerID;
	private int ownerLevel = -1;
	private int level = -1;

	private static final long serialVersionUID = 6384248030531941625L;
	private Registry registry;
	public int number;
	private String name;
	private DA_Process_RMI[] rp;
	public static final int FACTOR = 1;
	private int[] vectorClock = new int[3];
	private static final String NAMING = "proc";
	private boolean elected = false;
	private boolean ready= false;
	private int receivedSafeMsgs = 0;

	private ArrayList<DA_Process_RMI> e = new ArrayList<DA_Process_RMI>();
	private ArrayList<DA_Process_RMI> e2 = new ArrayList<DA_Process_RMI>();
	private ArrayList<DA_Process_RMI> eRest = new ArrayList<DA_Process_RMI>();
	private int k=0;
	private int acksReceived =0;
	private int emptyAcksReceived =0;
	private ArrayList<Node> requestsReceived = new ArrayList<Node>();
	private ArrayList<Node> candidates = new ArrayList<Node>();

	private boolean isCandidate = false;
	private int round =0;
	private int roundToBeCandidate = -1;
	private boolean[] remoteOrdinariesReady;

	protected DA_Process(int n) throws RemoteException{
		super();
		this.number = n;
	}

	public void setRegistry(Registry registry){
		this.registry = registry;
	}

	public void setName(String name){
		this.name = name;
	}

	public void setIsCandidate(boolean isCandidate){
		this.isCandidate = isCandidate;
	}

	public void setRoundToBeCandidate(int roundToBeCandidate){
		this.roundToBeCandidate = roundToBeCandidate;
	}

	public int getProcessNumber() throws RemoteException{
		return number;
	}

	public void createProcesses(ArrayList<String> addresses) throws RemoteException{
		try {
			rp = new DA_Process_RMI[addresses.size()];
			remoteOrdinariesReady = new boolean[addresses.size()];
			for(int i=0; i<addresses.size();i++){
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
		e = new ArrayList<DA_Process_RMI>(Arrays.asList(rp));
		synchronize();
	}

	private void synchronize() throws RemoteException{
		System.out.println("Synchronizing...");
		ready = true;
		for(DA_Process_RMI process: rp){
			while(!process.isReady()){
				try{
					Thread.sleep(1000);
				}
				catch(Exception e){

				}
			}
		}
	}

	public boolean isReady() throws RemoteException{
		return ready;
	}

	private void shutdownInitiate() throws RemoteException{
		for(DA_Process_RMI proc: rp){
			proc.shutdown();
		}
		this.shutdown();
	}

	public void shutdown() throws RemoteException{
		isCandidate = false;
		try{
	    UnicastRemoteObject.unexportObject(this, true);
		}
		catch(Exception e){

		}
	}

	public void requestElection(int level, int link, UUID id) throws RemoteException{
		try{
		System.out.println("\tREQUEST RECEIVED from process " + link);
		if(link > number)link--;
		link--;
		Node node = new Node(link, id, level);
		requestsReceived.add(node);
		if(level >-1)
			candidates.add(node);
		if(requestsReceived.size()>=rp.length){
			System.out.println("ALL REQUESTS RECEIVED: READY TO ELECT");
			requestsReceived.clear();
			@SuppressWarnings("unchecked")
			ArrayList<Node> candidatesCopy = (ArrayList)candidates.clone();
			candidates.clear();
			startOrdinary(candidatesCopy);
		}
	}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	synchronized public void startCandidate() throws RemoteException{
		if(round++==roundToBeCandidate)this.isCandidate=true;
		if(isCandidate && !elected){
			System.out.println("Start candidate");
			level ++;
			if(level%2==0){
				if(e.size() ==0){
					elected = true;
					System.out.println("\nElected!\n");
					isCandidate = false;
					//shutdownInitiate();
					return;
				}
				else{
					try{
						k=Math.min((int)Math.pow(2, level/2), e.size());
						for(int i=0; i<k; i++){
							e2.add(e.remove(0));
						}
						for(DA_Process_RMI proc: e2){
							System.out.println("REQUEST SENT TO ONE OF PROCESSES"
								+"ELECTED IN THIS ROUND: Process "+proc.getProcessNumber());
							executor.submit (() ->{
								try{
									proc.requestElection(level,number,id);
								}
								catch(RemoteException rme){

								}});
						}
						for(DA_Process_RMI proc: e){
							System.out.println("REQUEST SENT EMPTY TO ONE OF THOSE PROCESSES "
								+"NOT ELECTED IN THIS ROUND: Process "+proc.getProcessNumber());
							executor.submit (() ->{ // -1 causes error later
								try{
									proc.requestElection(-1,number,id);
								}
								catch(RemoteException rme){

								}});
						}
						for(DA_Process_RMI proc: eRest){
							System.out.println("REQUEST SENT EMPTY TO ONE OF THOSE PROCESSES"
								+" NOT ELECTED IN THIS ROUND: Process "+proc.getProcessNumber());
							executor.submit (() ->{ // -1 causes error later
								try{
									proc.requestElection(-1,number,id);
								}
								catch(RemoteException rme){

								}});
						}
						int e2Size = e2.size();
						for(int i=0; i<e2Size; i++){
							eRest.add(e2.remove(0));
						}
					}
					catch(java.util.ConcurrentModificationException cme){

					}
				}
			}
		}
		else{
			if (elected) return;
			for(DA_Process_RMI proc: rp){
				System.out.println("REQUEST SENT EMPTY BECAUSE THIS IS NOT A CANDIDATE PROCESS:"
							+" Process " + proc.getProcessNumber());
				executor.submit (() ->{ // -1 causes error later
					try{
						proc.requestElection(-1,number,id);
					}
					catch(RemoteException rme){

					}});
			}
		}
	}

	public void acknowledge(int acknowledgement, int processNumber) throws RemoteException{
		System.out.println("\tACKNOWLEDGEMENT RECEIVED from "+ processNumber);
		if(acknowledgement == 1){
			acksReceived++;
		}
		else{
			emptyAcksReceived++;
		}
		if(acksReceived+emptyAcksReceived >= rp.length){
			System.out.println("ALL ACKs RECEIVED: IS IT ELECTED?");
			if(acksReceived<k){
				System.out.println("\nNot elected..\n.");
				isCandidate = false;
			}
			else{
				if(isCandidate)
					level++;
			}
			acksReceived = 0;
			emptyAcksReceived = 0;
			startCandidate();
		}
	}

	public void startOrdinary(ArrayList<Node> candidates) throws RemoteException{
		System.out.println("Start ordinary");
		//calculate maximum of messages that came in
		int maxLevel =-1;
		UUID maxId = UUID.randomUUID();
		int maxLink =-1;
		if(!(candidates.size() <1)){
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
				System.out.println("ACKNOWLEDGEMENT SENT FULL NEW ELECTED OWNER: Process "+rp[maxLink].getProcessNumber());
				rp[maxLink].acknowledge(1, number);
			}
			else{
				//send an empty message to max process of this round
				System.out.println("ACKNOWLEDGEMENT SENT EMPTY TO NOT ELECTED OWNER: Process "+rp[maxLink].getProcessNumber());
				rp[maxLink].acknowledge(-1, number);
			}
		}
		//Send empties to everybody else
		for(int i =0; i<rp.length; i++){
			if(i!=maxLink){
				System.out.println("ACKNOWLEDGEMENT SENT EMPTY TO PROCESS THAT ARE NOT CANDIDATES: Process "+rp[i].getProcessNumber());
				rp[i].acknowledge(-1, number);
			}
		}
		if(ownerLevel > -1) ownerLevel++;
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
