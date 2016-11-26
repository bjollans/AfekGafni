import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class DA_Process_main {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		int registryPort = Integer.parseInt(args[0]);
		int processNumber = Integer.parseInt(args[1]);
		boolean isCandidate = args[2].equals("true");
		
		ArrayList<String> addresses = new ArrayList<String>();

		int processID = 1;
		for (int i = 3; i < args.length; i++) {
			if(processID==processNumber) processID++;
			System.out.println(args[i]);
			addresses.add("rmi://"+args[i]+"/proc"+processID);
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

			DA_Process localProcess=new DA_Process(processNumber);

			Naming.rebind("rmi://localhost/"+"proc"+processNumber, localProcess);
			localProcess.createProcesses(addresses);
			System.out.println("Server is Ready");
			
			//
			//client
			//
			System.out.println("Running process "+processNumber+".....");
			String processesNames = " ";
			for (int i = 0; i < addresses.size(); i++) {
				processesNames+=addresses.get(i);
				processesNames+=", ";
			}
			System.out.println("Connected to processes..."+processesNames);
			System.out.println("Start to send");
			
			if(isCandidate)
				localProcess.setIsCandidate(isCandidate);
				
			localProcess.startCandidate();

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
