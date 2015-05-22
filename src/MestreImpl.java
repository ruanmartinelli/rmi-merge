import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MestreImpl implements MestreService {

	private Map<String, EscravoService> escravos = new HashMap<String, EscravoService>();

	private List<Thread> threads = new ArrayList<Thread>();

	/* Metodo para unir as listas dos escravos */
	@SuppressWarnings("unused")
	private List<Integer> merge(List<Integer> l1, List<Integer> l2) {
		List<Integer> resultado = new ArrayList<Integer>(l1.size() + l2.size());

		int i = 0, j = 0;
		while (i < l1.size() && j < l2.size()) {
			if (l1.get(i) < l2.get(j)) {
				resultado.add(l1.get(i++));
			} else {
				resultado.add(l2.get(j++));
			}
		}
		while (i < l1.size()) {
			resultado.add(l1.get(i++));
		}
		while (j < l2.size()) {
			resultado.add(l2.get(j++));
		}
		return resultado;
	}

	/* Metodo que captura o termino do mestre */
	public void attachShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {

				/* Remove todos escravos da lista em caso de mestre offline */
				for (Map.Entry<String, EscravoService> escravo : escravos
						.entrySet()) {
					escravos.remove(escravo);
				}
				System.out.println("Master down, slaves are free.");
			}
		});
	}
	/* Metodo chamado pelo cliente para ordenar a lista. */
	@SuppressWarnings("unused")
	@Override
	public List<Integer> ordena(List<Integer> lista) throws RemoteException {

		/* Tamanho da lista que cada escravo recebera. */
		Integer pedaco = lista.size() / escravos.size();

		/* Lista resultante do merge de todos pedacos dos Escravos. */
		List<Integer> listaOrdenada = new ArrayList<Integer>();

		/* ThreadDTO: Objeto que retorna o valor da execucao de threads de cada escravo.*/
		List<ThreadDTO> workers = new ArrayList<ThreadDTO>();

		int from = 0, to = pedaco;

		for (Map.Entry<String, EscravoService> entry : escravos.entrySet()) {
			List<Integer> sublista = new ArrayList<Integer>();

			/* Distribui sublista de cada escravo. */
			if (to + from > lista.size()) {
				sublista.addAll(lista.subList(from, lista.size()));
			} else {
				sublista.addAll(lista.subList(from, to));
				from = to;
				to += from;
			}

			/* Criacao de threads. */
			ThreadDTO exec = new ThreadDTO(entry.getValue(), sublista);
			workers.add(exec);
			Thread t = new Thread(exec);
			threads.add(t);

			t.start();

		}

		/* Espera escravos terminarem a tarefa */
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		/* Faz o Merge da lista ordenada de cada escravo.
		 * Comentado para executar o caculo do Overhead de Comunicacao.
		*/
		for (ThreadDTO w : workers) {
			//listaOrdenada = merge(listaOrdenada, w.lista);
		}

		return listaOrdenada;
	}

	/* Adiciona um escravo na lista do Mestre. */
	public void registraEscravo(EscravoService e, String id)
			throws RemoteException {
		escravos.put(id, e);
		System.out.println("Slave reporting for duty!");
	}

	/* Remove um escravo da lista. */
	public void removeEscravo(String id) throws RemoteException {
		escravos.remove(escravos.get(id));
	}

	public static void main(String[] args) {
		String host = (args.length < 1) ? "" : args[0];
		if (args.length > 0) {
			System.setProperty("java.rmi.server.hostname", args[0]);
		}

		System.out.println("Connection try at host: " + host);

		try {
			MestreImpl obj = new MestreImpl();

			MestreService ref = (MestreService) UnicastRemoteObject
					.exportObject(obj, 2001);

			/* Comentado para executar remoto. */
			// MestreService ref = (MestreService)
			// UnicastRemoteObject.exportObject(obj, 0);

			Registry registry = LocateRegistry.getRegistry(host);
			registry.rebind("RuanBruno", ref);
			obj.attachShutDownHook();
			System.out.println("Master reporting!");
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	/* Inner class para auxiliar*/
	public class ThreadDTO extends Thread {

		public final EscravoService escravoService;
		public List<Integer> lista;

		public ThreadDTO(EscravoService es, List<Integer> lista) {
			this.escravoService = es;
			this.lista = lista;
		}

		/* Executa quando a thread eh iniciacada. */
		@Override
		public void run() {

			try {
				lista = escravoService.ordenaEscravo(lista);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
