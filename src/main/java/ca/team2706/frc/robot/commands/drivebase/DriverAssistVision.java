package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Command;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

/**
 * Command for 2-D driver assist using vision
 * <p>
 * This will drive the robot from its current position and heading to a position
 * at a specific distance in front of and aligned with the nearest target (either on
 * the cargo ship, rocket, or loading bay) using data provided by the vision system.
 * The distance in front of the target or loading bay is specified in the TARGET_OFFSET_DISTANCE
 * attribute of the Config class.
 * <p>
 * For the feature to work properly, the following must hold
 * i.  the robot must have a heading that is "most facing" the desired target compared
 * to other targets at the time driver assist is requested
 * ii. the robot must be powered up at the start of the match at the heading specified
 * in degrees by the ROBOT_START_ANGLE attribute of the Config class, where
 * this heading is relative to a horizontal line extending across the field and
 * positive heading is measured CCW
 * <p>
 * NOTE: MOTION CONTROL FUNCTIONALITY IS CURRENTLY AWAITING COMPLETION
 */
public class DriverAssistVision extends Command {
    /**
     * Angle of robot heading with respect to x axis of field frame
     */
    private double angRobotHeadingFinal_Field = 0.0;

    /**
     * Value of angYawTargetWrtCameraLOSCWposAngle (yaw angle to target wrt camera line of sight in degrees)
     * from the previous time the command was issued.
     */
    private double angYawTargetWrtCameraLOSCWposPrev = 0.0;

    private boolean commandAborted;

    /**
     * Value of distanceCameraToTarget_Camera (distance from camera frame origin to target in feet)
     * from the previous time the command was issued.
     */
    private double distanceCameraToTarget_CameraPrev = 0.0;

    /**
     * True if target is cargo ship or loading bay
     */
    private boolean driverAssistCargoAndLoading = false;

    /**
     * True if target is rocket
     */
    private boolean driverAssistRocket = false;

    /**
     * Network table instance to get data from vision subsystem
     */
    private NetworkTableInstance inst;

    /**
     * Network table to get data from vision subsystem
     */
    private NetworkTable table;

    /**
     * Trajectory generated to move robot to target
     */
    private Trajectory traj;

    /**
     * True if trajectory has been generated for this command, false otherwise.
     * (This will be removed when motion control functionality is implemented.)
     */
    private boolean trajGenerated = false;

    /**
     * True if vision system has been assessed to be offline
     */
    boolean visionOffline = false;

    /**
     * Creates empty driver assist command object (needed for unit testing framework only)
     */
    public DriverAssistVision() {
        // Ensure that this command is the only one to run on the drive base
        requires(DriveBase.getInstance());

        setupNetworkTables();

        commandAborted = false;
        trajGenerated = false;
        visionOffline = false;
        angYawTargetWrtCameraLOSCWposPrev = 0.0;
        distanceCameraToTarget_CameraPrev = 0.0;
    }

    /**
     * Creates the driver assist command
     *
     * @param driverAssistCargoAndLoading: True if target is cargo ship or loading bay, false otherwise
     * @param driverAssistRocket:          True if target is a rocket ship, false otherwise
     */
    public DriverAssistVision(boolean driverAssistCargoAndLoading, boolean driverAssistRocket) {
        this.driverAssistCargoAndLoading = driverAssistCargoAndLoading;
        this.driverAssistRocket = driverAssistRocket;

        // Ensure that this command is the only one to run on the drive base
        requires(DriveBase.getInstance());

        setupNetworkTables();

        commandAborted = false;
        trajGenerated = false;
        visionOffline = false;
        angYawTargetWrtCameraLOSCWposPrev = 0.0;
        distanceCameraToTarget_CameraPrev = 0.0;
    }

    /**
     * Sets up network tables used to get data from vision system
     */
    private void setupNetworkTables() {
        // Set up network table
        inst = NetworkTableInstance.getDefault();
        table = inst.getTable("PathFinder");
    }

    /**
     * initialize() method does all the work of the command up to and including generating the trajectory
     */
    @Override
    public void initialize() {
        // See method generateTrajectoryRobotToTarget(...) for an explanation of variable names and coodinate
        // frames

        Log.d("DAV: initialize() called");

        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().setPositionNoGyroMode();

        commandAborted = false;
        trajGenerated = false;

        // Get angle and position of vision target in camera frame from network table
        // (computed by vision subsystem)
        NetworkTableEntry vectorCameraToTarget = table.getEntry("vectorCameraToTarget");

        double[] vectorCameraToTarget_Camera = vectorCameraToTarget.getDoubleArray(new double[]{0, 0});
        double angYawTargetWrtCameraLOSCWpos = vectorCameraToTarget_Camera[0];
        double distanceCameraToTarget_Camera = vectorCameraToTarget_Camera[1];

        Log.d("DAV: angYawTargetWrtCameraLOSCWpos [deg]: " + angYawTargetWrtCameraLOSCWpos);
        Log.d("DAV: distanceCameraToTarget_Camera [ft]: " + distanceCameraToTarget_Camera);

        /*
        Due to the inherent jitter of values computed by the vision system, it is next to impossible
        that values of yaw angle and distance to target will both be equal on successive commands unless
        the vision system is not updating them for some reason. Therefore, is this occurs, consider
        vision system offline. If they are different on successive commands, consider vision system
        online.
        */
        if ((angYawTargetWrtCameraLOSCWpos == angYawTargetWrtCameraLOSCWposPrev) &&
                (distanceCameraToTarget_Camera == distanceCameraToTarget_CameraPrev)) {
            visionOffline = true;
        } else {
            visionOffline = false;
        }
        angYawTargetWrtCameraLOSCWposPrev = angYawTargetWrtCameraLOSCWpos;
        distanceCameraToTarget_CameraPrev = distanceCameraToTarget_Camera;

        // Abort if vision is offline or vision system is giving an unreasonably low value 
        if (visionOffline || distanceCameraToTarget_Camera < Config.VISION_DISTANCE_MIN.value()) {
            Log.d("DAV: Vision system offline. Driver assist command not performed.");
            commandAborted = true;
            return;
        }

        Log.d("DAV: Generating trajectory");

        // Compute the trajectory
        generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, angYawTargetWrtCameraLOSCWpos,
                driverAssistCargoAndLoading, driverAssistRocket);

        Log.d("DAV: Exiting initialize()");

        trajGenerated = true;
    }

    @Override
    public boolean isFinished() {
        return (trajGenerated || commandAborted);
    }

    @Override
    public void end() {
        // Go back to disabled mode
        DriveBase.getInstance().setDisabledMode();
    }

    /**
     * Gets the network table instance
     *
     * @return Network table instance
     */
    public NetworkTableInstance getNetworkTableInstance() {
        return inst;
    }

    /**
     * Gets the network table
     */
    public NetworkTable getNetworkTable() {
        return table;
    }

    /**
     * Generates a trajectory using the Pathfinder library and sends it to the motion control
     * system
     *
     * @param distanceCameraToTarget_Camera distance from camera to target [ft]
     * @param angYawTargetWrtCameraLOSCWpos yaw angle to target wrt camera line of sight, CW with increase angle [deg]
     * @param driverAssistCargoAndLoading   true if driver assist on cargo ship of loading bay target is requested
     * @param driverAssistRocket            true if drivers assist on rocket target is requested
     */
    public void generateTrajectoryRobotToTarget(double distanceCameraToTarget_Camera, double angYawTargetWrtCameraLOSCWpos,
                                                boolean driverAssistCargoAndLoading, boolean driverAssistRocket) {

        /**
         * Explanation of vector and coordinate frame notation in a 2-d plane:
         *
         * A vector from point P1 to point P2 represented in a coordinate frame F3 is written as vP1ToP2_F3.
         *
         * The x and y components of this vector are written as vP1ToP2_F3X and vP1ToP2_F3Y. Note that it
         * only makes sense to speak of x and y coordinates in relation to a coordinate frame (which in this
         * case is F3).
         *
         * Points P1 or P2 can also be the origin of a coordinate frame. In this case, P1 is replaced by
         * the name of the frame. Thus, if F1, F2, and F3 are coordinatre frames, then vF1ToF2_F3 represents 
         * the vector from the origin of frame F1 to the origin of frame F2 represented in the frame F3. 
         *
         * In this class, all vectors represent distances measured in feet.
         *
         * An angle in the 2-d plane can be defined with respect to a particular coordinate frame as the 
         * angle between the line of interest and the x-axis of the coordinate frame. An angle between 
         * a line of interest having name "ZZZ" and with respect to the x-axis of a frame F1 will be written 
         * as angZZZ_F1. Angles are assumed to be in degrees by default. If an angle is represented in units 
         * of radians (which is needed when the sin or cos of an angle is taken), then the text "Rad" will be 
         * added after the name of the line of interest. Thus the previous angle in radians would be 
         * angZZZRad_F1. Angles are assumed to increase in the counter-clockwise direction by default. If an 
         * angle increases in the clockwise direction, "CWpos" will be added after the name and after the
         * "Rad" text. In this case, if the original example is in units of radians and if the angle increases
         * in the clockwise direction, it would be written as angZZZRadCWpos.
         *
         * An angle in the 2-d plane can also be defined as the angular measurement between the line of
         * interest and a suitably chosen reference line. This will be written as angZZZWrtWWW where
         * ZZZ is the name of the line of interest and WWW is the name of the reference line. Use of
         * non-default units of degrees or a clockwise positive angle direction are represented as 
         * described above.
         *
         * The three coordinate frames of interest are given below. Note that these are planar frames and
         * their height is not relevant for the model used in this class.
         *
         *   Camera: Coordinate frame attached to camera with origin at focal point of lens, y axis 
         *           pointing along the camera line of sight, and x axis pointing to the right when 
         *           looking from the back to the front of the camera.
         *
         *   Robot:  Coordinate frame attached to camera with origin at the centre of the robot base, 
         *           y axis pointing toward the front along the centre line, and x axis pointing to 
         *           the right when looking from the back to the front of the robot.
         *
         *   Field:  Coordinate frame attached to field where the origin is in the lower left corner 
         *           of the field, the y axis points down the field and the x axis points horizontally 
         *           across the field. The home team is located at the bottom of the field and the 
         *           drivers are looking down the field. The location of the origin is actually not 
         *           important since this frame is used only as a reference for the angular measurement.
         *
         * Two point of interest are
         *
         *   Target: The vision target. If the target is actually two pieces of reflective tape, then
         *           the target is at the centre of the tape.
         *
         *   Final:  Final position of robot as represented by the location of the origin of the robot
         *           frame. This is offset by a user-specified distance Config.TARGET_OFFSET_DISTANCE
         *           from the front of the target.
         *
         */

        Log.d("driverAssistCargoAndLoading: " + driverAssistCargoAndLoading);
        Log.d("driverAssistRocket: " + driverAssistRocket);

        // Step 1: Compute vector from current robot position to final robot position in robot frame, 
        // vRobotToFinal_Robot, where
        //  
        // vRobotToFinal_Robot = vRobotToCamera_Robot + vCameraToTarget_Robot + vTargetToFinal_Robot
        //
        // The three vectors on the right hand side will be denoted by Vector 1, Vector 2, and 
        // Vector 3 in the comments below

        // Vector 1: vRobotToCamera_Robot: Vector from robot to camera in robot frame
        double vRobotToCamera_RobotX = Config.getROBOTTOCAMERA_ROBOTX();
        double vRobotToCamera_RobotY = Config.getROBOTTOCAMERA_ROBOTY();
        Log.d("DAV: vRobotToCamera_RobotX: " + vRobotToCamera_RobotX + ", vRobotToCamera_RobotY: " + vRobotToCamera_RobotY);

        // Vector 2: vCameraToTarget_Robot: Vector from camera to target in robot frame
        double angYawTargetWrtCameraLOSRadCWpos = Pathfinder.d2r(angYawTargetWrtCameraLOSCWpos);
        double vCameraToTarget_RobotX = distanceCameraToTarget_Camera * Math.sin(angYawTargetWrtCameraLOSRadCWpos);
        double vCameraToTarget_RobotY = distanceCameraToTarget_Camera * Math.cos(angYawTargetWrtCameraLOSRadCWpos);
        Log.d("DAV: vec_CameraToTargetX_Robot: " + vCameraToTarget_RobotX + ", vCameraToTarget_RobotY: " + vCameraToTarget_RobotY);

        // Get current robot heading relative to field frame from IMU
        double angRobotHeadingCurrent_Field = DriveBase.getInstance().getAbsoluteHeading();
        Log.d("DAV: angRobotHeadingCurrent_Field: " + angRobotHeadingCurrent_Field);

        // Compute final desired robot heading relative to field
        double angRobotHeadingFinal_Field =
                computeAngRobotHeadingFinal_Field(angRobotHeadingCurrent_Field, driverAssistCargoAndLoading, driverAssistRocket);

        // Compute unit vector in direction facing target in field frame
        double finalRobotAngleRad_Field = Pathfinder.d2r(angRobotHeadingFinal_Field);
        double vUnitFacingTarget_FieldX = Math.cos(finalRobotAngleRad_Field);
        double vUnitFacingTarget_FieldY = Math.sin(finalRobotAngleRad_Field);

        // Compute vector from target to final robot position in field frame from unit vector in field frame
        double d = Config.getTARGET_OFFSET_DISTANCE();
        double vTargetToFinal_FieldX = -d * vUnitFacingTarget_FieldX;
        double vTargetToFinal_FieldY = -d * vUnitFacingTarget_FieldY;
        Log.d("DAV: vec_TargetToFinalX_Field: " + vTargetToFinal_FieldX + ", vTargetToFinal_FieldY: " + vTargetToFinal_FieldY);

        // Vector 3: vTargetToFinal_Robot: Vector from target to final robot position in robot frame
        //           (need to do a coordinate frame transformation on vTargetToFinal_Field)
        // Note: angRobotCurrent_Field is the current angle from the x axis of robot frame with respect to the
        //       x axis of the field frame, and the x axis of the robot frame is 90deg less than the robot heading
        //       which points along the y axis by definition
        double angRobotCurrent_Field = angRobotHeadingCurrent_Field - 90.0;
        double angRobotCurrentRad_Field = Pathfinder.d2r(angRobotCurrent_Field);
        double cosAngRobotCurrentRad_Field = Math.cos(angRobotCurrentRad_Field);
        double sinAngRobotCurrentRad_Field = Math.sin(angRobotCurrentRad_Field);
        double vTargetToFinal_RobotX = vTargetToFinal_FieldX * cosAngRobotCurrentRad_Field + vTargetToFinal_FieldY * sinAngRobotCurrentRad_Field;
        double vTargetToFinal_RobotY = -vTargetToFinal_FieldX * sinAngRobotCurrentRad_Field + vTargetToFinal_FieldY * cosAngRobotCurrentRad_Field;
        Log.d("DAV: vTargetToFinal_RobotX: " + vTargetToFinal_RobotX + ", vTargetToFinal_RobotY: " + vTargetToFinal_RobotY);

        // Compute vRobotToFinal_Robot as sum of Vectors 1, 2, and 3
        // vRobotToFinal_Robot = vRobotToCamera_Robot + vCameraToTarget_Robot + vTargetToFinal_Robot
        double vRobotToFinal_RobotX = vRobotToCamera_RobotX + vCameraToTarget_RobotX + vTargetToFinal_RobotX;
        double vRobotToFinal_RobotY = vRobotToCamera_RobotY + vCameraToTarget_RobotY + vTargetToFinal_RobotY;
        Log.d("DAV: vRobotToFinal_RobotX: " + vRobotToFinal_RobotX + ", vRobotToFinal_RobotY: " + vRobotToFinal_RobotY);

        // STEP 2: Compute final robot heading in robot frame
        double angRobotHeadingFinal_Robot = angRobotHeadingFinal_Field - angRobotCurrent_Field;
        Log.d("DAV: angRobotHeadingFinal_Robot: " + angRobotHeadingFinal_Robot);

        // STEP 3: Generate trajectory in robot frame with PathFinder library using two waypoints: one at initial position
        // and one at final position
        Log.d("DAV: Generating trajectory");
        Trajectory.Config config =
                new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH,
                        Config.getTRAJ_DELTA_TIME(), Config.ROBOT_MAX_VEL.value(), Config.ROBOT_MAX_ACC.value(),
                        Config.ROBOT_MAX_JERK.value());
        Waypoint[] points = new Waypoint[]{
                // Initial position/heading of robot: at origin with heading at 90 deg
                new Waypoint(0, 0, Pathfinder.d2r(90)),
                // Final position/heading of robot: in front of target
                new Waypoint(vRobotToFinal_RobotX, vRobotToFinal_RobotY, Pathfinder.d2r(angRobotHeadingFinal_Robot)),
        };
        traj = Pathfinder.generate(points, config);
        Log.d("DAV: Trajectory generated");

        /*
        Send trajectory to motion control system
        (Wait until integration with robot code) TODO
        */
    }

    /**
     * Returns trajectory generated to move robot to target
     *
     * @return Trajectory generated to move robot to target
     */
    public Trajectory getTraj() {
        return traj;
    }

    /**
     * Computes and returns final desired angle of robot heading respect to field frame
     *
     * @param angRobotHeadingCurrent_Field Angle of robot heading with respect to field frame
     * @param driverAssistCargoAndLoading  True if target is cargo ship or loading bay
     * @param driverAssistRocket           True if target is rocket
     * @return Final desired angle of robot heading respect to field frame in degrees
     */
    public double computeAngRobotHeadingFinal_Field(double angRobotHeadingCurrent_Field, boolean driverAssistCargoAndLoading,
                                                    boolean driverAssistRocket) {

        /*
        Compute final robot angle relative to field based on current angle of robot relative to field.
        Robot must be in an angular range such that it is approximately facing the desired target.
        */
        angRobotHeadingFinal_Field = 0.0;

        if (driverAssistCargoAndLoading) {
            // Assist requested for cargo ship or loading dock targets
            Log.d("DAV: Assist for cargo ship or loading dock requested");
            if ((angRobotHeadingCurrent_Field >= 0.0 && angRobotHeadingCurrent_Field <= 45.0) || (angRobotHeadingCurrent_Field >= 315.0 && angRobotHeadingCurrent_Field < 360.0)) {
                angRobotHeadingFinal_Field = 0.0;
            } else if (angRobotHeadingCurrent_Field >= 45.0 && angRobotHeadingCurrent_Field < 135.0) {
                angRobotHeadingFinal_Field = 90.0;
            } else if (angRobotHeadingCurrent_Field >= 135.0 && angRobotHeadingCurrent_Field < 225.0) {
                angRobotHeadingFinal_Field = 180.0;
            } else if (angRobotHeadingCurrent_Field >= 225.0 && angRobotHeadingCurrent_Field < 315.0) {
                angRobotHeadingFinal_Field = 270.0;
            }
        } else if (driverAssistRocket) {
            // Assist request for rocket ship targets
            Log.d("DAV: Assist for rocket requested");

            // Rockets on left
            if (angRobotHeadingCurrent_Field >= 90.0 && angRobotHeadingCurrent_Field <= 150.0) {
                angRobotHeadingFinal_Field = 120.0;
            } else if (angRobotHeadingCurrent_Field >= 150.0 && angRobotHeadingCurrent_Field <= 210.0) {
                angRobotHeadingFinal_Field = 180.0;
            } else if (angRobotHeadingCurrent_Field >= 210.0 && angRobotHeadingCurrent_Field <= 270.0) {
                angRobotHeadingFinal_Field = 240.0;
            }
            // Rockets on right
            else if (angRobotHeadingCurrent_Field >= 30.0 && angRobotHeadingCurrent_Field <= 90.0) {
                angRobotHeadingFinal_Field = 60.0;
            } else if ((angRobotHeadingCurrent_Field >= 0.0 && angRobotHeadingCurrent_Field <= 30.0) || (angRobotHeadingCurrent_Field >= 330.0 && angRobotHeadingCurrent_Field <= 360.0)) {
                angRobotHeadingFinal_Field = 0.0;
            } else if (angRobotHeadingCurrent_Field >= 270.0 && angRobotHeadingCurrent_Field <= 330.0) {
                angRobotHeadingFinal_Field = 300.0;
            }
        }
        Log.d("DAV: angRobotHeadingFinal_Field: " + angRobotHeadingFinal_Field);

        return angRobotHeadingFinal_Field;
    }

    /**
     * Returns previously computed final desired angle of robot heading respect to field frame.
     * (For testing purposes.)
     *
     * @return Previously computed final desired angle of robot heading respect to field frame in degrees
     */
    public double getAngRobotHeadingFinal_Field() {
        return angRobotHeadingFinal_Field;
    }
}
