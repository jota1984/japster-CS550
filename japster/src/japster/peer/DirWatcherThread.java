package japster.peer;

/**
 * Thread that periodically register this peer's files with the server
 * @author jota
 *
 */
public class DirWatcherThread extends Thread {
	
	public static final int WATCH_PERIOD = 10000;
	
	private Peer peer;
	
	/**
	 * Created a new DirWatcherThread
	 * @param peer Peer object to attach to. 
	 */
	public DirWatcherThread(Peer peer) {
		this.peer = peer;
	}

	@Override
	public void run() {
		while(true) {
			peer.updateFileRegistry();
			try {
				sleep(WATCH_PERIOD);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}		
	}
}
