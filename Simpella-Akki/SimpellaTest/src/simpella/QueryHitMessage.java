package simpella;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

<<<<<<< HEAD
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
=======
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
	
>>>>>>> 76350556fedfdfc45925bef3dc0e301e871307bd
	public byte getNoOfHits() {
		return noOfHits;
	}
	public void setNoOfHits(byte noOfHits) {
<<<<<<< HEAD
		QueryHitMessage.noOfHits = noOfHits;
	}
	public short getAccpetingPort() {
		return accpetingPort;
	}
	public void setAccpetingPort(short accpetingPort) {
		QueryHitMessage.accpetingPort = accpetingPort;
	}
=======
		this.noOfHits = noOfHits;
	}
>>>>>>> 76350556fedfdfc45925bef3dc0e301e871307bd
	public byte[] getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(byte[] ipAddress) {
<<<<<<< HEAD
		QueryHitMessage.ipAddress = ipAddress;
	}
	public int getSpeed_RespondinHost() {
		return speed_RespondinHost;
	}
	public void setSpeed_RespondinHost(int speed_RespondinHost) {
		QueryHitMessage.speed_RespondinHost = speed_RespondinHost;
=======
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
>>>>>>> 76350556fedfdfc45925bef3dc0e301e871307bd
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
