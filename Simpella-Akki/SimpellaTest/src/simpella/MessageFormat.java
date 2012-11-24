package simpella;

public class MessageFormat {

	String messageID = "";
	String messageType = "";
	int TTL = 0;
	int hops = 0;
	int payloadLen = 0;
	String payload = "";

	public String getMessageID() {
		return messageID;
	}
	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
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
