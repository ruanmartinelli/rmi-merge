import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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

			PrintWriter arquivo = new PrintWriter("benchmark" + situacao
					+ ".csv");

			StringBuilder linha = new StringBuilder();

			for (int i = 0; i < 1000; i = i + 100) {
				lista = initLista(i);

				Long antes = System.nanoTime();
				List<Integer> ordenada = stub.ordena(lista);
				Long depois = System.nanoTime();

				Long tempo = depois - antes;
				linha.append(i);
				linha.append(",");
				linha.append(tempo / 1000000000.0);
				linha.append("\n");
				arquivo.write(linha.toString());
				linha.setLength(0);
			}
			arquivo.close();

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

	}
}
