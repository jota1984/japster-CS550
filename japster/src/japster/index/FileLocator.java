package japster.index;

import java.util.ArrayList;

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

	public void addLocation(FileLocation location) {
		int idx = locationList.indexOf(location);
		if (idx == -1) 
			locationList.add(location);
		else {
			locationList.get(idx).refresh();
		}
	}
}
