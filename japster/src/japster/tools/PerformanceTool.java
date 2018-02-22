package japster.tools;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Executes various performance tests for the P2P system
 * @author jota
 *
 */
public class PerformanceTool {

	public static void main(String[] args) {
		runRegistrationTests();
	}
	
	private static void runRegistrationTests() {
		
		try {
			int fileNumber = 1;
			RegistrationTest rt = new RegistrationTest(1,fileNumber);
			rt.runTest();
			rt.printResult();
			rt = new RegistrationTest(10,fileNumber);
			rt.runTest();
			rt.printResult();
			rt = new RegistrationTest(100,fileNumber);
			rt.runTest();
			rt.printResult();
			rt = new RegistrationTest(1000,fileNumber);
			rt.runTest();
			rt.printResult();
			fileNumber = 10;
			rt = new RegistrationTest(1,fileNumber);
			rt.runTest();
			rt.printResult();
			rt = new RegistrationTest(10,fileNumber);
			rt.runTest();
			rt.printResult();
			rt = new RegistrationTest(100,fileNumber);
			rt.runTest();
			rt.printResult();
			rt = new RegistrationTest(1000,fileNumber);
			rt.runTest();
			rt.printResult();
			fileNumber = 100;
			rt = new RegistrationTest(1,fileNumber);
			rt.runTest();
			rt.printResult();
			rt = new RegistrationTest(10,fileNumber);
			rt.runTest();
			rt.printResult();
			rt = new RegistrationTest(100,fileNumber);
			rt.runTest();
			rt.printResult();
			rt = new RegistrationTest(1000,fileNumber);
			rt.runTest();
			rt.printResult();
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
