package ca.team2706.frc.robot.config;

import ca.team2706.frc.robot.Robot;
import ca.team2706.frc.robot.RobotState;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Map;


/**
 * Config manager for the robot.
 */
public class Config {
    private static final ArrayList<FluidConstant<?>> CONSTANTS = new ArrayList<>();


    /**
     * Path to the file which identifies which
     */
    private static final Path ROBOT_ID_LOC = Paths.get(System.getProperty("user.home"), "robot.conf");
    private static final Path SAVE_FILE = Paths.get(System.getProperty("user.home"), "FluidConstants.txt");

    /**
     * ID of the robot that code is running on
     */
    private static final int ROBOT_ID = getRobotId();


    // #### Fluid constants ####
    static final NetworkTable constantsTable = NetworkTableInstance.getDefault().getTable("Fluid Constants");
    public static final FluidConstant<String> testAction = constant("testAction", XBOX_VALUE.XBOX_A_BUTTON.NTString);

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
     * Reads the robot type from the filesystem
     *
     * @return The integer ID of the robot defaulting to 0
     */
    private static int getRobotId() {
        int id = 0;

        try (BufferedReader reader = Files.newBufferedReader(ROBOT_ID_LOC)) {
            id = Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            id = 0;
            e.printStackTrace();
        }

        return id;
    }

    /**
     * Returns one of the values passed based on the robot ID
     *
     * @param first The first value (default value)
     * @param more  Other values that could be selected
     * @param <T>   The type of the value
     * @return The value selected based on the ID of the robot
     */
    @SafeVarargs
    private static <T> T robotSpecific(T first, T... more) {
        // Return the first value if the robot id doesn't fall between second and last index
        if (ROBOT_ID < 1 || ROBOT_ID > more.length) {
            return first;
        } else {
            return more[ROBOT_ID - 1];
        }
    }

    /**
     * Creates a new integer fluid constant.
     *
     * @param name         The name for the constant type.
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
     *
     * @param writable The string to be written to file.
     */
    private static void writeFile(String writable) {
        // Attempt to write the string to the file, catching any errors.
        try (BufferedWriter writer = Files.newBufferedWriter(SAVE_FILE)) {
            writer.write(writable);
        } catch (IOException e) {
            DriverStation.reportWarning("Unable to save fluid constants to file.", true);
        }
    }

    public enum XBOX_VALUE { 
        // Axis and triggers
        // Left on the Left Stick
        XBOX_LEFT_STICK_X (0, "L_STICK_X"),
        XBOX_LEFT_STICK_Y (1, "L_STICK_Y"),
        XBOX_BACK_LEFT_TRIGGER (2, "L_TRIG"),
        XBOX_BACK_RIGHT_TRIGGER (3, "R_TRIG"),
        XBOX_RIGHT_STICK_X (4, "R_STICK_X"),
        XBOX_RIGHT_STICK_Y (5, "R_STICK_Y"),

        // Buttons
        XBOX_A_BUTTON (1, "A"),
        XBOX_B_BUTTON (2, "B"),
        XBOX_X_BUTTON (3, "X"),
        XBOX_Y_BUTTON (4, "Y"),
        XBOX_LB_BUTTON (5, "LB"),
        XBOX_RB_BUTTON (6, "RB"),
        XBOX_SELECT_BUTTON (7, "SELECT"),
        XBOX_START_BUTTON (8, "START"),
        XBOX_LEFT_AXIS_BUTTON (9, "L_AXIS_BUTTON"),
        XBOX_RIGHT_AXIS_BUTTON (10, "R_AXIS_BUTTON"),

        // POV (The D-PAD on the XBOX Controller)
        XBOX_POV_UP (0, "UP"),
        XBOX_POV_UP_RIGHT (45, "UP_RIGHT"),
        XBOX_POV_RIGHT (90, "RIGHT"),
        XBOX_POV_DOWN_RIGHT (135, "DOWN_RIGHT"),
        XBOX_POV_DOWN (180, "DOWN"),
        XBOX_POV_DOWN_LEFT (225, "DOWN_LEFT"),
        XBOX_POV_LEFT (270, "LEFT"),
        XBOX_POV_UP_LEFT (315, "UP_LEFT");
        
        private String NTString;
        private int port;

        XBOX_VALUE (int port, String NTString) {
            this.NTString = NTString;
            this.port = port;
        }

        /**
         * @return the nTString
         */
        public String getNTString() {
            return NTString;
        }

        /**
         * @return the port
         */
        public int getPort() {
            return port;
        }


        // Create a hashmap of 
        private static final HashMap<String, String> nameMap = new HashMap<>();

        static {
            for (XBOX_VALUE value : XBOX_VALUE.values()) {
                nameMap.put(value.getNTString(), value.name());
            }
        }
        /**
         * @param NTString
         * @return The name of the constant corresponding to the string NTString
         */

        public static String getConstantName(String NTString) {
            return XBOX_VALUE.nameMap.get(NTString);
            
        }

    }

    // All Xbox controller constants.
    // public static final int
    //         // Axis and triggers
    //         XBOX_LEFT_AXIS = 0,
    //         XBOX_RIGHT_AXIS = 1,
    //         XBOX_BACK_LEFT_TRIGGER = 2,
    //         XBOX_BACK_RIGHT_TRIGGER = 3,
    //         XBOX_RIGHT_AXIS_X = 4,
    //         XBOX_RIGHT_AXIS_Y = 5,
    //         // Buttons
    //         XBOX_A_BUTTON = 1,
    //         XBOX_B_BUTTON = 2,
    //         XBOX_X_BUTTON = 3,
    //         XBOX_Y_BUTTON = 4,
    //         XBOX_LB_BUTTON = 5,
    //         XBOX_RB_BUTTON = 6,
    //         XBOX_SELECT_BUTTON = 7,
    //         XBOX_START_BUTTON = 8,
    //         XBOX_LEFT_AXIS_BUTTON = 9,
    //         XBOX_RIGHT_AXIS_BUTTON = 10,
    //         // POV
    //         XBOX_POV_UP = 0,
    //         XBOX_POV_UP_RIGHT = 45,
    //         XBOX_POV_RIGHT = 90,
    //         XBOX_POV_DOWN_RIGHT = 135,
    //         XBOX_POV_DOWN = 180,
    //         XBOX_POV_DOWN_LEFT = 225,
    //         XBOX_POV_LEFT = 270,
    //         XBOX_POV_UP_LEFT = 315;
}
