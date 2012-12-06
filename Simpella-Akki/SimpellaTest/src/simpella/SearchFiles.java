package simpella;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SearchFiles {
	
	int size;
	String fileName;
	String fileIndex;
	
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public byte[] convertToByteArray(){
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		try {
			DataOutputStream pStream = new DataOutputStream(byteStream);
			byte[] fileInBytes = Util.convertIntToByteArray(Integer.parseInt(fileIndex));
			
			for(int i=0;i<fileInBytes.length;i++)
				pStream.writeByte(fileInBytes[i]);
			
			byte[] sizeByte =  Util.convertIntToByteArray(size);
			for(int i=0;i<sizeByte.length;i++)
				pStream.writeByte(sizeByte[i]);
			
			fileInBytes = Util.convertIntToByteArray(Integer.parseInt(fileName));
			for(int i=0;i<fileInBytes.length;i++)
				pStream.writeByte(fileInBytes[i]);
			
			pStream.close();
			
		}catch (IOException io) {
			System.out.println("Issue in Data input Stream");
		}
		return byteStream.toByteArray();
	}
}
