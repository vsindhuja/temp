package simpella;

public class SearchFiles {
	
	String ipAddress;
	int size;
	String fileName;
	String fileIndex;
	int port;
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getFileIndex() {
		return fileIndex;
	}
	public void setFileIndex(String fileIndex) {
		this.fileIndex = fileIndex;
	}	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
