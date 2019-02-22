package ca.team2706.frc.robot.sensors;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.OI;
import ca.team2706.frc.robot.subsystems.DriveBase;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class DriverAssistToTargetWithVision {
    
    public DriverAssistToTargetWithVision() {}
    
    public void startMonitoringNetworkTableVisionEntry() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        NetworkTable table = inst.getTable("PathFinder");
        String serverName = new String("10.27.6.100");
        int port = 1735;
        inst.startClient(serverName, port);

        table.addEntryListener("vectorCameraToTarget", (table1, key, entry, value, flags) -> {
            
            double[] vectorCameraToTarget_Camera = value.getDoubleArray();
            double yawAngleCameraToTarget_Camera = vectorCameraToTarget_Camera[0];
            double distanceCameraToTarget_Camera = vectorCameraToTarget_Camera[1];
            Log.d("DAV: vectorToTarget changed value");
            Log.d("  yawAngleCameraToTarget (deg): " + yawAngleCameraToTarget_Camera);
            Log.d("  distanceCameraToTarget (ft): " + distanceCameraToTarget_Camera);
            
            generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera);
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    }
    
    public void generateTrajectoryRobotToTarget(double distanceCameraToTarget_Camera, double yawAngleCameraToTarget_Camera) {

        // Get button inputs to determine if driver assist has been requested
        // Change hard code below to get input from buttons
        // boolean driverAssistCargoAndLoading = false;
        // boolean driverAssistRocket = true;
        boolean driverAssistCargoAndLoading = OI.getInstance().getButtonDriverAssistVisionCargoAndLoading();
        boolean driverAssistRocket = OI.getInstance().getButtonDriverAssistVisionRocket();
        System.out.println("driverAssistCargoAndLoading: " + driverAssistCargoAndLoading);
        System.out.println("driverAssistRocket: " + driverAssistRocket);

        // Don't do anything if driver assist is not requested
        if ((driverAssistCargoAndLoading == false) && (driverAssistRocket == false)) {
            Log.d("DAV: Driver assist not requested, no trajectory generated");
            return;
        }

        // VECTOR 1: Vector from robot to camera in robot frame
        double vecRobotToCameraX_Robot = Config.get_ROBOT_TO_CAMERA_X_ROBOT();
        double vecRobotToCameraY_Robot = Config.get_ROBOT_TO_CAMERA_Y_ROBOT();
        System.out.println("DAV: vecRobotToCameraX_Robot: " + vecRobotToCameraX_Robot + ", vecRobotToCameraY_Robot: " + vecRobotToCameraY_Robot);

        // VECTOR 2: Vector from camera to target in robot frame
        double yawAngleCameraToTargetRad_Camera = Pathfinder.d2r(yawAngleCameraToTarget_Camera);
        double vecCameraToTargetX_Robot = distanceCameraToTarget_Camera * Math.sin(yawAngleCameraToTargetRad_Camera);
        double vecCameraToTargetY_Robot  = distanceCameraToTarget_Camera * Math.cos(yawAngleCameraToTargetRad_Camera);        
        Log.d("DAV: vecX_CameraToTarget_Robot: " + vecCameraToTargetX_Robot + ", vecCameraToTargetY_Robot: " + vecCameraToTargetY_Robot);

        // Get current heading of robot relative to field horizontal from IMU
        double currentRobotAngle_Field = DriveBase.getInstance().getAbsoluteHeading();
        System.out.println("DAV: currentRobotAngle_Field: " + currentRobotAngle_Field);
        
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
        System.out.println("DAV: Generating trajectory");
        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 
            Config.TRAJ_DELTA_TIME.value(), Config.ROBOT_MAX_VEL.value(), Config.ROBOT_MAX_ACC.value(), Config.ROBOT_MAX_JERK.value());
        Waypoint[] points = new Waypoint[] {
                // Initial position/heading of robot (robot oriented along robot y axis so at 90 deg)
                new Waypoint(0, 0, Pathfinder.d2r(90)), 
                // Final position/heading in front of target
                new Waypoint(vecRobotToFinalX_Robot, vecRobotToFinalY_Robot, Pathfinder.d2r(finalRobotAngle_Robot)),
        };
        Trajectory traj = Pathfinder.generate(points, config);

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
    
}
