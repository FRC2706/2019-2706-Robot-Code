package ca.team2706.frc.robot.talon;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * Utility class for allocating and configuring Talon motor controllers
 */
public final class TalonFactory {

    private TalonFactory() {
        throw new IllegalStateException("Utility method cannot be instantiated");
    }

    /**
     * Allocates a Talon and leaves the settings as default
     *
     * @param id The CAN id that the Talon is on
     * @return The newly allocated Talon
     */
    public static WPI_TalonSRX defaultConfig(int id) {
        return new WPI_TalonSRX(id);
    }

    /**
     * Allocates a Talon and loads a JSON file to get settings
     *
     * @param id The CAN id that the Talon is on
     * @param configLocation The location of the file to load
     * @return The newly allocated Talon
     */
    public static WPI_TalonSRX fileConfig(int id, String configLocation) {
        // TODO: Actually configure
        return new WPI_TalonSRX(id);
    }

    /**
     * Allocates a Talon and reads a JSON string to get settings
     *
     * @param id The CAN id that the Talon is on
     * @param configString The JSON formatted string to load settings from
     * @return The newly allocated Talon
     */
    public static WPI_TalonSRX stringConfig(int id, String configString) {
        // TODO: Actually configure
        return new WPI_TalonSRX(id);
    }

    /**
     * Allocates a Talon and initializes it to the settings provided
     *
     * @param id The CAN id that the Talon is on
     * @param config The settings to use
     * @return The newly allocated Talon
     */
    public static WPI_TalonSRX talonConfig(int id, Object config) {
        // TODO: Actually configure
        return new WPI_TalonSRX(id);
    }
}
