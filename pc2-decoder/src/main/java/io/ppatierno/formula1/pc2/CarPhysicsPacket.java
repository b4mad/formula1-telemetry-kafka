/*
 * Car physics packet definition (based on code from https://github.com/ralfhergert/pc2-telemetry)
 */

package io.ppatierno.formula1.pc2;

import io.ppatierno.formula1.pc2.helpers.StringParser;
import io.ppatierno.formula1.pc2.helpers.Vector;

import java.nio.ByteBuffer;

/**
 * Holds the data of a single CarPhysics packet.
 *
 * @see <a href="https://www.projectcarsgame.com/project-cars-2-api.html"></a>
 * @see <a href="https://www.projectcarsgame.com/uploads/2/0/6/5/20658008/sms_udp_definitions.hpp">UDP Patch 3</a>
 */
public class CarPhysicsPacket extends BasePacket {

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
        unfilteredThrottle = (short) Byte.toUnsignedInt(byteBuffer.get());
        unfilteredBrake = (short) Byte.toUnsignedInt(byteBuffer.get());
        unfilteredSteering = byteBuffer.get();
        unfilteredClutch = (short) Byte.toUnsignedInt(byteBuffer.get());

        carFlags = byteBuffer.get();
        oilTempCelsius = byteBuffer.getShort(); // signed short
        oilPressureKPa = byteBuffer.getShort(); // unsigned short
        waterTempCelsius = byteBuffer.getShort(); // signed short
        waterPressureKpa = byteBuffer.getShort(); // unsigned short
        fuelPressureKpa = byteBuffer.getShort(); // unsigned short
        fuelCapacity = (short) Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
        brake = (short) Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
        throttle = (short) Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
        clutch = (short) Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
        fuelLevel = byteBuffer.getFloat(); // float
        speed = byteBuffer.getFloat(); // float
        rpm = byteBuffer.getShort(); // unsigned short
        maxRpm = byteBuffer.getShort(); // unsigned short
        steering = byteBuffer.get(); // signed char
        gearNumGears = (short) Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
        boostAmount = (short) Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
        crashState = (short) Byte.toUnsignedInt(byteBuffer.get()); // unsigned char
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
        tyreTemp = new short[]{(short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get())};
        tyreHeightAboveGround = new float[]{byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()};
        tyreWear = new short[]{(short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get())};
        brakeDamage = new short[]{(short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get())};
        suspensionDamage = new short[]{(short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get())};
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
        wings = new short[]{(short) Byte.toUnsignedInt(byteBuffer.get()), (short) Byte.toUnsignedInt(byteBuffer.get())};
        handBrake = (short) Byte.toUnsignedInt(byteBuffer.get());
        // car damage
        aeroDamage = (short) Byte.toUnsignedInt(byteBuffer.get());
        engineDamage = (short) Byte.toUnsignedInt(byteBuffer.get());
        joyPad0 = byteBuffer.getInt();

        dPad = (short) Byte.toUnsignedInt(byteBuffer.get());
        tyreCompound = new String[]{StringParser.parse(byteBuffer, 40), StringParser.parse(byteBuffer, 40), StringParser.parse(byteBuffer, 40), StringParser.parse(byteBuffer, 40)};
        turboBoostPressure = byteBuffer.getFloat();
        fullPosition = new Vector(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
        brakeBias = (short) Byte.toUnsignedInt(byteBuffer.get());
    }
}
