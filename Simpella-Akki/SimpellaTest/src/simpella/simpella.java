package simpella;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class Server extends Thread{

	public static ServerSocket tcpservsock;
	public Socket socket;
	public static int count = 0;

	public Server (Socket socket)
	{
		this.socket = socket;
	}

	public Server(int tcpport) throws IOException {
		try {
			tcpservsock = new ServerSocket(tcpport);
		} catch (IOException e) {
			System.out.println("Exception on new Server Socket" + e);
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
				Boolean connAcceptStatus = false;


				/****** FOR HANDSHAKE MESSAGES**********/
				if(Util.inCount<3)
				{
					connAcceptStatus = true;
					Util.inCount ++;
				}
				if (connAcceptStatus) {
					try{
						//input.mark(0);
						while ((incomingMess = input.readLine()) != null) {

							if (incomingMess.startsWith("SIMPELLA CONNECT/0.6")) {
								// Checking for existing connections

								if (incomingMess.equalsIgnoreCase(Util.CONNECTION_ACK)){
									System.out.println("" + incomingMess);
									System.out.print("Simpella>>");
									Client newCli = new Client();
									newCli.setSock(socket);
									newCli.setIpAddress(socket.getInetAddress().toString().substring(1));
									simpella.connCount = simpella.connCount + 1;
									newCli.setConID(simpella.connCount);
									newCli.settcpPort(socket.getLocalPort());
									simpella.hmClients.put(simpella.connCount,newCli);
									input.reset();
								}
								output.println(Util.CONNECTION_ACCEPTED);
								System.out.print("Simpella>>");

							} else if(incomingMess.startsWith("GET /get/")){/*
								FileSharing fs = new FileSharing(true);//Constructor to Send a file, NOT for Receiving
								String[] str = incomingMess.split("/");
								for (int i=0;i<str.length;i++)
									System.out.println(str[i]);

								fs.setFileName(Util.fileIndexTofileNameMap.get(str[2]));

								String fileName = str[3].substring(0,str[3].length()-4);
								PrintStream ps = new PrintStream (socket.getOutputStream());
								if(fileName.equals(fs.getFileName())){
									ps.println(fs.constructFreshResponse(true)); //Send a response of acknowledgement
									fs.setFile();
									fs.fileSend(socket); //Send the file using the same socket as this is a direct incoming connection.
								}*
								else{
									ps.println(fs.constructFreshResponse(false));
								}
							 */}
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

				else {
					System.out.println(" ");
					output.println(Util.CONNECTION_REFUSED);
				}
				/*************************************/
				new ClientHandler(socket).start();
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

class ClientHandler extends Thread{
	private Socket sock;
	public static HashMap<String,String> routetable = new HashMap<String, String>();
	static boolean isguid;


	public static ArrayList<PongMessageFormat> ponglist = new ArrayList<PongMessageFormat>();


	ClientHandler(Socket sock){
		this.sock = sock;
	}

	public void run(){

		try{
			DataInputStream obis = new DataInputStream(sock.getInputStream());			
			ParentMessageFormat pmf = new ParentMessageFormat();
			byte[] inputByteArray = new byte[4096];
			int sizeRead = -1;
			while ((sizeRead = obis.read(inputByteArray)) > 0)
			{
				if(!(inputByteArray[0] == 10 && inputByteArray[1] == 0))
				{
					for(int i=0;i<30;i++){
						//inputByteArray[i] =  (byte) obis.readUnsignedByte();
						System.out.println("CH : Value at byte"+i+"="+inputByteArray[i]);
					}

					//*******************************PING ****************************************//

					if((byte)inputByteArray[16] == (byte)Util.PING){
						pmf = Util.convertByteArrayToParentMF(inputByteArray);
						//Checking to see if i have the ping

						Socket tempClientSock;

						String ipstr = sock.getInetAddress().toString();
						String ip ;
						if(ipstr.contains("cse.buffalo.edu"))
						{
							String[] temparr = ipstr.split("/");
							ip= temparr[1];
						}
						else
						{
							ip = ipstr.substring(1);
						}

						/*for(int i =0;i<pmf.getGUID().length;i++)
						{
							System.out.println("PING GUID at"+i+":"+pmf.getGUID()[i]);

						}*/						

						if(!routetable.containsKey(pmf.guidToRawString()))
						{
							routetable.put(pmf.guidToRawString(), ip);
						}
						else
						{
							System.out.println("Already have this ping on my table");
						}
						//System.out.println("Route table Key set = "+routetable.keySet());
						for(int i=0;i<=simpella.hmClients.size();i++)
						{
							if(simpella.hmClients.get(i)!=null){

								if(!simpella.hmClients.get(i).getIpAddress().trim().equals(ip.trim()))
									try {
										tempClientSock = (simpella.hmClients.get(i)).getSock();

										//Use DataOutputStream for sending objects
										DataOutputStream clientOutput = new DataOutputStream(tempClientSock.getOutputStream());
										clientOutput.write(pmf.convertToByteArray());
										clientOutput.flush();

									} catch (IOException e) {
										e.printStackTrace();
									}
							}
						}

						//Sending Pong back to guy who sent ping
						PongMessageFormat pongmsg = new PongMessageFormat();
						pongmsg.setPort((short)sock.getLocalPort());
						String localaddr = sock.getLocalAddress().toString();
						String ip2 = localaddr.substring(1);
						pongmsg.setIpAddress(ip2);

						int numfiles =0;
						int size =0;

						if(simpella.currentPath!=null)
						{
							File mydir = new File(simpella.currentPath);
							numfiles = mydir.list().length;
							size = (int) mydir.length();

						}

						pongmsg.setFileSharingCount(numfiles);
						pongmsg.setKbShared(size);

						System.out.println("Putting pong in PMF with PORT:"+sock.getLocalPort()+" IP:"+ip2+" No of Files:"+numfiles+" Kb:"+size);
						byte[] pongbyte = pongmsg.toByteArray();
						pmf.setMessageType(Util.PONG);
						pmf.setPayloadLen(pongbyte.length);
						/*for(int i =0 ;i<pongbyte.length;i++)
						{
							System.out.println("Putting into PMF Payload"+i+":"+pongbyte[i]);
						}*/

						pmf.setPayload(pongbyte);
						/*for(int i=0;i<pmf.getPayload().length;i++)
						{
							System.out.println("PAYLOAD I AM SENDING at"+i+":"+pmf.getPayload()[i]);
						}

						for(int i =0 ;i<pongbyte.length;i++)
						{
							System.out.println("At PMF Payload"+i+":"+pmf.getPayload()[i]);
						}*/


						String routeip = routetable.get(pmf.guidToRawString());

						Socket tempClientSockpong ;

						for(int i=0;i<=simpella.hmClients.size();i++)
						{
							if(simpella.hmClients.get(i)!=null){

								if(simpella.hmClients.get(i).getIpAddress().trim().equals(routeip.trim()))
								{
									try {
										//System.out.println("Sending PONG to"+routeip);
										tempClientSockpong =  (simpella.hmClients.get(i)).getSock();
										DataOutputStream clientOutput = new DataOutputStream(tempClientSockpong.getOutputStream());
										clientOutput.write(pmf.convertToByteArray());
										for(int j=0;j<pmf.convertToByteArray().length-32;j++)
										{
											System.out.println("SENDING KBPS at"+j+":"+pmf.convertToByteArray()[j+32]);
										}
										clientOutput.flush();

									}catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

					//*******************************PONG ****************************************//
					else if((byte)inputByteArray[16] == (byte)Util.PONG)
					{
						/*for(int k=0; k<36;k++)
						{
							System.out.println("Byte received at"+k+":"+inputByteArray[k]);
						}*/
						pmf = Util.convertByteArrayToParentMF(inputByteArray);

						//checking to see if it is its own guid
						for(int i =0;i<pmf.getGUID().length;i++)
						{
							if(simpella.servguid[i]==pmf.getGUID()[i])
							{
								isguid = true;
							}
							else
							{
								isguid = false;
								break;
							}
						}

						/*for(int i =0;i<pmf.getGUID().length;i++)
						{
							System.out.println("PONG GUID at"+i+":"+pmf.getGUID()[i]);

						}

						for(int i =0;i<pmf.getGUID().length;i++)
						{
							System.out.println("MY GUID at"+i+":"+simpella.servguid[i]);

						}*/


						/*if(Arrays.equals(simpella.servguid,pmf.getGUID()))
						{
							isguid = true;
						}*/

						if(isguid)
						{
							System.out.println("This is my GUID");


							byte[] pongmsg = pmf.getPayload();

							byte[] temp = new byte[2];
							temp[0] = pongmsg[0];
							temp[1] = pongmsg[1];
							short port;
							//To turn bytes to shorts as either big endian or little endian
							//ByteBuffer.wrap(temp).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(port);

							ByteBuffer bb = ByteBuffer.wrap(temp).order(ByteOrder.BIG_ENDIAN);
							ShortBuffer sb = bb.asShortBuffer();
							port = sb.get();
							System.out.println("Port:"+port);

							byte[] temp2 = new byte[4];
							for(int i=0;i<4;i++)
								temp2[i] = pongmsg[i+2];

							String ipaddr = "";
							for(int i =0 ;i<4; i++)
								ipaddr = ipaddr + "."+(int)(temp2[i] & 255);

							ipaddr = ipaddr.substring(1); 
							//To turn shorts to bytes as either big endian or little endian

							//ByteBuffer.wrap(tempPort).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(port);
							for(int i=0;i<4;i++)
								temp2[i] = pongmsg[i+6];
							bb= ByteBuffer.wrap(temp2).order(ByteOrder.BIG_ENDIAN);
							int nofs= bb.getInt();
							System.out.println("No. of files ="+nofs);

							for (int i=0;i<4;i++)
							{
								System.out.println("KBS BYTE"+i+"="+pongmsg[i+10]);
							}
							for(int i=0;i<4;i++)
								temp2[i] = pongmsg[i+10];
							bb= ByteBuffer.wrap(temp2).order(ByteOrder.BIG_ENDIAN);
							int kbs= bb.getInt();

							System.out.println("Kbs ="+kbs);

							System.out.println("Got this PONG in PMF with PORT:"+port+" IP:"+ipaddr+" No of Files:"+nofs+" Kb:"+kbs);
							PongMessageFormat pongstore = new PongMessageFormat();

							pongstore.setPort(port);
							pongstore.setIpAddress(ipaddr);
							pongstore.setFileSharingCount(nofs);
							pongstore.setKbShared(kbs);

							for(int j =0;j<ponglist.size();j++)
							{
								if(ponglist.get(j).getIpAddress().equals(ipaddr))
								{
									ponglist.remove(j);
								}
								System.out.println("Adding pong to my list:"+ipaddr);
								ponglist.add(pongstore);

							}

							//TODO Extract the pong info from the message and put in table

						}

						//if not the same guid
						else
						{
							System.out.println("This is not my GUID");

							String ip = routetable.get(pmf.guidToRawString());

							Socket tempClientSock ;

							for(int i=0;i<=simpella.hmClients.size();i++)
							{
								if(simpella.hmClients.get(i)!=null){

									if(simpella.hmClients.get(i).getIpAddress().trim().equals(ip.trim()))
									{
										try {
											tempClientSock =  (simpella.hmClients.get(i)).getSock();
											DataOutputStream clientOutput = new DataOutputStream(tempClientSock.getOutputStream());
											clientOutput.write(pmf.convertToByteArray());
											clientOutput.flush();
										}catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
							}


						}
					}

					//*******************************QUERY ****************************************//
					else if((byte)inputByteArray[16] == (byte)Util.QUERY)
					{
						pmf = Util.convertByteArrayToParentMF(inputByteArray);

						//TODO Add the size check validations here

						//******NEEDS TO BE ITERATED BEFORE PROCEEDING ELSE IT WILL THROW ARRAY INDEX OUT OF BOUNDS
						byte[] temp = new byte[pmf.getPayloadLen()];
						//NEEDS TO BE ITERATED BEFORE PROCEEDING ELSE IT WILL THROW ARRAY INDEX OUT OF BOUNDS*******

						temp = pmf.getPayload();

						int querylen = temp.length - 2;

						String query;
						byte[] temp2 = new byte[querylen];

						for(int j=0;j<querylen;j++)
						{
							temp2[j] = temp[j+2];
						}
						query = new String(temp);

						System.out.println("Query i got "+query);

						if(simpella.currentPath!=null)
						{
							String[] splitArr = query.split(" ");

							File f = new File(simpella.currentPath);

							String[] filelist = f.list();
							File[] fileNames = f.listFiles();
							int fileCount = 0;
							QueryHitMessage qhm = new QueryHitMessage();
							ArrayList<SearchFiles> sfl = new ArrayList<SearchFiles>();

							for(int i=0;i<filelist.length;i++)
							{
								String[] mp3 = fileNames[i].getName().split("\\.");
								String fullname = mp3[0];
								String[] names = fullname.split(" ");

								System.out.println("Files I have : " + fileNames[i].getName() 
										+ " Path " + fileNames[i].getAbsolutePath() + "Size : " + fileNames[i].length());

								for(int j=0;j<splitArr.length;j++)
								{
									if(names.length>0){
										for(int k=0;k<names.length;k++)
										{
											if(names[k].toLowerCase().trim().equalsIgnoreCase((splitArr[j].toLowerCase().trim()))){
												System.out.println("Match"+fileNames[i].getName());
												SearchFiles sf = searchMyFiles(fileNames[i].getName());
												sfl.add(sf);
												fileCount++;



											}
										}
									}
									if(fullname.toLowerCase().trim().equalsIgnoreCase((splitArr[j].toLowerCase().trim()))) 
									{
										System.out.println("Match"+fileNames[i].getName());
										SearchFiles sf = searchMyFiles(fileNames[i].getName());
										sfl.add(sf);
										fileCount++;
									}
								}
							}

							SearchFiles[] search= null;
							sfl.toArray(search);

							qhm.setSearchFiles(search);
							qhm.setNoOfHits((byte)fileCount);
							qhm.setAccpetingPort(simpella.dloadport);
							qhm.setIpAddress(sock.getInetAddress().toString().substring(1).getBytes());
							ParentMessageFormat par = new ParentMessageFormat();
							par.setGUID(Util.generateGUID());
							qhm.setServentID(par.guidToRawString());

						}
					}
					else
					{
						System.out.println("Sorry ! I do not have a shared directory");
					}

					System.out.print("Simpella>>");
				}
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	public SearchFiles searchMyFiles(String fileName){
		SearchFiles[] sf = (SearchFiles[]) simpella.myfiles.values().toArray();
		for(int i=0;i<sf.length;i++){
			if(sf[i].getFileName().equalsIgnoreCase(fileName))
				return sf[i];
		}
		return null;
	}
}

public class simpella {
	static int connCount = 0;

	static int tcpport = 6346;		//Set to Default Port
	static short dloadport = 5635;	//Set to Default Port

	static Scanner scan = new Scanner(System.in);

	public static HashMap<Integer, Thread> threadMap = new HashMap<Integer, Thread>();

	static HashMap<Integer,Client> hmClients = new HashMap<Integer, Client>();

	static HashMap<String,SearchFiles> myfiles = new HashMap<String,SearchFiles>();

	static String currentPath = null;

	public static short[] servguid;

	static String myaddr;


	public static Boolean handshake = false;

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		if(args.length==1){
			int tcpport = Integer.parseInt(args[0]);
		}
		if(args.length==2){
			int dloadport = Integer.parseInt(args[1]);
		}

		Util pingutil = new Util();
		servguid = pingutil.generateGUID();

		try {
			Socket sock1 = new Socket("8.8.8.8", 53);
			InetAddress ipaddr = sock1.getLocalAddress();
			myaddr = ipaddr.toString().substring(1);
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
			Thread fileThread = new FileSharingServer(dloadport);
			fileThread.start();

		} catch (IOException e) {
			System.out.println("Exception on new Server Socket" + e);
		}

		String ipAddr;
		int tcpPort = 0000;
		String input;
		// Create sockets

		while (true) {
			try{
				System.out.print("Simpella>>");
				if (scan.hasNext()) {
					input = scan.nextLine();
					if (input.startsWith("info")) {
						String[] splitArr = input.split(" ");

						//info c
						if(splitArr[1].equalsIgnoreCase("h"))
						{
							/*try {
								Socket sock = new Socket("8.8.8.8", 53);
								System.out.println(String.format("%-20s%-30s%-10s%-10s",
										"IP", "Hostname", "TCP port", "Download Port"));
								System.out
								.println("-------------------------------------------------------------------------");
								InetAddress ipaddr = sock.getLocalAddress();
								System.out.print((String.format("%-20s", ipaddr.toString().substring(1))));
								System.out.print((String.format("%-30s", ipaddr
										.getLocalHost().getHostName())));
								System.out.print((String.format("%-10d", tcpport)));
								System.out.println((String.format("%-10d", dloadport)));
							} catch (IOException e) {
								System.out.println(e.getMessage());
							}*/
							
							System.out.println("HOST STATS:");
							System.out.println("----------------");
							
						}

						//info d 
						if(splitArr[1].equalsIgnoreCase("d"))
						{
							

						}

					}

					else if (input.startsWith("show")) {
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
					}else if(input.startsWith("send")){
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
							Socket tempClientSock = (hmClients.get(conID)).getSock(); 
							try {
								PrintStream clientOutput = new PrintStream(tempClientSock.getOutputStream());
								BufferedReader clientInputLine = new BufferedReader(new InputStreamReader(tempClientSock.getInputStream()));
								clientOutput.println(message);
								System.out.println("Response from server $$$$$ " + clientInputLine.readLine());
								//clientOutput.print(new Client());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}else{
							System.out.println("More parameters required to run the command.");
						}
					}
					else if(input.startsWith("update")){

						ParentMessageFormat message = new ParentMessageFormat();

						message.setGUID(servguid);
						message.setMessageType((byte)Util.PING);
						message.setTTL(7);
						message.setHops(0);

						Socket tempClientSock ;

						for(int i=0;i<=hmClients.size();i++)
						{
							if(hmClients.get(i)!=null){
								try {
									tempClientSock =  (hmClients.get(i)).getSock();
									DataOutputStream clientOutput = new DataOutputStream(tempClientSock.getOutputStream());
									clientOutput.write(message.convertToByteArray());
									clientOutput.flush();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}

					else if (input.startsWith("share"))
					{
						String[] splitArr = input.substring(6).split(" ");
						String mypath;
						//for setting the directory to share
						try{
							if(splitArr[0].equalsIgnoreCase("dir"))
							{

								if(splitArr[1].startsWith("/"))
								{
									mypath = splitArr[1];
									File mydir = new File(mypath);
									if(mydir.isDirectory())
									{
										mydir.setWritable(true, false);
										currentPath = mydir.getPath();
									}
									else
									{ 
										int a = 1/0;
									}
								}

								else
								{
									File f = new File("");

									currentPath = f.getAbsolutePath();
									System.out.println("Curent Path"+currentPath);
									mypath =currentPath+"/"+splitArr[1];
									File mydir = new File(mypath);
									if(mydir.isDirectory())
									{
										mydir.setWritable(true, false);
										currentPath = mydir.getPath();
									}
									else
									{ 
										int a = 1/0;
									}
								}

								File f = new File(currentPath);
								String[] fname =f.list();
								SearchFiles sf = new SearchFiles();

								for(int i=0;i<fname.length;i++)
								{
									sf.setFileIndex(Util.generateFileIndex());
									sf.setFileName(fname[i]);
									sf.setIpAddress(myaddr);
									sf.setPort(dloadport);
									sf.setSize((int)f.listFiles()[i].length());
									myfiles.put(sf.getFileIndex(), sf);
								}
							}

							//for checking which is the current shared directory
							if(splitArr[0].equalsIgnoreCase("-i"))
							{
								System.out.println("sharing "+currentPath);
							}

							else{
								System.out.println("Entered share option does not exist");
							}

						}
						catch(ArithmeticException e)
						{
							System.out.println("The entered directory does not exist");
						}
					}

					else if (input.startsWith("scan"))
					{

						System.out.println("scanning "+currentPath+" for files ...");
						File mydir = new File(currentPath);
						int numfiles = mydir.list().length;
						mydir.list();
						long size = mydir.length();
						System.out.println("Scanned "+numfiles+" files and "+size+" bytes.");
						Util.existingFiles = new String[numfiles];

					}

					else if(input.startsWith("open")){
						try {
							String[] splitArr = input.substring(5).split(" ");

							String[] splitArr2 = splitArr[0].split(":");
							ipAddr = splitArr2[0].trim();
							//Trim the last PORT number from the input string.
							tcpPort = Integer.parseInt(splitArr2[1].trim());
							Socket sock = new Socket(ipAddr, tcpPort);

							//checking if the entered ip is ip or hostname and parsing accordingly
							String ipstr = sock.getInetAddress().toString();
							String ip ;
							if(ipstr.contains("cse.buffalo.edu"))
							{
								String[] temparr = ipstr.split("/");
								ip= temparr[1];
							}
							else
							{
								ip = ipstr.substring(1);
							}

							// For each socket we create and start off a new thread.
							Client newCli = new Client();
							newCli.setSock(sock);
							newCli.handShake();
							if(newCli.getHandshake())
							{
								connCount = connCount + 1;
								newCli.setIpAddress(ip);
								newCli.setConID(connCount);
								newCli.settcpPort(tcpPort);
								Util.inCount++;
								newCli.setdloadPort(dloadport);
								hmClients.put(connCount, newCli);
								Thread t = new Thread(newCli);
								threadMap.put(connCount, t);
								t.start();
								new ClientHandler(sock).start();
								//Sending Ping Part
								ParentMessageFormat message = new ParentMessageFormat();
								/*for (int i =0;i<servguid.length;i++)
								{
									System.out.println("Initial GUID at SIMPELLA"+i+":"+servguid[i]);

								}*/

								message.setGUID(servguid);
								/*for(int i =0;i<servguid.length;i++)
								{
									System.out.println("Gettin from msg GUID at SIMPELLA"+i+":"+message.getGUID()[i]);

								}*/
								message.setMessageType((byte)Util.PING);
								message.setTTL(7);
								message.setHops(0);

								servguid = message.getGUID().clone();

								Socket tempClientSock ;


								ParentMessageFormat pmf = new ParentMessageFormat();
								pmf = pingutil.convertByteArrayToParentMF(message.convertToByteArray());

								servguid =pmf.getGUID().clone();

								for(int i=0;i<=hmClients.size();i++)
								{
									if(hmClients.get(i)!=null){
										try {
											tempClientSock =  (hmClients.get(i)).getSock();
											DataOutputStream clientOutput = new DataOutputStream(tempClientSock.getOutputStream());
											clientOutput.write(message.convertToByteArray());
											/*for(int j=0;j<message.convertToByteArray().length;j++)
											{
												System.out.println("UP at"+j+":"+message.convertToByteArray()[j]);
											}*/
											clientOutput.flush();

										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
							}

						} catch (IndexOutOfBoundsException ie) {
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
					}

					else if(input.startsWith("disconnect")){
						if(!(input.substring(10)).equals("")){
							int conID = Integer.parseInt(input.substring(10).trim());
							try {
								//Close the socket opened against the connection ID and remove it from the HashMap.
								Socket tempClientSock = (hmClients.get(conID)).getSock();
								PrintStream clientOutput = new PrintStream(tempClientSock.getOutputStream());
								clientOutput.println("Disconnected from "+tempClientSock.getLocalSocketAddress());
								hmClients.get(conID).getSock().close();
								hmClients.remove(conID);
								connCount = connCount - 1;
								//Also stop the thread that was associated with it and delete it from the HashMan.
								threadMap.remove(conID);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}else if(input.startsWith("find")){
						String[] splitArr = input.substring(5).split(" ");

						String search = input.substring(5, input.length());
						ParentMessageFormat pmf = new ParentMessageFormat();

						byte[] query2;
						query2 = search.getBytes();
						int querylen = 2+query2.length;

						byte[] query = new byte[querylen];

						query[0] = 0;
						query[1] = 0;

						System.out.println("Searching for "+search+" on the simpella network");

						for(int i=0;i<query2.length;i++)
						{
							query[i+2] = query2[i];
						}

						pmf.setGUID(servguid);
						pmf.setMessageType(Util.QUERY);
						pmf.setTTL(7);
						pmf.setHops(0);
						pmf.setPayload(query);
						pmf.setPayloadLen(querylen+23);

						Socket tempClientSock;

						for(int i=0;i<=hmClients.size();i++)
						{
							if(hmClients.get(i)!=null){
								try {
									tempClientSock =  (hmClients.get(i)).getSock();
									DataOutputStream clientOutput = new DataOutputStream(tempClientSock.getOutputStream());
									clientOutput.write(pmf.convertToByteArray());
									clientOutput.flush();
									for(int j=0;j<pmf.convertToByteArray().length;j++)
									{
										System.out.println("UP at"+j+":"+pmf.convertToByteArray()[j]);
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}

					else if(input.startsWith("download")){
						try{
							int filenum = Integer.parseInt(input.substring(8).trim());
							//Initiate a request for the file.
							FileSharing fileShare = new FileSharing(); //Constructor to Receive a file, NOT send a file.
							//FileSharing fileShare = new FileSharing(new File(sfs.fileName), input, sfs.getIpAddress(), sfs.getPort());
							fileShare.recvFile=true;
							fileShare.ipaddString = "localhost";	//CHANGE
							fileShare.port = 3300;	//hardcoded CHANGE
							Socket temp = fileShare.establishConn();
							boolean accepted = false;
							//send request to remote user for file.
							try {
								PrintStream ps = new PrintStream(temp.getOutputStream());
								ps.println(fileShare.constructFreshReq());

								BufferedReader br = new BufferedReader(new InputStreamReader(temp.getInputStream()));
								String response ="";

								br.mark(0);

								if((response = br.readLine())!=null){
									if(response.equalsIgnoreCase(Util.ACCEPT_FILE_SHARING_REQ)){
										accepted = true;
										ps.println("ACCEPTED");
										while((response = br.readLine())!=null){
											if(response.startsWith(Util.CONTENT_LENGTH)){
												fileShare.fileSize = Integer.parseInt(response.substring(Util.CONTENT_LENGTH.length()));
												br.reset();
											}

										}
									}
								}
							} catch (IOException e) {
								//Dont do anything. This exception is coming from the reset method of BufferedReader.
								System.out.print("");
							}
							if(accepted){
								fileShare.start();
							}
						}catch(NumberFormatException nfe){
							System.out.println("Incorrect input");
						}
					}

					else {
						System.out.println("Incorrect Input");
					}
				}
			}catch(StringIndexOutOfBoundsException e){
				System.out.println("Incorrect output " + e.getMessage());

			}
		}
	}
}

class FileSharingServer extends Thread{
	public static ServerSocket dloadServSock;
	public Socket socket;
	public static int count = 0;

	public FileSharingServer (Socket socket){
		this.socket = socket;
	}

	public FileSharingServer(int dloadPort) throws IOException{
		try {
			dloadServSock = new ServerSocket(dloadPort);
		} catch (IOException e) {
			System.out.println("Exception on download Server Socket" + e);
		}
	}
	//My file is to be read and written on the Output Stream.
	public void run(){
		Socket socket;
		try{
			while(true){
				socket = dloadServSock.accept();  // accept connection
				PrintStream output = new PrintStream(socket.getOutputStream());
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String incomingMess = "";
				Boolean fileAcceptStatus = false;

				if(Util.downloadCount==0){
					fileAcceptStatus = true;
				}
				if (fileAcceptStatus) {
					try{
						//input.mark(0);
						if((incomingMess = input.readLine()).startsWith("GET /get/")){
							Util.downloadCount++;
							FileSharing fs = null;

							fs = new FileSharing();		//Constructor to Send a file, NOT for Receiving
							//Send response to server.
							fs.fileSize = 450560;
							output.println(fs.constructFreshResponse(true));
							String[] str = incomingMess.split("/");
							for (int i=0;i<str.length;i++)
								System.out.println("**" + str[i]);

							//File INDEX
							//(Util.fileIndexTofileNameMap.get(str[2]));

							String fileName = str[3].substring(0,str[3].length()-4).trim();

							fs.setFileName(fileName);
							fs.file = new File(simpella.currentPath + "/" + fileName);

							while ((incomingMess = input.readLine()) != null) {
								if(incomingMess.equalsIgnoreCase("ACCEPTED")){
									PrintStream ps = new PrintStream (socket.getOutputStream());
									//if(fileName.trim().equals(fs.getFileName().trim())){
									fs.fileSend(socket); //Send the file using the same socket as this is a direct incoming connection.
								}
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
					Util.downloadCount--;
				}
				else {
					System.out.println(" ");
					output.println(Util.REJECT_FILE_SHARING_REQ);
				}
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
