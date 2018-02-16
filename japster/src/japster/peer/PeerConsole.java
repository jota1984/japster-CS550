package japster.peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

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
					String query = s.next();
					System.out.println("Searching for \"" + query + "\"");
					FileLocator result = peer.search(query);
					if (result == null)
						System.out.println("Not found!");
					else
						System.out.println(result);
					break;
				case "register": 
					peer.updateFileRigistry();
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
