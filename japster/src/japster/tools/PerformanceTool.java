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
		runSearchtests();
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
	
	private static void runSearchtests() {
		try {
			int fileNumber = 10;
			int searches = 100; 
			SearchTest st =new SearchTest(fileNumber, 1,searches );
			st.runTest();
			st.printResult();
			st =new SearchTest(fileNumber, 10,searches );
			st.runTest();
			st.printResult();
			st =new SearchTest(fileNumber, 100,searches );
			st.runTest();
			st.printResult();
			st =new SearchTest(fileNumber, 1000,searches );
			st.runTest();
			st.printResult();
			
			fileNumber = 100; 
			st =new SearchTest(fileNumber, 1,searches );
			st.runTest();
			st.printResult();
			st =new SearchTest(fileNumber, 10,searches );
			st.runTest();
			st.printResult();
			st =new SearchTest(fileNumber, 100,searches );
			st.runTest();
			st.printResult();
			st =new SearchTest(fileNumber, 1000,searches );
			st.runTest();
			st.printResult();
			
			fileNumber = 1000; 
			st =new SearchTest(fileNumber, 1,searches );
			st.runTest();
			st.printResult();
			st =new SearchTest(fileNumber, 10,searches );
			st.runTest();
			st.printResult();
			st =new SearchTest(fileNumber, 100,searches );
			st.runTest();
			st.printResult();
			st =new SearchTest(fileNumber, 1000,searches );
			st.runTest();
			st.printResult();
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
