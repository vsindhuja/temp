package MessageFormats;

public class QueryHitMessage {

	private static byte noOfHits;
	private static byte accpetingPort;
	private static byte[] ipAddress = new byte[4];
	private static int speed_RespondinHost = 0; // Needs to be stored in Big Endian format.
	private static String serventID="";
	private static ResultSet resultSet = new ResultSet();
	
	public static ResultSet getResultSet() {
		return resultSet;
	}
	public static void setResultSet(ResultSet resultSet) {
		QueryHitMessage.resultSet = resultSet;
	}
	public static String getServentID() {
		return serventID;
	}
	public static void setServentID(String serventID) {
		QueryHitMessage.serventID = serventID;
	}
	public static byte getNoOfHits() {
		return noOfHits;
	}
	public static void setNoOfHits(byte noOfHits) {
		QueryHitMessage.noOfHits = noOfHits;
	}
	public static byte getAccpetingPort() {
		return accpetingPort;
	}
	public static void setAccpetingPort(byte accpetingPort) {
		QueryHitMessage.accpetingPort = accpetingPort;
	}
	public static byte[] getIpAddress() {
		return ipAddress;
	}
	public static void setIpAddress(byte[] ipAddress) {
		QueryHitMessage.ipAddress = ipAddress;
	}
	public static int getSpeed_RespondinHost() {
		return speed_RespondinHost;
	}
	public static void setSpeed_RespondinHost(int speed_RespondinHost) {
		QueryHitMessage.speed_RespondinHost = speed_RespondinHost;
	}
}

// Nested, since it is encapsulated in/by the QueryHit class.
class ResultSet{
	
	private static int fileIndex = 0;
	private static int fileSize = 0;
	private static String fileName = "";
	
	public static int getFileIndex() {
		return fileIndex;
	}
	public static void setFileIndex(int fileIndex) {
		ResultSet.fileIndex = fileIndex;
	}
	public static int getFileSize() {
		return fileSize;
	}
	public static void setFileSize(int fileSize) {
		ResultSet.fileSize = fileSize;
	}
	public static String getFileName() {
		return fileName;
	}
	public static void setFileName(String fileName) {
		ResultSet.fileName = fileName;
	}
}
