package japster.index;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
		locationList.add(location);
	}
}
