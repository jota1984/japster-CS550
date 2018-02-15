package japster.index;

import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import japster.common.Const;

public class IndexServer implements Index {

	private FileIndex fileIndex; 
	
	public IndexServer() {
		this.fileIndex = new FileIndex(); 
	}
	
 	public void exportIndex() throws RemoteException { 
 		Index stub = (Index) UnicastRemoteObject.exportObject(this, Const.INDEX_SERVICE_PORT);
		Registry registry = LocateRegistry.createRegistry(Const.INDEX_REGISTRY_PORT);
        registry.rebind(Const.INDEX_SERVICE_NAME, stub);
  	}
	

	public static void main(String[] args) {
		try {
			IndexServer server = new IndexServer();
			
			server.exportIndex();
			
	        System.out.println("Index server started");
		} catch (Exception e) { 
            System.err.println("Cant start index server:");
            e.printStackTrace();
		}
	}
	
	@Override
	public FileLocator search(String name) throws RemoteException {
		// TODO Prints whole file index instead of doing the actual search 
		System.out.println("Searching:" + name);
		fileIndex.printFileTable(); 
		return null;
	}

	@Override
	public void register(InetSocketAddress address, String name) throws RemoteException {
		FileLocation location = new FileLocation(address);
		fileIndex.register(name, location);
	}
}
