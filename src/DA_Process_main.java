import java.rmi.Naming;
import java.rmi.RemoteException;

public class DA_Process_main {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		int registryPort = Integer.parseInt(args[0]);
		int processNumber = Integer.parseInt(args[1]);
		
		String ipAddress1 = args[2];
		int rp1Number = Integer.parseInt(args[3]);
		String ipAddress2 = args[4];
		int rp2Number = Integer.parseInt(args[5]);
		
		try{
			//
			//server
			//

			System.setProperty("java.rmi.server.hostname","145.94.174.30");
			try {
				java.rmi.registry.LocateRegistry.createRegistry(registryPort);
			} catch (RemoteException e){
				e.printStackTrace();
			} catch (Exception e){
				
			}

			DA_Process process1=new DA_Process();

			
			Naming.rebind("rmi://145.94.174.30/"+"proc"+processNumber, process1);
			process1.createProcesses(processNumber, ipAddress1, rp1Number, ipAddress2, rp2Number);
			System.out.println("Server is Ready");
			//
			//client
			//
			System.out.println("Running process "+processNumber+".....");
			System.out.println("Connected to process "+rp1Number+" and process "+rp2Number+".....");
			Thread.sleep(10000);
			System.out.println("Start to send");
			boolean running = true;
			while(running){
				java.util.Random r = new java.util.Random();
				int waitTime = (r.nextInt((5 - 1) + 1) + 1)*1000;
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				process1.sendMessage("time: "+(System.currentTimeMillis()-startTime)+" --Sender: proc"+processNumber);
				//if (System.currentTimeMillis()==startTime+30000) running=false;
			}
			

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}