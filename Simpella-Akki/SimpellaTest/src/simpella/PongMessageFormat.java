package simpella;

public class PongMessageFormat {
	private byte port;			//0-1 Port for accepting connection
	private byte[] ipAddress;	//2-5 IP Address of self 
	private int fileSharingCount = 0;	// No of files servent is sharing on the network.
	private int kbShared;		// No of Kilobytes of data servent is sharing on the network.
	
	public byte getPort() {
		return port;
	}
	public void setPort(byte port) {
		this.port = port;
	}
	public byte[] getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(byte[] ipAddress) {
		this.ipAddress = ipAddress;
	}
	public int getFileSharingCount() {
		return fileSharingCount;
	}
	public void setFileSharingCount(int fileSharingCount) {
		this.fileSharingCount = fileSharingCount;
	}
	public int getKbShared() {
		return kbShared;
	}
	public void setKbShared(int kbShared) {
		this.kbShared = kbShared;
	}
	
}
