package simpella;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileSharing extends Thread{

	File file;
	String fileName = "";
	String ipaddString;
	int port;
	SearchFiles searchedFile;
	int fileSize=0;
	boolean  recvFile = false;
	boolean sendFile = false;
	Socket sock;

	public FileSharing() {
		// TODO Auto-generated constructor stub
	}

	public String constructFreshReq(){
		
		StringBuffer sbReq = new StringBuffer();
		String fileRequest = "GET /get/1/Aahatein.mp3 HTTP/1.1\r\n";
		sbReq.append(fileRequest);
		sbReq.append(Util.USER_AGENT);
		sbReq.append(Util.NEWLINE_TAB);
		sbReq.append(Util.HOST + ipaddString + ":" + port + Util.NEWLINE_TAB);
		sbReq.append(Util.NEWLINE_TAB);
		sbReq.append(Util.CONNECTION_TYPE);
		sbReq.append(Util.NEWLINE_TAB);
		sbReq.append(Util.RANGE);
		sbReq.append(Util.NEWLINE_TAB);
		return sbReq.toString();
	}

	public String constructFreshResponse(boolean accept){
		StringBuffer sbReq = new StringBuffer();
		if(accept){				//Send accepted response if Accepting request.
			sbReq.append(Util.ACCEPT_FILE_SHARING_REQ);
			sbReq.append(Util.NEWLINE_TAB);
			sbReq.append(Util.SERVER_INFO);
			sbReq.append(Util.NEWLINE_TAB);
			sbReq.append(Util.CONTENT_TYPE);
			sbReq.append(Util.NEWLINE_TAB);
			sbReq.append(Util.CONTENT_LENGTH + fileSize + Util.NEWLINE_TAB);
			sbReq.append(Util.NEWLINE_TAB);
		}else{					//Send rejected response if Rejecting request.
			sbReq.append(Util.REJECT_FILE_SHARING_REQ);
			sbReq.append(Util.NEWLINE_TAB);
		}
		return sbReq.toString();
	}

	public void fileSend(Socket sock){
		try {
			FileInputStream fin = new FileInputStream(file);
			DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

			int len = (int) file.length();
			byte[] b = new byte[len];

			//Read the contents of the file and put it in an array, send the array through the dos.
			while((fin.read(b, 0, len))>0){
				dos.write(b);
				dos.flush();
			}
			sendFile = false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void fileReceive(){
		try {
			//sock = establishConn();
			File recvFile = new File(simpella.currentPath + "/" + "1.mp3");
			if(sock!=null){
				DataInputStream dis = new DataInputStream(sock.getInputStream());
				byte[] inputByteArray = new byte[fileSize];
				int sizeRead = 0;
				while ((sizeRead = dis.read(inputByteArray)) > 1){
					FileOutputStream fos = new FileOutputStream(recvFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					bos.write(inputByteArray); 	// Write to the file.
					bos.flush();
				}
			}
			this.recvFile = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Socket establishConn(){
		
		try {
			sock = new Socket(ipaddString,port);
		} catch (UnknownHostException e) {
			System.out.println("No Listener on the port " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sock;
	}
	public void run(){
		while(recvFile){
			fileReceive();
		}
	}
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile() {
		if(searchedFile!=null){
			this.file = new File(simpella.currentPath+ "/" +searchedFile.fileName);
			System.out.println("Setting this as file :" + this.file.getAbsolutePath());
			}
		//this.file = new File(simpella.currentPath+"/" + getFileName());
	}
}