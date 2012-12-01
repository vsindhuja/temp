package simpella;

public class ParentMessageFormat implements java.io.Serializable{
	
	byte[] messageID = new byte[16]; //Will Store the GUID.
	byte messageType;				//Defined by the Message Type in Util class
	int TTL = 7;					//Unsigned integer, decreased by 1 by every sender. Initialized to 7. 
	int hops = 0;					//Number of hops the message has passed through
	int payloadLen = 0;
	String payload = "";			//Set to String for testing purpose. NEEDS TO BE CHANGED TO THE RESPECTIVE OBJECT.
	
	public byte[] getGUID() {
		return messageID;
	}
	public String getStringGUID(byte[] guidMessage){
		return Util.guidToRawString(guidMessage);
	}
	public void setGUID(byte[] msgid) {
		messageID = msgid;
	}
	public byte getMessageType() {
		return messageType;
	}
	public void setMessageType(byte messageType) {
		this.messageType = messageType;
	}
	public int getTTL() {
		return TTL;
	}
	public void setTTL(int tTL) {
		TTL = tTL;
	}
	public int getHops() {
		return hops;
	}
	public void setHops(int hops) {
		this.hops = hops;
	}
	public int getPayloadLen() {
		return payloadLen;
	}
	public void setPayloadLen(int payloadLen) {
		this.payloadLen = payloadLen;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}

	//Convert the fields that comprise this class into a byteArray as per the specifications.
	public byte[] convertToByteArray(){
		byte[] tempByte = new byte[24];
		for(int i=0;i<messageID.length;i++){
			tempByte[i] = messageID[i];
		}
		tempByte[16] = messageType;
		
		tempByte[17] = (byte)TTL;
		
		tempByte[18] = (byte)hops;
		
		int payloadLen1 = 0x000000FF & payloadLen;
		int payloadLen2 = (0x0000FF00 & payloadLen) >> 8;
		int payloadLen3 = (0x00FF0000 & payloadLen) >> 16;
		int payloadLen4 = (0xFF000000 & payloadLen) >> 24;

		tempByte[19] = (byte)payloadLen1;
		tempByte[20] = (byte)(payloadLen2);
		tempByte[22] = (byte)(payloadLen3);
		tempByte[23] = (byte)(payloadLen4);
		
		return tempByte;
	}
}
