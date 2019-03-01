package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;

import static org.junit.Assert.*;

public class DriverAssistVisionTest {

    private final DriverAssistVision driverAssistVision = new DriverAssistVision();

    // Since these two classes are accessed statically (they are singletons), we have to mock 
    // all instances and stub out the static initializer

    @Mocked(stubOutClassInitialization = true)
    private DriveBase driveBase;

    @Mocked(stubOutClassInitialization = true)
    private Config config;

    /**
     * Runs various test for generating a trajectory to targets on the cargo ship or 
     * the loading bay. For each test, the same vision input is used but the heading
     * of the robot in the field frame (referred to as "absolute heading" in the code)
     * is changed.
     */
    @Test
    public void testGenerateTrajectoryCargoShipAndLoading() {

        // Set up values that the mocked methods return
        new Expectations() {{
            driveBase.getAbsoluteHeading();
            returns(0.0, 90.0, 180.0, 270.0);

            Config.getTRAJ_DELTA_TIME();
            result = 0.2;

            Config.getROBOTTOCAMERA_ROBOTX(); 
            result = 1.0;

            Config.getROBOTTOCAMERA_ROBOTY();
            result = 1.0;

            Config.getTARGET_OFFSET_DISTANCE();
            result = 0.5;
        }};

        // Run the test
        double distanceCameraToTarget_Camera = 6.0*1.414213562373095;  //2.0
        double yawAngleCameraToTarget_Camera = -45.0;
        boolean driverAssistCargoAndLoading = true;
        boolean driverAssistRocket = false;

        

        // Verify the values produced with expected values. Note that the generated trajectory
        // is compared against the expected trajectory by comparing the expected x, y, and heading
        // against those of the generated trajectory for the first and last segments.
        double[] expectedAngRobotHeadingFinal_Field = {0.0, 90.0, 180.0, 270.0};
        for (int i = 0; i < 4; i++) {
            driverAssistVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera,
                driverAssistCargoAndLoading, driverAssistRocket);
            Trajectory traj = driverAssistVision.getTraj();

            System.out.println("angRobotHeadingFinal_Field(): " + driverAssistVision.getAngRobotHeadingFinal_Field());
            System.out.println("traj.length(): " + traj.length());
            System.out.println("traj.segments[0].x: " + traj.segments[0].x);
            System.out.println("traj.segments[0].y: " + traj.segments[0].y);
            System.out.println("traj.segments[0].heading: " + traj.segments[0].heading);
            System.out.println("traj.segments[traj.length()-1].x: " + traj.segments[traj.length()-1].x);
            System.out.println("traj.segments[traj.length()-1].y: " + traj.segments[traj.length()-1].y);
            System.out.println("traj.segments[traj.length()-1].heading: " + traj.segments[traj.length()-1].heading);
            assertEquals(driverAssistVision.getAngRobotHeadingFinal_Field(), expectedAngRobotHeadingFinal_Field[i], 0.1);
            assertEquals(traj.segments[0].y, 0.0, 0.3);
            assertEquals(traj.segments[0].heading, Pathfinder.d2r(90.0), 0.3);
            assertEquals(traj.segments[traj.length()-1].x, -5.0, 0.3);
            assertEquals(traj.segments[traj.length()-1].y, 6.5, 0.3);
            assertEquals(traj.segments[traj.length()-1].heading, Pathfinder.d2r(90.0), 0.3);
        }
    }

    /**
     * Runs various test for generating a trajectory to targets on the rocket. For each test, 
     * the same vision input is used but the heading of the robot in the field frame (referred 
     * to as "absolute heading" in the code) is changed.
     */
    @Test
    public void testGenerateTrajectoryRocket() {

        // Set up values that the mocked methods return
        new Expectations() {{
            driveBase.getAbsoluteHeading();
            returns(60.0, 0.0, 300.0, 120.0, 180.0, 240.0);

            Config.getTRAJ_DELTA_TIME();
            result = 0.2;

            Config.getROBOTTOCAMERA_ROBOTX(); 
            result = 1.0;

            Config.getROBOTTOCAMERA_ROBOTY();
            result = 1.0;

            Config.getTARGET_OFFSET_DISTANCE();
            result = 0.5;
        }};

        // Run the test
        double distanceCameraToTarget_Camera = 6.0*1.414213562373095;
        double yawAngleCameraToTarget_Camera = -45.0;
        boolean driverAssistCargoAndLoading = false;
        boolean driverAssistRocket = true;

        // Verify the values produced with expected values. Note that the generated trajectory
        // is compared against the expected trajectory by comparing the expected x, y, and heading
        // against those of the generated trajectory for the first and last segments.
        double[] expectedAngRobotHeadingFinal_Field = {60.0, 0.0, 300.0, 120.0, 180.0, 240.0};
        for (int i = 0; i < 6; i++) {
            driverAssistVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera,
                driverAssistCargoAndLoading, driverAssistRocket);
            Trajectory traj = driverAssistVision.getTraj();

            System.out.println("angRobotHeadingFinal_Field(): " + driverAssistVision.getAngRobotHeadingFinal_Field());
            System.out.println("traj.length(): " + traj.length());
            System.out.println("traj.segments[0].x: " + traj.segments[0].x);
            System.out.println("traj.segments[0].y: " + traj.segments[0].y);
            System.out.println("traj.segments[0].heading: " + traj.segments[0].heading);
            System.out.println("traj.segments[traj.length()-1].x: " + traj.segments[traj.length()-1].x);
            System.out.println("traj.segments[traj.length()-1].y: " + traj.segments[traj.length()-1].y);
            System.out.println("traj.segments[traj.length()-1].heading: " + traj.segments[traj.length()-1].heading);
            assertEquals(driverAssistVision.getAngRobotHeadingFinal_Field(), expectedAngRobotHeadingFinal_Field[i], 0.1);
            assertEquals(traj.segments[0].y, 0.0, 0.3);
            assertEquals(traj.segments[0].heading, Pathfinder.d2r(90.0), 0.3);
            assertEquals(traj.segments[traj.length()-1].x, -5.0, 0.3);
            assertEquals(traj.segments[traj.length()-1].y, 6.5, 0.3);
            assertEquals(traj.segments[traj.length()-1].heading, Pathfinder.d2r(90.0), 0.3);
        }
    }
}