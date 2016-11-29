import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.rmi.registry.Registry;

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
			Registry reg;
			DA_Process localProcess=new DA_Process(processNumber);
			try {
				reg =java.rmi.registry.LocateRegistry.createRegistry(registryPort);
				String name = "rmi://localhost/"+"proc"+processNumber;
				Naming.rebind(name,localProcess);
				localProcess.setRegistry(reg);
				localProcess.setName(name);
			} catch (RemoteException e){
				e.printStackTrace();
			} catch (Exception e){

			}

			localProcess.createProcesses(addresses);
			System.out.println("Server is Ready");

			if(isCandidate)
				localProcess.setIsCandidate(isCandidate);

			localProcess.startCandidate();

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
