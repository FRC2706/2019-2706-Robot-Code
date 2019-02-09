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

/**
 * Config manager for the robot.
 */
public class Config {
    /**
     * Initializes a new config instance.
     */
    public static void init() {
        new Config();
    }

    private static final ArrayList<FluidConstant<?>> CONSTANTS = new ArrayList<>();

    // #### Static constants ####

    /**
     * Path to the file which identifies which robot this is.
     */
    private static final Path ROBOT_ID_LOC = Paths.get(System.getProperty("user.home"), "robot.conf");
    private static final Path SAVE_FILE = Paths.get(System.getProperty("user.home"), "FluidConstants.txt");

    /**
     * ID of the robot that code is running on
     */
    private static int robotId = -1;


    // Values for driving robot with joystick
    public static final boolean
            TELEOP_SQUARE_JOYSTICK_INPUTS = true,
            TELEOP_BRAKE = false;

    // Timeouts for sending CAN bus commands
    public static final int
            CAN_SHORT = 10,
            CAN_LONG = 100;

    // DriveBase motor CAN IDs
    public static final int
            LEFT_FRONT_DRIVE_MOTOR_ID = robotSpecific(1, 1, 1),
            LEFT_BACK_DRIVE_MOTOR_ID = robotSpecific(3, 3, 3),
            RIGHT_FRONT_DRIVE_MOTOR_ID = robotSpecific(2, 2, 2),
            RIGHT_BACK_DRIVE_MOTOR_ID = robotSpecific(4, 4, 4);

    public static final boolean
            INVERT_FRONT_LEFT_DRIVE = robotSpecific(false, false, false),
            INVERT_BACK_LEFT_DRIVE = robotSpecific(false, false, false),
            INVERT_FRONT_RIGHT_DRIVE = robotSpecific(true, true, true),
            INVERT_BACK_RIGHT_DRIVE = robotSpecific(true, true, true);

    public static final boolean DRIVEBASE_CURRENT_LIMIT = robotSpecific(false, false, false);

    // Talon ID for the Pigeon
    public static final int GYRO_TALON_ID = robotSpecific(5, 5, 5);

    // Selector Channel
    public static final int SELECTOR_ID = robotSpecific(0, 0, 0);

    // The amount of encoder ticks that the robot must drive to go one foot
    public static final double DRIVE_ENCODER_DPP
            = robotSpecific(Math.PI / 8192.0, Math.PI / 8192.0, Math.PI / 8192.0);

    public static final boolean ENABLE_CAMERA = robotSpecific(true, true, false);

    public static final int PURPLE_LIGHT = robotSpecific(3, 3, 3);

    public static final FluidConstant<Double> DRIVE_CLOSED_LOOP_DEADBAND = constant("drive-deadband", 0.001);
    public static final FluidConstant<Double> DRIVE_OPEN_LOOP_DEADBAND = constant("drive-deadband", 0.04);

    public static final FluidConstant<Boolean> DRIVE_SUM_PHASE_LEFT = constant("drive-sum-phase-left", true);
    public static final FluidConstant<Boolean> DRIVE_SUM_PHASE_RIGHT = constant("drive-sum-phase-right", true);

    public static final FluidConstant<Double> DRIVE_CLOSED_LOOP_P = constant("drive-P", 0.1);
    public static final FluidConstant<Double> DRIVE_CLOSED_LOOP_I = constant("drive-I", 0.0);
    public static final FluidConstant<Double> DRIVE_CLOSED_LOOP_D = constant("drive-D", 0.0);

    public static final int ARCADE_DRIVE_FORWARD = 5;
    public static final int ARCADE_DRIVE_ROTATE = 4;

    // #### Fluid constants ####


    // ### Methods, fields and Constructors ###
    /**
     * The network table for fluid constants.
     */
    private NetworkTable configTable;

    Config() {
        this(NetworkTableInstance.getDefault().getTable("Fluid Constants"));
    }

    /**
     * Createds a new config manager for handling config tasks.
     *
     * @param ntTable The networktable instance to be used.
     */
    Config(NetworkTable ntTable) {
        this.configTable = ntTable;

        Robot.setOnStateChange(this::robotStateChange);
    }

    /**
     * Called when the robot's state is updated.
     *
     * @param newState The new robot state.
     */
    private void robotStateChange(final RobotState newState) {
        switch (newState) {
            case ROBOT_INIT:
                initializeFluidConstantNetworktables();
                break;
            case DISABLED:
                saveConstants();
                break;
            default:
                break;
        }
    }

    /**
     * Initializes the network tables elements for all the fluid constants.
     */
    private void initializeFluidConstantNetworktables() {
        ArrayList<FluidConstant<?>> constants = new ArrayList<>(CONSTANTS);
        constants.forEach(fluidConstant -> fluidConstant.addNTEntry(configTable));
    }

    /**
     * Gets the network table for fluid constants.
     *
     * @return The Network Table used for fluid constants.
     */
    public NetworkTable getNetworkTable() {
        return configTable;
    }

    /**
     * Reads the robot type from the filesystem
     *
     * @return The integer ID of the robot defaulting to 0
     */
    private static int getRobotId() {
        if (robotId < 0) {
            try (BufferedReader reader = Files.newBufferedReader(ROBOT_ID_LOC)) {
                robotId = Integer.parseInt(reader.readLine());
            } catch (IOException | NumberFormatException e) {
                robotId = 0;
                e.printStackTrace();
            }

        }

        return robotId;
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
        if (getRobotId() < 1 || getRobotId() > more.length) {
            return first;
        } else {
            return more[getRobotId() - 1];
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
     * Writes the given string to a file on the Roborio.
     *
     * @param writable The string to be written to file.
     */
    private static void writeFile(String writable) {
        // Attempt to write the string to the file, catching any errors.
        try (BufferedWriter writer = Files.newBufferedWriter(SAVE_FILE)) {
            writer.write(writable);
        } catch (IOException e) {
            DriverStation.reportWarning("Unable to save fluid constants to file.", false);
        }
    }

    /**
     * Xbox controller binding information.
     * Contains the link between the Xbox's buttons' port and the NetworkTables key used to describe the action.
     */
    public enum XboxValue {
        // Axis and triggers
        // Left on the Left Stick
        XBOX_LEFT_STICK_X(0, "L_STICK_X"),
        XBOX_LEFT_STICK_Y(1, "L_STICK_Y"),
        XBOX_BACK_LEFT_TRIGGER(2, "L_TRIG"),
        XBOX_BACK_RIGHT_TRIGGER(3, "R_TRIG"),
        XBOX_RIGHT_STICK_X(4, "R_STICK_X"),
        XBOX_RIGHT_STICK_Y(5, "R_STICK_Y"),

        // Buttons
        XBOX_A_BUTTON(1, "A"),
        XBOX_B_BUTTON(2, "B"),
        XBOX_X_BUTTON(3, "X"),
        XBOX_Y_BUTTON(4, "Y"),
        XBOX_LB_BUTTON(5, "LB"),
        XBOX_RB_BUTTON(6, "RB"),
        XBOX_SELECT_BUTTON(7, "SELECT"),
        XBOX_START_BUTTON(8, "START"),
        XBOX_LEFT_AXIS_BUTTON(9, "L_AXIS_BUTTON"),
        XBOX_RIGHT_AXIS_BUTTON(10, "R_AXIS_BUTTON"),

        // POV (The D-PAD on the XBOX Controller)
        XBOX_POV_UP(0, "UP"),
        XBOX_POV_UP_RIGHT(45, "UP_RIGHT"),
        XBOX_POV_RIGHT(90, "RIGHT"),
        XBOX_POV_DOWN_RIGHT(135, "DOWN_RIGHT"),
        XBOX_POV_DOWN(180, "DOWN"),
        XBOX_POV_DOWN_LEFT(225, "DOWN_LEFT"),
        XBOX_POV_LEFT(270, "LEFT"),
        XBOX_POV_UP_LEFT(315, "UP_LEFT");

        private String NTString;
        private int port;

        XboxValue(int port, String NTString) {
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


        // Create a hashmap of the networktables entry and the
        private static final HashMap<String, XboxValue> nameMap = new HashMap<>();

        static {
            for (XboxValue value : XboxValue.values()) {
                nameMap.put(value.getNTString(), value);
            }
        }

        /**
         * Gets the XboxValue constant with the given NetworkTables key.
         *
         * @param ntKey The NetworkTables key for the constant.
         * @return The constant object.
         */
        public static XboxValue getXboxValueFromNTKey(final String ntKey) {
            return nameMap.get(ntKey);
        }
    }
}
