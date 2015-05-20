import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class ClienteSerialImpl {

	private static List<Integer> initLista(int tamanho){
		List<Integer> lista = new ArrayList<Integer>();
		
		Random rand = new Random();
		
		for(int i = 0; i < tamanho ; i++){
			lista.add(rand.nextInt(tamanho - (tamanho/10) + 1) + (tamanho/10));
		}
		return lista;
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

		PrintWriter arquivo = new PrintWriter("benchmark-serial.csv", "UTF-8");
		
		StringBuilder linha = new StringBuilder();
		
		for(int i = 0 ; i < 1000 ; i = i + 100){
			List<Integer> lista = new ArrayList<Integer>();
			lista =	initLista(i);
			
			Long antes = System.nanoTime();
			Collections.sort(lista);
			Long depois = System.nanoTime();
			
			Long tempo = depois - antes;
			linha.append(i);
			linha.append(",");
			linha.append(tempo/1000000000.0);
			linha.append("\n");
			arquivo.write(linha.toString());
			linha.setLength(0);
		}
		arquivo.close();
		
	}

}
