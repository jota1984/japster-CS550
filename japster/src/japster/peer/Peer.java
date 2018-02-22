package japster.peer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import japster.common.Const;
import japster.common.FileLocation;
import japster.common.FileLocator;
import japster.common.Index;

/**
 * Implements the peer program for the P2P application.
 * <br>
 * The Peer class implements the Remote interface FileServer, which can be used by 
 * other peers to call the obtain method on this peer. The obtain method is used
 * to download files from this peer.
 * <br>
 * The main method of this class created a Peer object using the arguments read from the
 * command line and creates a Peer object and a PeerConsole object which is used 
 * by the user to control the Peer object through a command line interface.
 * <br>
 * The Peer object can do the following 
 * <br>
 * 	- Create a remote RMI object exposing the FileServer interface which can be used to request files to the Peer
 * <br>
 *  - Register all the files of the directory associated with the Peer on the IndexServer
 * <br>
 *  - Search for a file on the IndexServer and get a FileLocator for that file if found
 * <br>
 *  - Send a request to another Peer's FileServer to download a file
 *  
 * @author jota
 *
 */
public class Peer implements FileServer {
	
	private String localAddress;
	private int localPort; 
	private String fileDirectoryName;
	private String indexAddress; 
	
	private static Options options;
	
	private Index indexStub;
	
	
	/**
	 * Create a new Peer object 
	 * @param indexAddress String representing the address of the IndexServer
	 * @param localAddress String representing the address used to expose the FileServer remote object by creating 
	 * a registry on this address and binding the remote object to it. This is the address that will be advertised to the IndexServer
	 * @param localPort int representing the port used to create the registry where the FileServer remote object
	 * will be bound. This is the port that will be advertised to the IndexServer.
	 * @param fileDirectory This is the directory containing the files that will be shared and where new files
	 * will be created. 
	 */
	public Peer(String indexAddress, 
			String localAddress, 
			int localPort, 
			String fileDirectory ) { 
		this.indexAddress = indexAddress;
		this.localAddress = localAddress;
		this.localPort = localPort;
		this.fileDirectoryName = fileDirectory;
	}
	
	public static void main(String[] args) {
		//create and parse options
		createOptions();

		CommandLine cmd;
		try {
			cmd = (new DefaultParser()).parse( options, args);
			
			if (!cmd.hasOption("L") || !cmd.hasOption("D") || !cmd.hasOption("I") || 
					!cmd.hasOption("P") || cmd.hasOption("h")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "Peer", options );
				System.exit(0);
			}

			//Required for RMI exportObject()
			System.setProperty("java.rmi.server.hostname",cmd.getOptionValue("L"));

			//Create a new Peer object using command line arguments
			Peer peer = new Peer(cmd.getOptionValue("I"),
					cmd.getOptionValue("L"),
					Integer.parseInt(cmd.getOptionValue("P")),
					cmd.getOptionValue("D"));

			//if running in non interactive mode
			if (!cmd.hasOption("i")) {
				System.out.println("Connecting to IndexServer");
				try {
					peer.obtainIndexStub();
				} catch (RemoteException|NotBoundException e) {
					System.out.println("Error contacting server");
					System.exit(0);
				}
				System.out.println("Exporting FileServer interface");
				try {
					peer.exportFileServer();
				} catch (RemoteException e) {
					System.out.println("Error exporting FileServer interface");
					System.exit(0);
				}
				System.out.println("Starting DirWatcherThread");
				new DirWatcherThread(peer).start();
			}

			//Create a new PeerConsole attached to the Peer object
			new PeerConsole(peer);
		} catch (ParseException e) {
			System.out.println("Error parsing arguments");
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
                .desc(  "use provided ip address as IndexServer" )
                .longOpt("index-address")
                .build();
		Option localAddress   = Option.builder("L")
				.argName( "ip_address" )
                .hasArg()
                .desc(  "use provided ip address as Local address to listen for other peer connections" )
                .longOpt("local-address")
                .build();		
		Option localPort   = Option.builder("P")
				.argName( "port" )
                .hasArg()
                .desc(  "use provided port to listen for other peer connections" )
                .longOpt("local-port")
                .build();		
		Option directory   = Option.builder("D")
				.argName( "dir-name" )
                .hasArg()
                .desc(  "use provided directory to read shared files and store downloaded files" )
                .longOpt("dir")
                .build();	
		Option help   = Option.builder("h")
                .desc(  "print this help" )
                .longOpt("help")
                .build();	
		Option interactive   = Option.builder("i")
                .desc(  "Run in interactive mode. DirWatcherThread is not run and user must handle "
                		+ "connection and registration manually")
                .longOpt("interactive")
                .build();
		
		options.addOption(indexAddress);
		options.addOption(localAddress);
		options.addOption(localPort);
		options.addOption(directory);
		options.addOption(help);
		options.addOption(interactive);
	}
	
	/**
	 * Register all the files from the fileDirectoryName on the IndexServer.
	 * @throws RemoteException 
	 */
	public void updateFileRegistry() throws RemoteException {
		if (indexStub == null) {
			System.out.println("Must obtain Index stub first");
			return;
		}
		
		File fileDir = new File(fileDirectoryName);
		File[] files = fileDir.listFiles();
		long fileSize; 
		String fileName; 

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile() && !files[i].isHidden()) {
				fileSize = files[i].length();
				fileName = files[i].getName();
				FileLocation location = new FileLocation(new InetSocketAddress(localAddress, localPort), fileName, fileSize);
				indexStub.register(location);
			} 
		}	

	}
	
	/**
	 * Removes all files found on fileDirectoryName from the IndexServer
	 * @throws RemoteException
	 */
	public void unregisterFiles() throws RemoteException {
		if (indexStub == null) {
			System.out.println("Must obtain Index stub first");
			return;
		}
		
		File fileDir = new File(fileDirectoryName);
		File[] files = fileDir.listFiles();
		
		long fileSize; 
		String fileName; 

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile() && !files[i].isHidden()) {
				fileSize = files[i].length();
				fileName = files[i].getName();
				FileLocation location = new FileLocation(new InetSocketAddress(localAddress, localPort), fileName, fileSize);
				indexStub.unregister(location);
			} 
		}	
	}
	
	/**
	 * Obtains a stub for the IndexServer remote object. 
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public void obtainIndexStub() throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(indexAddress);
		this.indexStub = (Index) registry.lookup(Const.INDEX_SERVICE_NAME);		
	}
	
	/**
	 * Uses the Index stub to execute the search method on the IdexServer remote
	 * object.
	 * @param name String representing the name of the file to be searched
	 * @return
	 * @throws RemoteException 
	 */
	public FileLocator search(String name) throws RemoteException {
		return indexStub.search(name);
	}
	
	
	private Registry registry;
	private FileServer serverStub;
	
	/**
	 * Creates an RMI registry and binds the FileServer remote object to it.  
	 * @throws RemoteException
	 */
	public void exportFileServer() throws RemoteException {
 		serverStub = (FileServer) UnicastRemoteObject.exportObject(this,0);
 		registry = LocateRegistry.getRegistry(localPort);
		registry = LocateRegistry.createRegistry(localPort);
        registry.rebind(Const.PEER_SERVICE_NAME, serverStub);
	}
	
	public void shutdownFileServer() throws AccessException, RemoteException, NotBoundException {
		registry.unbind(Const.PEER_SERVICE_NAME);
		UnicastRemoteObject.unexportObject(this, false);
		UnicastRemoteObject.unexportObject(registry, false);
	}
	
	/**
	 * Download a file from another peer represented by a FileLocation
	 * @param fileName String representing the name of the file
	 * @param location FileLocation pointing to the registry of a Peer that has the file available
	 * @param quiet DownloaderThread wont print progress if true
	 * @throws NotBoundException
	 * @throws IOException 
	 */
	public Thread download(String fileName, FileLocation location, boolean quiet) throws NotBoundException, IOException {
		String address = location.getLocationAddress().getHostString();
		int port = location.getLocationAddress().getPort();
		long size = location.getSize();
		
		//check if file already exists
		String newfileName = fileDirectoryName + File.separator + fileName;
		if( new File(newfileName).exists() )
			throw new IOException("File exists");
		
		
		//Query the Peer's registry to obtain its FileServer remote object
		Registry registry = LocateRegistry.getRegistry(address, port);
		FileServer server = (FileServer) registry.lookup(Const.PEER_SERVICE_NAME);
		
		//Call the obtain method on the peer to get the TCP port where it will 
		//server the requested file. 
		int downloadPort = server.obtain(fileName);
		
		//Start a downloader thread to download the file 
		FileDownloaderThread fileDownloader = 
				new FileDownloaderThread(newfileName, size, address, downloadPort, quiet);
		fileDownloader.start();
		
		return fileDownloader;
				
	}
	
	@Override
	public int obtain(String name) throws RemoteException, IOException  {
		String fileName = fileDirectoryName + File.separator + name;
		FileServerThread serverThread = null; 
		int port = 0;
//		try {
			//Create  a FileServerThread to serve the file
			serverThread = new FileServerThread(fileName);
			port = serverThread.getPort();
			serverThread.start();
//		} catch (IOException e) {
//			System.out.println("File Not found: " + fileName);
//			throw new 
//		} //catch (IOException e) {
//			System.out.println("Could not create server thread");
//		}
		return port;
	}
}
