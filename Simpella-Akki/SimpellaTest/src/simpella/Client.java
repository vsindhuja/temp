package simpella;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

class Client extends Thread
{
	Socket sock;
	Socket outsock;
	String ipAddress;
	String hostName;
	int tcpPort;
	int dloadPort;
	String message;
	Integer conID;
	Boolean handshake;
	boolean send = false;

	public int gettcpPort() {
		return tcpPort;
	}

	public void settcpPort(int localPort) {
		this.tcpPort = localPort;
	}

	public int getdloadPort() {
		return dloadPort;
	}

	public void setdloadPort(int remotePort) {
		this.dloadPort = remotePort;
	}

	public Integer getConID() {
		return conID;
	}

	public void setConID(Integer conID) {
		this.conID = conID;
	}

	public Boolean getHandshake() {
		return handshake;
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

	public void setoutSock(Socket sock) {
		this.outsock = sock;
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

	public Socket getoutSock() {
		return outsock;
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
		Socket tempClientSock = sock; 
		String inputline = "";
		try {
			BufferedReader clientInputLine = new BufferedReader(new InputStreamReader(tempClientSock.getInputStream()));
			PrintStream clientOutput = new PrintStream(tempClientSock.getOutputStream());
			clientInputLine.mark(0);
			clientOutput.println("SIMPELLA CONNECT/0.6 \r \n");
			clientOutput.flush();
			while((inputline=clientInputLine.readLine())!=null){
				if(inputline.startsWith("SIMPELLA/0.6")){
					if(inputline.substring(13,16).equals("200")){
						handshake = true;
						System.out.println(inputline);
						clientOutput.println(Util.CONNECTION_ACK);
						clientOutput.flush();
						clientInputLine.reset();
					}else if(inputline.substring(13,16).equals("503")){
						handshake=false;
						System.out.println(inputline);
						clientInputLine.reset();
					}
				}
			}

		} catch (IOException e) {
			//Do nothing, since this exception is coming from the BufferedReader's reset method call.
			System.out.print("");
		}
	}
}
