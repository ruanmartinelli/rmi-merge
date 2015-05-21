import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.stream.FileImageOutputStream;

public class ClienteImpl implements ClienteService {

	private static List<Integer> lista = new ArrayList<Integer>();

	private static List<Integer> initLista(int tamanho) {
		List<Integer> lista = new ArrayList<Integer>();

		Random rand = new Random();

		for (int i = 0; i < tamanho; i++) {
			lista.add(rand.nextInt(tamanho - (tamanho / 10) + 1)
					+ (tamanho / 10));
		}
		return lista;
	}

	@Override
	public MestreService getMestre(String host) throws NotBoundException {
		MestreService stubMestre = null;

		try {
			Registry registry = LocateRegistry.getRegistry(host);
			stubMestre = (MestreService) registry.lookup("RuanBruno");

		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return stubMestre;
	}

	public static void main(String[] args) throws FileNotFoundException,
			UnsupportedEncodingException {
		String host = null, situacao = null; 

		if(args.length == 1){
			situacao = args[0];
		}
		if(args.length == 2){
			situacao = args[0];
			host = args[1];
		}
		
		for (Integer i : lista) {
			System.out.print(i + ", ");
		}
		System.out.println("------------");

		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(host);
			MestreService stub = (MestreService) registry.lookup("RuanBruno");

			PrintWriter tamanhoXtempo = new PrintWriter("benchmark " + situacao
					+ ".csv");
			
			PrintWriter overhead = new PrintWriter("overhead " + situacao
					+ ".csv");
			
			StringBuilder linha = new StringBuilder();

			for (int i = 1; i <= 1000000 ; i = i + 999) {
				lista = initLista(i);

				Long antes = System.nanoTime();
				System.out.println(lista.size() + " tamanho lista");
				List<Integer> ordenada = stub.ordena(lista);
				Long depois = System.nanoTime();
				
				
				Long tempo = depois - antes;
				linha.append(i);
				linha.append(",");
				linha.append(tempo / 1000000000.0);
				linha.append("\n");
				tamanhoXtempo.write(linha.toString());
				//overhead.write(linha.toString());
				linha.setLength(0);
			}
			tamanhoXtempo.close();
			overhead.close();

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

	}
}
