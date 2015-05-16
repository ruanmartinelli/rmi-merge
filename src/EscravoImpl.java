import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;

public class EscravoImpl implements EscravoService{

	public String id;

	public String getId() throws RemoteException {
		return id;
	}

	public void setId(String id) throws RemoteException {
		this.id = id;
	}

	public List<Integer> ordenaEscravo(List<Integer> lista)	throws RemoteException {
				Collections.sort(lista);
				System.out.println("ESCRAVO: ");
				for(Integer i : lista){
					System.out.print(i + "-");
				}
				lista.add(8888);
				return lista;
	}

	public static void main(String[] args) {

		/* Procura mestre */
		String host = (args.length < 1) ? null : args[1];

		MestreService mestre;

		System.out.println("Connection try at: " +  host);

		if(args.length > 1){
			/* Para rodar remoto */
			System.setProperty("java.rmi.server.hostname", args[0]);
		}
		
		try {
			Registry registry = LocateRegistry.getRegistry(host);

			mestre = (MestreService) registry.lookup("RuanBruno");

			EscravoImpl escravo = new EscravoImpl();
			// escravo.setId(UUID.randomUUID().toString());
			
			
			
			
			
			
			/* REMOTO
			EscravoService stub = (EscravoService) UnicastRemoteObject
					.exportObject(escravo, 2001);
			*/
			EscravoService stub = (EscravoService) UnicastRemoteObject
					.exportObject(escravo, 0);
			
			
			
			
			
			
			// aqui deve vir a função de ordernar do escravo,STUBzar o
			// resultado e mandar de volta pro mestre
			mestre.registraEscravo(stub);

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

	}

	
}
