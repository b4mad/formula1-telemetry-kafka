/*
 * Base packet definition (based on code from https://github.com/ralfhergert/pc2-telemetry)
 */
package io.ppatierno.formula1.pc2;

import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Each packet shares the same base packet information identifying the packet.
 * @see <a href="https://www.projectcarsgame.com/project-cars-2-api.html"></a>
 * @see <a href="https://www.projectcarsgame.com/uploads/2/0/6/5/20658008/sms_udp_definitions.hpp">UDP Patch 3</a>
 */
public class BasePacket {

	private Date receivedDate;

	private long packetNumber;          //0 counter reflecting all the packets that have been sent during the game run
	private long categoryPacketNumber;  //4 counter of the packet groups belonging to the given category
	private short partialPacketIndex;  //8 If the data from this class had to be sent in several packets, the index number
	private short partialPacketNumber; //9 If the data from this class had to be sent in several packets, the total number
	private PacketTypes packetType;   //10 what is the type of this packet (ordinal of the PacketTypes)
	private short packetVersion;       //11 what is the version of protocol for this handler, to be bumped with data structure change

	public BasePacket() {}

	protected void ReadData(ByteBuffer data) {
		ReadData(data, new Date());
	}

	private void ReadData(ByteBuffer byteBuffer, Date receivedDate) {
		packetNumber = Integer.toUnsignedLong(byteBuffer.getInt());
		categoryPacketNumber = Integer.toUnsignedLong(byteBuffer.getInt());
		partialPacketIndex = (short) Byte.toUnsignedInt(byteBuffer.get());
		partialPacketNumber = (short) Byte.toUnsignedInt(byteBuffer.get());
		packetType = PacketTypes.values()[byteBuffer.get()];
		packetVersion = (short) Byte.toUnsignedInt(byteBuffer.get());

		this.receivedDate = receivedDate;
	}

	public BasePacket(byte[] data) {
		this(data, new Date());
	}

	public BasePacket(byte[] data, Date receivedDate) {
		if (data == null) {
			throw new IllegalArgumentException("data must not be null");
		}
		if (data.length < 12) {
			throw new IllegalArgumentException("given data array is too short to be read as BasePacket");
		}
		var byteBuffer = ByteBuffer.wrap(data);

		ReadData(byteBuffer, receivedDate);
	}

	public long getPacketNumber() {
		return packetNumber;
	}

	public void setPacketNumber(int packetNumber) {
		this.packetNumber = packetNumber;
	}

	public long getCategoryPacketNumber() {
		return categoryPacketNumber;
	}

	public void setCategoryPacketNumber(int categoryPacketNumber) {
		this.categoryPacketNumber = categoryPacketNumber;
	}

	public short getPartialPacketIndex() {
		return partialPacketIndex;
	}

	public void setPartialPacketIndex(short partialPacketIndex) {
		this.partialPacketIndex = partialPacketIndex;
	}

	public short getPartialPacketNumber() {
		return partialPacketNumber;
	}

	public void setPartialPacketNumber(short partialPacketNumber) {
		this.partialPacketNumber = partialPacketNumber;
	}

	public PacketTypes getPacketType() {
		return packetType;
	}

	public void setPacketType(PacketTypes packetType) {
		this.packetType = packetType;
	}

	public short getPacketVersion() {
		return packetVersion;
	}

	public void setPacketVersion(short packetVersion) {
		this.packetVersion = packetVersion;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	@Override
	public String toString() {
		return "BasePacket{" +
			"packetNumber=" + packetNumber +
			", categoryPacketNumber=" + categoryPacketNumber +
			", partialPacketIndex=" + partialPacketIndex +
			", partialPacketNumber=" + partialPacketNumber +
			", packetType=" + packetType +
			", packetVersion=" + packetVersion +
			'}';
	}
}
