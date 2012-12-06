package simpella;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class QueryHitMessage {

	private static byte noOfHits;
	private static short accpetingPort;
	private static byte[] ipAddress = new byte[4];
	private static int speed_RespondinHost = 10000; // Needs to be stored in Big Endian format.
	private static byte[] serventID;
	private static SearchFiles[] searchFiles;	
	
	public static byte[] getServentID() {
		return serventID;
	}
	public static void setServentID(byte[] serventID) {
		QueryHitMessage.serventID = serventID;
	}
	public static short getAccpetingPort() {
		return accpetingPort;
	}
	public static void setAccpetingPort(short accpetingPort) {
		QueryHitMessage.accpetingPort = accpetingPort;
	}
	public static SearchFiles[] getSearchFiles() {
		return searchFiles;
	}
	public static void setSearchFiles(SearchFiles[] searchFiles) {
		QueryHitMessage.searchFiles = searchFiles;
	}
	
	public static byte getNoOfHits() {
		return noOfHits;
	}
	public static void setNoOfHits(byte noOfHits) {
		QueryHitMessage.noOfHits = noOfHits;
	}
	public static byte[] getIpAddress() {
		return ipAddress;
	}
	public static void setIpAddress(byte[] ipAddress) {
		QueryHitMessage.ipAddress = ipAddress;
	}
	public static byte[] getSpeed_RespondinHost() {
		return Util.convertIntToByteArray(speed_RespondinHost);
	}
	public static void setSpeed_RespondinHost(int speed_RespondinHost) {
		QueryHitMessage.speed_RespondinHost = speed_RespondinHost;
	}
	
	public byte[] convertToByteArray(){
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		try {

			DataOutputStream pStream = new DataOutputStream(byteStream);
			pStream.writeByte(noOfHits);
			byte[] portBytes = ByteBuffer.allocate(2).putShort(accpetingPort).array();
			
			for(int i=0;i<portBytes.length;i++)
				pStream.writeByte(portBytes[i]);
			
			for(int i=0;i<ipAddress.length;i++)
				pStream.writeByte(ipAddress[i]);
			
			byte[] speedBytes = Util.convertIntToByteArray(speed_RespondinHost); 
			for(int i=0;i<speedBytes.length;i++)
				pStream.writeByte(speedBytes[i]);
			
			for(int i=0;i<searchFiles.length;i++){
				for(int j=0;j<searchFiles[i].convertToByteArray().length;j++){
					byte[] bytes = searchFiles[i].convertToByteArray();
					pStream.writeByte(bytes[j]);
				}
			}

			for(int i=0;i<serventID.length;i++)
				pStream.writeByte(serventID[i]);
			
			pStream.close();
		}
		catch (IOException io) {
			System.out.println("Issue in Data input Stream");
		}
		return byteStream.toByteArray();
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
