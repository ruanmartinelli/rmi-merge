import java.io.Serializable;
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

	Map<String, EscravoService> escravos = new HashMap<String, EscravoService>();
	List<Thread> threads				 = new ArrayList<Thread>();
	
	@Override
	public List<Integer> ordena(List<Integer> lista) throws RemoteException {

		// ----------OBS: O METODO RUN() QUE CHAMA O ORDENAR DO
		// ESCRAVO----------
		/*
		 * for (Map.Entry<String, EscravoService> entry : escravos.entrySet()) {
			entry.getValue().ordenaEscravo(lista);
			System.out.println("Escravo: preparando para ordenar! ");

		}
		*/
		
		Integer pedaco = lista.size() / escravos.size();
		System.out.println(pedaco + ": Tamanho do vetor para cada escravo.");
		
		List<Integer> listaordenada;
		
		List<ThreadDTO> workers = new ArrayList<ThreadDTO>();
		

		int from = 0, to = pedaco;
		// Divide a lista em pedacos para cada escravo.
		// O ultimo escravo recebe os pedacos que sobrarem.

		//List<List<Integer>> sublistas = new List<LinkedList<Integer>(list.subList()>());
		
		/*for (int i = 0; i < escravos.size(); i++) {
			if (to + from > lista.size()) {
				sublistas.add(lista.subList(from, lista.size()));
			} else {
				sublistas.add(lista.subList(from, to));
				from = to;
				to += from;
			}
		}*/
		
		/*Imprime sublistas*/
		/*System.out.println("Sublistas: ");
		for(List<Integer> l : sublistas){
			for(Integer i : l){
				System.out.print(i + " - ");
			}
			System.out.println();
		}*/
		
		int j = 0;
		/* Cria threads para cada escravo */
		for (Map.Entry<String, EscravoService> entry : escravos.entrySet()) {
			//if (!sublistas.get(0).isEmpty()) { /* Lista */

				/*ThreadDTO exec = new ThreadDTO(entry.getValue(),sublistas.get(0));
				workers.add(exec);
				Thread t = new Thread(exec);
				threads.add(t);

				sublistas.remove(0);

				System.out.println("Iniciando THREAD" + j++);
				t.start();*/
				
				
				/*for (int i = 0; i < escravos.size(); i++) {
					if (to + from > lista.size()) {
						sublistas.add(lista.subList(from, lista.size()));
					} else {
						sublistas.add(lista.subList(from, to));
						from = to;
						to += from;
					}
				}*/
				
				
				
				ThreadDTO exec = new ThreadDTO(entry.getValue(),lista);
				workers.add(exec);
				Thread t = new Thread(exec);
				threads.add(t);
				
				System.out.println("Iniciando THREAD");
				t.start();
				
		//	}

		}

		/* Espera escravos terminarem a tarefa */
		for(Thread t: threads){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Primeiro da lista >");
		System.out.println(workers.get(0).lista.get(0));
		
		
		/*for (ThreadDTO w : workers) {
			for (Integer i : w.lista) {
				System.out.print(i + " - ");
			}
			System.out.println();
		}*/
		/*
		for (ThreadDTO w : workers) {
				System.out.println(w.contador + "    CONTADOR");
		}
		*/
		System.out.println("Vou retornar ");
		return lista;
	}

	public void registraEscravo(EscravoService e) throws RemoteException {
		System.out.println("Preparando para registraEscravo");
		escravos.put(UUID.randomUUID().toString(), e);
		System.out.println("Map vazio?" + escravos.isEmpty());
		for (Map.Entry<String, EscravoService> entry : escravos.entrySet()) {
			System.out.println("Escravo Registrado: " + entry.getKey());

		}
	}

	/* Main */
	public static void main(String[] args) {
		String host = (args.length < 1) ? "" : args[1];
		if(args.length > 1){
			
			/* Para rodar remoto */
			System.setProperty("java.rmi.server.hostname", args[0]);
		}

		System.out.println("Tentando conectar em: " + host);

		try {
			MestreImpl obj = new MestreImpl();
			
			
			
			/*
			 * REMOTO
				MestreService ref = (MestreService) UnicastRemoteObject
						.exportObject(obj, 2001);
			*/
			MestreService ref = (MestreService) UnicastRemoteObject
					.exportObject(obj, 0);
			
			
			Registry registry = LocateRegistry.getRegistry(host);
			System.out.println("Registry found.");
			registry.rebind("RuanBruno", ref);
			System.out.println("Master Ready.");

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	
		public class ThreadDTO extends Thread{

			/**
			 * 
			 */
			public final EscravoService escravoService;
			public List<Integer> lista;
			public Integer[] vet = new Integer[10];
			
			
			public ThreadDTO(EscravoService es, List<Integer> lista) {
				this.escravoService = es;
				this.lista = lista;
			}

			@Override
			public void run() {
					
				try {
					lista = escravoService.ordenaEscravo(lista);
					for(Integer i : lista){
						System.out.println(i + " : ");
					}
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
