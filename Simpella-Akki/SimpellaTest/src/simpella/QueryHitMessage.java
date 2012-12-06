package simpella;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class QueryHitMessage {

	private  byte noOfHits;
	private short accpetingPort;
	private byte[] ipAddress = new byte[4];
	private int speed_RespondinHost = 10000; // Needs to be stored in Big Endian format.
	private short[] serventID;
	private SearchFiles[] searchFiles;	
	
	public short[] getServentID() {
		return serventID;
	}
	public void setServentID(short[] serventID) {
		this.serventID = serventID;
	}
	public short getAccpetingPort() {
		return accpetingPort;
	}
	public void setAccpetingPort(short accpetingPort) {
		this.accpetingPort = accpetingPort;
	}
	public SearchFiles[] getSearchFiles() {
		return searchFiles;
	}
	public void setSearchFiles(SearchFiles[] searchFiles) {
		this.searchFiles = searchFiles;
	}
	
	public byte getNoOfHits() {
		return noOfHits;
	}
	public void setNoOfHits(byte noOfHits) {
		this.noOfHits = noOfHits;
	}
	public byte[] getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(byte[] ipAddress) {
		this.ipAddress = ipAddress;
	}
	public byte[] getSpeed_RespondinHost() {
		return Util.convertIntToByteArray(speed_RespondinHost);
	}
	public void setSpeed_RespondinHost(int speed_RespondinHost) {
		this.speed_RespondinHost = speed_RespondinHost;
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
				pStream.writeByte((byte)serventID[i]);
			
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
