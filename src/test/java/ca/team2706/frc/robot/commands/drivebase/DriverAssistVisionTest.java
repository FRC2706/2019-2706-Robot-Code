package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.SendablesTest;
import ca.team2706.frc.robot.commands.drivebase.DriverAssistVision.DriverAssistVisionTarget;
import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import jaci.pathfinder.Trajectory;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;
import util.Util;

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

    @Mocked(stubOutClassInitialization = true)
    private BuffTrajPointStreamJNI jni2;

    @Injectable
    private SensorCollection sensorCollection;

    @Mocked
    private Relay relay;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
            minTimes = 0;
        }};

        Util.resetSubsystems();
    }

    /**
     * Runs various test for generating a trajectory to targets on the cargo ship or
     * the loading bay. For each test, the same vision input is used but the heading
     * of the robot in the field frame (referred to as "absolute heading" in the code)
     * is changed.
     */
    @Test
    public void testGenerateTrajectoryCargoShipAndLoading(@Injectable("CARGO_AND_LOADING") DriverAssistVisionTarget target) {
        new Expectations() {{
            pigeon.getYawPitchRoll((double[]) any);
            returns(SendablesTest.makePigeonExpectation(-90.0),
                    SendablesTest.makePigeonExpectation(-90.0),
                    SendablesTest.makePigeonExpectation(-90.0),
                    SendablesTest.makePigeonExpectation(0.0),
                    SendablesTest.makePigeonExpectation(90.0),
                    SendablesTest.makePigeonExpectation(180.0)
            );
        }};

        // Run the test
        double vRobotToTarget_CameraX = -5.0;
        double vRobotToTarget_CameraY = 7.0;
        double vCameraToTarget_CameraX = vRobotToTarget_CameraX - Config.ROBOTTOCAMERA_ROBOTX.value();
        double vCameraToTarget_CameraY = vRobotToTarget_CameraY - Config.ROBOTTOCAMERA_ROBOTY.value();
        double distanceCameraToTarget_Camera = Math.sqrt(Math.pow(vCameraToTarget_CameraX, 2) + Math.pow(vCameraToTarget_CameraY, 2));
        double yawAngleCameraToTarget_Camera = Math.toDegrees(Math.atan2(vCameraToTarget_CameraX, vCameraToTarget_CameraY));

        // Verify the values produced with expected values. Note that the generated trajectory
        // is compared against the expected trajectory by comparing the expected x, y, and heading
        // against those of the generated trajectory for the first and last segments.
        double[] expectedAngRobotHeadingFinal_Field = {0.0, 90.0, 180.0, 270.0};
        double offsetDistance_Robot = Config.ROBOT_HALF_LENGTH.value() + Config.TARGET_OFFSET_DISTANCE_CARGO_AND_LOADING.value();
        for (int i = 0; i < 4; i++) {
            driverAssistVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera);
            Trajectory traj = driverAssistVision.getTrajectory();

            assertEquals(driverAssistVision.getAngRobotHeadingFinal_Field(), expectedAngRobotHeadingFinal_Field[i], 0.1);
            assertEquals(traj.segments[0].x, 0.0, 0.3);
            assertEquals(traj.segments[0].y, 0.0, 0.3);
            assertEquals(traj.segments[0].heading, 0.0, 0.3);
            assertEquals(traj.segments[traj.length() - 1].x, vRobotToTarget_CameraX, 0.3);
            assertEquals(traj.segments[traj.length() - 1].y, vRobotToTarget_CameraY - offsetDistance_Robot, 0.3); // 6.5
            assertEquals(traj.segments[traj.length() - 1].heading, 0.0, 0.3);
        }
    }

    /**
     * Runs various test for generating a trajectory to targets on the rocket. For each test,
     * the same vision input is used but the heading of the robot in the field frame (referred
     * to as "absolute heading" in the code) is changed.
     */
    @Test
    public void testGenerateTrajectoryRocket(@Injectable("ROCKET") DriverAssistVisionTarget target) {
        new Expectations() {{
            pigeon.getYawPitchRoll((double[]) any);
            returns(SendablesTest.makePigeonExpectation(-30.0),
                    SendablesTest.makePigeonExpectation(-30.0),
                    SendablesTest.makePigeonExpectation(-30.0),
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

        // Verify the values produced with expected values. Note that the generated trajectory
        // is compared against the expected trajectory by comparing the expected x, y, and heading
        // against those of the generated trajectory for the first and last segments.
        double[] expectedAngRobotHeadingFinal_Field = {60.0, 0.0, 300.0, 120.0, 180.0, 240.0};
        double offsetDistance_Robot = Config.ROBOT_HALF_LENGTH.value() + Config.TARGET_OFFSET_DISTANCE_ROCKET.value();
        for (int i = 0; i < 6; i++) {
            driverAssistVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera);
            Trajectory traj = driverAssistVision.getTrajectory();

            assertEquals(driverAssistVision.getAngRobotHeadingFinal_Field(), expectedAngRobotHeadingFinal_Field[i], 0.1);
            assertEquals(traj.segments[0].x, 0.0, 0.3);
            assertEquals(traj.segments[0].y, 0.0, 0.3);
            assertEquals(traj.segments[0].heading, 0.0, 0.3);
            assertEquals(traj.segments[traj.length() - 1].x, vRobotToTarget_CameraX, 0.3);
            assertEquals(traj.segments[traj.length() - 1].y, vRobotToTarget_CameraY - offsetDistance_Robot, 0.3);
            assertEquals(traj.segments[traj.length() - 1].heading, 0.0, 0.3);
        }
    }

    /**
     * Runs various test for generating a trajectory to ball targets. For each test,
     * the same vision input is used but the heading of the robot in the field frame (referred
     * to as "absolute heading" in the code) is changed.
     */
    @Test
    public void testGenerateTrajectoryBall(@Injectable("BALL") DriverAssistVisionTarget target) {
        // Run the test
        double vRobotToTarget_CameraX = -5.0;
        double vRobotToTarget_CameraY = 7.0;
        double vRobotToTarget_RobotX = vRobotToTarget_CameraX;
        double vRobotToTarget_RobotY = vRobotToTarget_CameraY;
        double vCameraToTarget_CameraX = vRobotToTarget_CameraX - Config.ROBOTTOCAMERA_ROBOTX.value();
        double vCameraToTarget_CameraY = vRobotToTarget_CameraY - Config.ROBOTTOCAMERA_ROBOTY.value();
        double distanceCameraToTarget_Camera = Math.sqrt(Math.pow(vCameraToTarget_CameraX, 2) + Math.pow(vCameraToTarget_CameraY, 2));
        double yawAngleCameraToTarget_Camera = Math.toDegrees(Math.atan2(vCameraToTarget_CameraX, vCameraToTarget_CameraY));

        // Verify the values produced with expected values. Note that the generated trajectory
        // is compared against the expected trajectory by comparing the expected x, y, and heading
        // against those of the generated trajectory for the first and last segments.
        double PI_OVER_2 = Math.PI / 2.0;
        double angRobotHeadingFinalExpectedRad_Robot = Math.atan2(vRobotToTarget_RobotY, vRobotToTarget_RobotX);
        double angRobotHeadingFinalExpectedRad_Motion = PI_OVER_2 - angRobotHeadingFinalExpectedRad_Robot;
        double mag = Math.sqrt(Math.pow(vRobotToTarget_RobotX, 2.0) + Math.pow(vRobotToTarget_RobotY, 2.0));
        double vUnitRobotToTarget_RobotX = vRobotToTarget_RobotX / mag;
        double vUnitRobotToTarget_RobotY = vRobotToTarget_RobotY / mag;
        double d = Config.ROBOT_HALF_LENGTH.value() + Config.TARGET_OFFSET_DISTANCE_BALL.value();
        double vRobotToFinalExpected_RobotX = vRobotToTarget_RobotX - d * vUnitRobotToTarget_RobotX;
        double vRobotToFinalExpected_RobotY = vRobotToTarget_RobotY - d * vUnitRobotToTarget_RobotY;

        driverAssistVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera);
        Trajectory traj = driverAssistVision.getTrajectory();
        assertEquals(traj.segments[0].x, 0.0, 0.3);
        assertEquals(traj.segments[0].y, 0.0, 0.3);
        assertEquals(traj.segments[0].heading, 0.0, 0.3);
        assertEquals(traj.segments[traj.length() - 1].x, vRobotToFinalExpected_RobotX, 0.3);
        assertEquals(traj.segments[traj.length() - 1].y, vRobotToFinalExpected_RobotY, 0.3);
        assertEquals(traj.segments[traj.length() - 1].heading, angRobotHeadingFinalExpectedRad_Motion, 0.3);
    }
}