package japster.index;

import java.util.Date;

/**
 * A FileWatcher thread is attached to a FileIndex and periodically calls the purge 
 * method on it to make sure that files that have not been refreshed for a while are removed 
 * from the index.
 * @author jota
 *
 */
public class FileWatcher extends Thread{
	private static final int WATCH_PERIOD = 10000;
	private FileIndex fileIndex;
	
	public FileWatcher(FileIndex fileIndex) {
		this.fileIndex = fileIndex;  
	}

	@Override
	public void run() {
		while(true) {
			System.out.println(new Date() + "| Updating FileLocators");
			fileIndex.purge();
			
			try {
				sleep(WATCH_PERIOD);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
