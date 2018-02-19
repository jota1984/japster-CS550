package japster.peer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import japster.common.Const;
import japster.index.FileLocation;
import japster.index.FileLocator;
import japster.index.Index;

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
        try {
        	
        	//Create a new Peer object using command line arguments
        	Peer peer = new Peer(args[0],
        			args[1],
        			Integer.parseInt(args[2]),
        			args[3]);
        	//Create a new PeerConsole attached to the Peer object
            new PeerConsole(peer);

        } catch (Exception e) {
            System.err.println("Client exception:");
            e.printStackTrace();
        }
	}
	
	/**
	 * Register all the files from the fileDirectoryName on the IndexServer.
	 */
	public void updateFileRigistry() {
		if (indexStub == null) {
			System.out.println("Must obtain Index stub first");
			return;
		}
		
		File fileDir = new File(fileDirectoryName);
		File[] files = fileDir.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile() && !files[i].isHidden()) {
				try {
	            	System.out.println("Registering file: " + files[i].getName());
	            	indexStub.register(new InetSocketAddress(localAddress, localPort), files[i].getName());
					System.out.println("Registered test file successfully ");
	            } catch (RemoteException e) {
	            	System.out.println("Failed to contact server");
	            	e.printStackTrace();
				}
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
	 */
	public FileLocator search(String name) {
		
		try {
			return indexStub.search(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Creates an RMI registry and binds the FileServer remote object to it.  
	 * @throws RemoteException
	 */
	public void exoportFileServer() throws RemoteException {
 		FileServer stub = (FileServer) UnicastRemoteObject.exportObject(this, Const.PEER_SERVICE_PORT);
		Registry registry = LocateRegistry.createRegistry(localPort);
        registry.rebind(Const.PEER_SERVICE_NAME, stub);
	}
	
	/**
	 * Download a file from another peer represented by a FileLocation
	 * @param fileName String representing the name of the file
	 * @param location FileLocation pointing to the registry of a Peer that has the file available
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public void download(String fileName, FileLocation location) throws RemoteException, NotBoundException {
		String address = location.getLocationAddress().getHostString();
		int port = location.getLocationAddress().getPort();
		
		//Query the Peer's registry to obtain its FileServer remote object
		Registry registry = LocateRegistry.getRegistry(address, port);
		FileServer server = (FileServer) registry.lookup(Const.PEER_SERVICE_NAME);
		
		//Call the obtain method on the peer to get the TCP port where it will 
		//server the requested file. 
		int downloadPort = server.obtain(fileName);
		
		//Start a downloader thread to download the file 
		fileName = fileDirectoryName + File.separator + fileName;
		FileDownloaderThread fileDownloader = 
				new FileDownloaderThread(fileName, address, downloadPort);
		fileDownloader.start();
				
	}
	
	@Override
	public int obtain(String name) throws RemoteException  {
		String fileName = fileDirectoryName + File.separator + name;
		FileServerThread serverThread = null; 
		int port = 0;
		try {
			//Create  a FileServerThread to serve the file
			serverThread = new FileServerThread(fileName);
			port = serverThread.getPort();
			serverThread.start();
		} catch (IOException e) {
			System.out.println("Could not create server thread");
		}
		return port;
	}
}
