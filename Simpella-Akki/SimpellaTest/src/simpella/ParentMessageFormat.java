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
}
