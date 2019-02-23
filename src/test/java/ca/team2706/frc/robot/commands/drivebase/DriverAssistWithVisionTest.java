package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.OI;
import ca.team2706.frc.robot.subsystems.DriveBase;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class DriverAssistWithVisionTest {

    private final DriverAssistWithVision driverAssistWithVision = new DriverAssistWithVision();

    // Since these two classes are accessed statically (they are singletons), we have to mock 
    // all instances and stub out the static initializer

    @Mocked(stubOutClassInitialization = true)
    private DriveBase driveBase;

    @Mocked(stubOutClassInitialization = true)
    private Config config;

/**
     * Tests that the trajectory is generated
     *
     * @throws NoSuchMethodException     In case the shutdown method can't be found
     * @throws InvocationTargetException In case the shutdown method can't be invoked
     * @throws IllegalAccessException    In case the reflection is illegal
     */
    @Test
    public void generateTrajectory() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        new Expectations() {{
            driveBase.getAbsoluteHeading();
            result = 40.0;

            Config.get_ROBOT_TO_CAMERA_X_ROBOT(); 
            result = 0.8;

            Config.get_ROBOT_TO_CAMERA_Y_ROBOT();
            result = 0.9;
        }};

        double distanceCameraToTarget_Camera = 6.0;
        double yawAngleCameraToTarget_Camera = 27.0;
        boolean driverAssistCargoAndLoading = false;
        boolean driverAssistRocket = true;

        driverAssistWithVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera,
            driverAssistCargoAndLoading, driverAssistRocket);

        Trajectory traj = driverAssistWithVision.getTraj();

        assert(traj.length() > 1);
        assertEquals(traj.segments[0].x, 0.0, 2);
        assertEquals(traj.segments[0].y, 0.0, 2);
        assertEquals(traj.segments[0].heading, Pathfinder.d2r(90.0), 2);
        assertEquals(traj.segments[traj.length()-1].x, 3.368, 2);
        assertEquals(traj.segments[traj.length()-1].y, 6.721, 2);
        assertEquals(traj.segments[traj.length()-1].heading, 0.349, 2);
    }

}