import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;


public class EscravoImpl implements EscravoService {

	public String id;

	public String getId() throws RemoteException {
		return id;
	}

	public void setId(String id) throws RemoteException {
		this.id = id;
	}
        public List<Integer> ordenaEscravo(List<Integer> lista) throws RemoteException{
            Collections.sort(lista);
            return lista;
        }

	public static void main(String[] args) {

		/* Procura mestre */
		String host = (args.length < 1) ? null : args[1];
                
                // esta interface(mestre) tem que ficar junto ao escravo
		MestreService mestre;
		
		System.out.println(host);
		
		System.setProperty("java.rmi.server.hostname", args[0]);
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			
			
			mestre = (MestreService) registry.lookup("RuanBruno");

			EscravoImpl escravo = new EscravoImpl();
			//escravo.setId(UUID.randomUUID().toString());
			EscravoService stub = (EscravoService) UnicastRemoteObject.exportObject(escravo, 2001);
                        //aqui deve vir a função de ordernar do escravo,STUBzar o resultado e mandar de volta pro mestre
                        //Modificações feita hj-------------------------------------
                        
                        //modificações feita hj-------------------------------------
			mestre.registraEscravo(stub);
			
			

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

	}

}
