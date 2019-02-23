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
 * Drives the robot using values for driving forward and rotation
 */
public class DriverAssistWithVision extends Command {

    public DriverAssistWithVision() {
        // Ensure that this command is the only one to run on the drive base
        requires(DriveBase.getInstance());

        setupNetworkTables();
    }

    /**
     * Creates the driver assist with vision command
     */

    public DriverAssistWithVision(boolean driverAssistCargoAndLoading, boolean driverAssistRocket) {
        this.driverAssistCargoAndLoading = driverAssistCargoAndLoading;
        this.driverAssistRocket = driverAssistRocket;

        // Ensure that this command is the only one to run on the drive base
        requires(DriveBase.getInstance());

        setupNetworkTables();
    }

    public void setupTargetFlags(boolean driverAssistCargoAndLoading, boolean driverAssistRocket) {
        this.driverAssistCargoAndLoading = driverAssistCargoAndLoading;
        this.driverAssistRocket = driverAssistRocket;
    }

    private void setupNetworkTables() {
        // Set up network table
        inst = NetworkTableInstance.getDefault();
        table = inst.getTable("PathFinder");
        String serverName = new String("roboRIO-2706-FRC.local");
        int port = 1735;
        inst.startClient(serverName, port);

        trajGenerated = false;
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().setPositionNoGyroMode();

        trajGenerated = false;

        // Get angle and position of vision target computed by vision subsystem through network table
        NetworkTableEntry vectorCameraToTarget = table.getEntry("vectorCameraToTarget");

        double[] vectorCameraToTarget_Camera = vectorCameraToTarget.getDoubleArray(new double[] {0,0});
        double yawAngleCameraToTarget_Camera = vectorCameraToTarget_Camera[0];
        double distanceCameraToTarget_Camera = vectorCameraToTarget_Camera[1];
        Log.d("DAV: yawAngleCameraToTarget_Camera (deg): " + yawAngleCameraToTarget_Camera);
        Log.d("DAV: distanceCameraToTarget_Camera (ft): " + distanceCameraToTarget_Camera);
            
        if(distanceCameraToTarget_Camera <= 0.1) {
            Log.d("DAV: Vision data not available. Driver assist command rejected");
            trajGenerated = true;
            return;
        }

        // Compute the trajectory
        generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera,
                driverAssistCargoAndLoading, driverAssistRocket);

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
        DriveBase.getInstance().setDisabledMode();
    }

    private boolean driverAssistCargoAndLoading;
    private boolean driverAssistRocket;
    private NetworkTableInstance inst;
    private NetworkTable table;

    /**
     * Gets the network table instance
     * 
     * @return The network table instance
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

    public void generateTrajectoryRobotToTarget(double distanceCameraToTarget_Camera, double yawAngleCameraToTarget_Camera,
        boolean driverAssistCargoAndLoading, boolean driverAssistRocket) {

        Log.d("driverAssistCargoAndLoading: " + driverAssistCargoAndLoading);
        Log.d("driverAssistRocket: " + driverAssistRocket);

        // Don't do anything if driver assist is not requested
        if ((driverAssistCargoAndLoading == false) && (driverAssistRocket == false)) {
            Log.d("DAV: Driver assist not requested, no trajectory generated");
            return;
        }

        // VECTOR 1: Vector from robot to camera in robot frame
        double vecRobotToCameraX_Robot = Config.get_ROBOT_TO_CAMERA_X_ROBOT();
        double vecRobotToCameraY_Robot = Config.get_ROBOT_TO_CAMERA_Y_ROBOT();
        Log.d("DAV: vecRobotToCameraX_Robot: " + vecRobotToCameraX_Robot + ", vecRobotToCameraY_Robot: " + vecRobotToCameraY_Robot);

        // VECTOR 2: Vector from camera to target in robot frame
        double yawAngleCameraToTargetRad_Camera = Pathfinder.d2r(yawAngleCameraToTarget_Camera);
        double vecCameraToTargetX_Robot = distanceCameraToTarget_Camera * Math.sin(yawAngleCameraToTargetRad_Camera);
        double vecCameraToTargetY_Robot  = distanceCameraToTarget_Camera * Math.cos(yawAngleCameraToTargetRad_Camera);        
        Log.d("DAV: vecX_CameraToTarget_Robot: " + vecCameraToTargetX_Robot + ", vecCameraToTargetY_Robot: " + vecCameraToTargetY_Robot);

        // Get current heading of robot relative to field horizontal from IMU
        double currentRobotAngle_Field = DriveBase.getInstance().getAbsoluteHeading();
        Log.d("DAV: currentRobotAngle_Field: " + currentRobotAngle_Field);
        
        // Compute final robot angle relative to field based on current angle of robot relative to field.
        // Robot must be in an angular range such that it is approximately facing the desired target. 
        double finalRobotAngle_Field = 0.0;

        if (driverAssistCargoAndLoading == true) {
            // Assist requested for cargo ship or loading dock targets
            Log.d("DAV: Assist for cargo ship or loading dock requested");
            if ((currentRobotAngle_Field >= 0.0 && currentRobotAngle_Field <= 45.0) || (currentRobotAngle_Field >= 315.0 && currentRobotAngle_Field <= 360.0)) {
                finalRobotAngle_Field = 0.0;
            } else if (currentRobotAngle_Field >= 45.0 && currentRobotAngle_Field <= 135.0) {
                finalRobotAngle_Field = 90.0;
            } else if (currentRobotAngle_Field >= 135.0 && currentRobotAngle_Field <= 225.0) {
                finalRobotAngle_Field = 180.0;
            }   
        }
        else if (driverAssistRocket == true) {
            // Assist request for rocket ship targets
            Log.d("DAV: Assist for rocket requested");

            // Rockets on left
            if (currentRobotAngle_Field >= 90.0 && currentRobotAngle_Field <= 150.0) {
                finalRobotAngle_Field = 120.0;
            } else if (currentRobotAngle_Field >= 150.0 && currentRobotAngle_Field <= 210.0) {
                finalRobotAngle_Field = 180.0;
            } else if (currentRobotAngle_Field >= 210.0 && currentRobotAngle_Field <= 270.0) {
                finalRobotAngle_Field = 240.0;
            } 
            // Rockets on right
            else if (currentRobotAngle_Field >= 30.0 && currentRobotAngle_Field <= 90.0) {
                finalRobotAngle_Field = 60.0;
            } else if ((currentRobotAngle_Field >= 0.0 && currentRobotAngle_Field <= 30.0) || (currentRobotAngle_Field >= 330.0 && currentRobotAngle_Field <= 360.0)) {
                finalRobotAngle_Field = 0.0;
            } else if (currentRobotAngle_Field >= 270.0 && currentRobotAngle_Field <= 330.0) {
                finalRobotAngle_Field = 300.0;
            }
        }

        // Compute unit vector in direction facing target
        double finalRobotAngleRad_Field = Pathfinder.d2r(finalRobotAngle_Field); 
        double uvecFacingTargetX_Field = Math.cos(finalRobotAngleRad_Field);
        double uvecFacingTargetY_Field = Math.sin(finalRobotAngleRad_Field);
     
        // Compute vector from target to final robot position in field frame from unit vector in field frame
        double d = 0.5; // offset distance from target [feet] (hard coded)
        double vecTargetToFinalX_Field = -d * uvecFacingTargetX_Field;
        double vecTargetToFinalY_Field = -d * uvecFacingTargetY_Field;
        Log.d("DAV: vecX_TargetToFinal_Field: " + vecTargetToFinalX_Field + ", vecTargetToFinalY_Field: " + vecTargetToFinalY_Field);


        // VECTOR 3: Convert previous vector from field frame to robot frame
        double cosCurrentRobotAngle_Field = Math.cos(currentRobotAngle_Field);
        double sinCurrentRobotAngle_Field = Math.sin(currentRobotAngle_Field);
        double vecTargetToFinalX_Robot = vecTargetToFinalX_Field * cosCurrentRobotAngle_Field + vecTargetToFinalY_Field * sinCurrentRobotAngle_Field;
        double vecTargetToFinalY_Robot = -vecTargetToFinalX_Field * sinCurrentRobotAngle_Field + vecTargetToFinalY_Field * cosCurrentRobotAngle_Field;
        Log.d("DAV: vecTargetToFinalX_Robot: " + vecTargetToFinalX_Robot + ", vecTargetToFinalY_Robot: " + vecTargetToFinalY_Robot);

        // Vector from robot to final position obtained by adding Vector 1, 2, and 3
        double vecRobotToFinalX_Robot = vecRobotToCameraX_Robot + vecCameraToTargetX_Robot + vecTargetToFinalX_Robot;
        double vecRobotToFinalY_Robot = vecRobotToCameraY_Robot + vecCameraToTargetY_Robot + vecTargetToFinalY_Robot;
        Log.d("DAV: vecX_RobotToFinal_Robot: " + vecRobotToFinalX_Robot + ", vecRobotToFinalY_Robot: " + vecRobotToFinalY_Robot);

        // Compute final robot angle in robot frame
        double finalRobotAngle_Robot = finalRobotAngle_Field - currentRobotAngle_Field;
        Log.d("DAV: finalRobotAngle_Robot: " + finalRobotAngle_Robot);

        // Generate trajectory using PathFinder library
        Log.d("DAV: Generating trajectory");
        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 
            Config.TRAJ_DELTA_TIME.value(), Config.ROBOT_MAX_VEL.value(), Config.ROBOT_MAX_ACC.value(), Config.ROBOT_MAX_JERK.value());
        Waypoint[] points = new Waypoint[] {
                // Initial position/heading of robot (robot oriented along robot y axis so at 90 deg)
                new Waypoint(0, 0, Pathfinder.d2r(90)), 
                // Final position/heading in front of target
                new Waypoint(vecRobotToFinalX_Robot, vecRobotToFinalY_Robot, Pathfinder.d2r(finalRobotAngle_Robot)),
        };

        traj = Pathfinder.generate(points, config);

        // Send trajectory to motion control system
        // (Wait until integration with robot code)
        
        Log.d("DAV: Trajectory length: " + traj.length());
        for (int i = 0; i < traj.length(); i++)
        {
            String str = 
                traj.segments[i].x + "," +
                traj.segments[i].y + "," +
                traj.segments[i].heading;

            //System.out.println(str);
        }

    }

    public Trajectory getTraj() {
        return traj;
    }

    private Trajectory traj;

    private boolean trajGenerated = false;

}
