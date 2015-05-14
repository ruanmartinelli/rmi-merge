import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MestreImpl implements MestreService {

	Map<String, EscravoService> escravos = new HashMap<String, EscravoService>();

	@Override
	public List<Integer> ordena(List<Integer> lista) throws RemoteException {
		lista.add(1337);
		return lista;
	}

	public void registraEscravo(EscravoService e) throws RemoteException {
		escravos.put(UUID.randomUUID().toString(), e);
		System.out.println("Map vazio?" + escravos.isEmpty());
		for(Map.Entry<String,EscravoService> entry : escravos.entrySet()){
			System.out.println("Escravo Registrado: " + entry.getKey());
			
		}
	}

	/* Main */
	public static void main(String[] args) {

		try {
			Registry registry = LocateRegistry.getRegistry();

			MestreImpl obj = new MestreImpl();
			MestreService ref = (MestreService) UnicastRemoteObject.exportObject(obj, 0);

			registry.rebind("Mestre", ref);
			System.out.println("Master Ready.");

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

}
