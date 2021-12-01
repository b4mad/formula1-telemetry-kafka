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
public class PC2Decoder extends RouteBuilder {
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
     * This class parses the next 4 bytes as an integer.
     */
    public static class IntegerParser {

        public static int parse(byte[] data) {
            return parse(data, 0);
        }

        public static int parse(byte[] data, int offset) {
            if (data == null) {
                throw new IllegalArgumentException("data must not be null");
            }
            if (offset + 4 > data.length) {
                throw new IllegalArgumentException("given data array is too short to read an integer from");
            }
            int value = (int) data[offset + 3];
            value = (int) data[offset + 2] + value * 256;
            value = (int) data[offset + 1] + value * 256;
            return (int) data[offset] + value * 256;
        }
    }

    /**
     * Holds the data of a single CarPhysics packet.
     *
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
        public Vector orientation = new Vector(0, 0, 0);     // 52 12
        public Vector localVelocity = new Vector(0, 0, 0);     // 64 12
        public Vector worldVelocity = new Vector(0, 0, 0);     // 76 12
        public Vector angularVelocity = new Vector(0, 0, 0);     // 88 12
        public Vector localAcceleration = new Vector(0, 0, 0);     // 100 12
        public Vector worldAcceleration = new Vector(0, 0, 0);     // 112 12
        public Vector extentsCentre = new Vector(0, 0, 0);     // 124 12
        public byte tyreFlags[] = new byte[]{0, 0, 0, 0};  // 136 4
        public byte terrain[] = new byte[]{0, 0, 0, 0};  // 140 4
        public float tyreY[] = new float[]{0, 0, 0, 0}; // 144 16
        public float tyreRPS[] = new float[]{0, 0, 0, 0}; // 160 16
        public short tyreTemp[] = new short[]{0, 0, 0, 0}; // 176 4
        public float tyreHeightAboveGround[] = new float[]{0, 0, 0, 0}; // 180 16
        public short tyreWear[] = new short[]{0, 0, 0, 0}; // 196 4
        public short brakeDamage[] = new short[]{0, 0, 0, 0}; // 200 4
        public short suspensionDamage[] = new short[]{0, 0, 0, 0}; // 204 4
        public short brakeTempCelsius[] = new short[]{0, 0, 0, 0}; // 208 8
        public short tyreTreadTemp[] = new short[]{0, 0, 0, 0}; // 216 8
        public short tyreLayerTemp[] = new short[]{0, 0, 0, 0}; // 224 8
        public short tyreCarcassTemp[] = new short[]{0, 0, 0, 0}; // 232 8
        public short tyreRimTemp[] = new short[]{0, 0, 0, 0}; // 240 8
        public short tyreInternalAirTemp[] = new short[]{0, 0, 0, 0}; // 248 8
        public short tyreTempLeft[] = new short[]{0, 0, 0, 0}; // 256 8
        public short tyreTempCenter[] = new short[]{0, 0, 0, 0}; // 264 8
        public short tyreTempRight[] = new short[]{0, 0, 0, 0}; // 272 8
        public float wheelLocalPositionY[] = new float[]{0, 0, 0, 0}; // 280 16
        public float rideHeight[] = new float[]{0, 0, 0, 0}; // 296 16
        public float suspensionTravel[] = new float[]{0, 0, 0, 0}; // 312 16
        public float suspensionVelocity[] = new float[]{0, 0, 0, 0}; // 328 16
        public float suspensionRideHeight[] = new float[]{0, 0, 0, 0}; // 344 8
        public float airPressure[] = new float[]{0, 0, 0, 0}; // 352 8
        public float engineSpeed;             // 360 4
        public float engineTorque;            // 364 4
        public short wings[] = new short[]{0, 0};       // 368 2
        public short handBrake;               // 370 1
        // Car damage
        public short aeroDamage;              // 371 1
        public short engineDamage;            // 372 1
        //  HW state
        public int joyPad0;                   // 376 4
        public short dPad;                    // 377 1
        public String tyreCompound[] = new String[]{"", "", "", ""}; // 378 160
        public float turboBoostPressure;      // 538 4
        public Vector fullPosition = new Vector(0, 0, 0);     // 542 12 -- position of the viewed participant with full precision
        public short brakeBias;               // 554 1  -- quantized brake bias

        public CarPhysicsPacket() {
        }

        public CarPhysicsPacket(byte[] data) {
            super(data);
            if (data.length < 556) {
                throw new IllegalArgumentException("given data array is too short to be read as CarPhysicsPacket");
            }
            if (getPacketType() != PacketTypes.CarPhysics || getPacketVersion() > 3) {
                throw new IllegalArgumentException("data does resemble a CarPhysics packet in version 2 or 3");
            }
            viewedParticipantIndex = data[12];
            unfilteredThrottle = (short) Byte.toUnsignedInt(data[13]);
            unfilteredBrake = (short) Byte.toUnsignedInt(data[14]);
            unfilteredSteering = data[15];
            unfilteredClutch = (short) Byte.toUnsignedInt(data[16]);

            carFlags = data[17];
            oilTempCelsius = ShortParser.parse(data, 18); // signed short
            oilPressureKPa = ShortParser.parse(data, 20); // unsigned short
            waterTempCelsius = ShortParser.parse(data, 22); // signed short
            waterPressureKpa = ShortParser.parse(data, 24); // unsigned short
            fuelPressureKpa = ShortParser.parse(data, 26); // unsigned short
            fuelCapacity = (short) Byte.toUnsignedInt(data[28]); // unsigned char
            brake = (short) Byte.toUnsignedInt(data[29]); // unsigned char
            throttle = (short) Byte.toUnsignedInt(data[30]); // unsigned char
            clutch = (short) Byte.toUnsignedInt(data[31]); // unsigned char
            fuelLevel = FloatParser.parse(data, 32); // float
            speed = FloatParser.parse(data, 36); // float
            rpm = ShortParser.parse(data, 40); // unsigned short
            maxRpm = ShortParser.parse(data, 42); // unsigned short
            steering = data[44]; // signed char
            gearNumGears = (short) Byte.toUnsignedInt(data[45]); // unsigned char
            boostAmount = (short) Byte.toUnsignedInt(data[46]); // unsigned char
            crashState = (short) Byte.toUnsignedInt(data[47]); // unsigned char
            odometerKM = FloatParser.parse(data, 48); // float
            orientation = new Vector(FloatParser.parse(data, 52), FloatParser.parse(data, 56), FloatParser.parse(data, 60));
            localVelocity = new Vector(FloatParser.parse(data, 64), FloatParser.parse(data, 68), FloatParser.parse(data, 72));
            worldVelocity = new Vector(FloatParser.parse(data, 76), FloatParser.parse(data, 80), FloatParser.parse(data, 84));
            angularVelocity = new Vector(FloatParser.parse(data, 88), FloatParser.parse(data, 92), FloatParser.parse(data, 96));
            localAcceleration = new Vector(FloatParser.parse(data, 100), FloatParser.parse(data, 104), FloatParser.parse(data, 108));
            worldAcceleration = new Vector(FloatParser.parse(data, 112), FloatParser.parse(data, 116), FloatParser.parse(data, 120));
            extentsCentre = new Vector(FloatParser.parse(data, 124), FloatParser.parse(data, 130), FloatParser.parse(data, 134));
            tyreFlags = new byte[]{data[136], data[137], data[138], data[139]}; // unsigned char
            terrain = new byte[]{data[140], data[141], data[142], data[143]}; // unsigned char
            tyreY = new float[]{FloatParser.parse(data, 144), FloatParser.parse(data, 148), FloatParser.parse(data, 152), FloatParser.parse(data, 156)};
            tyreRPS = new float[]{FloatParser.parse(data, 160), FloatParser.parse(data, 164), FloatParser.parse(data, 168), FloatParser.parse(data, 172)};
            tyreTemp = new short[]{(short) Byte.toUnsignedInt(data[176]), (short) Byte.toUnsignedInt(data[177]), (short) Byte.toUnsignedInt(data[178]), (short) Byte.toUnsignedInt(data[179])};
            tyreHeightAboveGround = new float[]{FloatParser.parse(data, 180), FloatParser.parse(data, 184), FloatParser.parse(data, 188), FloatParser.parse(data, 192)};
            tyreWear = new short[]{(short) Byte.toUnsignedInt(data[196]), (short) Byte.toUnsignedInt(data[197]), (short) Byte.toUnsignedInt(data[198]), (short) Byte.toUnsignedInt(data[199])};
            brakeDamage = new short[]{(short) Byte.toUnsignedInt(data[200]), (short) Byte.toUnsignedInt(data[201]), (short) Byte.toUnsignedInt(data[202]), (short) Byte.toUnsignedInt(data[203])};
            suspensionDamage = new short[]{(short) Byte.toUnsignedInt(data[204]), (short) Byte.toUnsignedInt(data[205]), (short) Byte.toUnsignedInt(data[206]), (short) Byte.toUnsignedInt(data[207])};
            brakeTempCelsius = new short[]{ShortParser.parse(data, 208), ShortParser.parse(data, 210), ShortParser.parse(data, 212), ShortParser.parse(data, 214)};
            tyreTreadTemp = new short[]{ShortParser.parse(data, 216), ShortParser.parse(data, 218), ShortParser.parse(data, 220), ShortParser.parse(data, 222)};
            tyreLayerTemp = new short[]{ShortParser.parse(data, 224), ShortParser.parse(data, 226), ShortParser.parse(data, 228), ShortParser.parse(data, 230)};
            tyreCarcassTemp = new short[]{ShortParser.parse(data, 232), ShortParser.parse(data, 234), ShortParser.parse(data, 236), ShortParser.parse(data, 238)};
            tyreRimTemp = new short[]{ShortParser.parse(data, 240), ShortParser.parse(data, 242), ShortParser.parse(data, 244), ShortParser.parse(data, 246)};
            tyreInternalAirTemp = new short[]{ShortParser.parse(data, 248), ShortParser.parse(data, 250), ShortParser.parse(data, 252), ShortParser.parse(data, 254)};
            tyreTempLeft = new short[]{ShortParser.parse(data, 256), ShortParser.parse(data, 258), ShortParser.parse(data, 260), ShortParser.parse(data, 262)};
            tyreTempCenter = new short[]{ShortParser.parse(data, 264), ShortParser.parse(data, 266), ShortParser.parse(data, 268), ShortParser.parse(data, 270)};
            tyreTempRight = new short[]{ShortParser.parse(data, 272), ShortParser.parse(data, 274), ShortParser.parse(data, 276), ShortParser.parse(data, 278)};
            wheelLocalPositionY = new float[]{FloatParser.parse(data, 280), FloatParser.parse(data, 284), FloatParser.parse(data, 288), FloatParser.parse(data, 292)};
            rideHeight = new float[]{FloatParser.parse(data, 296), FloatParser.parse(data, 300), FloatParser.parse(data, 304), FloatParser.parse(data, 308)};
            suspensionTravel = new float[]{FloatParser.parse(data, 312), FloatParser.parse(data, 316), FloatParser.parse(data, 320), FloatParser.parse(data, 324)};
            suspensionVelocity = new float[]{FloatParser.parse(data, 328), FloatParser.parse(data, 332), FloatParser.parse(data, 336), FloatParser.parse(data, 340)};
            suspensionRideHeight = new float[]{ShortParser.parse(data, 344), ShortParser.parse(data, 346), ShortParser.parse(data, 348), ShortParser.parse(data, 350)};
            airPressure = new float[]{ShortParser.parse(data, 352), ShortParser.parse(data, 354), ShortParser.parse(data, 356), ShortParser.parse(data, 358)};
            engineSpeed = FloatParser.parse(data, 360);
            engineTorque = FloatParser.parse(data, 364);
            wings = new short[]{(short) Byte.toUnsignedInt(data[368]), (short) Byte.toUnsignedInt(data[369])};
            handBrake = (short) Byte.toUnsignedInt(data[370]);
            // car damage
            aeroDamage = (short) Byte.toUnsignedInt(data[371]);
            engineDamage = (short) Byte.toUnsignedInt(data[372]);
            joyPad0 = IntegerParser.parse(data, 376);
            dPad = (short) Byte.toUnsignedInt(data[377]);
            tyreCompound = new String[]{StringParser.parse(data, 378, 40), StringParser.parse(data, 418, 40), StringParser.parse(data, 458, 40), StringParser.parse(data, 498, 40)};
            turboBoostPressure = FloatParser.parse(data, 338);
            fullPosition = new Vector(FloatParser.parse(data, 542), FloatParser.parse(data, 546), FloatParser.parse(data, 550));
            brakeBias = (short) Byte.toUnsignedInt(data[554]);
        }
    }

    /**
     * Each packet shares the same base packet information identifying the packet.
     *
     * @see <a href="https://www.projectcarsgame.com/project-cars-2-api.html"></a>
     * @see <a href="https://www.projectcarsgame.com/uploads/2/0/6/5/20658008/sms_udp_definitions.hpp">UDP Patch 3</a>
     */
    public static class BasePacket {

        private Date receivedDate;

        private int packetNumber;          //0 counter reflecting all the packets that have been sent during the game run
        private int categoryPacketNumber;  //4 counter of the packet groups belonging to the given category
        private short partialPacketIndex;  //8 If the data from this class had to be sent in several packets, the index number
        private short partialPacketNumber; //9 If the data from this class had to be sent in several packets, the total number
        private PacketTypes packetType;   //10 what is the type of this packet (ordinal of the PacketTypes)
        private short packetVersion;       //11 what is the version of protocol for this handler, to be bumped with data structure change

        public BasePacket() {
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
            packetNumber = IntegerParser.parse(data, 0);
            categoryPacketNumber = IntegerParser.parse(data, 4);
            partialPacketIndex = data[8];
            partialPacketNumber = data[9];
            packetType = PacketTypes.values()[data[10]];
            packetVersion = data[11];
            this.receivedDate = receivedDate;
        }

        public int getPacketNumber() {
            return packetNumber;
        }

        public void setPacketNumber(int packetNumber) {
            this.packetNumber = packetNumber;
        }

        public int getCategoryPacketNumber() {
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
     * This class parses the next 4 bytes as a float.
     */
    public static class FloatParser {

        public static float parse(byte[] data) {
            return parse(data, 0);
        }

        public static float parse(byte[] data, int offset) {
            if (data == null) {
                throw new IllegalArgumentException("data must not be null");
            }
            if (offset + 4 > data.length) {
                throw new IllegalArgumentException("given data array is too short to read an float from");
            }
            return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get();
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
     * This class parses two bytes into a short.
     */
    public static class ShortParser {

        public static short parse(byte[] data) {
            return parse(data, 0);
        }

        public static short parse(byte[] data, int offset) {
            if (data == null) {
                throw new IllegalArgumentException("data must not be null");
            }
            if (offset + 2 > data.length) {
                throw new IllegalArgumentException("given data array is too short to read an short from");
            }
            return (short) ((short) data[offset] + (short) data[offset + 1] * 256);
        }
    }

    /**
     * This class reads the next bytes into a string.
     */
    public static class StringParser {

        public static String parse(byte[] data, int length) {
            return parse(data, 0, length);
        }

        public static String parse(byte[] data, int offset, int length) {
            String value = "";
            for (int i = 0; i < length && i + offset < data.length; i++) {
                if (data[i + offset] == 0) {
                    break; // null-terminated-string.
                }
                value += (char) data[i + offset];
            }
            return value;
        }
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
