package ca.team2706.frc.robot.config;

import ca.team2706.frc.robot.Robot;
import ca.team2706.frc.robot.RobotState;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Config manager for the robot.
 */
public class Config {
    private static final ArrayList<FluidConstant<?>> CONSTANTS = new ArrayList<>();

    // #### Static constants ####
    private static final String SAVE_FILE = "/home/lvuser/FluidConstants.txt";

    // All Xbox controller constants.
    public static final int
            // Axis and triggers
            XBOX_LEFT_AXIS = 0,
            XBOX_RIGHT_AXIS = 1,
            XBOX_BACK_LEFT_TRIGGER = 2,
            XBOX_BACK_RIGHT_TRIGGER = 3,
            XBOX_RIGHT_AXIS_X = 4,
            XBOX_RIGHT_AXIS_Y = 5,
            // Buttons
            XBOX_A_BUTTON = 1,
            XBOX_B_BUTTON = 2,
            XBOX_X_BUTTON = 3,
            XBOX_Y_BUTTON = 4,
            XBOX_LB_BUTTON = 5,
            XBOX_RB_BUTTON = 6,
            XBOX_SELECT_BUTTON = 7,
            XBOX_START_BUTTON = 8,
            XBOX_LEFT_AXIS_BUTTON = 9,
            XBOX_RIGHT_AXIS_BUTTON = 10,
            // POV
            XBOX_POV_UP = 0,
            XBOX_POV_UP_RIGHT = 45,
            XBOX_POV_RIGHT = 90,
            XBOX_POV_DOWN_RIGHT = 135,
            XBOX_POV_DOWN = 180,
            XBOX_POV_DOWN_LEFT = 225,
            XBOX_POV_LEFT = 270,
            XBOX_POV_UP_LEFT = 315;

    // #### Fluid constants ####
    static final NetworkTable constantsTable = NetworkTableInstance.getDefault().getTable("Fluid Constants");

    static {
        initialize();
    }

    private static boolean initialized = false;
    /**
     * Initializes the Config class.
     */
    public static void initialize() {
        if (!initialized) {
            Robot.setOnStateChange(Config::saveConstants);

            initialized = true;
        }
    }

    /**
     * Creates a new integer fluid constant.
     * @param name The name for the constant type.
     * @param initialValue The initialValue of the constant.
     * @return A new FluidConstant object representing the constant.
     */
    private static <A> FluidConstant<A> constant(final String name, final A initialValue) {
        FluidConstant<A> constant = new FluidConstant<>(name, initialValue);
        Objects.requireNonNull(CONSTANTS).add(constant);
        return constant;
    }

    /**
     * Saves all the value of the constants to a human-readable (but not machine readable) text file.
     */
    private static void saveConstants(RobotState state) {
        if (state == RobotState.DISABLED) {
            StringBuilder totalString = new StringBuilder();

            // Iterate through each constant and collect its file string value.
            for (FluidConstant<?> constant : CONSTANTS) {
                totalString.append(constant.toFileString()).append("\n");
            }

            // Now just need to create and write to the file.
            writeFile(totalString.toString());
        }
    }

    /**
     * Writes the given string to a file on the Roborio.
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
