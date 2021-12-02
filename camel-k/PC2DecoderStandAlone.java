// camel-k: language=java

import io.netty.channel.ChannelHandler;
import org.apache.camel.BindToRegistry;
import org.apache.camel.builder.RouteBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.Date;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

/**
 * This classes configures the routing for receiving and decoding
 * pc2 packets.
 */
public class PC2DecoderStandAlone extends RouteBuilder {
    @BindToRegistry("decoder")
    public ChannelHandler getDecoder() {
        return new Decoder();
    }

    @Override
    public void configure() {

        from("netty:udp://0.0.0.0:10666?decoders=#decoder&sync=false")
            .log("Raw Packet: ${body}")
            .routeId("udp-dispatcher");
    }

    /**
     * Decodes incoming packets into pc2 data types.
     */
    public static class Decoder extends MessageToMessageDecoder<DatagramPacket> {
        private final PC2PacketParser packetParser;

        public Decoder() {
            this.packetParser = new PC2PacketParser();
        }


        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket, List<Object> list) throws Exception {
            ByteBuf buffer = datagramPacket.content();

            BasePacket packet = packetParser.parse(buffer);
            list.add(packet);
        }
    }

    /**
     * This parser converts {@link }
     */
    public static class PC2PacketParser {

        public BasePacket parse(ByteBuf byteBuffer) throws IllegalArgumentException {
            try
            {
                byte[] packetData = new byte[byteBuffer.readableBytes()];
                byteBuffer.duplicate().readBytes(packetData);

                BasePacket basePacket = new BasePacket(packetData);

                if (basePacket.getPacketType() == PacketTypes.CarPhysics && basePacket.getPacketVersion() == 3) {
                    return new CarPhysicsPacket(packetData);
                } else {
                    return basePacket;
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Each packet shares the same base packet information identifying the packet.
     * @see <a href="https://www.projectcarsgame.com/project-cars-2-api.html"></a>
     * @see <a href="https://www.projectcarsgame.com/uploads/2/0/6/5/20658008/sms_udp_definitions.hpp">UDP Patch 3</a>
     */
    public static class BasePacket {

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
            partialPacketIndex = (short)Byte.toUnsignedInt(byteBuffer.get());
            partialPacketNumber = (short)Byte.toUnsignedInt(byteBuffer.get());
            packetType = PacketTypes.values()[byteBuffer.get()];
            packetVersion = (short)Byte.toUnsignedInt(byteBuffer.get());;
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

    /**
     * Holds the data of a single CarPhysics packet.
     * @see <a href="https://www.projectcarsgame.com/project-cars-2-api.html"></a>
     * @see <a href="https://www.projectcarsgame.com/uploads/2/0/6/5/20658008/sms_udp_definitions.hpp">UDP Patch 3</a>
     */
    public static class CarPhysicsPacket extends BasePacket {

        public short viewedParticipantIndex;  // 12 1
        // Unfiltered input
        public short unfilteredThrottle;      // 13 1
        public short unfilteredBrake;         // 14 1
        public short unfilteredSteering;      // 15 1
        public short unfilteredClutch;        // 16 1
        // Car state
        public byte carFlags;                 // 17 1
        public short oilTempCelsius;          // 18 2
        public short oilPressureKPa;          // 20 2
        public short waterTempCelsius;        // 22 2
        public short waterPressureKpa;        // 24 2
        public short fuelPressureKpa;         // 26 2
        public short fuelCapacity;            // 28 1
        public short brake;                   // 29 1
        public short throttle;                // 30 1
        public short clutch;                  // 31 1
        public float fuelLevel;               // 32 4
        public float speed;                   // 36 4
        public short rpm;                     // 40 2
        public short maxRpm;                  // 42 2
        public short steering;                // 44 1
        public short gearNumGears;            // 45 1
        public short boostAmount;             // 46 1
        public short crashState;              // 47 1
        public float odometerKM;              // 48 4
        public Vector orientation            = new Vector(0, 0, 0);     // 52 12
        public Vector localVelocity          = new Vector(0, 0, 0);     // 64 12
        public Vector worldVelocity          = new Vector(0, 0, 0);     // 76 12
        public Vector angularVelocity        = new Vector(0, 0, 0);     // 88 12
        public Vector localAcceleration      = new Vector(0, 0, 0);     // 100 12
        public Vector worldAcceleration      = new Vector(0, 0, 0);     // 112 12
        public Vector extentsCentre          = new Vector(0, 0, 0);     // 124 12
        public byte tyreFlags[]              = new byte[]{0, 0, 0, 0};  // 136 4
        public byte terrain[]                = new byte[]{0, 0, 0, 0};  // 140 4
        public float tyreY[]                 = new float[]{0, 0, 0, 0}; // 144 16
        public float tyreRPS[]               = new float[]{0, 0, 0, 0}; // 160 16
        public short tyreTemp[]              = new short[]{0, 0, 0, 0}; // 176 4
        public float tyreHeightAboveGround[] = new float[]{0, 0, 0, 0}; // 180 16
        public short tyreWear[]              = new short[]{0, 0, 0, 0}; // 196 4
        public short brakeDamage[]           = new short[]{0, 0, 0, 0}; // 200 4
        public short suspensionDamage[]      = new short[]{0, 0, 0, 0}; // 204 4
        public short brakeTempCelsius[]      = new short[]{0, 0, 0, 0}; // 208 8
        public short tyreTreadTemp[]         = new short[]{0, 0, 0, 0}; // 216 8
        public short tyreLayerTemp[]         = new short[]{0, 0, 0, 0}; // 224 8
        public short tyreCarcassTemp[]       = new short[]{0, 0, 0, 0}; // 232 8
        public short tyreRimTemp[]           = new short[]{0, 0, 0, 0}; // 240 8
        public short tyreInternalAirTemp[]   = new short[]{0, 0, 0, 0}; // 248 8
        public short tyreTempLeft[]          = new short[]{0, 0, 0, 0}; // 256 8
        public short tyreTempCenter[]        = new short[]{0, 0, 0, 0}; // 264 8
        public short tyreTempRight[]         = new short[]{0, 0, 0, 0}; // 272 8
        public float wheelLocalPositionY[]   = new float[]{0, 0, 0, 0}; // 280 16
        public float rideHeight[]            = new float[]{0, 0, 0, 0}; // 296 16
        public float suspensionTravel[]      = new float[]{0, 0, 0, 0}; // 312 16
        public float suspensionVelocity[]    = new float[]{0, 0, 0, 0}; // 328 16
        public float suspensionRideHeight[]  = new float[]{0, 0, 0, 0}; // 344 8
        public float airPressure[]           = new float[]{0, 0, 0, 0}; // 352 8
        public float engineSpeed;             // 360 4
        public float engineTorque;            // 364 4
        public short wings[]                 = new short[]{0, 0};       // 368 2
        public short handBrake;               // 370 1
        // Car damage
        public short aeroDamage;              // 371 1
        public short engineDamage;            // 372 1
        //  HW state
        public int joyPad0;                   // 376 4
        public short dPad;                    // 377 1
        public String tyreCompound[]         = new String[]{"", "", "", ""}; // 378 160
        public float turboBoostPressure;      // 538 4
        public Vector fullPosition           = new Vector(0, 0, 0);     // 542 12 -- position of the viewed participant with full precision
        public short brakeBias;               // 554 1  -- quantized brake bias

        public CarPhysicsPacket() {}

        public CarPhysicsPacket(byte[] data) throws IllegalArgumentException {
            var byteBuffer = ByteBuffer.wrap(data);

            ReadData(byteBuffer);

            if (data.length < 556) {
                throw new IllegalArgumentException("given data array is too short to be read as CarPhysicsPacket");
            }
            if (getPacketType() != PacketTypes.CarPhysics || getPacketVersion() > 3) {
                throw new IllegalArgumentException("data does resemble a CarPhysics packet in version 2 or 3");
            }

            viewedParticipantIndex = byteBuffer.get();
            unfilteredThrottle = (short)Byte.toUnsignedInt(byteBuffer.get());
            unfilteredBrake = (short)Byte.toUnsignedInt(byteBuffer.get());
            unfilteredSteering = byteBuffer.get();
            unfilteredClutch = (short)Byte.toUnsignedInt(byteBuffer.get());

            carFlags = byteBuffer.get();
            oilTempCelsius = byteBuffer.getShort(); // signed short
            oilPressureKPa = byteBuffer.getShort(); // unsigned short
            waterTempCelsius = byteBuffer.getShort(); // signed short
            waterPressureKpa = byteBuffer.getShort(); // unsigned short
            fuelPressureKpa = byteBuffer.getShort(); // unsigned short
            fuelCapacity = (short)Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
            brake = (short)Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
            throttle = (short)Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
            clutch = (short)Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
            fuelLevel = byteBuffer.getFloat(); // float
            speed = byteBuffer.getFloat(); // float
            rpm = byteBuffer.getShort(); // unsigned short
            maxRpm = byteBuffer.getShort(); // unsigned short
            steering = byteBuffer.get(); // signed char
            gearNumGears = (short)Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
            boostAmount = (short)Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
            crashState = (short)Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
            odometerKM = byteBuffer.getFloat(); // float
            orientation = new Vector(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
            localVelocity = new Vector(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
            worldVelocity = new Vector(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
            angularVelocity = new Vector(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
            localAcceleration = new Vector(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
            worldAcceleration = new Vector(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
            extentsCentre = new Vector(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
            tyreFlags = new byte[]{byteBuffer.get(), byteBuffer.get(), byteBuffer.get(), byteBuffer.get()}; // unsigned char
            terrain = new byte[]{byteBuffer.get(), byteBuffer.get(), byteBuffer.get(), byteBuffer.get()}; // unsigned char
            tyreY = new float[]{byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()};
            tyreRPS = new float[]{byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()};
            tyreTemp = new short[]{(short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get())};
            tyreHeightAboveGround = new float[]{byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()};
            tyreWear = new short[]{(short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get())};
            brakeDamage = new short[]{(short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get())};
            suspensionDamage = new short[]{(short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get())};
            brakeTempCelsius = new short[]{byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort()};
            tyreTreadTemp = new short[]{byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort()};
            tyreLayerTemp = new short[]{byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort()};
            tyreCarcassTemp = new short[]{byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort()};
            tyreRimTemp = new short[]{byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort()};
            tyreInternalAirTemp = new short[]{byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort()};
            tyreTempLeft = new short[]{byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort()};
            tyreTempCenter = new short[]{byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort()};
            tyreTempRight = new short[]{byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort()};
            wheelLocalPositionY = new float[]{byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()};
            rideHeight = new float[]{byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()};
            suspensionTravel = new float[]{byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()};
            suspensionVelocity = new float[]{byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()};
            suspensionRideHeight = new float[]{byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort()};
            airPressure = new float[]{byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort(), byteBuffer.getShort()};
            engineSpeed = byteBuffer.getFloat();
            engineTorque = byteBuffer.getFloat();
            wings = new short[]{(short)Byte.toUnsignedInt(byteBuffer.get()), (short)Byte.toUnsignedInt(byteBuffer.get())};
            handBrake = (short)Byte.toUnsignedInt(byteBuffer.get());
            // car damage
            aeroDamage = (short)Byte.toUnsignedInt(byteBuffer.get());
            engineDamage = (short)Byte.toUnsignedInt(byteBuffer.get());
            joyPad0 = byteBuffer.getInt();

            dPad = (short)Byte.toUnsignedInt(byteBuffer.get());
            tyreCompound = new String[]{StringParser.parse(byteBuffer, 40), StringParser.parse(byteBuffer, 40), StringParser.parse(byteBuffer, 40), StringParser.parse(byteBuffer, 40)};
            turboBoostPressure = byteBuffer.getFloat();
            fullPosition = new Vector(byteBuffer.getFloat(), byteBuffer.getFloat(),byteBuffer.getFloat());
            brakeBias = (short)Byte.toUnsignedInt(byteBuffer.get());
        }
    }

    /**
     * Existing packet types.
     * Do not change order. Ordinal is important.
     */
    public enum PacketTypes {
        CarPhysics,
        RaceDefinition,
        Participants,
        Timings,
        GameState,
        WeatherState, // not sent at the moment, information can be found in the game state packet
        VehicleNames, //not sent at the moment
        TimeStats,
        ParticipantVehicleNames
    }

    /**
     * Defines all terrains.
     * Don't change the order. The ordinal is important.
     */
    public enum Terrain {
        Asphalt,
        u1,
        u2,
        u3,
        u4,
        u5,
        Grass
    }

    /**
     * This enum defines the tyres.
     * Don't change the order. The ordinal is important.
     */
    public enum Tire {
        FrontLeft,
        FrontRight,
        RearLeft,
        RearRight,
    }

    /**
     * This class reads the next bytes into a string.
     */
    public static class StringParser {

        public static String parse(ByteBuffer byteBuffer, int length) {
            StringBuilder value = new StringBuilder();
            for (int i = 0; i < length && byteBuffer.hasRemaining(); i++) {
                byte aByte = byteBuffer.get();
                if (aByte == 0) {
                    break; // null-terminated-string.
                }
                value.append((char) aByte);
            }
            return value.toString();
        }
    }

    /**
     * A vector of float.
     */
    public static class Vector {

        final float[] values;

        public Vector(float... values) {
            if (values == null) {
                throw new IllegalArgumentException("values can not be null");
            }
            this.values = values;
        }

        public float get(int index) {
            return values[index];
        }

        public double length() {
            double sum = 0;
            for (float value : values) {
                sum += value * value;
            }
            return Math.sqrt(sum);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Vector vector = (Vector) o;

            return Arrays.equals(values, vector.values);

        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(values);
        }
    }

}
