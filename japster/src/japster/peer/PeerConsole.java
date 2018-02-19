package japster.peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import japster.index.FileLocation;
import japster.index.FileLocator;

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
        FileLocation location = null;
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
						if (result == null)
							System.out.println("Not found!");
						else { 
							location = result.getLocationList().get(0);
							fileName = query;
							System.out.println(result);
						}
					} catch( RemoteException e) {
						System.out.println("Exception communicating with IndexServer");
					} 
					break;
				case "register": 
					peer.updateFileRigistry();
					break;
				case "download": 
					if (location != null ) {
						try {
							peer.download(fileName,location);
						} catch (NotBoundException|RemoteException e) {
							System.out.println("Download failed");
						} 
					}
					break;
				case "export": 
					peer.exoportFileServer();
					break;
				case "quit":
			        System.out.println("quitting");
					System.exit(0);				
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
