package japster.tools;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Creates 1 PeerTester with a large number of files and creates specified number
 * of PeerTesters that will perform concurrently a specified number of sequential search operations 
 * on the index. 
 * @author jota
 *
 */
public class SearchTest {
	
	private int fileNumber;
	private int peerNumber;
	private int searchNumber; 
	
	private PeerTester peerFileHost;
	
	private PeerTester[] peerSearchers; 
	
	private String indexAddress; 
	
	private long totalTestTime;
	private double averageSearchTime;
	
	public SearchTest(int fileNumber, int peerNumber, int searchNumber) {
		this.fileNumber = fileNumber; 
		this.peerNumber = peerNumber; 
		this.searchNumber = searchNumber; 
		indexAddress = "127.0.0.1";
	}
	
	private void createTesters() throws RemoteException, NotBoundException {
		peerFileHost = new PeerTester("peer_file_server", 
				fileNumber,
				indexAddress,
				"127.0.0.1",
				9999);
		peerFileHost.createFiles(1024);
		
		//peerFileHost.createWatcher();
		peerFileHost.testRegistration();
		
		peerSearchers = new PeerTester[peerNumber];
		
		for (int i = 0; i < peerNumber; i++) {
			String peerName = "peer_" + i;
			PeerTester pt;
			pt = new PeerTester(
					peerName,
					0, 
					indexAddress,
					"127.0.0.1", 9999);
			peerSearchers[i] = pt; 
		}
	}
	
	/**
	 * Creates testers then creates a thread for each tester. On each thread the tester's 
	 * testRegistration() method is called. 
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 */
	public void runTest() throws RemoteException, NotBoundException { 
		
		createTesters();
		
		String[] fileNames = peerFileHost.getFileNames();
		Thread[] threads = new Thread[peerNumber];
		
		//Creates a thread for each searcher
		for (int i = 0; i < peerNumber; i++) {
			PeerTester pt = peerSearchers[i];
			threads[i] = new Thread() {
				public void run() {
					try {
						pt.testSearch(searchNumber, fileNames);
					} catch (RemoteException e) {
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
		long totalTime = 0;
		for (int i = 0; i < peerNumber; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(peerTesters[i].getName() + ":" + peerTesters[i].getRegistrationTime());
			totalTime += peerSearchers[i].getSearchTime();
		}
		long endTestTime = System.currentTimeMillis();
		//Calculate test results
		totalTestTime = endTestTime - beginTestTime;
		averageSearchTime = (double) totalTime/(double)peerNumber;
		
		//Do cleanup 
		for (int i = 0; i < peerNumber; i++) {
			PeerTester pt = peerSearchers[i];
			pt.cleanup();
		}
		peerFileHost.stopWatcher();
		peerFileHost.cleanup();
	}
	
	public void printResult() {
		System.out.println(" Average search time for each peer (" +
				peerNumber + "peers, " +
				fileNumber + "files, " + 
				searchNumber + "searches): " +
				averageSearchTime + " Total test time " + totalTestTime);
		
	}

}
