import java.rmi.RemoteException;
import java.util.List;


public interface MestreService extends java.rmi.Remote{

	public List<Integer> ordena (List<Integer> lista) throws RemoteException;

	public void registraEscravo(EscravoService escravo) throws RemoteException;
	
	public void removeEscravo(String id) throws RemoteException;
	
	public Long getRecebido() throws RemoteException;
	
	public Long getEnviado() throws RemoteException;
	
	Long getOverheadEscravos() throws RemoteException;
}
