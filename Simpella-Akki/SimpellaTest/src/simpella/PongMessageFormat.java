package simpella;

import java.nio.ByteBuffer;

public class PongMessageFormat {
	private short port;			//0-1 Port for accepting connection
	private String ipAddress;	//2-5 IP Address of self 
	private int fileSharingCount = 0;	// No of files servent is sharing on the network.
	private int kbShared;		// No of Kilobytes of data servent is sharing on the network.

	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public short getPort() {
		return port;
	}
	public void setPort(short port) {
		this.port = port;
	}
	public int getFileSharingCount() {
		return fileSharingCount;
	}
	public void setFileSharingCount(int fileSharingCount) {
		this.fileSharingCount = fileSharingCount;
	}
	public int getKbShared() {
		return kbShared;
	}
	public void setKbShared(int kbShared) {
		this.kbShared = kbShared;
	}

	//Put all the variables in this class into a Byte Array.
	public byte[] toByteArray(){
		byte[] pongMessageFormat = new byte[14];
		int i=0;

		byte[] bytes = ByteBuffer.allocate(2).putShort(port).array();

		pongMessageFormat[0] = bytes[0];
		pongMessageFormat[1] = bytes[1];

		i=i+2;
		//System.out.println("IP Address : in PONGMESSAGE : " + new String(ipAddress) );

		/*if(ipAddress!=null){
		for(int j=0;j<ipAddress.length;j++)
			pongMessageFormat[i++] = ipAddress[j];
		}*/

		byte[] ipadd = asBytes(ipAddress);
		//System.out.println("PONGMESSAGE :::: " + new String(ipadd) + " **** " + ipadd.length + " Remove this later!! ");
		for(int j=0;j<ipadd.length;j++){
			pongMessageFormat[i] = ipadd[j];
			i++;
		}

		byte[] fsc = ByteBuffer.allocate(4).putInt(fileSharingCount).array();

		for (byte b : fsc) {
			//System.out.format("0x%x ", b);
		}

		for(int j=0;j<fsc.length;j++){
			pongMessageFormat[i] = fsc[j];
			i++;
		}

		byte[] bytes2 = ByteBuffer.allocate(4).putInt(kbShared).array();

		for (byte b : bytes2) {
			//System.out.format("0x%x ", b);
		}
		for(int j=0;j<fsc.length;j++){
			pongMessageFormat[i] = fsc[j];
			i++;
		}

		//System.out.println("pongMessageFormat :: " + pongMessageFormat.length);
		return pongMessageFormat;
	}

	public final static byte[] asBytes(String addr) {

		// Convert the TCP/IP address string to an integer value

		int ipInt = parseNumericAddress(addr);
		if ( ipInt == 0)
			return null;

		// Convert to bytes

		byte[] ipByts = new byte[4];

		ipByts[3] = (byte) (ipInt & 0xFF);
		ipByts[2] = (byte) ((ipInt >> 8) & 0xFF);
		ipByts[1] = (byte) ((ipInt >> 16) & 0xFF);
		ipByts[0] = (byte) ((ipInt >> 24) & 0xFF);

		// Return the TCP/IP bytes

		return ipByts;
	}
	public final static int parseNumericAddress(String ipaddr) {

		//  Check if the string is valid
		if ( ipaddr == null || ipaddr.length() < 7 || ipaddr.length() > 15)
			return 0;

		//  Check the address string, should be n.n.n.n format
		String[] str = ipaddr.split("\\.");
		if(str.length!=4)
			return 0;

		int ipInt = 0;

		for(int i=0;i<4;i++){
			String ipNum = str[i];
			try {
				//  Validate the current address part
				int ipVal = Integer.valueOf(ipNum).intValue();
				if ( ipVal < 0 || ipVal > 255)
					return 0;
				//  Add to the integer address
				ipInt = (ipInt << 8) + ipVal;
			}
			catch (NumberFormatException ex) {
				return 0;
			}
		}
		return ipInt;
	}
}
