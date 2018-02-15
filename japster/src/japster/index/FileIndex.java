package japster.index;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class FileIndex {
	
	private Hashtable<String,FileLocator> fileTable;
	
	public FileIndex() {
		this.fileTable = new Hashtable<String,FileLocator>();
		//new FileWatcher(fileTable);
	}
	
	/**
	 * Prints the whole index to stdout
	 * 
	 */
	synchronized public void printFileTable() {
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
	 * Registers a new location on the index 
	 * @param name
	 * @param location
	 */
	synchronized public void register(String name, FileLocation location) {
		FileLocator locator = fileTable.get(name);
		//File not already in list
		if(locator == null) {
			locator = new FileLocator(name);
			fileTable.put(name, locator);
		}
		locator.addLocation(location);
		System.out.println("Added " + name + " to index");
	}
}
