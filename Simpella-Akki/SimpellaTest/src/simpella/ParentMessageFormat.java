package simpella;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParentMessageFormat implements java.io.Serializable{

	short[] messageID; 				//Will Store the GUID. HAVE to call generateGUID() to initialize this.
	byte messageType;				//Defined by the Message Type in Util class
	int TTL = 7;					//Unsigned integer, decreased by 1 by every sender. Initialized to 7. 
	int hops = 0;					//Number of hops the message has passed through
	int payloadLen = 0;
	String payload = "";			//Set to String for testing purpose. NEEDS TO BE CHANGED TO THE RESPECTIVE OBJECT.

	public short[] getGUID() {
		return messageID;
	}
	public String getStringGUID(byte[] guidMessage){
		return guidToRawString();
	}
	public void setGUID(short[] msgid) {
		this.messageID = msgid;
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
	public byte[] convertToByteArray(){/*
		byte[] tempByte = new byte[23];
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
		tempByte[21] = (byte)(payloadLen3);
		tempByte[22] = (byte)(payloadLen4);

		for(int i=0;i<=22;i++)
			System.out.println("CONVERTINGBYTESSSS +++ "+ i + " *** "+ tempByte[i]);

		return tempByte;
	 */

		// TODO handle exception avoid null pointer
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		try {

			//byteStream = ;
			DataOutputStream payloadStream = new DataOutputStream(byteStream);

			// Write the guid
			short[] guidData = getGUID();
			for (int i = 0; i < guidData.length; i++) {
				payloadStream.writeByte((byte)guidData[i]);
			}

			// Write the function (payload descriptor)
			payloadStream.writeByte(messageType);

			// Write the time to live
			payloadStream.writeByte(TTL);

			// Write the hop count
			payloadStream.writeByte(0);

			if (getMessageType() == Util.PING) {
				payload = null;
			}

			// Write the Payload size in little-endian
			int payloadSize1 = 0x000000FF & payloadLen;
			int payloadSize2 = (0x0000FF00 & payloadLen) >> 8;
			int payloadSize3 = (0x00FF0000 & payloadLen) >> 16;
			int payloadSize4 = (0xFF000000 & payloadLen) >> 24;

			payloadStream.writeByte(payloadSize1);
			payloadStream.writeByte(payloadSize2);
			payloadStream.writeByte(payloadSize3);
			payloadStream.writeByte(payloadSize4);

			// Write the payload
			/*if (null != payload) {
				// Message may not have a payload (ex. Ping)
				for (int i = 0; i < payload.length; i++) {
					byte payloadByte = (byte)payload[i];
					payloadStream.writeByte(payloadByte);
				}
			}*/

			// all done
			payloadStream.close();

		}
		catch (IOException io) {
			System.out.println("Issue in Data input Stream");
		}

		return byteStream.toByteArray();
	}

	public String guidToRawString(){
		StringBuffer message = new StringBuffer();

		for (int i = 0; i < messageID.length; i++) {
			StringBuffer messageSection = new StringBuffer();
			messageSection.append(Integer.toHexString(messageID[i]));

			// Ensure every value is 2 chars long (i.e. 0F instead of F)
			if (messageSection.length() < 2) {
				message.append('0');
			}
			message.append(messageSection);
		}

		return message.toString();
	}

	public void constructMessage(){/*
	StringBuffer buffer = new StringBuffer();

	for (int i = 0; i < rawMessage.length; i++) {
		buffer.append("[" + Integer.toHexString(rawMessage[i]) + "]");

	}

	LOG.debug("Message constructor: Raw Message Bytes: " + buffer.toString());

	// Copy the GUID
	short[] guidData = new short[16];
	System.arraycopy(rawMessage, 0, guidData, 0, guidData.length);
	guid = new GUID(guidData);

	// Copy the function identifier
	type = rawMessage[16];

	// Copy the TTL 
	ttl = (byte)rawMessage[17];

	// Copy the hop count
	hops = (byte)rawMessage[18];

	// Copy the payload size (little endian)
	int byte1 = rawMessage[19];
	int byte2 = rawMessage[20];
	int byte3 = rawMessage[21];
	int byte4 = rawMessage[22];

	payloadSize += byte1;
	payloadSize += (byte2 << 8);
	payloadSize += (byte3 << 16);
	payloadSize += (byte4 << 24);
	 */}

}
