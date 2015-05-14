import java.rmi.RemoteException;


public interface EscravoService extends java.rmi.Remote{

	public String getId() throws RemoteException ;
	
	public void setId(String id) throws RemoteException;
	

}
