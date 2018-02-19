package japster.index;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Keeps track of registered files on a Hashtable
 * @author jota
 *
 */
public class FileIndex {
	
	private Hashtable<String,FileLocator> fileTable;
	
	public FileIndex() {
		this.fileTable = new Hashtable<String,FileLocator>();
	}
	
	/**
	 * Prints the whole index to stdout
	 * 
	 */
	public synchronized void printFileTable() {
		for (Map.Entry<String, FileLocator> entry : fileTable.entrySet()) {
		    String n = entry.getKey();
		    FileLocator locator = entry.getValue();
		    System.out.println(n + ":");
		    ArrayList<FileLocation> locations = locator.getLocationList();
		    for( FileLocation location : locations) { 
		    	System.out.println("-> " + location.toString());
		    }
		}		
	}
	
	
	/**
	 * Calls purge() on each FileLocator of the Index. After each FileLocator has 
	 * been purged it checks the FileLocator's number of references. If the FileLocator
	 * has 0 FileLocations then the entry is removed from the Index
	 */
	public synchronized void purge() {
		ArrayList<String> purgeList = new ArrayList<String>(); 
		for(FileLocator fileLocator : fileTable.values() ) {
			fileLocator.purge();
			if(fileLocator.getLocationCount() == 0 ) {
				purgeList.add(fileLocator.getFileName());
			}
		}
		for(String name : purgeList) {
			fileTable.remove(name);
		}
	}
	
	/**
	 * Registers a new location on the index 
	 * @param name
	 * @param location
	 */
	public synchronized void register(String name, FileLocation location) {
		FileLocator locator = fileTable.get(name);
		//File not already in list
		if(locator == null) {
			locator = new FileLocator(name);
			fileTable.put(name, locator);
		}
		locator.addLocation(location);
		System.out.println("Added " + name + " to index");
	}
	
	public synchronized FileLocator search(String name) {
		return fileTable.get(name);
	}
}
