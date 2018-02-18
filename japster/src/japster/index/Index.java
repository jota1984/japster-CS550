package japster.index;

import java.net.InetSocketAddress;
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
	 * @param address InetSocketAddress containing the host address and port number
	 * where the file can be requested
	 * @param name String containing the name of the file that is being registered
	 * @throws RemoteException
	 */
	void register(InetSocketAddress address, String name) throws RemoteException;
}
