package japster.index;

import java.util.ArrayList;
import java.util.Date;

import japster.common.Const;

public class FileLocator {
	
	private String fileName;
	private ArrayList<FileLocation> locationList;
	
	public FileLocator(String name) {
		this.fileName = name;
		this.locationList = new ArrayList<FileLocation>();
	}
	
	public String getFileName() {
		return fileName;
	}

	public ArrayList<FileLocation> getLocationList() {
		return locationList;
	}

	public synchronized void addLocation(FileLocation location) {
		int idx = locationList.indexOf(location);
		if (idx == -1) 
			locationList.add(location);
		else {
			locationList.get(idx).refresh();
		}
	}
	
	public synchronized boolean removeLocation(FileLocation location) {
		return locationList.remove(location);
	}
	
	public synchronized int getLocationCount() {
		return locationList.size();
	}
	
	/**
	 * Remove FileLocations older than INDEX_TIMEOUT milliseconds
	 */
	public synchronized void purge() { 
		Date oldest = new Date(System.currentTimeMillis() - Const.INDEX_TIMEOUT);
		ArrayList<FileLocation> purgeList = new ArrayList<FileLocation>();
		for(FileLocation location : locationList ) {
			Date date = location.getRefreshDate();
			if (date.before(oldest)) {
				purgeList.add(location);
			}
		}
		for(FileLocation location : purgeList ) {
			System.out.println("Location outdated! " + location.toString() );
			removeLocation(location);
		}
		
	}
}
