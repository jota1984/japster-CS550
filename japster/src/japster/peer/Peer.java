package japster.peer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import japster.common.Const;
import japster.index.FileLocator;
import japster.index.Index;

public class Peer {
	
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
        	
        	peer.obtainIndexStub();
        	peer.updateFileRigistry();
            String indexAddress = args[0]; 
            String localAddress = args[1];
            int localPort = Integer.parseInt(args[2]);
            
            BufferedReader cin = new BufferedReader( new InputStreamReader(System.in));
            String line;
            while ( (line = cin.readLine()) != null) {
            	switch(line) {
            	case "search": 
            		peer.search("test");
            		break;
            	case "register": 
            		peer.updateFileRigistry();
            		break;
            	case "quit":
                    System.out.println("quitting");
            		return;
            	}
            	
            }
            

        } catch (Exception e) {
            System.err.println("Client exception:");
            e.printStackTrace();
        }
	}
}
