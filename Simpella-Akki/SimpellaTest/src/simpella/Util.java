package simpella;

import java.util.Random;

public class Util {

	public static String CONNECT = "SIMPELLA CONNECT/0.6 \r \n";
	public static String CONNECTION_ACCEPTED = "SIMPELLA/0.6 200 OK \r \n";
	public static String CONNECTION_REFUSED = "SIMPELLA/0.6 503 Exceeded Connection Capacity \r \n";
	static byte[] data = new byte[16];
	private static Random randNum = new Random();
	
	public static byte[] generateGUID(){
		
		int arrayIndex = 15;
		int randInt = 0x00000000;
		long temp ;
		
		for (int i = 0; i < 4; i++) {
			randInt = randNum.nextInt();
			for (int j = 0; j < 4; j++) {
				int mask = 0x000000FF;

				mask = mask << (4 * j);
				long result = (randInt & mask);

				result = result >> (4 * j);
				if(i==1 && j==3){
					result = (byte) 1;	//storing all 1's on byte number 8.
				}
				if(i==0 && j==0){
					result = (byte)0; //storing all 0's on byte 15.
				}
				System.out.println("int upcasting ::: " + result);
				data[arrayIndex--] = (byte)result;
			}
		}
		for(int j=0;j<=15;j++)
			System.out.println(" GUID " + j + ":::: " + data[j]);
		return data;
	}
	
	public static String guidToRawString(){
		StringBuffer guidString = new StringBuffer();
		for(int i=0;i<data.length;i++){
			String message = (Integer.toHexString(data[i]));
			if(message.length() < 2){
				message = "0" + message;
			}
			guidString.append(message);
		}
		System.out.println("GUID String : " + guidString.toString());
		return guidString.toString();
	}
	
}
