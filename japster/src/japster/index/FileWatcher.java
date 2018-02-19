package japster.index;

import java.util.Date;

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
