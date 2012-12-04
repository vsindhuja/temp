package simpella;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	//String Messages
	public static String CONNECT = "SIMPELLA CONNECT/0.6 \r \n";
	public static String CONNECTION_ACCEPTED = "SIMPELLA/0.6 200 OK \r \n";
	public static String CONNECTION_REFUSED = "SIMPELLA/0.6 503 Exceeded Connection Capacity \r \n";
	public static String CONNECTION_ACK = "SIMPELLA CONNECT/0.6 Initiator thanking for accepting connection";

	static int outCount = 0;
	static int inCount = 0;
	static int downloadCount = 0;

	//Message Types 
	public static byte PING = 0x00;
	public static byte PONG = 0x01;
	public static byte QUERY = (byte) 0x80;
	public static byte QUERY_HIT = (byte) 0x81;

	//Variables 
	static byte[] data = new byte[16];
	private static Random randNum = new Random();
	
	//File Sharing and related constants
	public static String USER_AGENT = "User-Agent: Simpella \\r\\n";
	public static String HOST = "Host : ";
	public static String CONNECTION_TYPE = "Connection : Keep-Alive\\r\\n";
	public static String RANGE = "Range : bytes= 0-\\r\\n";
	public static String ACCEPT_FILE_SHARING_REQ = "HTTP/1.1 200 OK\\r\\n";
	public static String REJECT_FILE_SHARING_REQ = "HTTP/1.1 503 File not found.\\r\\n";
	public static String SERVER_INFO = "Server: Simpella0.6\\r\\n"; 
	public static String CONTENT_TYPE = "Content-type: application/binary\\r\\n"; 
	public static String CONTENT_LENGTH = "Content-length: ";
	public static String SEND_FILE = "SENDFILE";
	
	public static String NEWLINE_TAB = "\r\n";
	static ArrayList<SearchFiles> searchResult = new ArrayList();
	static HashMap<String, String> fileIndexTofileNameMap = new HashMap<String, String>();

	//Global Utility Methods

	/* Keep in mind that only one instance of this class calls this method for one whole session fo running
	 * to avoid any duplicate numbers. Multiple instances calling the Random function will not lead to uniqueness.
	 */

	public static ArrayList<SearchFiles> getSearchResult() {
		return searchResult;
	}

	public static void setSearchResult(ArrayList<SearchFiles> searchResult) {
		Util.searchResult = searchResult;
	}

	public static short[] generateGUID(){
		short[] data = new short[16];
		int arrayIndex = 15;
		int randInt = 0x00000000;

		for (int i = 0; i < 4; i++) {
			randInt = randNum.nextInt();
			for (int j = 0; j < 4; j++) {
				int mask = 0x000000FF;

				mask = mask << (4 * j);
				int result = randInt & mask;

				result = result >> (4 * j);

				data[arrayIndex--] = (short)result;
			}
		}
		return data;
	}

	public static String convert16bytesIntoInteger(String numToBeConverted){
		String convertedNum = "";

		//splitting into 4 bytes every time to be able to convert into an integer.
		for(int i=7;i<56;i=i+7){
			int temp = Integer.parseInt(numToBeConverted.toString().substring(i,i+7),16);
			convertedNum = convertedNum + temp;
		}
		return convertedNum;
	}

	//Validate the format of the IP address using Regex.
	public boolean validateIpAddress(byte[] ipAddress){
		String IPADDRESS_PATTERN = 
				"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern pat = Pattern.compile(IPADDRESS_PATTERN);
		Matcher mat = pat.matcher(ipAddress.toString());
		return mat.matches();
	}

	public static PongMessageFormat convertByteArrayToPongMF(byte[] inputBytes){
		PongMessageFormat pmf = new PongMessageFormat();
		int i=0;
		byte[] str = new byte[2];
		byte[] str1 = new byte[4];

		str [0] = inputBytes[0];
		str [1] = inputBytes[1];
		pmf.setPort((short)Util.getIntfromByteArray(str));

		String ipadd="";
		for(int j=0;j<4;j++)
			str1[j] = inputBytes[i+2];
		ipadd = Util.getIPAddressfromArray(str1);
		pmf.setIpAddress(ipadd);

		for(int j=0;j<4;j++)
			str1[j] = inputBytes[i+6];
		pmf.setFileSharingCount(byteArrayToInt(str));

		for(int j=0;j<4;j++)
			str1[j] = inputBytes[i+10];
		pmf.setKbShared(byteArrayToInt(str));

		System.out.println("PMF Variables are ::: FileSharingCount " + pmf.getFileSharingCount() + " KbShared " + pmf.getKbShared()
				+"Port" + pmf.getPort() + "IpAddress" + pmf.getIpAddress());

		return pmf;
	}

	public static int byteArrayToInt(byte[] b) 
	{
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}
		return value;
	}
	public static ParentMessageFormat convertByteArrayToParentMF(byte[] inputBytes){
		ParentMessageFormat pmf = new ParentMessageFormat();
		short[] tempGUID = new short[16];
		for(int i=0;i<16;i++){
			tempGUID[i] = inputBytes[i];
		}
		pmf.setGUID(tempGUID);
		pmf.setMessageType(inputBytes[16]);
		pmf.setTTL(inputBytes[17]);
		pmf.setHops(inputBytes[18]);
		int payloadLen = 0;

		int byte1 = inputBytes[19];
		int byte2 = inputBytes[20];
		int byte3 = inputBytes[21];
		int byte4 = inputBytes[22];

		payloadLen += byte1;
		payloadLen += (byte2 << 8);
		payloadLen += (byte3 << 16);
		payloadLen += (byte4 << 24);
		pmf.setPayloadLen(payloadLen);

		byte[] payload = new byte[pmf.getPayloadLen()];

		// Write the payload

		// Message may not have a payload (ex. Ping)
		if(pmf.getPayloadLen()!=0)
		{
			for(int i=0;i<pmf.getPayloadLen();i++)
			{

				payload[i]=inputBytes[i+23];
			}

		}
		pmf.setPayload(payload);

		//System.out.println("PMF Variables are ::: GUID " + pmf.guidToRawString() + " Message Type " + pmf.getMessageType()
		//	+" TTL :" + pmf.getTTL() + " Hops :" + pmf.getHops() + " Payload Length : " + pmf.getPayloadLen());

		return pmf;
	}

	public static String getIPAddressfromArray(byte[] tempPort){
		String ipaddr = "";
		for(int i =0 ;i<4; i++)
			ipaddr = ipaddr + "."+(int)(tempPort[i] & 255);

		ipaddr = ipaddr.substring(0,ipaddr.length()-1);
		return ipaddr;
	}

	public static int getIntfromByteArray(byte[] bytes){
		int temp=0;
		int byte1 = bytes[0];
		int byte2 = bytes[1];
		int byte3 = bytes[2];
		int byte4 = bytes[3];

		temp += byte1;
		temp += (byte2 << 8);
		temp += (byte3 << 16);
		temp += (byte4 << 24);

		return temp;
	}
}