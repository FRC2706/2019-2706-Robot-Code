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

    public static final int
            INTAKE_MOTOR_ID = robotSpecific(6, 6, 6),
            CARGO_IR_SENSOR_ID = robotSpecific(3, 3, 3),
            INTAKE_LIFT_SOLENOID_FORWARD_ID = robotSpecific(2, 2, 2),
            INTAKE_LIFT_SOLENOID_BACKWARD_ID = robotSpecific(3, 3, 3),
            HATCH_EJECTOR_SOLENOID_FORWARD_ID = robotSpecific(0, 0, 0),
            HATCH_EJECTOR_SOLENOID_BACKWARD_ID = robotSpecific(1, 1, 1);

    public static final int
            LIFT_MOTOR_ID = robotSpecific(5, 5, 5);

    public static final double
            // Max speed of the lift going up in override (between 0 and 1).
            LIFT_OVERRIDE_UP_SPEED = 0.4,
            /**
             * Speed for automatically ejecting cargo from the intake, from 0 to 1.
             */
            AUTO_EJECT_CARGO_INTAKE_SPEED = 1.0,
            // Max speed of the lift going down in override (between -1 and 0).
            LIFT_OVERRIDE_DOWN_SPEED = -0.2,
            /**
             * Speed (from 0 to 1) for automatically intaking cargo.
             */
            AUTO_INTAKE_CARGO_SPEED = 0.8;


    public static boolean
            INVERT_LIFT_MOTOR = robotSpecific(false, false, false),
            ENABLE_LIFT_CURRENT_LIMIT = robotSpecific(false, false, false);

    public static int MAX_LIFT_ENCODER_TICKS = 54_000;


    public static final boolean ENABLE_DRIVEBASE_CURRENT_LIMIT = robotSpecific(false, false, false);

    // Talon ID for the Pigeon
    public static final int GYRO_TALON_ID = robotSpecific(5, 3, 5);

    // Selector Channel
    public static final int SELECTOR_ID = robotSpecific(0, 0, 0);

    // Ring light
    public static final int RING_LIGHT_ID = robotSpecific(0, 0, 0);

    // The amount of encoder ticks that the robot must drive to go one foot
    public static final double DRIVE_ENCODER_DPP
            = robotSpecific(Math.PI / 8192.0, Math.PI / 8192.0, Math.PI / 8192.0);

    /**
     * The amount of encoder ticks that the robot must move the lift to travel one foot
     */
    public static final double LIFT_ENCODER_DPP
            = robotSpecific(0.7 * Math.PI / 16_384.0, 0.7 * Math.PI / 16_384.0, 0.7 * Math.PI / 16_384.0);

    public static final double PIGEON_DPP = robotSpecific(360.0 / 8192.0, 360.0 / 8192.0, 360.0 / 8192.0);

    public static final boolean ENABLE_CAMERA = robotSpecific(true, true, true);

    public static final int PURPLE_LIGHT = robotSpecific(3, 3, 3);

    public static final int ARCADE_DRIVE_FORWARD = XboxValue.XBOX_RIGHT_STICK_Y.getPort();
    public static final int ARCADE_DRIVE_ROTATE = XboxValue.XBOX_RIGHT_STICK_X.getPort();

    public static final int CURVATURE_DRIVE_FORWARD = XboxValue.XBOX_LEFT_STICK_Y.getPort();
    public static final int CURVATURE_CURVE_SPEED = XboxValue.XBOX_RIGHT_STICK_X.getPort();
    public static final int SLOW_MODE = XboxValue.XBOX_LB_BUTTON.getPort();

    public static final int TANK_DRIVE_RIGHT = XboxValue.XBOX_RIGHT_STICK_Y.getPort();
    public static final int TANK_DRIVE_LEFT = XboxValue.XBOX_LEFT_STICK_Y.getPort();

    /**
     * Amount of time (in seconds) that it takes for the plunger to be stowed.
     */
    public static final double PLUNGER_TIMEOUT = 0.25;
    /**
     * How long the intake motors should be running before the plunger deploys, in seconds.
     */
    public static final double EXHALE_CARGO_WAIT_UNTIL_PLUNGER = 0.5;

    /**
     * How long to wait to ensure the intake arms have fully moved.
     */
    public static final double INTAKE_ARMS_DELAY = 1.0;

    /**
     * How much height (in feet) to subtract from the lift's height for ejecting hatches.
     */
    public static final double SUBTRACT_LIFT_HEIGHT = -0.125;
    /**
     * How far from the top and the bottom of the lift that the lift should begin to slow down, in manual control.
     */
    public static final double LIFT_SLOWDOWN_RANGE_UP = 0.5;
    public static final double LIFT_SLOWDOWN_RANGE_DOWN = 1.0;


    public static final double MAX_INTAKE_SPEED = 1.0; //to be finalized later, this has yet to be tested

    public static final double[] ENCODER_LIFT_PID_UP = {0.5, 0, 50}; //to be finalized later, these values have not yet been tested

    public static final double[] ENCODER_LIFT_PID_DOWN = {0.5, 0.5, 160}; //to be finalized later, these values have not yet been tested


    public static final double LOG_PERIOD = robotSpecific(0.02, 0.02, 0.02, Double.POSITIVE_INFINITY);


    // #### Fluid constants ####
    public static final FluidConstant<Double> DRIVE_CLOSED_LOOP_DEADBAND = constant("closed-loop-drive-deadband", 0.001);
    public static final FluidConstant<Double> DRIVE_OPEN_LOOP_DEADBAND = constant("open-loop-drive-deadband", 0.04);
    public static final FluidConstant<Double> LIFT_CLOSED_LOOP_DEADBAND = constant("lift-deadband", 0.001);

    public static final FluidConstant<Boolean> DRIVE_SUM_PHASE_LEFT = constant("drive-sum-phase-left", true);
    public static final FluidConstant<Boolean> DRIVE_SUM_PHASE_RIGHT = constant("drive-sum-phase-right", true);

    public static final FluidConstant<Boolean> ENABLE_LIFT_SUM_PHASE = constant("lift-sum-phase", true);

    public static final FluidConstant<Double> DRIVE_CLOSED_LOOP_P = constant("drive-P", 0.1);
    public static final FluidConstant<Double> DRIVE_CLOSED_LOOP_I = constant("drive-I", 0.0);
    public static final FluidConstant<Double> DRIVE_CLOSED_LOOP_D = constant("drive-D", 0.0);

    public static final FluidConstant<Double> MOTION_MAGIC_CRUISE_VELOCITY = constant("mm-cruise-velocity", 7.77);
    public static final FluidConstant<Double> MOTION_MAGIC_ACCELERATION = constant("mm-acceleration", 7.77);
    public static final FluidConstant<Double> LIFT_MOTION_MAGIC_ACCELERATION = constant("mm-lift-acceleration", 4.0);
    public static final FluidConstant<Double> LIFT_MOTION_MAGIC_VELOCITY = constant("mm-lift-velocity", 7.77);
    /**
     * Max speed of the lift in encoder ticks.
     */
    public static final FluidConstant<Integer> LIFT_MAX_SPEED = constant("max-lift-velocity", 3000);

    public static final FluidConstant<Double> TURN_P = constant("turn-P", 0.5);
    public static final FluidConstant<Double> TURN_I = constant("turn-I", 0.0);
    public static final FluidConstant<Double> TURN_D = constant("turn-D", 0.0);

    public static final FluidConstant<Double>
            LIFT_P = constant("lift-P", 0.5),
            LIFT_I = constant("lift-I", 0.0),
            LIFT_D = constant("lift-D", 0.0);


    public static final FluidConstant<Double> PIGEON_KP = constant("pigeon-kp", 2.0);
    public static final FluidConstant<Double> PIGEON_KI = constant("pigeon-ki", 0.0);
    public static final FluidConstant<Double> PIGEON_KD = constant("pigeon-ki", 4.0);
    public static final FluidConstant<Double> PIGEON_KF = constant("pigeon-kf", 0.0);

    // All controller bindings.
    public static final FluidConstant<String> INTAKE_BACKWARD_BINDING = constant("intake-backward-binding", XboxValue.XBOX_BACK_LEFT_TRIGGER.getNTString()),
            INTAKE_FORWARD_BINDING = constant("intake-forward-binding", XboxValue.XBOX_BACK_RIGHT_TRIGGER.getNTString()),
            MOVE_LIFT_BINDING = constant("move-lift-binding", XboxValue.XBOX_LEFT_STICK_Y.getNTString()),
            LIFT_ARMS_BINDING = constant("lift-arms-binding", XboxValue.XBOX_A_BUTTON.getNTString()),
            LOWER_ARMS_BINDING = constant("lower-arms-binding", XboxValue.XBOX_Y_BUTTON.getNTString()),
            OVERRIDE_LIFT_DOWN_BINDING = constant("override-lift-down-binding", XboxValue.XBOX_B_BUTTON.getNTString()),
            OVERRIDE_LIFT_UP_BINDING = constant("override-lift-up-binding", XboxValue.XBOX_X_BUTTON.getNTString()),
            LIFT_FIRST_SETPOINT_BINDING = constant("lift-bottom-setpoint-binding", XboxValue.XBOX_POV_DOWN.getNTString()),
            LIFT_SECOND_SETPOINT_BINDING = constant("lift-second-setpoint-binding", XboxValue.XBOX_POV_LEFT.getNTString()),
            LIFT_THIRD_SETPOINT_BINDING = constant("lift-third-setpoint-binding", XboxValue.XBOX_POV_UP.getNTString()),
            LIFT_FOURTH_SETPOINT_BINDING = constant("lift-top-setpoint-binding", XboxValue.XBOX_POV_RIGHT.getNTString()),
            MANUAL_PISTON_BINDING = constant("manual-plunger-toggle", XboxValue.XBOX_RIGHT_AXIS_BUTTON.getNTString()),
            EJECT_BINDING = constant("eject-multi-purpose-binding", XboxValue.XBOX_RB_BUTTON.getNTString()),
            AUTO_INTAKE_CARGO_BINDING = constant("auto-intake-cargo-binding", XboxValue.XBOX_LB_BUTTON.getNTString()),
            TOGGLE_RING_LIGHT_BINDING = constant("toggle-ring-light-binding", XboxValue.XBOX_START_BUTTON.getNTString());

    /**
     * The minimum reading on the cargo IR sensor to assert that we have cargo in the mechanism.
     */
    public static final FluidConstant<Double> CARGO_CAPTURED_IR_MIN_VOLTAGE = constant("cargo-min-ir-voltage", 0.27);

    /**
     * The idea voltage for captured cargo.
     */
    public static final FluidConstant<Double> CARGO_CAPTURED_IDEAL_IR_VOLTAGE = constant("cargo-ideal-ir-voltage", 0.32);

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

        // If the robot has already initialized itself, we should initialize constants.
        if (Robot.isRobotInitialized()) {
            initializeFluidConstantNetworktables();
        }
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
        constants.forEach(fluidConstant -> fluidConstant.addNTEntry(getNetworkTable()));
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
                DriverStation.reportError("Could not find robot configuration file.", false);
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
}
