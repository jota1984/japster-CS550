package japster.tools;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import japster.common.FileLocation;

/**
 * Creates one peer with a number of files and a number of peers with no files and 
 * measures the time it takes for the all peers to download all files 
 * @author jota
 */
public class DownloadTest {
	
	private long fileSize; 
	private int fileNumber;
	private int peerNumber;
	
	private PeerTester peerHost;
	private PeerTester[] peerDownloaders;
	
	private String indexAddress;
	
	private FileLocation[] fileLocations; 
	
	private long totalTestTime = 0;
	private double averageDownloadsTime = 0.0;
	
	public DownloadTest(int peerNumber, int fileNumber, long fileSize) {
		this.peerNumber = peerNumber;
		this.fileSize = fileSize; 
		this.fileNumber = fileNumber;
		
		indexAddress = "127.0.0.1";
	}

	private void createTesters() throws RemoteException, NotBoundException {
		peerHost = new PeerTester("peer_file_host",
				fileNumber,
				indexAddress,
				"127.0.0.1",
				9999);
		peerHost.createFiles(fileSize);
		peerHost.exportFileServer();
		
		//Create FileLocations 
		String[] fileNames = peerHost.getFileNames();
		fileLocations = new FileLocation[fileNumber];
		for(int i = 0; i < fileNumber; i++) {
			String fileName = fileNames[i]; 
			FileLocation location = new FileLocation(
					new InetSocketAddress("127.0.0.1", 9999), 
					fileName, 
					fileSize);
			fileLocations[i] = location;
		}
		
		//Create peerDownloaders
		peerDownloaders = new PeerTester[peerNumber];
		for (int i = 0; i < peerNumber; i++) {
			String peerName = "peer_" + i;
			PeerTester pt;
			pt = new PeerTester(
					peerName,
					0, 
					indexAddress,
					"127.0.0.1", 9999);
			peerDownloaders[i] = pt; 
		}
	}
	/**
	 * Creates testers and performs download operations. 
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 */
	public void runTest() throws RemoteException, NotBoundException { 
		
		createTesters(); 

		long totalTime = 0 ;
		Thread[] threads = new Thread[peerNumber];
		
		String[] fileNames = peerHost.getFileNames();
		
		//Creates a thread for each downloaders
		for (int i = 0; i < peerNumber; i++) {
			PeerTester pt = peerDownloaders[i];
			threads[i] = new Thread() {
				public void run() {
					try {
						pt.testDownloads(fileNames,fileLocations);
					} catch (NotBoundException | IOException e) {
						e.printStackTrace();
					}
				}
			};
		}
		
		//Start each thread
		long beginTestTime = System.currentTimeMillis();
		for( Thread thread : threads) {
			thread.start();
		}
		
		//Wait for each thread to finish and record the result of each tester
		for (int i = 0; i < peerNumber; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			totalTime += peerDownloaders[i].getDownloadsTime();
		}
		long endTestTime = System.currentTimeMillis();
		//Calculate test results
		totalTestTime = endTestTime - beginTestTime;
		averageDownloadsTime = (double) totalTime/(double)peerNumber;
		
		//Do cleanup 
		for (int i = 0; i < peerNumber; i++) {
			PeerTester pt = peerDownloaders[i];
			pt.cleanup();
		}
		peerHost.shutdownFileServer();
		peerHost.cleanup(); 
	}
	
	public void printResult() {
		System.out.println(" Average download time for each peer (" +
				peerNumber + " peers, " +
				fileNumber + " files, " +
				fileSize + " bytes): " +
				averageDownloadsTime + " Total test time " + totalTestTime);
		
	}
}
