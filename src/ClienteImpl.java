import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class ClienteImpl implements ClienteService{
	
	
	private static List<Integer> lista = new ArrayList<Integer>();

	private static void initLista(List<Integer> lista){
		Random rand = new Random();
		
		for(int i = 0; i < 10 ; i++){
			lista.add(rand.nextInt(100));
		}
		
	}
	
	@Override
	public MestreService getMestre(String host) throws NotBoundException {
			MestreService stubMestre = null;
		
		try {
			Registry registry 	= LocateRegistry.getRegistry(host);
			stubMestre			= (MestreService) registry.lookup("RuanBruno");
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return stubMestre;
	}

	public static void main(String[] args) {
		String host = (args.length < 1) ? null : args[0];
		
		System.out.println(host);
		
		initLista(lista);
		for(Integer i : lista){
			System.out.print(i + ", ");
		}
		System.out.println("------------");
		 Registry registry;
		try {
			registry = LocateRegistry.getRegistry(host);
			MestreService stub = (MestreService) registry.lookup("RuanBruno");
			
			List<Integer> ordenada = stub.ordena(lista);
			System.out.println("Lista Cliente: ");
			for(Integer i : ordenada){
				System.out.print(i + ", ");
			}
			
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}	
		
	}
}
