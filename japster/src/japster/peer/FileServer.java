package japster.peer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileServer extends Remote {
	/**
	 * Starts a thread serving a file 
	 * @param name Name of file to serve
	 * @return The port number where the file is being served
	 */
	public int obtain(String name) throws RemoteException;
}
