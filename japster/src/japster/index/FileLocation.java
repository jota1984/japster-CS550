package japster.index;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Date;

public class FileLocation implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private InetSocketAddress locationAddress;
	private Date refreshDate;
	
	@Override
	public String toString() {
		return "" + locationAddress.toString() + 
				"(" + refreshDate + ")";
	}

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
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof FileLocation))
			return false;
		return locationAddress.equals(((FileLocation)arg0).getLocationAddress());
	}
	
}
