package japster.index;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import japster.common.Const;
import japster.common.FileLocation;
import japster.common.FileLocator;
import japster.common.Index;

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
	
	private static Options options;
	
	public IndexServer() {
		this.fileIndex = new FileIndex(); 
	}
	
	public static void main(String[] args) {
		try {
        	//create and parse options
        	createOptions();
        	
        	CommandLine cmd = (new DefaultParser()).parse( options, args);
        	if (!cmd.hasOption("I") || cmd.hasOption("h")) {
            	HelpFormatter formatter = new HelpFormatter();
            	formatter.printHelp( "IndexServer", options );
            	System.exit(0);
        	}
			
        	System.setProperty("java.rmi.server.hostname",cmd.getOptionValue("I"));
        	
			IndexServer server = new IndexServer();
			
			//Initialize RMI
			server.exportIndex();
	        System.out.println("Index server started");
	        
	        //Create FileWatcher thread to monitor the FileIndex
			System.out.println("Creating FileWatcher thread");
			server.createFileWatcher();

		} catch (Exception e) { 
            System.err.println("Cant start index server:");
            e.printStackTrace();
		}
	}
	
	/**
	 * Create command line options
	 */
	private static void createOptions() {
		options = new Options();
		
		Option indexAddress   = Option.builder("I")
				.argName( "ip_address" )
                .hasArg()
                .desc(  "use provided address to export RMI interface" )
                .longOpt("index-address")
                .build();
		Option help   = Option.builder("h")
                .desc(  "print this help" )
                .longOpt("help")
                .build();	
		
		options.addOption(indexAddress);
		options.addOption(help);
	}
	
	/**
	 * Creates and starts a new FileWatcher thread attached to the FileIndex
	 * @return The FileWatcher Thread
	 */
	public FileWatcher createFileWatcher() {
		FileWatcher fw = new FileWatcher(fileIndex);
		fw.start();
		return fw;
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
	public void register(FileLocation location) throws RemoteException {
		fileIndex.register(location);
	}

	@Override
	public void unregister(FileLocation location) throws RemoteException {
		fileIndex.unregister(location);
	}


}
