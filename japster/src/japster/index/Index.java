package japster.index;

import java.net.InetSocketAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Index extends Remote {
	FileLocator search(String name) throws RemoteException;
	void register(InetSocketAddress address, String name) throws RemoteException;
}
