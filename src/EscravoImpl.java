import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;

public class EscravoImpl implements EscravoService {

	public String id;
	private Long recebido, enviado;
	
	

	public Long getRecebido() throws RemoteException {
		return recebido;
	}

	public Long getEnviado() throws RemoteException {
		return enviado;
	}

	public String getId() throws RemoteException {
		return id;
	}

	public void setId(String id) throws RemoteException {
		this.id = id;
	}

	public List<Integer> ordenaEscravo(List<Integer> lista) throws RemoteException {
		recebido = System.nanoTime();
		Collections.sort(lista);
		enviado = System.nanoTime();
		return lista;
	}

	public void attachShutDownHook(final MestreService mestre) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					mestre.removeEscravo(id);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				System.out.println("Slave free: " + id);
			}
		});
	}
	
	public static void main(String[] args) {

		/* Procura mestre */
		String host = (args.length < 1) ? null : args[1];

		MestreService mestre;

		System.out.println("Connection try at: " + host);

		if (args.length > 1) {
			/* Para rodar remoto */
			System.setProperty("java.rmi.server.hostname", args[0]);
		}

		try {
			Registry registry = LocateRegistry.getRegistry(host);

			mestre = (MestreService) registry.lookup("RuanBruno");

			EscravoImpl escravo = new EscravoImpl();

			/*
			 * REMOTO 
			 * EscravoService stub = (EscravoService) UnicastRemoteObject.exportObject(escravo, 2001);
			 */
			EscravoService stub = (EscravoService) UnicastRemoteObject.exportObject(escravo, 0);

			mestre.registraEscravo(stub);
			escravo.attachShutDownHook(mestre);

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

	}

}
