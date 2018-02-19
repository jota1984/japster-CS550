package japster.common;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Date;

/**
 * A FileLocation stores a InetSocketAddress object with the host and port
 * of a peer that is serving a file. It keeps track of when the FileLocation was 
 * created on a Date object. This date can be updated by calling the refresh method.
 * 
 * @author jota
 *
 */
public class FileLocation implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private InetSocketAddress locationAddress;
	private Date refreshDate;

	public FileLocation(InetSocketAddress address) {
		locationAddress = address;
		refresh();  
	}
	
	public void refresh() {
		refreshDate = new Date(); 
	}
	
	public Date getRefreshDate() { 
		return refreshDate; 
	}
	
	public InetSocketAddress getLocationAddress() {
		return locationAddress;
	}

	@Override
	public String toString() {
		return "" + locationAddress.toString() + 
				"(" + refreshDate + ")";
	}
	
	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof FileLocation))
			return false;
		return locationAddress.equals(((FileLocation)arg0).getLocationAddress());
	}
	
}
