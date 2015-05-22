import java.rmi.RemoteException;
import java.util.List;

public interface EscravoService extends java.rmi.Remote {

	public String getId() throws RemoteException ;

	public void setId(String id) throws RemoteException;

	public List<Integer> ordenaEscravo(List<Integer> lista) throws RemoteException;

}
