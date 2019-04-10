package ca.team2706.frc.robot;

/**
 * The status of the connection to the FMS and driverstation
 */
public enum ConnectionState {
    /**
     * FMS has been connected
     */
    FMS_CONNECT,

    /**
     * FMS has been disconnected
     */
    FMS_DISCONNECT,

    /**
     * Driverstation has been connnected
     */
    DRIVERSTATION_CONNECT,

    /**
     * Driverstation has been disconnected
     */
    DRIVERSTATION_DISCONNECT
}
