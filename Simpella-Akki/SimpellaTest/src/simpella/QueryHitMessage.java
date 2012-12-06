package simpella;

public class QueryHitMessage {

	private static byte noOfHits;
	private static short accpetingPort;
	private static byte[] ipAddress = new byte[4];
	private static int speed_RespondinHost = 0; // Needs to be stored in Big Endian format.
	private static String serventID="";
	private static SearchFiles[] searchFiles;


	public SearchFiles[] getSearchFiles() {
		return searchFiles;
	}
	public void setSearchFiles(SearchFiles[] searchFiles) {
		QueryHitMessage.searchFiles = searchFiles;
	}
	public String getServentID() {
		return serventID;
	}
	public void setServentID(String serventID) {
		QueryHitMessage.serventID = serventID;
	}
	public byte getNoOfHits() {
		return noOfHits;
	}
	public void setNoOfHits(byte noOfHits) {
		QueryHitMessage.noOfHits = noOfHits;
	}
	public short getAccpetingPort() {
		return accpetingPort;
	}
	public void setAccpetingPort(short accpetingPort) {
		QueryHitMessage.accpetingPort = accpetingPort;
	}
	public byte[] getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(byte[] ipAddress) {
		QueryHitMessage.ipAddress = ipAddress;
	}
	public int getSpeed_RespondinHost() {
		return speed_RespondinHost;
	}
	public void setSpeed_RespondinHost(int speed_RespondinHost) {
		QueryHitMessage.speed_RespondinHost = speed_RespondinHost;
	}
}

// Nested, since it is encapsulated in/by the QueryHit class.
class ResultSet{

	private static int fileIndex = 0;
	private static int fileSize = 0;
	private static String fileName = "";

	public int getFileIndex() {
		return fileIndex;
	}
	public void setFileIndex(int fileIndex) {
		ResultSet.fileIndex = fileIndex;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		ResultSet.fileSize = fileSize;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		ResultSet.fileName = fileName;
	}
}
