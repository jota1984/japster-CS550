package japster.tools;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Performs registration tests on a given number of peers. 
 * 
 * The tests are performed concurrently by creating one thread for each peer. 
 * 
 * The tests can be run by calling runTest() and the result can be printed calling
 * printResult()
 * 
 * @author jota
 *
 */
public class RegistrationTest {
	
	private int fileNumber;
	private int peerNumber; 
		
	private PeerTester[] peerTesters;
	
	private double averageRegistrationTime = 0.0;  
	private long totalTestTime = 0;
	
	private String indexAddress; 
	
	
	public RegistrationTest(int peerNumber, int fileNumber) {
		this.fileNumber = fileNumber;
		this.peerNumber = peerNumber; 
		indexAddress = "127.0.0.1";
	}
	
	public void setIndexAddress(String address) {
		indexAddress = address; 
	}
	
	private void createTesters() throws RemoteException, NotBoundException {
		peerTesters = new PeerTester[peerNumber];
		
		for (int i = 0; i < peerNumber; i++) {
			String peerName = "peer_" + i;
			PeerTester pt;
			pt = new PeerTester(
					peerName,
					fileNumber, 
					indexAddress,
					"127.0.0.1", 9999);
			pt.createFiles(1024);
			peerTesters[i] = pt; 

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

		long totalTime = 0 ;
		Thread[] threads = new Thread[peerNumber];
		
		//Creates a thread for each tester
		for (int i = 0; i < peerNumber; i++) {
			PeerTester pt = peerTesters[i];
			threads[i] = new Thread() {
				public void run() {
					try {
						pt.testRegistration();
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
		for (int i = 0; i < peerNumber; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(peerTesters[i].getName() + ":" + peerTesters[i].getRegistrationTime());
			totalTime += peerTesters[i].getRegistrationTime();
		}
		long endTestTime = System.currentTimeMillis();
		//Calculate test results
		totalTestTime = endTestTime - beginTestTime;
		averageRegistrationTime = (double) totalTime/(double)peerNumber;
		
		//Do cleanup 
		for (int i = 0; i < peerNumber; i++) {
			PeerTester pt = peerTesters[i];
			pt.cleanup();
		}
	}
	
	public void printResult() {
		System.out.println(" Average registration time for each peer (" +
				peerNumber + "peers, " +
				fileNumber + "files): " +
				averageRegistrationTime + " Total test time " + totalTestTime);
		
	}
}
