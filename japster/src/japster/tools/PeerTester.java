package japster.tools;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;

import japster.peer.DirWatcherThread;
import japster.peer.Peer;

/**
 * Creates an instance of a Peer object and provides the ability to execute
 * performance tests on it.
 * 
 * This class provides methods to create and destroy a testing environment for the 
 * enclosed peer as well as methods to measure the time taken by certain actions
 * on the peer.
 * 
 * @author jota
 *
 */
public class PeerTester {
	
	private int numberOfFiles;
	private String peerName; 
	private String peerDir; 
	
	private Peer peer;
	private String[] fileNames = null; 

	private long registrationTime;
	private long searchTime; 
	
	private DirWatcherThread watcher; 
	
	/**
	 * Create a new PeerTester. A name must be provided which is used to create the 
	 * testing environment for the peer. The normal parameters that are usually
	 * required to create a peer must also be provided.
	 * @param name
	 * @param fileNumber
	 * @param indexAddress
	 * @param peerAddress
	 * @param peerPort
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public PeerTester(String name, 
			int fileNumber,
			String indexAddress, 
			String peerAddress, 
			int peerPort) throws RemoteException, NotBoundException {
		
		numberOfFiles = fileNumber;
		peerName = name; 
		
		//Share directory for the testing environment of this peer
		peerDir = "/tmp" + File.separator + peerName;
		
		new File(peerDir).mkdir();
		
		//create the peer
		peer = new Peer(indexAddress,peerAddress,peerPort,peerDir);
		
		//obtain the index server stub 
		peer.obtainIndexStub();
	}
	
	/**
	 * Returns an array of the names of the files created by this PeerTester
	 * @return
	 */
	public String[] getFileNames() { 
		
		if (fileNames != null )
			return fileNames;

		fileNames = new String[numberOfFiles];
		for (int i = 0; i < numberOfFiles; i++) {
			fileNames[i] = peerName + "_file" + i;  
		}
		
		return fileNames; 
	}
	
	/**
	 * Create test files for the peer. 
	 * @param size The size in bytes of each test file.
	 */
	public void createFiles(long size) { 
		
		if (fileNames == null)
			getFileNames();
		
		for (int i = 0; i < numberOfFiles; i++) {
			File f = new File(peerDir + File.separator + fileNames[i]);
			if (!f.exists() ) {
				 RandomAccessFile newfile = null;
				try {
					newfile = new RandomAccessFile(f, "rw");
					newfile.setLength(size);
					newfile.close(); 
				} catch (IOException e) {
					System.out.println("Failed to create file");
				} 
			}
		}
	}
	
	/**
	 * Deletes the test files created by createFiles()
	 */
	private void deleteFiles() {
		if (fileNames == null)
			return;
		for (int i = 0; i < numberOfFiles; i++) {
			File f = new File(peerDir + File.separator + fileNames[i]);
			if (f.exists() ) {
				f.delete();
			}
		}
	}
	
	/**
	 * Deletes the directory created for this PeerTester
	 */
	private void deleteDir() {
		File f = new File(peerDir);
		if (f.exists())
			f.delete();
	}
	
	/**
	 * Create a DirWatcherThread for the Peer
	 */
	public void createWatcher() {
		watcher = new DirWatcherThread(peer);
		watcher.start(); 
	}
	
	public void stopWatcher() {
		if(watcher != null)
			watcher.interrupt();
	}
	
	
	/**
	 * Performs cleanup after finishing tests.
	 * 
	 */
	public void cleanup() {
		try {
			peer.unregisterFiles();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		deleteFiles();
		deleteDir(); 
	}
	
	/**
	 * Performs a registration operation and measures how long it takes. 
	 * @throws RemoteException
	 */
	public void testRegistration() throws RemoteException { 
		long beginTime = System.currentTimeMillis();
		peer.updateFileRegistry();
		long endTime = System.currentTimeMillis();
		registrationTime = endTime - beginTime; 
	}
	
	/**
	 * Performs a number of sequential searches and measures how long it takes.
	 * @param searchNumber
	 * @param fileNames
	 * @throws RemoteException
	 */
	public void testSearch(int searchNumber, String[] fileNames ) throws RemoteException {
		Random rand = new Random(0);
		long beginTime = System.currentTimeMillis();
		for(int i = 0; i < searchNumber; i++) {
			int idx = rand.nextInt(fileNames.length);
			String name = fileNames[idx];
			peer.search(name);
		}
		long endTime = System.currentTimeMillis();
		searchTime = endTime - beginTime; 
		
	}
	
	public long getRegistrationTime() {
		return registrationTime;
	}
	
	public long getSearchTime() {
		return searchTime;
	}
	
	public String getName() {
		return peerName;
	}
	
	

}
