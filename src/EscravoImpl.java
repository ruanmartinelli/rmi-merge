import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class EscravoImpl implements EscravoService {

	public String id;

	public String getId() throws RemoteException {
		return id;
	}

	public void setId(String id) throws RemoteException {
		this.id = id;
	}

	/* Metodo que ordena o pedaco do vetor do cliente recebido pelo escravo. */
	public List<Integer> ordenaEscravo(List<Integer> lista) throws RemoteException {
		//Collections.sort(lista);
		return lista;
	}
	/* Desregistra o escravo da lista do Mestre em caso termino. */
	public void attachShutDownHook(final MestreService mestre) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					mestre.removeEscravo(id);
					System.out.println("Slave free!");
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) {

		String host = (args.length < 1) ? null : args[1];

		MestreService mestre;

		if (args.length > 0) {
			System.setProperty("java.rmi.server.hostname", args[0]);
		}

		try {
			/* Procura Mestre no Registry. */
			Registry registry = LocateRegistry.getRegistry(host);
			mestre = (MestreService) registry.lookup("RuanBruno");

			EscravoImpl escravo = new EscravoImpl();
			escravo.setId(UUID.randomUUID().toString());

			 EscravoService stub = (EscravoService) UnicastRemoteObject.exportObject(escravo, 2001);

			//EscravoService stub = (EscravoService) UnicastRemoteObject.exportObject(escravo, 0);

			/* Utiliza interface remota do Mestre para se registrar.*/
			mestre.registraEscravo(stub, escravo.getId());
			escravo.attachShutDownHook(mestre);

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

	}
}
