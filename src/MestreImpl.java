import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MestreImpl implements MestreService {

	private Map<String, EscravoService> escravos = new HashMap<String, EscravoService>();
	private List<Thread> threads = new ArrayList<Thread>();
	private Long recebido, enviado;
	
	private List<Long> overheadEscravos = new ArrayList<Long>();

	
	
	public Long getOverheadEscravos() throws RemoteException {
		Long overheadMax = 0L;
		for(Long ovhEscravo : overheadEscravos){
			if(ovhEscravo>overheadMax)
				overheadMax = ovhEscravo;
		}
		return overheadMax;
	}

	public Long getRecebido() throws RemoteException {
		return recebido;
	}

	public Long getEnviado() throws RemoteException{
		return enviado;
	}

	/* Metodo para unir as listas dos escravos */
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

	/* Metodo para capturar excecoes de invocaoes de escravos/mestres */
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

	@Override
	public List<Integer> ordena(List<Integer> lista) throws RemoteException {
		recebido = System.nanoTime();
		
		Integer pedaco = lista.size() / escravos.size();

		List<Integer> listaOrdenada = new ArrayList<Integer>();

		List<ThreadDTO> workers = new ArrayList<ThreadDTO>();

		int from = 0, to = pedaco;

		/* Cria threads para cada escravo */
		for (Map.Entry<String, EscravoService> entry : escravos.entrySet()) {
			List<Integer> sublista = new ArrayList<Integer>();

			/* Distribuindo sublista */
			if (to + from > lista.size()) {
				sublista.addAll(lista.subList(from, lista.size()));
			} else {
				sublista.addAll(lista.subList(from, to));
				from = to;
				to += from;
			}
			
			
			ThreadDTO exec = new ThreadDTO(entry.getValue(), sublista);
			workers.add(exec);
			Thread t = new Thread(exec);
			threads.add(t);

			System.out.println("Iniciando THREAD");
			
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

		for (ThreadDTO w : workers) {
			overheadEscravos.add(w.overhead);
			listaOrdenada = merge(listaOrdenada, w.lista);
			
		}
		
		enviado = System.nanoTime();
		return listaOrdenada;
	}
	



	public void registraEscravo(EscravoService e) throws RemoteException {
		escravos.put(UUID.randomUUID().toString(), e);
		System.out.println("Escravo Registrado!");
	}
	
	public void removeEscravo(String id) throws RemoteException {
		escravos.remove(escravos.get(id));
	}

	/* Main */
	public static void main(String[] args) {
		String host = (args.length < 1) ? "" : args[0];
		if (args.length > 0) {

			/* Para rodar remoto */
			System.setProperty("java.rmi.server.hostname", args[0]);
		}

		System.out.println("Tentando conectar em: " + host);

		try {
			MestreImpl obj = new MestreImpl();

			
			 MestreService ref = (MestreService) UnicastRemoteObject.exportObject(obj, 2001);
			 
			//MestreService ref = (MestreService) UnicastRemoteObject.exportObject(obj, 0);

			Registry registry = LocateRegistry.getRegistry(host);
			System.out.println("Registry found.");
			registry.rebind("RuanBruno", ref);
			obj.attachShutDownHook();
			System.out.println("Master Ready.");

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public class ThreadDTO extends Thread {

		public final EscravoService escravoService;
		public List<Integer> lista;
		public Integer[] vet = new Integer[10];
		public Long recebidoMestre, enviadoMestre;
		public Long recebidoEscravo, enviadoEscravo;
		public Long overhead = 0L;

		public ThreadDTO(EscravoService es, List<Integer> lista) {
			this.escravoService = es;
			this.lista = lista;
		}

		@Override
		public void run() {

			try {
				enviadoMestre = System.nanoTime();
				lista = escravoService.ordenaEscravo(lista);
				recebidoMestre = System.nanoTime();
				
				Long recebidoEscravo = escravoService.getRecebido();
				Long enviadoEscravo = escravoService.getEnviado();
				
				overhead = (recebidoEscravo - enviadoMestre) + (recebidoMestre - enviadoEscravo);
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		public List<Integer> getLista() {
			return lista;
		}

		public void setLista(List<Integer> lista) {
			this.lista = lista;
		}
	}

}
