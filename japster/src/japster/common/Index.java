package japster.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface exposed by IndexServer remote object. Peers use this interface to
 * register and search for files on the IndexServer  
 * @author jota
 *
 */
public interface Index extends Remote {
	/**
	 * Searches the Index for a file and returns a FileLocator. 
	 * @param name The String to be searched for. 
	 * @return FileLocator or null if not found.
	 * @throws RemoteException
	 */
	FileLocator search(String name) throws RemoteException;
	/**
	 * Registers a  new location for a file on the Index.
	 * @param location FileLocation that represents the information of a file on a Peer
	 * @throws RemoteException
	 */
	void register(FileLocation location) throws RemoteException;
}
