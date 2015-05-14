import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class EscravoImpl implements EscravoService {

	public String id;

	public String getId() throws RemoteException {
		return id;
	}

	public void setId(String id) throws RemoteException {
		this.id = id;
	}

	public static void main(String[] args) {

		/* Procura mestre */
		String host = (args.length < 1) ? null : args[0];

		MestreService mestre;

		try {
			Registry registry = LocateRegistry.getRegistry(host);
			mestre = (MestreService) registry.lookup("Mestre");

			EscravoImpl escravo = new EscravoImpl();
			//escravo.setId(UUID.randomUUID().toString());

			EscravoService stub = (EscravoService) UnicastRemoteObject.exportObject(escravo, 0);

			mestre.registraEscravo(stub);

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

	}

}
