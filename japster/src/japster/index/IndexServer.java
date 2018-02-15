package japster.index;

import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import japster.common.Const;

public class IndexServer implements Index {

	private Hashtable<String,FileLocator> fileList;
	
	public IndexServer() {
		this.fileList = new Hashtable<String,FileLocator>();
		new FileWatcher(fileList);
	}
	
 	public Hashtable<String,FileLocator> getFileList() {
		return fileList;
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
	
	public void printFileList() {
		for (Map.Entry<String, FileLocator> entry : fileList.entrySet()) {
		    String n = entry.getKey();
		    FileLocator locator = entry.getValue();
		    System.out.println(n + ":");
		    Set<FileLocation> locations = locator.getLocationList();
		    for( FileLocation location : locations) { 
		    	System.out.println("-> " + location.toString());
		    }
		    // ...
		}		
	}

	@Override
	public FileLocator search(String name) throws RemoteException {
		// TODO Prints whole file index instead of doing the actual search 
		System.out.println("Searching:" + name);
		printFileList(); 
		
		return null;
	}

	@Override
	public void register(InetSocketAddress address, String name) throws RemoteException {
		FileLocator locator = fileList.get(name);
		//File not already in list
		if(locator == null) {
			locator = new FileLocator(name);
			fileList.put(name, locator);
		}
		locator.addLocation(new FileLocation(address));
		System.out.println("Added " + name + " to index");
	}

}
