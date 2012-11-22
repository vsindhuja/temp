package simpella;

import java.io.*; 
import java.net.*; 
import java.util.*;

class Server extends Thread{

	public static ServerSocket tcpservsock;
	public Socket socket;

	public Server (Socket socket)
	{
		this.socket = socket;
	}

	public Server(int tcpport) throws IOException
	{
		try{
			tcpservsock = new ServerSocket (tcpport);
		}
		catch (IOException e)
		{
			System.out.println("Exception on new Server Socket"+e);
		}
	}

	public void run(){
		Socket socket;
		try{
			while(true){
				socket = tcpservsock.accept();  // accept connection

				PrintStream output = new PrintStream(socket.getOutputStream());
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String incomingMess = "";
				while((incomingMess = input.readLine())!=null){
					if(incomingMess.startsWith("SIMPELLA CONNECT/0.6")){
						//Check for existing connection count to be introduced.
						Boolean connAcceptStatus = true;
						if(connAcceptStatus){
							System.out.println("Received " + incomingMess + " from client, sending response." + Util.CONNECTION_ACCEPTED);
							output.println(Util.CONNECTION_ACCEPTED);
							System.out.println("Echoer>>");
						}else{ 
							System.out.println("Received " + incomingMess + " from client, sending response." + Util.CONNECTION_REFUSED);
							output.println(Util.CONNECTION_REFUSED);
						}
					}
					else if(incomingMess.equalsIgnoreCase("done")){
						System.out.println("Handshake SUCCESSFUL!!!");
						System.out.print("Echoer>>");
						output.println("Successful");
					}
				}
				new ClientHandler(socket).start();
			}
		}catch (IOException ioe){
			/*Do nothing, since this exception is coming from the BufferedReader's reset method call.
			 * and doesn't bother the functionality.
			 */
			System.out.print("");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}

class ClientHandler extends Thread{
	private Socket sock;
	PrintStream output ;
	BufferedReader input ;

	ClientHandler(Socket sock){
		this.sock = sock;
	}

	public void run(){
		String line;
		try{
			output = new PrintStream(sock.getOutputStream());
			input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			while(true){
				while((line=input.readLine())!=null){
					System.out.println(line);
					output.println("Welcome to server");
					output.println("Received : " + line);
				}
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}


public class Simpella
{
	static int connCount = 0;

	static Scanner scan = new Scanner(System.in);

	public static HashMap<Integer, Thread> threadMap = new HashMap<Integer, Thread>();

	static HashMap<Integer,Client> hmClients = new HashMap<Integer, Client>();

	public static Boolean handshake = false;

	@SuppressWarnings("static-access")
	public static void main(String[] args)
	{

		int tcpport = Integer.parseInt(args[0]);

		try{

			Thread t = new Server(tcpport);
			t.start();

		}
		catch (IOException e)
		{
			System.out.println("Exception on new Server Socket"+e);
		}

		String ipAddr;
		int tcpPort = 0000;
		String input;
		//Create sockets

		while(true){
			System.out.print("Echoer>>");
			if(scan.hasNext()){
				input = scan.nextLine();
				if(input.substring(0,4).equalsIgnoreCase("info")){
					try
					{ 
						Socket sock = new Socket("8.8.8.8", 53);
						System.out.println (String.format("%10s%10s%10s%10s","IP","Hostname","TCP port"));
						System.out.println ("-------------------------------------------------------------------------------------------------");
						InetAddress ipaddr = sock.getLocalAddress();
						System.out.print((String.format("%10s",ipaddr)));
						System.out.print((String.format("%10s",ipaddr.getLocalHost().getHostName())));
						System.out.println((String.format("%10d",tcpport)));
					}
					catch (IOException e) {
						System.out.println(e.getMessage());
					} 
				}

				else if(input.substring(0,4).equalsIgnoreCase("show")){
					System.out.println (String.format("%10s%10s%20s%20s%20s","conn. ID","IP","Hostname","local port","remote port"));
					System.out.println ("-------------------------------------------------------------------------------------------------");
					for(int i=0;i<=hmClients.size();i++){
						Client tempInfo = hmClients.get(i);
						if(tempInfo!=null){
							System.out.print(String.format("%-10d",i));
							System.out.print((String.format("%10s",tempInfo.getIpAddress())));
							System.out.print((String.format("%20s",tempInfo.getHostName())));
							System.out.print((String.format("%20d",tempInfo.getLocalPort())));
							System.out.println((String.format("%20d",tempInfo.getRemotePort())));

						}
					}
				}else if(input.substring(0,6).equalsIgnoreCase("sendto")){
					String[] splitArr ;
					try
					{
						if(input.substring(7)!=null){
							splitArr = input.substring(7).split(" ");
							int port = Integer.parseInt(splitArr[1]);
							InetAddress IPAddress = InetAddress.getByName(splitArr[0]);
							String message = "";
							for(int i=2;i<splitArr.length;i++){
								message = splitArr[2] + " ";
							}
							DatagramSocket clientSocket = new DatagramSocket();
							byte[] sendData = new byte[1024];
							sendData = message.getBytes();
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
							clientSocket.send(sendPacket);
							clientSocket.close();

						}
					}	catch (UnknownHostException e) {
						System.out.println(e.getMessage());
					} catch (IOException e) {
						System.out.println(e.getMessage());
					} 

				}else if(input.substring(0,4).equalsIgnoreCase("send")){
					String[] splitArr ;
					if(input.substring(5)!=null){
						splitArr = input.substring(5).split(" ");
						int conID = Integer.parseInt(splitArr[0]);
						String message = "";
						for(int i=1;i<splitArr.length;i++){
							message = splitArr[1] + " ";
						}
						System.out.println("Message : " + message);
						//Call the object of Info corresponding to the conID and set the message variable value.
						(hmClients.get(conID)).setMessage(message);
						System.out.println("Thread state ::: "+ (threadMap.get(conID)).getState());
						Socket tempClientSock = (hmClients.get(conID)).getSock(); 
						try {
							PrintStream clientOutput = new PrintStream(tempClientSock.getOutputStream());
							BufferedReader clientInputLine = new BufferedReader(new InputStreamReader(tempClientSock.getInputStream()));
							clientOutput.println(message);
							System.out.println("Response from server $$$$$ " + clientInputLine.readLine());
							//clientOutput.print(new Client());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						System.out.println("More parameters required to run the command.");
					}
				}else if(input.substring(0,7).equalsIgnoreCase("Connect")){
					try {
						String[] splitArr = input.substring(8).split(" ");

						ipAddr = splitArr[0];
						//Trim the last PORT number from the input string.
						tcpPort = Integer.parseInt(splitArr[1].trim());
						Socket sock = new Socket(ipAddr, tcpPort);
						//For each socket we create and start off a new thread. Take-id and go! #russelpeters
						connCount = connCount + 1;
						Client newCli = new Client();
						newCli.setSock(sock);
						newCli.setIpAddress(ipAddr);
						newCli.setConID(connCount);
						newCli.setLocalPort(tcpPort);
						handshake = true;
						newCli.handShake();
						hmClients.put(connCount, newCli);
						Thread t = new Thread(newCli);

						threadMap.put(connCount, t);
						t.start();

					} catch (IndexOutOfBoundsException ie){
						System.out.println(" Input needs more parameters. ");
					} catch (BindException be){
						System.out.println("Address already in use");
					} catch (ConnectException ce){
						System.out.println(" No listner for the port "+ tcpPort + " found. Please activate the listener.");
					} catch (UnknownHostException e) {
						System.out.println(e.getMessage());
					} catch (IOException e) {
						System.out.println(e.getMessage());
					} 
				}else
					if(input.substring(0, 10).equalsIgnoreCase("disconnect")){
						if(!(input.substring(10)).isEmpty()){
							int conID = Integer.parseInt(input.substring(10).trim());
							try {
								//Close the socket opened against the connection ID and remove it from the HashMap.
								Socket tempClientSock = (hmClients.get(conID)).getSock();
								PrintStream clientOutput = new PrintStream(tempClientSock.getOutputStream());
								clientOutput.println("Disconnected from "+tempClientSock.getLocalSocketAddress());
								hmClients.get(conID).getSock().close();
								hmClients.remove(conID);
								Simpella.connCount = Simpella.connCount - 1;
								//Also stop the thread that was associated with it and delete it from the HashMan.
								Simpella.threadMap.remove(conID);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					else{
						//I wanted to write "Go do gangnam style" but guess i'm going to have to be happy with just this.
						System.out.println("Incorrect Input");
					}
			}
		}
	}	
}