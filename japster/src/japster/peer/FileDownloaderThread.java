package japster.peer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import japster.common.Const;

public class FileDownloaderThread extends Thread {
	
	private String fileName;
	private String address; 
	private int port; 
	
	private FileOutputStream output = null;
	private Socket socket = null;
	private InputStream input = null;
	
	public FileDownloaderThread(String fileName, String address, int port) { 
		this.fileName = fileName;
		this.address = address; 
		this.port = port; 
	}
	
	public void cleanup() {
		if( socket != null ) {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Error closing resource");
			}
		}
		if( output != null ){
			try {
				output.close();
			} catch (IOException e) {
				System.out.println("Error closing resource");
			}
		}
		if( input != null ){
			try {
				input.close();
			} catch (IOException e) {
				System.out.println("Error closing resource");
			}
		} 	
	}

	@Override
	public void run() {
		try {
			output = new FileOutputStream(new File(fileName));
			socket = new Socket(address,port);

			input = socket.getInputStream();

			byte buffer[] = new byte[Const.BUFFER_SIZE];

			int len = input.read(buffer);
			while(len>0) {
				output.write(buffer,0,len);
				len = input.read(buffer);
			}
		
			output.flush();
		} catch (IOException e) {
			System.out.println("Download failed");
			e.printStackTrace();
		} finally {
			cleanup();
		}
	}
}
