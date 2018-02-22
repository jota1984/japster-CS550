package japster.peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import japster.common.FileLocation;
import japster.common.FileLocator;

/**
 * Implements command line interface for Peer class. 
 * <br>
 * Supports the following commands
 * <br>
 *	- connect: gets stub from IndexServer
 * <br>
 *	- search FILENAME: Searches IndexServer for a file
 * <br>	
 *	- register: Registers all local files with IndexServer
 *	<br>
 *	- export: Exports FileServer stub so other peers can download files from this Peer
 *	<br>
 *	- quit
 * 
 * @author jota
 *
 */
public class PeerConsole extends Thread {
	
	private Peer peer; 
	
	public PeerConsole(Peer peer) { 
		this.peer = peer; 
		start(); 
	}

	@Override
	public void run() {
        BufferedReader cin = new BufferedReader( new InputStreamReader(System.in));
        String line;
        
        //Used to store a FileLocation object obtained after a search command
        FileLocation location = null;
        //Used to store fileName of file searched using the search command
        String fileName = null;
        try {
			while ( (line = cin.readLine()) != null) {

				line = line.trim();
				if (line.length() == 0)
					continue;
				Scanner s = new Scanner(line);
				String cmd = s.next();
				switch(cmd) {
				case "connect": 
					try {
						peer.obtainIndexStub();
						System.out.println("Connection established");
					} catch (NotBoundException|RemoteException e) {
						System.out.println("Can't get Index Stub");
						e.printStackTrace();
					} 
					break;
				case "search": 
					s.useDelimiter("$");
					String query = s.next().trim();
					System.out.println("Searching for \"" + query + "\"");
					try {
						FileLocator result = peer.search(query);
						if (result == null) {
							fileName = null;
							location = null;
							System.out.println("Not found!");
						} else { 
							//store query result to be used by download command
							location = result.getLocationList().get(0);
							fileName = query;
							System.out.println(result);
						}
					} catch( RemoteException e) {
						System.out.println("Exception communicating with IndexServer");
					} 
					break;
				case "register": 
					try { 
						peer.updateFileRegistry();
					} catch (RemoteException e) {
						System.out.println("failed to contact server");
					}
					break;
				case "download": 
					if (location != null && fileName != null ) {
						try {
							System.out.println("Attempting to download " + fileName +
									" from " + location);
							peer.download(fileName,location);
						} catch (NotBoundException|IOException e) {
							System.out.println("Download failed." + e.getMessage());
						} 
					} else {
						System.out.println("Must search a file first");
					}
					break;
				case "export": 
					peer.exportFileServer();
					System.out.println("FileServer object exported");
					break;
				case "unregister": 
					peer.unregisterFiles();
					System.out.println("Removing files from IndexServer");
					break; 
				case "quit":
			        System.out.println("quitting");
			        peer.unregisterFiles();
					System.exit(0);				
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
