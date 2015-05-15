import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//CLASS INTERNA DE THREAD 
/*public class ThreadDTO extends Thread{

	private EscravoService escravoService;
	
	
	public ThreadDTO(EscravoService es){
		this.escravoService = es;
	}

	@Override
	public void run() {
		Collections.sort(escravoService.getEscravo().getLista());
	}
}*/

public class MestreImpl implements MestreService {

	Map<String, EscravoService> escravos = new HashMap<String, EscravoService>();

	@Override
	public List<Integer> ordena(List<Integer> lista) throws RemoteException {
		//lista.add(1337);
            
                //Modificações feita hj-------------------------------------
                //----------OBS: O MÉTODO RUN() QUE CHAMA O ORDENAR DO ESCRAVO----------
                //TESTANDO MANDAR A LISTA INTEIRA PARA UM ESCRAVO ORDENAR
                for(Map.Entry<String,EscravoService> entry : escravos.entrySet()){
                        entry.getValue().ordenaEscravo(lista);
			System.out.println("Escravo ORDENOU? ");
			
		}
                
                
            
                //TESTANDO MANDAR A LISTA INTEIRA PARA UM ESCRAVO ORDENAR
            
            
                Integer pedaco = lista.size() / escravos.size();
                List<Integer> listaordenada;
		
		int from = 0, to = pedaco;
                //Divide a lista em pedacos para cada escravo.
		// O ultimo escravo recebe os pedacos que sobrarem.
                /*List<List<Integer>> sublistas = new ArrayList<List<Integer>>();
		for(int i = 0; i < escravos.size() ; i++){
			if(to + from > lista.size()){
				sublistas.add(lista.subList(from, lista.size()));
			}
			else{
				sublistas.add(lista.subList(from, to));
				from = to;
				to += from;
			}
		}*/
                /* Cria threads para cada escravo */
		/*for(EscravoService escravoService : mestre.getEscravos()){

			if(sublistas.get(0) != null){
				escravoService.getEscravo().setLista(sublistas.get(0));
				sublistas.remove(0);
			}
			ThreadDTO thread = new ThreadDTO(escravoService);
			mestre.getThreads().add(thread);
			thread.notify();
		}*/
		 
                        
                //modificações feita hj-------------------------------------
                
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
