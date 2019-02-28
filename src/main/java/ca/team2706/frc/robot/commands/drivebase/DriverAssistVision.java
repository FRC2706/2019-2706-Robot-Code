package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.subsystems.DriveBase;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

/**
 * Command for 2-D driver assist using vision
 * 
 * This will drive the robot from its current position and heading to a position 
 * at a specific distance in front of and aligned with a target (cargo ship, rocket, 
 * or loading bay) using data provided by the vision system. The vision system will 
 * send data corresponding to the target nearest to the camera. The distance in front 
 * of the target or loading bay is specified in the TARGET_OFFSET_DISTANCE attribute 
 * of the Config class.
 * 
 * For the feature to work properly, the following must hold
 *     i.  the robot must have a heading that is "most facing" the desired target compared 
 *         to other targets at the time driver assist is requested
 *     ii. the robot must be powered up at the start of the match at the heading specified
 *         in degrees by the ROBOT_START_ANGLE attribute of the Config class, where
 *         this heading is relative to a horizontal line extending across the field and
 *         positive heading is measured CCW
 * 
 * NOTE: MOTION CONTROL FUNCTIONALITY IS CURRENTLY AWAITING COMPLETION
 * 
 */
public class DriverAssistVision extends Command {

    /**
     * Creates empty driver assist command object (needed for unit testing framework only)
     */
    public DriverAssistVision() {
        // Ensure that this command is the only one to run on the drive base
        requires(DriveBase.getInstance());

        setupNetworkTables();
        trajGenerated = false;
        visionOffline = false;
    }

    /**
     * Creates the driver assist command
     * 
     * @param driverAssistCargoAndLoading: True if target is cargo ship or loading bay, false otherwise
     * @param driverAssistRocket: True if target is a rocket ship, false otherwise
     */
    public DriverAssistVision(boolean driverAssistCargoAndLoading, boolean driverAssistRocket) {
        this.driverAssistCargoAndLoading = driverAssistCargoAndLoading;
        this.driverAssistRocket = driverAssistRocket;

        // Ensure that this command is the only one to run on the drive base
        requires(DriveBase.getInstance());

        setupNetworkTables();
        trajGenerated = false;
        visionOffline = false;
    }

    /** 
     * Sets up network tables used to get data from vision system
     * 
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

        System.out.println("DAV: initialize() called");

        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().setPositionNoGyroMode();

        trajGenerated = false;

        // Get angle and position of vision target computed by vision subsystem in camera frame
        // from network table
        NetworkTableEntry vectorCameraToTarget = table.getEntry("vectorCameraToTarget");

        double[] vectorCameraToTarget_Camera = vectorCameraToTarget.getDoubleArray(new double[] {0,0});
        double angYawTargetWrtCameraLOSCWpos = vectorCameraToTarget_Camera[0];
        double distanceCameraToTarget_Camera = vectorCameraToTarget_Camera[1];

        System.out.println("DAV: angYawTargetWrtCameraLOSCWpos [deg]: " + angYawTargetWrtCameraLOSCWpos);
        System.out.println("DAV: distanceCameraToTarget_Camera [ft]: " + distanceCameraToTarget_Camera);
            
        // Due to the inherent jitter of values computed by the vision system, it is next to impossible
        // that values of yaw angle and distance to target will both be equal on successive commands unless
        // the vision system is not updating them for some reason. Therefore, is this occurs, consider
        // vision system offline. If they are different on any successive commands, consider vision system
        // online.
        if ((angYawTargetWrtCameraLOSCWpos == angYawTargetWrtCameraLOSCWposPrev) &&
            (distanceCameraToTarget_Camera == distanceCameraToTarget_CameraPrev)    ) {
            visionOffline = true;
        } else {
            visionOffline = false;
        }

        if(visionOffline) {
            Log.d("DAV: Vision system offline. Driver assist command not performed.");
            trajGenerated = true;
            return;
        }

        System.out.println("DAV: Generating trajectory");

        // Compute the trajectory
        generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, angYawTargetWrtCameraLOSCWpos,
                driverAssistCargoAndLoading, driverAssistRocket);

        System.out.println("RCVI: Exiting initialize()");

        trajGenerated = true;
    }

    @Override
    public void execute() {
        // Nothing to do here
    }

    @Override
    public boolean isFinished() {
        return trajGenerated;
    };

    @Override
    public void end() {
        // Go back to disabled mode
        //DriveBase.getInstance().setDisabledMode();
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

    public void generateTrajectoryRobotToTarget(double distanceCameraToTarget_Camera, double angYawTargetWrtCameraLOSCWpos,
        boolean driverAssistCargoAndLoading, boolean driverAssistRocket) {

        /**
         * Explanation of vector and coordinate frame notation in a 2-d plane:
         * 
         * A vector from point P1 to point P2 represented in a coordinate frame F3 is written as vP1ToP2_F3.
         * 
         * The x and y components of this vector are written as vP1ToP2_F3_X and vP1ToP2_F3_Y. Note that it
         * only makes sense to speak of x and y coordinates in relation to a coordinate frame which in this
         * case is F3.
         * 
         * Points P1 or P2 can also be the origin of a coordinate frames. In this case, P1 is replaced by
         * the name of the frame. Thus, if F1, F2, and F3 are coordinatre frames, then vF1ToF2_F3 represents 
         * the vector from the origin of frame F1 to the origin of frame F2 represented in the frame F3. 
         * 
         * In this class, all vectors represent distances measured in feet.
         *
         * An angle in the 2-d plane is the angular measurement between two lines: the line of interest
         * and a reference line. Angles will be measured in degrees and will increase in a counter-clockwise 
         * direction unless otherwise noted. The name of the angle should make clear what the line of interest
         * and the line of reference are, and whether
         *  
         * An angle in the 2-d plane can be defined with respect to a particular coordinate frame as the 
         * angle between the line of interest and the x-axis of the coordinate frame. An angle between 
         * a line of interest having name "ZZZ" and with respect to the x-axis of a frame F1 will be written 
         * as angZZZ_F1. Angles are assumed to be in degrees by default. If an angle is represented in units 
         * of radians (which is needed when the sin or cos of an angle is taken), then the text "Rad" will be 
         * added after the name of the line of interest. Thus the previous angle in radians would be 
         * angZZZRad_F1. Angles are assumed to increase in the counter-clockwise direction by default. If an 
         * angle increases in the clockwise direction, the "CWpos" will be added after the name and after the
         * "Rad" text. In this case, the original example measurement in radians and where the angle increases
         * in the clockwise direction would be written as angZZZRadCWpos.
         * 
         * An angle in the 2-d plane can also be defined as the angular measurement between the line of
         * interest and a suitably chosen reference line. This will be written as angZZZWrtWWW where
         * ZZZ is the name of the line of interest and WWW is the name of the reference line. Use of
         * non-default units of degrees or positive angle direction are represented as described above.
         * 
         * The three coordinate frame of interest are given below. Note that these are planar frames and
         * their height is not relevant for the model used in this class.
         * 
         *   Camera: Coordinate frame attached to camera with origin at focal point of lens, y axis 
         *           pointing along the camera line of sight, and x axis pointing to the right when 
         *           looking from the front to the back of the camera.
         * 
         *   Robot:  Coordinate frame attached to camera with origin at the centre of the robot base, 
         *           y axis pointing toward the front along the centre line, and x axis pointing to 
         *           the right when looking from the front to the back of the robot.
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
         *           frame. This is offset by a user-specified distance from the front of the target.
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
        System.out.println("DAV: vRobotToCamera_RobotX: " + vRobotToCamera_RobotX + ", vRobotToCamera_RobotY: " + vRobotToCamera_RobotY);

        // Vector 2: vCameraToTarget_Robot: Vector from camera to target in robot frame
        double angYawTargetWrtCameraLOSRadCWpos = Pathfinder.d2r(angYawTargetWrtCameraLOSCWpos);
        double vCameraToTarget_RobotX =  distanceCameraToTarget_Camera * Math.sin(angYawTargetWrtCameraLOSRadCWpos);
        double vCameraToTarget_RobotY  = distanceCameraToTarget_Camera * Math.cos(angYawTargetWrtCameraLOSRadCWpos);        
        System.out.println("DAV: vec_CameraToTargetX_Robot: " + vCameraToTarget_RobotX + ", vCameraToTarget_RobotY: " + vCameraToTarget_RobotY);

        // Get current robot heading relative to field frame from IMU
        double angRobotHeadingCurrent_Field = DriveBase.getInstance().getAbsoluteHeading();
        System.out.println("DAV: angRobotHeadingCurrent_Field: " + angRobotHeadingCurrent_Field);

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
        System.out.println("DAV: vec_TargetToFinalX_Field: " + vTargetToFinal_FieldX + ", vTargetToFinal_FieldY: " + vTargetToFinal_FieldY);
        
        // Vector 3: vTargetToFinal_Robot: Vector from target to final robot position in robot frame
        double angRobotCurrent_Field = angRobotHeadingCurrent_Field - 90.0;  // robot is along y of robot frame but want angle to x
        double angRobotCurrentRad_Field = Pathfinder.d2r(angRobotCurrent_Field);
        double cosAngRobotCurrentRad_Field = Math.cos(angRobotCurrentRad_Field);
        double sinAngRobotCurrentRad_Field = Math.sin(angRobotCurrentRad_Field);
        double vTargetToFinal_RobotX = vTargetToFinal_FieldX * cosAngRobotCurrentRad_Field + vTargetToFinal_FieldY * sinAngRobotCurrentRad_Field;
        double vTargetToFinal_RobotY = -vTargetToFinal_FieldX * sinAngRobotCurrentRad_Field + vTargetToFinal_FieldY * cosAngRobotCurrentRad_Field;
        System.out.println("DAV: vTargetToFinal_RobotX: " + vTargetToFinal_RobotX + ", vTargetToFinal_RobotY: " + vTargetToFinal_RobotY);

        // Compute vRobotToFinal_Robot as sum of Vectors 1, 2, and 3
        // vRobotToFinal_Robot = vRobotToCamera_Robot + vCameraToTarget_Robot + vTargetToFinal_Robot
        double vRobotToFinal_RobotX = vRobotToCamera_RobotX + vCameraToTarget_RobotX + vTargetToFinal_RobotX;
        double vRobotToFinal_RobotY = vRobotToCamera_RobotY + vCameraToTarget_RobotY + vTargetToFinal_RobotY;
        System.out.println("DAV: vRobotToFinal_RobotX: " + vRobotToFinal_RobotX + ", vRobotToFinal_RobotY: " + vRobotToFinal_RobotY);

        // Compute final robot heading in robot frame
        double angRobotHeadingFinal_Robot = angRobotHeadingFinal_Field - angRobotCurrent_Field;
        Log.d("DAV: angRobotHeadingFinal_Robot: " + angRobotHeadingFinal_Robot);

        // Generate trajectory in robot frame with PathFinder library using two waypoints: one at initial position
        // and one at final position
        Log.d("DAV: Generating trajectory");
        Trajectory.Config config = 
            new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 
                                  Config.getTRAJ_DELTA_TIME(), Config.ROBOT_MAX_VEL.value(), Config.ROBOT_MAX_ACC.value(), 
                                  Config.ROBOT_MAX_JERK.value());
        Waypoint[] points = new Waypoint[] {
                // Initial position/heading of robot: at origin with heading at 90 deg
                new Waypoint(0, 0, Pathfinder.d2r(90)), 
                // Final position/heading of robot: in front of target
                new Waypoint(vRobotToFinal_RobotX, vRobotToFinal_RobotY, Pathfinder.d2r(angRobotHeadingFinal_Robot)),
        };
        traj = Pathfinder.generate(points, config);
        Log.d("DAV: Trajectory generated");

        // Send trajectory to motion control system
        // (Wait until integration with robot code)
        
        System.out.println("DAV: Trajectory length: " + traj.length());
        for (int i = 0; i < traj.length(); i++)
        {
            String str = 
                traj.segments[i].x + "," +
                traj.segments[i].y + "," +
                traj.segments[i].heading;

            //System.out.println(str);
        }

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
     * @param driverAssistCargoAndLoading True if target is cargo ship or loading bay
     * @param driverAssistRocket True if target is rocket
     * 
     * @return Final desired angle of robot heading respect to field frame in degrees
     */
    public double computeAngRobotHeadingFinal_Field(double angRobotHeadingCurrent_Field, boolean driverAssistCargoAndLoading, 
                                                    boolean driverAssistRocket) {
 
        // Compute final robot angle relative to field based on current angle of robot relative to field.
        // Robot must be in an angular range such that it is approximately facing the desired target. 
        angRobotHeadingFinal_Field = 0.0;

        if (driverAssistCargoAndLoading == true) {
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
        }
        else if (driverAssistRocket == true) {
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
        System.out.println("angRobotHeadingFinal_Field: " + angRobotHeadingFinal_Field);

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

    /** 
     * Angle of robot heading with respect to x axis of field frame
     * 
    */
    private double angRobotHeadingFinal_Field;

    /**  
     * Value of angYawTargetWrtCameraLOSCWposAngle (yaw angle to target wrt camera line of sight in degrees)
     * from the previous time the command was issued.
     * 
    */
    private double angYawTargetWrtCameraLOSCWposPrev = 0.0;

   /**  
     * Value of distanceCameraToTarget_Camera (distance from camera frame origin to target in feet)
     * from the previous time the command was issued.
     * 
    */
    private double distanceCameraToTarget_CameraPrev;

    /**
     * True if target is cargo ship or loading bay
     */
    private boolean driverAssistCargoAndLoading;

    /**
     * True if target is rocket
     */
    private boolean driverAssistRocket;

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
}
