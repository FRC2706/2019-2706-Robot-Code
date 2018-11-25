package ca.team2706.frc.robot.config;

import ca.team2706.frc.robot.Robot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Config manager for the robot.
 */
public class Config {
    // #### Solid constants ####
    private static final String SAVE_FILE = "FluidConstants.txt";
    public static final int XBOX_A = 1;

    // #### Fluid constants ####
    static final NetworkTable constantsTable = NetworkTableInstance.getDefault().getTable("Fluid Constants");

    static {
        // Save the current constants when the robot disables.
        Robot.setOnDisabled(isDisabled -> {
            if (isDisabled) {
                saveConstants();
            }
        });
    }

    /* Control bindings */
    // Driver controls
    public static final FluidConstant<Integer> DRIVER_PRESS_A = constant("Driver Press A", XBOX_A);
    // Operator controls
    public static final FluidConstant<Integer> OPERATOR_PRESS_A = constant("Operator Press A", XBOX_A);

    private static final ArrayList<FluidConstant<?>> CONSTANTS = new ArrayList<>();
    /**
     * Creates a new integer fluid constant.
     * @param name The name for the constant type.
     * @param initialValue The initialValue of the constant.
     * @return A new FluidConstant object representing the constant.
     */
    private static <A> FluidConstant<A> constant(final String name, final A initialValue) {
        FluidConstant<A> constant = new FluidConstant<>(name, initialValue);
        CONSTANTS.add(constant);
        return constant;
    }

    /**
     * Saves all the value of the constants to a human-readable (but not machine readable) text file.
     */
    private static void saveConstants() {
        StringBuilder totalString = new StringBuilder();

        // Iterate through each constant and collect its file string value.
        for (FluidConstant<?> constant : CONSTANTS) {
            totalString.append(constant.toFileString()).append("\n");
        }

        // Now just need to create and write to the file.
        writeFile(totalString.toString());
    }

    /**
     * Writes the given string to a file on the roborio.
     * @param writable The string to be written to file.
     */
    private static void writeFile(String writable) {
        // Attempt to write the string to the file, catching any errors.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))){
            writer.write(writable);
        } catch (IOException e) {
            DriverStation.reportWarning("Unable to save fluid constants to file.", true);
        }
    }
}
