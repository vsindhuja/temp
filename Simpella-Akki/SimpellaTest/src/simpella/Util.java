package simpella;

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
	
	//Message Types 
	public static byte PING = 0x00;
	public static byte PONG = 0x01;
	public static byte QUERY = (byte) 0x80;
	public static byte QUERY_HIT = (byte) 0x81;
	
	//Variables 
	static byte[] data = new byte[16];
	private static Random randNum = new Random();
	
	//Global Utility Methods
	
	/* Keep in mind that only one instance of this class calls this method for one whole session fo running
	 * to avoid any duplicate numbers. Multiple instances calling the Random function will not lead to uniqueness.
	 */
	
	public static byte[] generateGUID(){
		
		int arrayIndex = 15;
		int randInt = 0x00000000;

		for (int i = 0; i < 4; i++) {
			randInt = randNum.nextInt();
			for (int j = 0; j < 4; j++) {
				int mask = 0x000000FF;

				mask = mask << (4 * j);
				long result = (randInt & mask);
				result = result >> (4 * j);
				if(i==2 && j==0){
					result = (byte) 1;	//storing all 1's on byte number 8.
				}
				if(i==0 && j==0){
					result = (byte)0; //storing all 0's on byte 15.
				}
				data[arrayIndex--] = (byte)result;
			}
		}
		return data;
	}
	
	public String guidToRawString(){
		StringBuffer guidString = new StringBuffer();
		for(int i=0;i<data.length;i++){
			String message = (Integer.toHexString(data[i]));
			if(message.length() < 8){				//Adding zeros so conversion to integer is more honest.
				int numOfZeros = 8-message.length();
				for(int l=0;l<numOfZeros;l++)
					message = "0" + message;
			}
			guidString.append(message);
		}
		System.out.println("GUID : " + convert16bytesIntoInteger(guidString.toString()));
		
		// Code required here to add GUID to the requisite variable or you need to call this method from somewhere else. 
		
		return guidString.toString();
	}
	
	public String convert16bytesIntoInteger(String numToBeConverted){
		String convertedNum = "";
		
		//splitting into 4 bytes every time to be able to convert into an integer.
		for(int i=7;i<56;i=i+7){
			int temp = Integer.parseInt(numToBeConverted.toString().substring(i,i+7),16);
			convertedNum = convertedNum + temp;
		}
		return convertedNum;
	}
	
	//Validate the format of the IP address using Regex.
	public boolean ipAddressValidator(byte[] ipAddress){
		String IPADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern pat = Pattern.compile(IPADDRESS_PATTERN);
		Matcher mat = pat.matcher(ipAddress.toString());
		return mat.matches();
	}
}