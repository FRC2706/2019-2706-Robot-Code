package ca.team2706.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.Map;
import java.util.function.Function;

/**
 * Extra sensors used on Plyboy for testing
 * <p>
 * Remove any sensors that are used on the real robot from the allocation table
 */
public class SensorExtras extends Subsystem {

    /**
     * Contains all sensors that should be allocated
     * <p>
     * Remove any ports in use elsewhere on the robot
     */
    // Put above singleton pattern for better visibility
    private static final Map<SensorType, int[]> allocationTable = Map.ofEntries(
            Map.entry(SensorType.Talon, new int[]{5, 6, 7, 8}),
            Map.entry(SensorType.Pwm, new int[]{0, 1, 2, 4}),
            Map.entry(SensorType.AnalogInput, new int[]{1}),
            Map.entry(SensorType.Dio, new int[]{0, 1, 2, 3, 4}),
            Map.entry(SensorType.Relay, new int[]{0, 1})
    );

    private static SensorExtras currentInstance;

    /**
     * Gets the reference to the subsystem
     *
     * @return The subsystem singleton
     */
    public static SensorExtras getInstance() {
        init();
        return currentInstance;
    }

    /**
     * Initializes the subsystem
     */
    public static void init() {
        if (currentInstance == null) {
            currentInstance = new SensorExtras();
        }
    }

    /**
     * Creates sensor extras
     */
    private SensorExtras() {
        // Iterate through each type of sensor
        for (Map.Entry<SensorType, int[]> entry : allocationTable.entrySet()) {
            // Iterate through each port that should be allocated for that sensor
            for (int port : entry.getValue()) {
                // Allocate the sensor, and add it to the subsystem in LiveWindow
                entry.getKey().allocate(this, port);
            }
        }
    }

    @Override
    protected void initDefaultCommand() {
    }

    /**
     * Represents a sensor type that can be allocated
     */
    private enum SensorType {
        Talon("TalonSRX", WPI_TalonSRX::new),
        Pwm("PWM", PWM::new),
        AnalogInput("Analog Input", AnalogInput::new),
        Dio("DIO", DigitalInput::new),
        Relay("Relay", Relay::new);

        final String name;
        final Function<Integer, Sendable> factory;

        /**
         * Creates a SensorType
         *
         * @param name    The name of the sensor
         * @param factory A reference to how to create the sensor
         */
        SensorType(String name, Function<Integer, Sendable> factory) {
            this.name = name;
            this.factory = factory;
        }

        /**
         * Allocates a sensor and catches any exceptions that may occur
         *
         * @param subsystem The subsystem to add the sensor to
         * @param port      The port to allocate
         */
        void allocate(Subsystem subsystem, int port) {
            try {
                subsystem.addChild("Unused " + name + " " + port, factory.apply(port));
            } catch (RuntimeException e) {
                System.out.println("Sensor Extras Warning for " + name + " " + port + " (Check Allocation Table):\n\t"
                        + e.getMessage());
            }
        }
    }
}
