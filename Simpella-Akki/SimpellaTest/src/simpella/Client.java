package simpella;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

class Client extends Thread
{
	Socket sock;
	String ipAddress;
	String hostName;
	int localPort;
	int remotePort;
	String message;
	Integer conID;
	DataInputStream dis;
	PrintStream os;
	boolean send = false; 

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public Integer getConID() {
		return conID;
	}

	public void setConID(Integer conID) {
		this.conID = conID;
	}

	public DataInputStream getDis() {
		return dis;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public PrintStream getOs() {
		return os;
	}

	public void setOs(PrintStream os) {
		this.os = os;
	}

	public boolean isSend() {
		return send;
	}

	public void setSend(boolean send) {
		this.send = send;
	}

	public void setSock(Socket sock) {
		this.sock = sock;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Socket getSock() {
		return sock;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getHostName() {
		return hostName;
	}

	public String getMessage(){
		return message;
	}
	public void setMessage(String message){
		this.message = message;
	}

	public Client(){
		//Default constructor.
	}

	public void send(String message){

		Socket tempClientSock = sock; 
		try {
			PrintStream clientOutput = new PrintStream(tempClientSock.getOutputStream());
			BufferedReader clientInputLine = new BufferedReader(new InputStreamReader(tempClientSock.getInputStream()));
			clientOutput.println(message);
			System.out.println("Response from server $$$$$ " + clientInputLine.readLine());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handShake(){
		if(Simpella.handshake){
			Socket tempClientSock = sock; 
			String inputline = "";
			try {
				PrintStream clientOutput = new PrintStream(tempClientSock.getOutputStream());
				BufferedReader clientInputLine = null;
				clientInputLine = new BufferedReader(new InputStreamReader(tempClientSock.getInputStream()));
				clientInputLine.mark(0);
				System.out.println("Initiating handshake by sending connection request to client");
				clientOutput.println("SIMPELLA CONNECT/0.6 \r \n");
					while((inputline=clientInputLine.readLine())!=null){
						if(inputline.startsWith("SIMPELLA/0.6")){
							if(inputline.substring(13,16).equals("200")){
								//System.out.println("Receieved "+ inputline +" from server, Connection ACCEPTED.");
								System.out.println(inputline);
							}else if(inputline.substring(13,16).equals("503")){
								//System.out.println("Receieved "+ inputline +" from server, Connection REFUSED.");
								System.out.println(inputline);
							}
							//clientOutput.println("Done");
							Simpella.handshake=false;
							System.out.println("Echoer>>");
							//clientInputLine.reset();
						}
						if(inputline.equalsIgnoreCase("Successful")){
							Simpella.handshake=false;
							clientInputLine.reset();
						}
					}
			} catch (IOException e) {
				//Do nothing, since this exception is coming from the BufferedReader's reset method call.
				System.out.print("");
			}
		}
	}
}
