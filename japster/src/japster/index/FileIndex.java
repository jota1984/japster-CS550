package japster.index;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import japster.common.FileLocation;
import japster.common.FileLocator;

/**
 * Keeps track of registered files on a Hashtable. 
 * 
 * The table is indexed by a String that represents the name of a file.
 * 
 * The values of the table are a FileLocator object which includes a list of 
 * FileLocation objects with information about the peers that currently have the file
 * available. 
 * 
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
			System.out.println(name + " is no longer on the network. Removed from Index");
			fileTable.remove(name);
		}
	}
	
	/**
	 * Registers a new location on the index 
	 * @param location
	 */
	public synchronized void register(FileLocation location) {
		String fileName = location.getName();
		FileLocator locator = fileTable.get(fileName);
		//File not already in list
		if(locator == null) {
			locator = new FileLocator(fileName);
			fileTable.put(fileName, locator);
		}
		locator.addLocation(location);
		System.out.println("Added " + fileName + " to index");
	}

	/**
	 * Removes a location from the index 
	 * @param location
	 */
	public synchronized void unregister(FileLocation location) {
		String fileName = location.getName();
		FileLocator locator = fileTable.get(fileName);
		//File not in list
		if(locator == null) {
			return;
		}
		locator.removeLocation(location);
		System.out.println("Removed " + location + " from index");
		//Check if the file has any locations left
		if (locator.getLocationCount() == 0 ) {
			System.out.println(fileName + " is no longer on the network. Removed from Index");
			fileTable.remove(fileName);
		}
	}
	
	public synchronized FileLocator search(String name) {
		return fileTable.get(name);
	}
}
