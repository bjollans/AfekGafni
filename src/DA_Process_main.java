import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.rmi.registry.Registry;

public class DA_Process_main {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		String ownIp = args[0];
		int registryPort = Integer.parseInt(args[1]);
		int processNumber = Integer.parseInt(args[2]);
		int roundToBeCandidate = Integer.parseInt(args[3]);

		ArrayList<String> addresses = new ArrayList<String>();

		int processID = 1;
		for (int i = 4; i < args.length; i++) {
			if(processID==processNumber) processID++;
			System.out.println(args[i]);
			addresses.add("rmi://"+args[i]+"/proc"+processID);
			processID++;
		}

		try{
			//
			//server
			//

			System.setProperty("java.rmi.server.hostname",ownIp);
			Registry reg;
			DA_Process localProcess=new DA_Process(processNumber);
			try {
				reg =java.rmi.registry.LocateRegistry.createRegistry(registryPort);
				String name = "rmi://"+ownIp+"/"+"proc"+processNumber;
				Naming.bind(name,localProcess);
				localProcess.setRegistry(reg);
				localProcess.setName(name);
			} catch (RemoteException e){
				e.printStackTrace();
			} catch (Exception e){

			}

			localProcess.createProcesses(addresses);
			System.out.println("Server is Ready");

			localProcess.setRoundToBeCandidate(roundToBeCandidate);

			localProcess.startCandidate();

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
