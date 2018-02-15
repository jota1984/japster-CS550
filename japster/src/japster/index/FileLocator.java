package japster.index;

import java.util.HashSet;
import java.util.Set;

public class FileLocator {
	
	private String fileName;
	private Set<FileLocation> locationList;
	
	public FileLocator(String name) {
		this.fileName = name;
		this.locationList = new HashSet<FileLocation>();
	}
	
	public String getFileName() {
		return fileName;
	}

	public Set<FileLocation> getLocationList() {
		return locationList;
	}

	public void addLocation(FileLocation location) {
		locationList.add(location);
	}
}
