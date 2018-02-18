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

public class Peer implements FileServer {
	
	private String localAddress;
	private int localPort; 
	private String fileDirectoryName;
	private String indexAddress; 
	
	private Index indexStub;
	
	
	public Peer(String indexAddress, 
			String localAddress, 
			int localPort, 
			String fileDirectory ) { 
		this.indexAddress = indexAddress;
		this.localAddress = localAddress;
		this.localPort = localPort;
		this.fileDirectoryName = fileDirectory;
	}
	
	public void updateFileRigistry() {
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
	
	public void obtainIndexStub() throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(indexAddress);
		this.indexStub = (Index) registry.lookup(Const.INDEX_SERVICE_NAME);		
	}
	
	public FileLocator search(String name) {
		
		try {
			return indexStub.search(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	


	public static void main(String[] args) {
        try {
        	
        	Peer peer = new Peer(args[0],
        			args[1],
        			Integer.parseInt(args[2]),
        			args[3]);
        	
            new PeerConsole(peer);

        } catch (Exception e) {
            System.err.println("Client exception:");
            e.printStackTrace();
        }
	}

	@Override
	public int obtain(String name) throws RemoteException  {
		String fileName = fileDirectoryName + File.separator + name;
		FileServerThread serverThread = null; 
		int port = 0;
		try {
			serverThread = new FileServerThread(fileName);
			port = serverThread.getPort();
			serverThread.start();
		} catch (IOException e) {
			System.out.println("Could not create server thread");
		}
		return port;
	}
	
	public void exoportFileServer() throws RemoteException {
 		FileServer stub = (FileServer) UnicastRemoteObject.exportObject(this, Const.PEER_SERVICE_PORT);
		Registry registry = LocateRegistry.createRegistry(localPort);
        registry.rebind(Const.PEER_SERVICE_NAME, stub);
	}
	
	public void download(String fileName, FileLocation location) throws RemoteException, NotBoundException {
		String address = location.getLocationAddress().getHostString();
		int port = location.getLocationAddress().getPort();
		
		Registry registry = LocateRegistry.getRegistry(address, port);
		FileServer server = (FileServer) registry.lookup(Const.PEER_SERVICE_NAME);
		
		int downloadPort = server.obtain(fileName);
		
		fileName = fileDirectoryName + File.separator + fileName;
		FileDownloaderThread fileDownloader = 
				new FileDownloaderThread(fileName, address, downloadPort);
		fileDownloader.start();
				
	}
	
}
