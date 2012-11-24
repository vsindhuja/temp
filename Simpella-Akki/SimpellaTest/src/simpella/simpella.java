package simpella;

import java.io.*;
import java.net.*;
import java.util.*;

class Server extends Thread {

	public static ServerSocket tcpservsock;
	public Socket socket;

	public Server(Socket socket) {
		this.socket = socket;
	}

	public Server(int tcpport) throws IOException {
		try {
			tcpservsock = new ServerSocket(tcpport);
		} catch (IOException e) {
			System.out.println("Exception on new Server Socket" + e);
		}
	}

	public void run() {
		Socket socket;
		try {
			while (true) {
				socket = tcpservsock.accept(); // accept connection

				PrintStream output = new PrintStream(socket.getOutputStream());
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String incomingMess = "";
				int count = 0;
				Boolean connAcceptStatus = false;
				for (int i = 0; i <= simpella.hmClients.size(); i++) {
					Client tempInfo = simpella.hmClients.get(i);

					if (tempInfo != null) {
						if (tempInfo.getIncoming()) {
							count++;
							System.out.println(count);
						}
					}
				}
				if (count < 3) {
					connAcceptStatus = true;
				}
				if (connAcceptStatus) {
					try{
						input.mark(0);
						while ((incomingMess = input.readLine()) != null) {
							if (incomingMess.startsWith("SIMPELLA CONNECT/0.6")) {
								// Checking for existing connenctions

								System.out.println(" ");
								if (incomingMess.equalsIgnoreCase(Util.CONNECTION_ACK)) {
									System.out.println("" + incomingMess);

									Client newCli = new Client();
									newCli.setSock(socket);
									newCli.setIpAddress(socket.getRemoteSocketAddress().toString());
									simpella.connCount = simpella.connCount + 1;
									newCli.setConID(simpella.connCount);
									newCli.setIncoming(false);
									newCli.settcpPort(socket.getLocalPort());
									simpella.hmClients.put(simpella.connCount,newCli);
									input.reset();
								}
								output.println(Util.CONNECTION_ACCEPTED);
								System.out.print("Simpella>>");

							} else {
								System.out.println(" ");
								output.println(Util.CONNECTION_REFUSED);
							}
						}
					}catch(IOException ioe){
						/*
						 * Do nothing, since this exception is coming from the
						 * BufferedReader's reset method call. and doesn't bother the
						 * functionality.
						 */
						System.out.println("");
					}
				}
				new ClientHandler(socket).start();
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

class ClientHandler extends Thread {
	private Socket sock;
	PrintStream output;
	BufferedReader input;

	ClientHandler(Socket sock) {
		this.sock = sock;
	}

	public void run() {
		String line;
		try {
			output = new PrintStream(sock.getOutputStream());
			input = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));
			while (true) {
				while ((line = input.readLine()) != null) {
					System.out.println(line);
					output.println("Welcome to server");
					output.println("Received : " + line);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

public class simpella {
	static int connCount = 0;

	static Scanner scan = new Scanner(System.in);

	public static HashMap<Integer, Thread> threadMap = new HashMap<Integer, Thread>();
	public static HashMap<Integer, Client> hmClients = new HashMap<Integer, Client>();

	public static Boolean handshake = false;

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		int tcpport = Integer.parseInt(args[0]);
		int dloadport = Integer.parseInt(args[1]);

		try {
			Socket sock1 = new Socket("8.8.8.8", 53);
			InetAddress ipaddr = sock1.getLocalAddress();
			System.out.println("Local IP:" + ipaddr);
			System.out.println("Host Name:"
					+ ipaddr.getLocalHost().getHostName());
			System.out.println("Simpella Net Port: " + tcpport);
			System.out.println("Downloading Port: " + dloadport);
			System.out.println("simpella version 0.6 (c) 2002-2003 XYZ");
			System.out.println("\r\n\n");

		}

		catch (IOException e) {
			System.out.println("Exception on DNS Socket");
		}

		try {

			Thread t = new Server(tcpport);
			t.start();

		} catch (IOException e) {
			System.out.println("Exception on new Server Socket" + e);
		}

		String ipAddr;
		int tcpPort = 0000;
		String input;
		// Create sockets

		while (true) {
			System.out.print("Simpella>>");
			if (scan.hasNext()) {
				input = scan.nextLine();
				if (input.substring(0, 4).equalsIgnoreCase("info")) {
					try {
						Socket sock = new Socket("8.8.8.8", 53);
						System.out.println(String.format("%20s%20s%20s%20s",
								"IP", "Hostname", "TCP port", "Download Port"));
						System.out
						.println("-------------------------------------------------------------------------");
						InetAddress ipaddr = sock.getLocalAddress();
						System.out.print((String.format("%-20s", ipaddr)));
						System.out.print((String.format("%20s", ipaddr
								.getLocalHost().getHostName())));
						System.out.println((String.format("%20d", tcpport)));
						System.out.println((String.format("%20d", dloadport)));
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				}

				else if (input.substring(0, 4).equalsIgnoreCase("show")) {
					System.out.println(String.format("%10s%10s%20s",
							"conn. ID", "Host", "TCP port"));
					System.out
					.println("-------------------------------------------------------------");
					for (int i = 0; i <= hmClients.size(); i++) {
						Client tempInfo = hmClients.get(i);
						if (tempInfo != null) {
							System.out.print(String.format("%-10d",
									tempInfo.getConID()));
							System.out.print((String.format("%10s",
									tempInfo.getIpAddress())));
							System.out.println((String.format("%10d",
									tempInfo.gettcpPort())));

						}
					}

					System.out.println("\r\n");

				} else if (input.substring(0, 6).equalsIgnoreCase("sendto")) {
					String[] splitArr;
					try {
						if (input.substring(7) != null) {
							splitArr = input.substring(7).split(" ");
							int port = Integer.parseInt(splitArr[1]);
							InetAddress IPAddress = InetAddress
							.getByName(splitArr[0]);
							String message = "";
							for (int i = 2; i < splitArr.length; i++) {
								message = splitArr[2] + " ";
							}
							DatagramSocket clientSocket = new DatagramSocket();
							byte[] sendData = new byte[1024];
							sendData = message.getBytes();
							DatagramPacket sendPacket = new DatagramPacket(
									sendData, sendData.length, IPAddress, port);
							clientSocket.send(sendPacket);
							clientSocket.close();

						}
					} catch (UnknownHostException e) {
						System.out.println(e.getMessage());
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}

				} else if (input.substring(0, 4).equalsIgnoreCase("send")) {
					String[] splitArr;
					if (input.substring(5) != null) {
						splitArr = input.substring(5).split(" ");
						int conID = Integer.parseInt(splitArr[0]);
						String message = "";
						for (int i = 1; i < splitArr.length; i++) {
							message = splitArr[1] + " ";
						}
						System.out.println("Message : " + message);
						// Call the object of Info corresponding to the conID
						// and set the message variable value.
						(hmClients.get(conID)).setMessage(message);
						System.out.println("Thread state ::: "
								+ (threadMap.get(conID)).getState());
						Socket tempClientSock = (hmClients.get(conID))
						.getSock();
						try {
							PrintStream clientOutput = new PrintStream(
									tempClientSock.getOutputStream());
							BufferedReader clientInputLine = new BufferedReader(
									new InputStreamReader(
											tempClientSock.getInputStream()));
							clientOutput.println(message);
							System.out.println("Response from server $$$$$ "
									+ clientInputLine.readLine());
							// clientOutput.print(new Client());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						System.out
						.println("More parameters required to run the command.");
					}
				} else if (input.substring(0, 7).equalsIgnoreCase("Connect")) {
					try {
						String[] splitArr = input.substring(8).split(" ");

						ipAddr = splitArr[0];
						// Trim the last PORT number from the input string.
						tcpPort = Integer.parseInt(splitArr[1].trim());
						Socket sock = new Socket(ipAddr, tcpPort);
						// For each socket we create and start off a new thread.
						// Take-id and go! #russelpeters
						Client newCli = new Client();
						newCli.setSock(sock);
						newCli.handShake();
						// if(newCli.getHandshake())
						// {
						connCount = connCount + 1;
						newCli.setIpAddress(ipAddr);
						newCli.setConID(connCount);
						newCli.settcpPort(tcpPort);
						newCli.setIncoming(true);
						newCli.setdloadPort(dloadport);
						hmClients.put(connCount, newCli);
						Thread t = new Thread(newCli);
						threadMap.put(connCount, t);
						t.start();
						// }
						// else
						// {
						// System.out.println("Try again another time");
						// }

					} catch (IndexOutOfBoundsException ie) {
						System.out.println(" Input needs more parameters. ");
					} catch (BindException be) {
						System.out.println("Address already in use");
					} catch (ConnectException ce) {
						System.out.println(" No listner for the port "
								+ tcpPort
								+ " found. Please activate the listener.");
					} catch (UnknownHostException e) {
						System.out.println(e.getMessage());
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				} else if (input.substring(0, 10)
						.equalsIgnoreCase("disconnect")) {
					if (!(input.substring(10)).equals("")) {
						int conID = Integer
						.parseInt(input.substring(10).trim());
						try {
							// Close the socket opened against the connection ID
							// and remove it from the HashMap.
							Socket tempClientSock = (hmClients.get(conID))
							.getSock();
							PrintStream clientOutput = new PrintStream(
									tempClientSock.getOutputStream());
							clientOutput.println("Disconnected from "
									+ tempClientSock.getLocalSocketAddress());
							hmClients.get(conID).getSock().close();
							hmClients.remove(conID);
							simpella.connCount = simpella.connCount - 1;
							// Also stop the thread that was associated with it
							// and delete it from the HashMan.
							simpella.threadMap.remove(conID);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					System.out.println("Incorrect Input");
				}
			}
		}
	}
}