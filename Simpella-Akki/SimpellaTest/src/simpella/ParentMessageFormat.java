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
	byte[] payload;			//Set to String for testing purpose. NEEDS TO BE CHANGED TO THE RESPECTIVE OBJECT.

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
	public byte[] getPayload() {
		return payload;
	}
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	//Convert the fields that comprise this class into a byteArray as per the specifications.
	public byte[] convertToByteArray(){

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		try {

			//byteStream = ;
			DataOutputStream pStream = new DataOutputStream(byteStream);

			// Write the guid
			short[] guidData = getGUID();
			for (int i = 0; i < guidData.length; i++) {
				pStream.writeByte((byte)guidData[i]);
			}

			// Write the function (payload descriptor)
			pStream.writeByte(messageType);

			// Write the time to live
			pStream.writeByte(TTL);

			// Write the hop count
			pStream.writeByte(0);

			if (getMessageType() == Util.PING) {
				payload = null;
			}

			// Write the Payload size in little-endian
			int payloadSize1 = 0x000000FF & payloadLen;
			int payloadSize2 = (0x0000FF00 & payloadLen) >> 8;
			int payloadSize3 = (0x00FF0000 & payloadLen) >> 16;
			int payloadSize4 = (0xFF000000 & payloadLen) >> 24;

			pStream.writeByte(payloadSize1);
			pStream.writeByte(payloadSize2);
			pStream.writeByte(payloadSize3);
			pStream.writeByte(payloadSize4);

			if (null != payload) {
				// Message may not have a payload (ex. Ping)
				for (int i = 0; i < payload.length; i++) {
					byte payloadByte = payload[i];
					pStream.writeByte(payloadByte);
				}
			}

			pStream.close();

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
}
