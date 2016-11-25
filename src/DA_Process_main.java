import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class DA_Process_main {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		int registryPort = Integer.parseInt(args[0]);
		int processNumber = Integer.parseInt(args[1]);
		
		ArrayList<Node> addresses = new ArrayList<Node>();

		int processID = 1;
		for (int i = 2; i < args.length; i++) {
			if(processID==processNumber) processID++;
			Node newNode = new Node(processID,args[i]);
			addresses.add(newNode);
			processID++;
		}

		try{
			//
			//server
			//

			System.setProperty("java.rmi.server.hostname","localhost");
			try {
				java.rmi.registry.LocateRegistry.createRegistry(registryPort);
			} catch (RemoteException e){
				e.printStackTrace();
			} catch (Exception e){
				
			}

			DA_Process process1=new DA_Process(processNumber);

			Naming.rebind("rmi://localhost/"+"proc"+processNumber, process1);
			process1.createProcesses(addresses);
			System.out.println("Server is Ready");
			//
			//client
			//
			System.out.println("Running process "+processNumber+".....");
			System.out.println("Connected to processes");
			System.out.println("Start to send");
			
			

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
