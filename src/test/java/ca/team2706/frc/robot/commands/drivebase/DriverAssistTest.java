package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;

import static org.junit.Assert.*;

public class DriverAssistTest {

    private final DriverAssist driverAssistWithVision = new DriverAssist();

    // Since these two classes are accessed statically (they are singletons), we have to mock 
    // all instances and stub out the static initializer

    @Mocked(stubOutClassInitialization = true)
    private DriveBase driveBase;

    @Mocked(stubOutClassInitialization = true)
    private Config config;

    /**
     * Tests that the trajectory is generated
     *
     */
    @Test
    public void generateTrajectory_01() {

        new Expectations() {{
            driveBase.getAbsoluteHeading();
            result = 40.0;

            Config.getTRAJ_DELTA_TIME(); 
            result = 0.05;

            Config.getROBOTTOCAMERA_ROBOTX(); 
            result = 0.8;

            Config.getROBOTTOCAMERA_ROBOTY();
            result = 0.9;

            Config.getTARGET_OFFSET_DISTANCE();
            result = 0.2;
        }};

        double distanceCameraToTarget_Camera = 6.0;
        double yawAngleCameraToTarget_Camera = 27.0;
        boolean driverAssistCargoAndLoading = false;
        boolean driverAssistRocket = true;

        driverAssistWithVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera,
            driverAssistCargoAndLoading, driverAssistRocket);

        Trajectory traj = driverAssistWithVision.getTraj();

        System.out.println("traj.length(): " + traj.length());
        System.out.println("traj.segments[0].x: " + traj.segments[0].x);
        System.out.println("traj.segments[0].y: " + traj.segments[0].y);
        System.out.println("traj.segments[0].heading: " + traj.segments[0].heading);
        System.out.println("traj.segments[traj.length()-1].x: " + traj.segments[traj.length()-1].x);
        System.out.println("traj.segments[traj.length()-1].y: " + traj.segments[traj.length()-1].y);
        System.out.println("traj.segments[traj.length()-1].heading: " + traj.segments[traj.length()-1].heading);
        assert(traj.length() > 1);
        assertEquals(traj.segments[0].x, 0.0, 0.3);
        assertEquals(traj.segments[0].y, 0.0, 0.3);
        assertEquals(traj.segments[0].heading, Pathfinder.d2r(90.0), 0.3);
        assertEquals(traj.segments[traj.length()-1].x, 3.592, 0.3);
        assertEquals(traj.segments[traj.length()-1].y, 6.058, 0.3);
        assertEquals(traj.segments[traj.length()-1].heading, 1.919, 0.3);
    }

    @Test
    public void generateTrajectory_02() {

        new Expectations() {{
            driveBase.getAbsoluteHeading();
            result = 180.0;

            Config.getTRAJ_DELTA_TIME(); 
            result = 0.5;

            Config.getROBOTTOCAMERA_ROBOTX(); 
            result = 1.0;

            Config.getROBOTTOCAMERA_ROBOTY();
            result = 1.0;

            Config.getTARGET_OFFSET_DISTANCE();
            result = 0.5;
        }};

        double distanceCameraToTarget_Camera = 2.0*1.414213562373095;
        double yawAngleCameraToTarget_Camera = -45.0;
        boolean driverAssistCargoAndLoading = true;
        boolean driverAssistRocket = false;

        driverAssistWithVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera,
            driverAssistCargoAndLoading, driverAssistRocket);

        Trajectory traj = driverAssistWithVision.getTraj();

        System.out.println("traj.length(): " + traj.length());
        System.out.println("traj.segments[0].x: " + traj.segments[0].x);
        System.out.println("traj.segments[0].y: " + traj.segments[0].y);
        System.out.println("traj.segments[0].heading: " + traj.segments[0].heading);
        System.out.println("traj.segments[traj.length()-1].x: " + traj.segments[traj.length()-1].x);
        System.out.println("traj.segments[traj.length()-1].y: " + traj.segments[traj.length()-1].y);
        System.out.println("traj.segments[traj.length()-1].heading: " + traj.segments[traj.length()-1].heading);
        assert(traj.length() > 1);
        assertEquals(traj.segments[0].x, 0.0, 0.3);
        assertEquals(traj.segments[0].y, 0.0, 0.3);
        assertEquals(traj.segments[0].heading, Pathfinder.d2r(90.0), 0.3);
        assertEquals(traj.segments[traj.length()-1].x, -1.0, 0.3);
        assertEquals(traj.segments[traj.length()-1].y, 2.5, 0.3);
        assertEquals(traj.segments[traj.length()-1].heading, Pathfinder.d2r(90.0), 0.3);
    }

    @Test
    public void generateTrajectory_03() {

        new Expectations() {{
            driveBase.getAbsoluteHeading();
            result = 180.0;

            Config.getTRAJ_DELTA_TIME(); 
            result = 0.5;

            Config.getROBOTTOCAMERA_ROBOTX(); 
            result = 0.0;

            Config.getROBOTTOCAMERA_ROBOTY();
            result = 0.0;

            Config.getTARGET_OFFSET_DISTANCE();
            result = 0.5;
        }};

        double distanceCameraToTarget_Camera = 2.0*1.414213562373095;
        double yawAngleCameraToTarget_Camera = 0.0;
        boolean driverAssistCargoAndLoading = true;
        boolean driverAssistRocket = false;

        driverAssistWithVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera,
            driverAssistCargoAndLoading, driverAssistRocket);
        
        Trajectory traj = driverAssistWithVision.getTraj();

        System.out.println("traj.length(): " + traj.length());
        System.out.println("traj.segments[0].x: " + traj.segments[0].x);
        System.out.println("traj.segments[0].y: " + traj.segments[0].y);
        System.out.println("traj.segments[0].heading: " + traj.segments[0].heading);
        System.out.println("traj.segments[traj.length()-1].x: " + traj.segments[traj.length()-1].x);
        System.out.println("traj.segments[traj.length()-1].y: " + traj.segments[traj.length()-1].y);
        System.out.println("traj.segments[traj.length()-1].heading: " + traj.segments[traj.length()-1].heading);
        assert(traj.length() > 1);
    }

    @Test
    public void generateTrajectory_04() {

        new Expectations() {{
            driveBase.getAbsoluteHeading();
            result = 180.0;

            Config.getTRAJ_DELTA_TIME(); 
            result = 0.05;

            Config.getROBOTTOCAMERA_ROBOTX(); 
            result = 1.0;

            Config.getROBOTTOCAMERA_ROBOTY();
            result = 1.0;

            Config.getTARGET_OFFSET_DISTANCE();
            result = 0.5;
        }};

        double distanceCameraToTarget_Camera = 1.414213562373095;
        double yawAngleCameraToTarget_Camera = -45.0;
        boolean driverAssistCargoAndLoading = true;
        boolean driverAssistRocket = false;

        driverAssistWithVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera,
            driverAssistCargoAndLoading, driverAssistRocket);

        Trajectory traj = driverAssistWithVision.getTraj();

        System.out.println("traj.length(): " + traj.length());
        System.out.println("traj.segments[0].x: " + traj.segments[0].x);
        System.out.println("traj.segments[0].y: " + traj.segments[0].y);
        System.out.println("traj.segments[0].heading: " + traj.segments[0].heading);
        System.out.println("traj.segments[traj.length()-1].x: " + traj.segments[traj.length()-1].x);
        System.out.println("traj.segments[traj.length()-1].y: " + traj.segments[traj.length()-1].y);
        System.out.println("traj.segments[traj.length()-1].heading: " + traj.segments[traj.length()-1].heading);
        assert(traj.length() > 1);
        assertEquals(traj.segments[0].x, 0.0, 0.3);
        assertEquals(traj.segments[0].y, 0.0, 0.3);
        assertEquals(traj.segments[0].heading, Pathfinder.d2r(90.0), 0.3);
        assertEquals(traj.segments[traj.length()-1].x, 0.0, 0.3);
        assertEquals(traj.segments[traj.length()-1].y, 1.5, 0.3);
        assertEquals(traj.segments[traj.length()-1].heading, Pathfinder.d2r(90.0), 0.3);
    }

    @Test
    public void generateTrajectory_06() {

        new Expectations() {{
            driveBase.getAbsoluteHeading();
            result = 180.0;

            Config.getTRAJ_DELTA_TIME(); 
            result = 0.1;

            Config.getROBOTTOCAMERA_ROBOTX(); 
            result = 1.0;

            Config.getROBOTTOCAMERA_ROBOTY();
            result = 1.0;

            Config.getTARGET_OFFSET_DISTANCE();
            result = 0.5;
        }};

        double distanceCameraToTarget_Camera = 1.414213562373095;
        double yawAngleCameraToTarget_Camera = -45.0;
        boolean driverAssistCargoAndLoading = true;
        boolean driverAssistRocket = false;

        driverAssistWithVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera,
            driverAssistCargoAndLoading, driverAssistRocket);

        Trajectory traj = driverAssistWithVision.getTraj();

        System.out.println("traj.length(): " + traj.length());
        System.out.println("traj.segments[0].x: " + traj.segments[0].x);
        System.out.println("traj.segments[0].y: " + traj.segments[0].y);
        System.out.println("traj.segments[0].heading: " + traj.segments[0].heading);
        System.out.println("traj.segments[traj.length()-1].x: " + traj.segments[traj.length()-1].x);
        System.out.println("traj.segments[traj.length()-1].y: " + traj.segments[traj.length()-1].y);
        System.out.println("traj.segments[traj.length()-1].heading: " + traj.segments[traj.length()-1].heading);
        assert(traj.length() > 1);
        assertEquals(traj.segments[0].x, 0.0, 0.3);
        assertEquals(traj.segments[0].y, 0.0, 0.3);
        assertEquals(traj.segments[0].heading, Pathfinder.d2r(90.0), 0.3);
        assertEquals(traj.segments[traj.length()-1].x, 0.0, 0.3);
        assertEquals(traj.segments[traj.length()-1].y, 1.5, 0.3);
        assertEquals(traj.segments[traj.length()-1].heading, Pathfinder.d2r(90.0), 0.3);
    }


}