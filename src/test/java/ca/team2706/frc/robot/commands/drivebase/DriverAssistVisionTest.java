package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.SendablesTest;
import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DriverAssistVisionTest {
    @Tested
    private DriverAssistVision driverAssistVision;

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private PWM pwm;

    @Mocked
    private AnalogInput analogInput;

    @Mocked(stubOutClassInitialization = true)
    private PigeonIMU pigeon;

    private DifferentialDrive differentialDrive;

    @Mocked(stubOutClassInitialization = true)
    private CTREJNIWrapper jni;

    @Mocked(stubOutClassInitialization = true)
    private MotControllerJNI motControllerJNI;

    @Mocked
    private Notifier notifier;

    @Injectable
    private SensorCollection sensorCollection;

    @Before
    public void setUp() {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
        }};
    }

    /**
     * Runs various test for generating a trajectory to targets on the cargo ship or
     * the loading bay. For each test, the same vision input is used but the heading
     * of the robot in the field frame (referred to as "absolute heading" in the code)
     * is changed.
     */
    @Test
    public void testGenerateTrajectoryCargoShipAndLoading() {
        new Expectations() {{
            pigeon.getYawPitchRoll((double[]) any);
            returns(SendablesTest.makePigeonExpectation(-90.0),
                    SendablesTest.makePigeonExpectation(0.0),
                    SendablesTest.makePigeonExpectation(90.0),
                    SendablesTest.makePigeonExpectation(180.0));
        }};

        // Run the test
        double vRobotToTarget_CameraX = -5.0;
        double vRobotToTarget_CameraY = 7.0;
        double vCameraToTarget_CameraX = vRobotToTarget_CameraX - Config.ROBOTTOCAMERA_ROBOTX.value();
        double vCameraToTarget_CameraY = vRobotToTarget_CameraY - Config.ROBOTTOCAMERA_ROBOTY.value();

        double distanceCameraToTarget_Camera = Math.sqrt(Math.pow(vCameraToTarget_CameraX, 2) + Math.pow(vCameraToTarget_CameraY, 2));
        double yawAngleCameraToTarget_Camera = Math.toDegrees(Math.atan2(vCameraToTarget_CameraX, vCameraToTarget_CameraY));
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

            assertEquals(driverAssistVision.getAngRobotHeadingFinal_Field(), expectedAngRobotHeadingFinal_Field[i], 0.1);
            assertEquals(traj.segments[0].x, 0.0, 0.3);
            assertEquals(traj.segments[0].y, 0.0, 0.3);
            assertEquals(traj.segments[0].heading, Pathfinder.d2r(90.0), 0.3);
            assertEquals(vRobotToTarget_CameraX, traj.segments[traj.length() - 1].x, 0.3);
            assertEquals(vRobotToTarget_CameraY - Config.TARGET_OFFSET_DISTANCE.value(), traj.segments[traj.length() - 1].y, 0.3); // 6.5
            assertEquals(Pathfinder.d2r(90.0), traj.segments[traj.length() - 1].heading, 0.3);
        }
    }

    /**
     * Runs various test for generating a trajectory to targets on the rocket. For each test,
     * the same vision input is used but the heading of the robot in the field frame (referred
     * to as "absolute heading" in the code) is changed.
     */
    @Test
    public void testGenerateTrajectoryRocket() {
        new Expectations() {{
            pigeon.getYawPitchRoll((double[]) any);
            returns(SendablesTest.makePigeonExpectation(-30.0),
                    SendablesTest.makePigeonExpectation(-90.0),
                    SendablesTest.makePigeonExpectation(210.0),
                    SendablesTest.makePigeonExpectation(30.0),
                    SendablesTest.makePigeonExpectation(90.0),
                    SendablesTest.makePigeonExpectation(150.0));
        }};

        // Run the test
        double vRobotToTarget_CameraX = -5.0;
        double vRobotToTarget_CameraY = 7.0;
        double vCameraToTarget_CameraX = vRobotToTarget_CameraX - Config.ROBOTTOCAMERA_ROBOTX.value();
        double vCameraToTarget_CameraY = vRobotToTarget_CameraY - Config.ROBOTTOCAMERA_ROBOTY.value();

        double distanceCameraToTarget_Camera = Math.sqrt(Math.pow(vCameraToTarget_CameraX, 2) + Math.pow(vCameraToTarget_CameraY, 2));
        double yawAngleCameraToTarget_Camera = Math.toDegrees(Math.atan2(vCameraToTarget_CameraX, vCameraToTarget_CameraY));
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

            assertEquals(expectedAngRobotHeadingFinal_Field[i], driverAssistVision.getAngRobotHeadingFinal_Field(), 0.1);
            assertEquals(0D, traj.segments[0].x, 0.3);
            assertEquals(0D, traj.segments[0].y, 0.3);
            assertEquals(Pathfinder.d2r(90.0), traj.segments[0].heading, 0.3);
            assertEquals(vRobotToTarget_CameraX, traj.segments[traj.length() - 1].x, 0.3);
            assertEquals(vRobotToTarget_CameraY - Config.TARGET_OFFSET_DISTANCE.value(), traj.segments[traj.length() - 1].y, 0.3);
            assertEquals(Pathfinder.d2r(90.0), traj.segments[traj.length() - 1].heading, 0.3);
        }
    }
}