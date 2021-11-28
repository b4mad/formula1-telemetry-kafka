/**
 * Packet types definition (based on code from https://github.com/ralfhergert/pc2-telemetry)
 */

package io.ppatierno.formula1.pc2;

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
