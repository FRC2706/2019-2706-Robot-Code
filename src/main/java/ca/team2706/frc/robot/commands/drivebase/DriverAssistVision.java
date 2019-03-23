package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.subsystems.DriveBase;
import ca.team2706.frc.robot.subsystems.RingLight;
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
 * This will drive the robot from its current position/heading to one of the following
 * positon/headings based on location data provided by the vision system:
 * 1) in front of and aligned with the nearest target either on the cargo ship, rocket,
 * or loading bay, offset by a distance in the Config class given by
 * TARGET_OFFSET_DISTANCE_<TARGET>, where <TARGET> is one of CARGO_AND_LOADING or
 * ROCKET depending on the type of target
 * 2) in front of a ball, offset by a distance in the Config class given by
 * TARGET_OFFSET_DISTANCE_BALL
 * <p>
 * For the feature to work properly, the following must hold
 * i.  the robot must have a heading that is "most facing" the desired target compared
 * to other targets at the time driver assist is requested
 * ii. the robot must be powered up at the start of the match at the heading specified
 * in degrees by the ROBOT_START_ANGLE attribute of the Config class, where
 * this angle is relative to a horizontal line extending across the field and
 * positive angle is measured CCW
 */
public class DriverAssistVision extends Command {
    /*
     * Timestamp from the vision system on the previous call to the command, used to assess if
     * the vision system is still running or is offline
     */
    static double videoTimestampPrev = 0.0;

    /**
     * Angle of final desired robot heading with respect to x axis of field frame
     */
    private double angRobotHeadingFinal_Field = 0.0;

    /**
     * Network table to get Chicken Vision data from vision subsystem
     */
    private NetworkTable chickenVisionTable;

    /**
     * True if command has been aborted, false otherwise
     */
    private boolean commandAborted;

    /*
     * Network table entry named Driver, a boolean set to true to request to the vision system
     * to enter Driver mode, false otherwise. Targets are not detected in this mode.
     */
    private NetworkTableEntry driverEntry;

    /*
     * Network table entry named Cargo, a boolean set to true to request to the vision
     * to detect ball targets, false otherwise
     */
    private NetworkTableEntry findCargoEntry;

    /*
     * Network table entry named Tape, a boolean set to true to request to the vision
     * to detect tape targets, false otherwise
     */
    private NetworkTableEntry findTapeEntry;

    /*
     * Motion control system object providing functionality to follow a Pathfinder trajectory
     */
    private FollowTrajectory followTrajectory;

    /*
     * True if the issueGenerateTrajectoryCommand stage of execute() method has completed,
     * indicating that a command has been issued to generate a trajectory, false otherwise
     */
    boolean generateTrajectoryRequestStageComplete = false;

    /*
     * Network table instance to get data from vision subsystem
     */
    private NetworkTableInstance inst;

    /**
     * Network table to get Pathfinder data from vision subsystem
     */
    private NetworkTable pathfinderTable;

    /**
     * True if ring light on delay stage of execute() method has completed, false otherwise
     */
    private boolean ringLightOnDelayStageComplete = false;

    /**
     * True if ring light is currently on, false otherwise
     */
    private boolean ringLightOn = false;

    /**
     * Current value of time delay after ring light has been turned on.
     */
    private double ringLightOnDelayTime;

    /*
     * Network table entry named TapeDetected, a boolean set to true if the tape target has been
     * detected by the vision system, false otherwise
     */
    private NetworkTableEntry tapeDetectedEntry;

    /*
     * Type of target that robot will move to (CARGO_AND_LOADING, ROCKET, or BALL)
     */
    private DriverAssistVisionTarget target;

    /**
     * Trajectory generated by Pathfinder to move robot to target. Declared volatile since it is shared between
     * different threads.
     */
    private volatile Trajectory trajectory;

    /**
     * Creates empty driver assist command object (needed for unit testing framework only)
     */
    public DriverAssistVision() {
        // Ensure that this command is the only one to run on the drive base
        requires(RingLight.getInstance());

        setupNetworkTables();

        commandAborted = false;
        ringLightOnDelayTime = 0.0;
        ringLightOnDelayStageComplete = false;
        generateTrajectoryRequestStageComplete = false;
    }

    /**
     * Creates the driver assist command
     *
     * @param DriverAssistVisionTarget target: The type of the destination target (CARGO_AND_LOADING, ROCKET, BALL)
     */
    public DriverAssistVision(DriverAssistVisionTarget target) {
        this.target = target;

        // Ensure that this command is the only one to run on the drive base
        requires(RingLight.getInstance());

        commandAborted = false;
        ringLightOnDelayTime = 0.0;
        ringLightOnDelayStageComplete = false;
        generateTrajectoryRequestStageComplete = false;
        
        setupNetworkTables();
    }

    /**
     * Sets up network tables used to get data from vision system
     */
    private void setupNetworkTables() {
        inst = NetworkTableInstance.getDefault();
        pathfinderTable = inst.getTable("PathFinder");
        chickenVisionTable = inst.getTable("ChickenVision");
        if ((pathfinderTable == null) || (chickenVisionTable == null)) {
            System.out.println("DAV: Network tables not found, command aborted");
            commandAborted = true;
        }
    }

    /**
     * initialize() method turns on ring light and sets up network tables
     */
    @Override
    public void initialize() {
        System.out.println("DAV: initialize() called");
        if (commandAborted) {
            System.out.println("DAV: Command aborted, initialize() not run");
        }

        if (visionOffline()) {
            System.out.println("DAV: execute(): Vision offline, command aborted");
            commandAborted = true;
            return;
        }

        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().setPositionNoGyroMode();

        // Turn on green ring light
        System.out.println("DAV: Turning on light");
        RingLight.getInstance().enableLight();
        System.out.println("DAV: Light turned on");
    
        commandAborted = false;
        ringLightOnDelayTime = 0.0;
        ringLightOnDelayStageComplete = false;
        generateTrajectoryRequestStageComplete = false;
        
        // Turn on ring light
        if(!ringLightOn) {
            System.out.println("DAV: Turning ring light on");
            RingLight.getInstance().toggleLight();
            ringLightOn = true;
        }
    
        System.out.println("DAV: Getting entries for network table");
        driverEntry = chickenVisionTable.getEntry("Driver");
        findTapeEntry = chickenVisionTable.getEntry("Tape");
        findCargoEntry = chickenVisionTable.getEntry("Cargo");
        tapeDetectedEntry = chickenVisionTable.getEntry("tapeDetected");

        if ((driverEntry == null) || (findTapeEntry == null) || (findCargoEntry == null) || (tapeDetectedEntry == null)) {
            System.out.println("DAV: Network table entries not set up, command aborted");
            commandAborted = true;
            return;
        }

        System.out.println("DAV: Setting network table entries to find target");
        if ((target == DriverAssistVisionTarget.CARGO_AND_LOADING) || (target == DriverAssistVisionTarget.ROCKET)) {
            driverEntry.setBoolean(false);
            findCargoEntry.setBoolean(false);
            findTapeEntry.setBoolean(true);
        } else if (target == DriverAssistVisionTarget.BALL) {
            driverEntry.setBoolean(false);
            findCargoEntry.setBoolean(true);
            findTapeEntry.setBoolean(false);
        } else {
            // target has unexpected value so abort command
            System.out.println ("DAV: Target value unexpected, aborting command");
            commandAborted = true;
            return;
        }

        System.out.println("DAV: Exiting initialize()");
    }

    /**
     * execute() method has three stages: 1) Turns on ring light, waits a fixed delay for vision to
     * 
     * 
     * delays for ring light to be on, waits for vision system to find target, 
     * prepares inputs to Pathfinder, calls Pathfinder in a separate thread to generate the trajectory, 
     * waits for trajectory to be generated, then calls motion control subsystem to move to the target
     */
    @Override
    public void execute() {
        // Stage 1: Ring Light on Delay Stage: Keep ring light on for a fixed delay to allow vision
        // measurement to stabilize before taking a reading
        if (!ringLightOnDelayStageComplete) {
            ringLightOnDelayTime += Config.EXECUTE_PERIOD;
            if (ringLightOnDelayTime < Config.RING_LIGHT_ON_DELAY.value()) {
                return;
            } else {
                ringLightOnDelayStageComplete = true;
            }
        }

        // Stage 2: Generate Trajectory Request Stage: Wait for vision to find target within reasonable bounds 
        // then, in a separate thread, issue request to generate Pathfinder trajectory
        if (!generateTrajectoryRequestStageComplete) {
            boolean tapeDetected = tapeDetectedEntry.getBoolean(false);
            if (!tapeDetected)
                return;
            else {
                System.out.println("DAV: Tape detected");
                
                // Read angle and position of target (wrt camera coordinate frame) computed by vision
                // system from network table 
                NetworkTableEntry vectorCameraToTarget = pathfinderTable.getEntry("vectorCameraToTarget");
                if (vectorCameraToTarget == null) {
                    System.out.println("DAV: vectorCameraToTarget network table entry not available, command aborted");
                    commandAborted = true;
                    return;
                }
                double[] vectorCameraToTarget_Camera = vectorCameraToTarget.getDoubleArray(new double[]{0, 0});
                double angYawTargetWrtCameraLOSCWpos = vectorCameraToTarget_Camera[0];
                double distanceCameraToTarget_Camera = vectorCameraToTarget_Camera[1];
                System.out.println("DAV: angYawTargetWrtCameraLOSCWpos [deg]: " + angYawTargetWrtCameraLOSCWpos);
                System.out.println("DAV: distanceCameraToTarget_Camera [ft]: " + distanceCameraToTarget_Camera);

                // If distance to target is below a reasonable value, abort command completely 
                if (distanceCameraToTarget_Camera < Config.VISION_DISTANCE_MIN.value()) {
                    System.out.println("DAV: Distance to target too low. Driver assist command aborted.");
                    commandAborted = true;
                    return;
                }

                // If distance to target is above a reasonable value, it is likely a temporary glitch in the
                // vision measurement so return from the current execute() cycle but don't abort the command 
                // so another reading can be taken on the next execute() call
                if (distanceCameraToTarget_Camera > Config.VISION_DISTANCE_MAX.value()) {
                    System.out.println("DAV: Distance to target too high. Driver assist command not performed.");
                    return;
                }

                // Turn off ring light since we now have the data from vision
                if (ringLightOn) {
                    System.out.println("DAV: Turning ring light off");
                    RingLight.getInstance().toggleLight();
                    ringLightOn = false;
                }

                // Send request to generate trajectory in a separate task to avoid execute() overruns
                System.out.println("DAV: Generating trajectory");
                Runnable task = new Runnable() {
                    public void run() {
                        generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, angYawTargetWrtCameraLOSCWpos, target);
                    }
                };
                new Thread(task).start();

                generateTrajectoryRequestStageComplete = true;
            }
        }

        // Stage 3: Command Robot to Follow Trajectory
        if ((followTrajectory == null) && (trajectory != null)) {
            System.out.println("DAV: Commanding robot to follow trajectory");
            followTrajectory = new FollowTrajectory(1.0, 100, trajectory);
            followTrajectory.start();
        }
    }

    @Override
    public boolean isFinished() {
        return ( ((followTrajectory != null) && followTrajectory.isFinished()) || commandAborted);
    }

    @Override
    public void end() {
        System.out.println("DAV: Calling end()");
        if (followTrajectory != null) {
            if (followTrajectory.isRunning()) {
                followTrajectory.cancel();
            }
            followTrajectory = null;
        }

        // Turn off green ring light
        RingLight.getInstance().disableLight();

        // Put vision back into driver mode
        driverEntry = chickenVisionTable.getEntry("Driver");
        findTapeEntry = chickenVisionTable.getEntry("Tape");
        findCargoEntry = chickenVisionTable.getEntry("Cargo");
        driverEntry.setBoolean(true);
        findCargoEntry.setBoolean(false);
        findTapeEntry.setBoolean(false);

        // Vision group has requested that tapeDetected entry is set to false here
        tapeDetectedEntry = chickenVisionTable.getEntry("tapeDetected");
        tapeDetectedEntry.setBoolean(false); 

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
     * Gets the Pathfinder network table
     */
    public NetworkTable getPathfinderNetworkTable() {
        return pathfinderTable;
    }

    /**
     * Gets the ChickenVision network table
     */
    public NetworkTable getChickenVisionNetworkTable() {
        return chickenVisionTable;
    }

    /**
     * Generates a trajectory using the Pathfinder library and sends it to the motion control
     * system
     *
     * @param distanceCameraToTarget_Camera distance from camera to target [ft]
     * @param angYawTargetWrtCameraLOSCWpos yaw angle to target wrt camera line of sight, CW with increase angle [deg]
     * @param target                        destination target (CARGO_AND_LOADING, ROCKET, or BALL)
     */
    public void generateTrajectoryRobotToTarget(double distanceCameraToTarget_Camera, double angYawTargetWrtCameraLOSCWpos,
                                                DriverAssistVisionTarget target) {
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
         *   Target: The vision target. If the "target" field is CARGO_AND_LOADING or ROCKET, then it is
         *           at the centre of two relective pieces of tape. If the "target" is BALL, then it is
         *           at the centre of the ball.
         *
         *   Final:  Final position of robot as represented by the location of the origin of the robot
         *           frame. This is offset by a user-specified distance distance in the Config class 
         *           given by TARGET_OFFSET_DISTANCE_<TARGET>, where <TARGET> is one of CARGO_AND_LOADING,
         *           ROCKET, or BALL.
         */
        System.out.println("DAV: isTrajectoryGenerated: " + isTrajectoryGenerated());

        // STEP 1: Compute vector from current robot position to final robot position in robot frame, 
        // vRobotToFinal_Robot, where
        //  
        // vRobotToFinal_Robot = vRobotToCamera_Robot + vCameraToTarget_Robot + vTargetToFinal_Robot
        //
        // The three vectors on the right hand side will be denoted by Vector 1, Vector 2, and 
        // Vector 3 in the comments below, and Vector 1+2 represents the sum Vector 1 and Vector 2

        // Vector 1: vRobotToCamera_Robot: Vector from robot to camera in robot frame
        double vRobotToCamera_RobotX = Config.ROBOTTOCAMERA_ROBOTX.value();
        double vRobotToCamera_RobotY = Config.ROBOTTOCAMERA_ROBOTY.value();
        Log.d("DAV: vRobotToCamera_RobotX: " + vRobotToCamera_RobotX + ", vRobotToCamera_RobotY: " + vRobotToCamera_RobotY);

        // Vector 2: vCameraToTarget_Robot: Vector from camera to target in robot frame
        double angYawTargetWrtCameraLOSRadCWpos = Pathfinder.d2r(angYawTargetWrtCameraLOSCWpos);
        double vCameraToTarget_RobotX = distanceCameraToTarget_Camera * Math.sin(angYawTargetWrtCameraLOSRadCWpos);
        double vCameraToTarget_RobotY = distanceCameraToTarget_Camera * Math.cos(angYawTargetWrtCameraLOSRadCWpos);
        Log.d("DAV: vec_CameraToTargetX_Robot: " + vCameraToTarget_RobotX + ", vCameraToTarget_RobotY: " + vCameraToTarget_RobotY);

        // Vector 1+2: vRobotToTarget_Robot: Vector from robot to target in robot frame
        double vRobotToTarget_RobotX = vRobotToCamera_RobotX + vCameraToTarget_RobotX;
        double vRobotToTarget_RobotY = vRobotToCamera_RobotY + vCameraToTarget_RobotY;

        // Vector 3: vTargetToFinal_Robot: Vector from target to final robot position in robot frame
        // (Depends on target type.)
        double vTargetToFinal_RobotX = 0.0;
        double vTargetToFinal_RobotY = 0.0;
        double angRobotCurrent_Field = 0.0;
        if ((target == DriverAssistVisionTarget.CARGO_AND_LOADING) ||
                (target == DriverAssistVisionTarget.ROCKET)) {
            // Vector 3 is aligned with and facing away from target

            // Get current robot heading relative to field frame from IMU
            double angRobotHeadingCurrent_Field = DriveBase.getInstance().getAbsoluteHeading() % 360;
            if(angRobotCurrent_Field < 0) {
                angRobotCurrent_Field += 360;
            }

            System.out.println("DAV: angRobotHeadingCurrent_Field: " + angRobotHeadingCurrent_Field);

            // Compute final desired robot heading relative to field
            double angRobotHeadingFinal_Field =
                    computeAngRobotHeadingFinal_Field(angRobotHeadingCurrent_Field, target);
            System.out.println("DAV: angRobotHeadingFinal_Field: " + angRobotHeadingFinal_Field);

            // Compute unit vector in direction facing target in field frame
            double finalRobotAngleRad_Field = Pathfinder.d2r(angRobotHeadingFinal_Field);
            double vUnitFacingTarget_FieldX = Math.cos(finalRobotAngleRad_Field);
            double vUnitFacingTarget_FieldY = Math.sin(finalRobotAngleRad_Field);

            // Compute vector from target to final robot position in field frame from unit vector in field frame
            double d = 0.0;
            if (target == DriverAssistVisionTarget.CARGO_AND_LOADING) {
                d = Config.ROBOT_HALF_LENGTH.value() + Config.TARGET_OFFSET_DISTANCE_CARGO_AND_LOADING.value();
            } else if (target == DriverAssistVisionTarget.ROCKET) {
                d = Config.ROBOT_HALF_LENGTH.value() + Config.TARGET_OFFSET_DISTANCE_ROCKET.value();
            }
            double vTargetToFinal_FieldX = -d * vUnitFacingTarget_FieldX;
            double vTargetToFinal_FieldY = -d * vUnitFacingTarget_FieldY;
            Log.d("DAV: vec_TargetToFinalX_Field: " + vTargetToFinal_FieldX);
            Log.d("DAV: vTargetToFinal_FieldY: " + vTargetToFinal_FieldY);

            // Vector 3: vTargetToFinal_Robot: Vector from target to final robot position in robot frame
            //           (need to do a coordinate frame transformation on vTargetToFinal_Field)
            // Note: angRobotCurrent_Field is the current angle from the x axis of robot frame with respect to the
            //       x axis of the field frame, and the x axis of the robot frame is 90deg less than the robot heading
            //       which points along the y axis by definition
            angRobotCurrent_Field = angRobotHeadingCurrent_Field - 90.0;
            double angRobotCurrentRad_Field = Pathfinder.d2r(angRobotCurrent_Field);
            double cosAngRobotCurrentRad_Field = Math.cos(angRobotCurrentRad_Field);
            double sinAngRobotCurrentRad_Field = Math.sin(angRobotCurrentRad_Field);
            vTargetToFinal_RobotX = vTargetToFinal_FieldX * cosAngRobotCurrentRad_Field + vTargetToFinal_FieldY * sinAngRobotCurrentRad_Field;
            vTargetToFinal_RobotY = -vTargetToFinal_FieldX * sinAngRobotCurrentRad_Field + vTargetToFinal_FieldY * cosAngRobotCurrentRad_Field;
            Log.d("DAV: vTargetToFinal_RobotX: " + vTargetToFinal_RobotX + ", vTargetToFinal_RobotY: " + vTargetToFinal_RobotY);

        } else if (target == DriverAssistVisionTarget.BALL) {
            // Vector 3 is oriented from cargo ball to origin of robot frame
            double vRobotToTarget_magnitude = Math.sqrt(Math.pow(vRobotToTarget_RobotX, 2) + Math.pow(vRobotToTarget_RobotY, 2));
            double d = Config.ROBOT_HALF_LENGTH.value() + Config.TARGET_OFFSET_DISTANCE_BALL.value();
            vTargetToFinal_RobotX = -d * (vRobotToTarget_RobotX / vRobotToTarget_magnitude);
            vTargetToFinal_RobotY = -d * (vRobotToTarget_RobotY / vRobotToTarget_magnitude);
        }

        // Compute vRobotToFinal_Robot as sum of Vectors 1+2 and 3
        // vRobotToFinal_Robot = (vRobotToCamera_Robot + vCameraToTarget_Robot) + vTargetToFinal_Robot
        //                        vRobotToTarget_Robot                          + vTargetToFinal_Robert
        double vRobotToFinal_RobotX = vRobotToTarget_RobotX + vTargetToFinal_RobotX;
        double vRobotToFinal_RobotY = vRobotToTarget_RobotY + vTargetToFinal_RobotY;
        System.out.println("DAV: vRobotToFinal_RobotX: " + vRobotToFinal_RobotX + ", vRobotToFinal_RobotY: " + vRobotToFinal_RobotY);

        // STEP 2: Compute final robot heading in robot frame
        double angRobotHeadingFinal_Robot = 0.0;
        double angRobotHeadingFinalRad_Robot = 0.0;
        if ((target == DriverAssistVisionTarget.CARGO_AND_LOADING) ||
                (target == DriverAssistVisionTarget.ROCKET)) {
            angRobotHeadingFinal_Robot = angRobotHeadingFinal_Field - angRobotCurrent_Field;
            angRobotHeadingFinalRad_Robot = Pathfinder.d2r(angRobotHeadingFinal_Robot);
        } else {
            // For ball target, heading is just current heading in robot frame
            angRobotHeadingFinalRad_Robot = Math.atan2(vRobotToFinal_RobotY, vRobotToFinal_RobotX);
            angRobotHeadingFinal_Robot = Pathfinder.r2d(angRobotHeadingFinalRad_Robot);
        }
        System.out.println("DAV: angRobotHeadingFinal_Robot: " + angRobotHeadingFinal_Robot);
        Log.d("DAV: angRobotHeadingFinalRad_Robot: " + angRobotHeadingFinalRad_Robot);

        // STEP 3: Generate trajectory in robot frame with PathFinder library using two waypoints: one at initial position
        // and one at final position
        System.out.println("DAV: Generating trajectory");
        Trajectory.Config config =
                new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_FAST,
                        Config.TRAJ_DELTA_TIME.value(), Config.VISION_ASSIST_MAX_VELOCITY.value(), Config.VISION_ASSIST_MAX_ACCELERATION.value(),
                        Config.VISION_ASSIST_MAX_JERK.value());
        Waypoint[] points = new Waypoint[]{
                // Initial position/heading of robot: at origin with heading at 90 deg
                new Waypoint(0, 0, Pathfinder.d2r(90)),
                // Final position/heading of robot: in front of target
                new Waypoint(vRobotToFinal_RobotX, vRobotToFinal_RobotY, angRobotHeadingFinalRad_Robot),
        };
        trajectory = Pathfinder.generate(points, config);

        /*
         * Headings in trajectory must each be converted into the robot motion control system's frame
         * whose x-y axes are the same as our robot frame but whose heading along the y-axis
         * is at 0 degrees with  positive heading clockwise (as compared to our robot frame whose
         * heading along the y-axis is 90 degrees with positive heading counter-clockwise).
         */
        double PI_OVER_2 = Math.PI / 2.0;
        for (int i = 0; i < trajectory.length(); i++) {
            trajectory.segments[i].heading = PI_OVER_2 - trajectory.segments[i].heading;
        }

        System.out.println("DAV: Trajectory length: " + trajectory.length());
        for (int i = 0; i < trajectory.length(); i++)
        {
            String str = 
                trajectory.segments[i].x + "," +
                trajectory.segments[i].y + "," +
                trajectory.segments[i].heading;

            System.out.println(str);
        }
        
        System.out.println("DAV: Trajectory generated");
    }

    /**
     * Returns trajectory generated to move robot to target
     *
     * @return Trajectory generated to move robot to target
     */
    public Trajectory getTrajectory() {
        return trajectory;
    }

    /**
     * Determines if a trajectory to the current target has been generated.
     *
     * @return True if the trajectory has been generated, false otherwise.
     */
    public boolean isTrajectoryGenerated() {
        return (trajectory != null);
    }

    /**
     * Determines if a vision target is currently in view and detected.
     *
     * @return True if vision target has been detected, false otherwise
     */
    public boolean isVisionTargetDetected() {
        return generateTrajectoryRequestStageComplete;
    }

    /**
     * Computes and returns final desired angle of robot heading respect to field frame
     *
     * @param angRobotHeadingCurrent_Field Angle of robot heading with respect to field frame
     * @param target                       Destination target
     * @return Final desired angle of robot heading respect to field frame in degrees
     */
    public double computeAngRobotHeadingFinal_Field(double angRobotHeadingCurrent_Field, DriverAssistVisionTarget target) {

        /*
        Compute final robot angle relative to field based on current angle of robot relative to field.
        Robot must be in an angular range such that it is approximately facing the desired target.
        */
        angRobotHeadingFinal_Field = 0.0;

        if (target == DriverAssistVisionTarget.CARGO_AND_LOADING) {
            // Assist requested for cargo ship or loading bay targets
            Log.d("DAV: Assist for cargo ship or loading bay requested");
            if ((angRobotHeadingCurrent_Field >= 0.0 && angRobotHeadingCurrent_Field <= 45.0) || (angRobotHeadingCurrent_Field >= 315.0 && angRobotHeadingCurrent_Field < 360.0)) {
                angRobotHeadingFinal_Field = 0.0;
            } else if (angRobotHeadingCurrent_Field >= 45.0 && angRobotHeadingCurrent_Field < 135.0) {
                angRobotHeadingFinal_Field = 90.0;
            } else if (angRobotHeadingCurrent_Field >= 135.0 && angRobotHeadingCurrent_Field < 225.0) {
                angRobotHeadingFinal_Field = 180.0;
            } else if (angRobotHeadingCurrent_Field >= 225.0 && angRobotHeadingCurrent_Field < 315.0) {
                angRobotHeadingFinal_Field = 270.0;
            }
        } else if (target == DriverAssistVisionTarget.ROCKET) {
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

    /**
     * Returns true of vision system is offline, false otherwise
     */
    public boolean visionOffline() {
        NetworkTableEntry videoTimestampEntry = chickenVisionTable.getEntry("VideoTimestamp");
        double videoTimestamp = videoTimestampEntry.getDouble(0.0);
        boolean offline = (videoTimestamp == videoTimestampPrev);
        videoTimestampPrev = videoTimestamp;
        return(offline);
    }

    /**
     * Enum for the different targets the robot may be request to go to
     */
    public enum DriverAssistVisionTarget {
        /**
         * Cargo ship or loading bay target
         */
        CARGO_AND_LOADING,

        /**
         * Rocket target
         */
        ROCKET,

        /**
         * Ball target
         */
        BALL
    }
}

