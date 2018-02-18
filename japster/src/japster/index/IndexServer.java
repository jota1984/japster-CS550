package japster.index;

import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import japster.common.Const;

/**
 * Implements the IndexServer for the P2P application.
 * 
 * The IndexServer class implements the Remote interface Index, which can be used by 
 * peers to register the location of files and to search for files on the P2P network.
 * 
 * The index information is kept on a FileIndex object. 
 * @author jota
 *
 */
public class IndexServer implements Index {

	/**
	 * Keeps the actual file index
	 */
	private FileIndex fileIndex; 
	
	public IndexServer() {
		this.fileIndex = new FileIndex(); 
	}
	
	public static void main(String[] args) {
		try {
			IndexServer server = new IndexServer();
			
			//Initialize RMI
			server.exportIndex();
			
	        System.out.println("Index server started");

		} catch (Exception e) { 
            System.err.println("Cant start index server:");
            e.printStackTrace();
		}
	}
	
	@Override
	public FileLocator search(String name) throws RemoteException {
		System.out.println("Searching: " + name);
		FileLocator result = fileIndex.search(name);
		if (result == null) {
			System.out.println("Not Found!");	
		} else {
			System.out.println(result);
		}
		return result;
	}

	@Override
	public void register(InetSocketAddress address, String name) throws RemoteException {
		FileLocation location = new FileLocation(address);
		fileIndex.register(name, location);
	}

 	/**
 	 * Exports the remote object, creates a registry and binds the remote object 
 	 * to the registry
 	 * @throws RemoteException
 	 */
 	private void exportIndex() throws RemoteException { 
 		Index stub = (Index) UnicastRemoteObject.exportObject(this, Const.INDEX_SERVICE_PORT);
		Registry registry = LocateRegistry.createRegistry(Const.INDEX_REGISTRY_PORT);
        registry.rebind(Const.INDEX_SERVICE_NAME, stub);
  	}
}
