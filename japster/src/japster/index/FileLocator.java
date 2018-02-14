package japster.index;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class FileLocator {
	
	private String fileName;
	private Set<InetSocketAddress> locationList;
	
	public FileLocator(String name) {
		this.fileName = name;
		this.locationList = new HashSet<InetSocketAddress>();
	}
	
	public String getFileName() {
		return fileName;
	}

	public Set<InetSocketAddress> getLocationList() {
		return locationList;
	}

	public void addLocation(InetSocketAddress location) {
		locationList.add(location);
	}
}
