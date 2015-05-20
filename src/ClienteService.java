import java.rmi.NotBoundException;

public interface ClienteService extends java.rmi.Remote {
	
	public MestreService getMestre(String host) throws NotBoundException;
	
}
