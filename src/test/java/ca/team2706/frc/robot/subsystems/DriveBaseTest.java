package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.SendablesTest;
import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motion.BufferedTrajectoryPointStream;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DriveBaseTest {

    @Tested
    private DriveBase driveBase;

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

    @Mocked
    private BufferedTrajectoryPointStream bufferedTrajectoryPointStream;

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
     * Ensures that the control mode for the drivebase is correct
     */
    @Test
    public void testControlMode() {
        assertEquals(DriveBase.DriveMode.Disabled, driveBase.getDriveMode());
        driveBase.tankDrive(0, 0, false);
        assertEquals(DriveBase.DriveMode.OpenLoopVoltage, driveBase.getDriveMode());

        driveBase.arcadeDrive(0, 0, false);
        driveBase.curvatureDrive(0, 0, false);

        new Verifications() {{
            talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
            talon.follow((IMotorController) any);
            times = 8;
        }};

        driveBase.setPositionNoGyro(0, 0);

        new Verifications() {{
            talon.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, 0, anyInt);
        }};
    }

    /**
     * Tests that the absolute gyro heading gets saved
     */
    @Test
    public void testAbsoluteGyro() {
        new Expectations() {{
            pigeon.getYawPitchRoll((double[]) any);
            returns(SendablesTest.makePigeonExpectation(0.0),
                    SendablesTest.makePigeonExpectation(19.0),
                    SendablesTest.makePigeonExpectation(-12.0),
                    SendablesTest.makePigeonExpectation(-12.0),
                    SendablesTest.makePigeonExpectation(0.0),
                    SendablesTest.makePigeonExpectation(90.0),
                    SendablesTest.makePigeonExpectation(34.0),
                    SendablesTest.makePigeonExpectation(34.0),
                    SendablesTest.makePigeonExpectation(0.0));
        }};

        driveBase.resetAbsoluteGyro();

        double absoluteHeadingOffset = Config.ROBOT_START_ANGLE.value();
        assertEquals(absoluteHeadingOffset + 0.0, driveBase.getAbsoluteHeading(), 0.0);
        assertEquals(absoluteHeadingOffset + 19.0, driveBase.getAbsoluteHeading(), 0.0);
        assertEquals(absoluteHeadingOffset - 12.0, driveBase.getAbsoluteHeading(), 0.0);

        driveBase.reset(); // Note: reset() calls getAbsoluteHeading() 
        absoluteHeadingOffset = absoluteHeadingOffset - 12.0;

        assertEquals(absoluteHeadingOffset + 0.0, driveBase.getAbsoluteHeading(), 0.0);
        assertEquals(absoluteHeadingOffset + 90.0, driveBase.getAbsoluteHeading(), 0.0);
        assertEquals(absoluteHeadingOffset + 34.0, driveBase.getAbsoluteHeading(), 0.0);

        driveBase.reset(); // Note: reset() calls getAbsoluteHeading()
        absoluteHeadingOffset = absoluteHeadingOffset + 34.0;

        assertEquals(absoluteHeadingOffset + 0.0, driveBase.getAbsoluteHeading(), 0.0);
    }

    @Test
    public void testPushMotionProfilePositive() {
        driveBase.pushMotionProfile1Wheel(true, new double[]{1.0, -1.0, 5.0, 0.1}, new double[]{3.0, 8.6, 42.3, -2.54}, new double[] {0.0, 0.0, 0.0, 0.0}, new double[]{35.32, 245.53, -53.53, 553.0}, new int[]{5, 5, 5, 5}, 4);

        final TrajectoryPoint[] expected = {
                trajectory(1.0 / Config.DRIVE_ENCODER_DPP, 3.0 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 35.32, 35.32 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, true, 5, true),
                trajectory(-1.0 / Config.DRIVE_ENCODER_DPP, 8.6 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 245.53, 245.53 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, false, 5, true),
                trajectory(5.0 / Config.DRIVE_ENCODER_DPP, 42.3 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, -53.53, -53.53 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, false, 5, true),
                trajectory(0.1 / Config.DRIVE_ENCODER_DPP, -2.54 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 553.0, 553.0 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, true, false, 5, true),
        };

        new Verifications() {{
            TrajectoryPoint[] trajectories;
            bufferedTrajectoryPointStream.Write(trajectories = withCapture());

            assertTrajectoriesEqual(trajectories, expected);
        }};
    }

    @Test
    public void testPushMotionProfileNegative() {
        driveBase.pushMotionProfile1Wheel(false, new double[]{1.0, -1.0, 5.0, 0.1}, new double[]{3.0, 8.6, 42.3, -2.54}, new double[] {0.0, 0.0, 0.0, 0.0}, new double[]{35.32, 245.53, -53.53, 553.0}, new int[]{5, 5, 5, 5}, 4);

        final TrajectoryPoint[] expected = {
                trajectory(-1.0 / Config.DRIVE_ENCODER_DPP, -3.0 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 35.32, 35.32 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, true, 5, true),
                trajectory(1.0 / Config.DRIVE_ENCODER_DPP, -8.6 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 245.53, 245.53 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, false, 5, true),
                trajectory(-5.0 / Config.DRIVE_ENCODER_DPP, -42.3 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, -53.53, -53.53 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, false, 5, true),
                trajectory(-0.1 / Config.DRIVE_ENCODER_DPP, 2.54 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 553.0, 553.0 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, true, false, 5, true),
        };

        new Verifications() {{
            TrajectoryPoint[] trajectories;
            bufferedTrajectoryPointStream.Write(trajectories = withCapture());

            assertTrajectoriesEqual(trajectories, expected);
        }};
    }

    @Test
    public void testPushMotionProfile2WheelPositive() {
        driveBase.pushMotionProfile2Wheel(true, new double[]{1.0, -1.0, 5.0, 0.1}, new double[]{3.0, 8.6, 42.3, -2.54}, new double[] {0.0, 0.0, 0.0, 0.0}, new double[]{35.32, 245.53, -53.53, 553.0}, new int[]{5, 5, 5, 5}, 4, new double[]{-1.88, 11.14, 20.56, 12.41}, new double[]{16.78, -1.48, 1.18, 27.67}, new double[] {0.0, 0.0, 0.0, 0.0});

        final TrajectoryPoint[] expected1 = {
                trajectory(1.0 / Config.DRIVE_ENCODER_DPP, 3.0 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 35.32, 35.32 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, true, 5, true),
                trajectory(-1.0 / Config.DRIVE_ENCODER_DPP, 8.6 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 245.53, 245.53 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, false, 5, true),
                trajectory(5.0 / Config.DRIVE_ENCODER_DPP, 42.3 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, -53.53, -53.53 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, false, 5, true),
                trajectory(0.1 / Config.DRIVE_ENCODER_DPP, -2.54 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 553.0, 553.0 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, true, false, 5, true),
        };

        final TrajectoryPoint[] expected2 = {
                trajectory(-1.88 / Config.DRIVE_ENCODER_DPP, 16.78 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 35.32, 35.32 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, true, 5, true),
                trajectory(11.14 / Config.DRIVE_ENCODER_DPP, -1.48 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 245.53, 245.53 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, false, 5, true),
                trajectory(20.56 / Config.DRIVE_ENCODER_DPP, 1.18 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, -53.53, -53.53 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, false, 5, true),
                trajectory(12.41 / Config.DRIVE_ENCODER_DPP, 27.67 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 553.0, 553.0 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, true, false, 5, true),
        };

        new Verifications() {{
            List<TrajectoryPoint[]> trajectories = new ArrayList<>();
            bufferedTrajectoryPointStream.Write(withCapture(trajectories));

            assertTrajectoriesEqual(trajectories.get(0), expected1);
            assertTrajectoriesEqual(trajectories.get(1), expected2);
        }};
    }

    @Test
    public void testPushMotionProfile2WheelNegative() {
        driveBase.pushMotionProfile2Wheel(false, new double[]{1.0, -1.0, 5.0, 0.1}, new double[]{3.0, 8.6, 42.3, -2.54}, new double[] {0.0, 0.0, 0.0, 0.0}, new double[]{35.32, 245.53, -53.53, 553.0}, new int[]{5, 5, 5, 5}, 4, new double[]{-1.88, 11.14, 20.56, 12.41}, new double[]{16.78, -1.48, 1.18, 27.67}, new double[] {0.0, 0.0, 0.0, 0.0});

        final TrajectoryPoint[] expected1 = {
                trajectory(-1.0 / Config.DRIVE_ENCODER_DPP, -3.0 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 35.32, 35.32 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, true, 5, true),
                trajectory(1.0 / Config.DRIVE_ENCODER_DPP, -8.6 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 245.53, 245.53 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, false, 5, true),
                trajectory(-5.0 / Config.DRIVE_ENCODER_DPP, -42.3 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, -53.53, -53.53 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, false, 5, true),
                trajectory(-0.1 / Config.DRIVE_ENCODER_DPP, 2.54 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 553.0, 553.0 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, true, false, 5, true),
        };

        final TrajectoryPoint[] expected2 = {
                trajectory(1.88 / Config.DRIVE_ENCODER_DPP, -16.78 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 35.32, 35.32 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, true, 5, true),
                trajectory(-11.14 / Config.DRIVE_ENCODER_DPP, 1.48 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 245.53, 245.53 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, false, 5, true),
                trajectory(-20.56 / Config.DRIVE_ENCODER_DPP, -1.18 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, -53.53, -53.53 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, false, false, 5, true),
                trajectory(-12.41 / Config.DRIVE_ENCODER_DPP, -27.67 / Config.DRIVE_ENCODER_DPP / 10.0, 0.0, 553.0, 553.0 / Config.PIGEON_DPP, 0.0, 0.0, 0, 1, true, false, 5, true),
        };

        new Verifications() {{
            List<TrajectoryPoint[]> trajectories = new ArrayList<>();
            bufferedTrajectoryPointStream.Write(withCapture(trajectories));

            assertTrajectoriesEqual(trajectories.get(0), expected1);
            assertTrajectoriesEqual(trajectories.get(1), expected2);
        }};
    }

    private void assertTrajectoriesEqual(TrajectoryPoint[] a, TrajectoryPoint[] b) {
        assertEquals(b.length, a.length);

        for (int i = 0; i < a.length; i++) {
            assertTrajectoryPointsEqual(a[i], b[i]);
        }
    }

    private void assertTrajectoryPointsEqual(TrajectoryPoint a, TrajectoryPoint b) {
        assertEquals(b.position, a.position, 0.05);
        assertEquals(b.velocity, a.velocity, 0.05);
        assertEquals(b.arbFeedFwd, a.arbFeedFwd, 0.05);
        assertEquals(b.headingDeg, a.headingDeg, 0.05);
        assertEquals(b.auxiliaryPos, a.auxiliaryPos, 0.05);
        assertEquals(b.auxiliaryVel, a.auxiliaryVel, 0.05);
        assertEquals(b.auxiliaryArbFeedFwd, a.auxiliaryArbFeedFwd, 0.05);
        assertEquals(b.profileSlotSelect0, a.profileSlotSelect0);
        assertEquals(b.profileSlotSelect1, a.profileSlotSelect1);
        assertEquals(b.isLastPoint, a.isLastPoint);
        assertEquals(b.zeroPos, a.zeroPos);
        assertEquals(b.timeDur, a.timeDur);
        assertEquals(b.useAuxPID, a.useAuxPID);
    }

    private TrajectoryPoint trajectory(double position, double velocity, double arbFeedFwd, double headingDeg, double auxiliaryPos, double auxiliaryVel, double auxiliaryArbFeedFwd, int profileSlotSelect0, int profileSlotSelect1, boolean isLastPoint, boolean zeroPos, int timeDur, boolean useAuxPID) {
        TrajectoryPoint trajectoryPoint = new TrajectoryPoint();
        trajectoryPoint.position = position;
        trajectoryPoint.velocity = velocity;
        trajectoryPoint.arbFeedFwd = arbFeedFwd;
        trajectoryPoint.headingDeg = headingDeg;
        trajectoryPoint.auxiliaryPos = auxiliaryPos;
        trajectoryPoint.auxiliaryVel = auxiliaryVel;
        trajectoryPoint.auxiliaryArbFeedFwd = auxiliaryArbFeedFwd;
        trajectoryPoint.profileSlotSelect0 = profileSlotSelect0;
        trajectoryPoint.profileSlotSelect1 = profileSlotSelect1;
        trajectoryPoint.isLastPoint = isLastPoint;
        trajectoryPoint.zeroPos = zeroPos;
        trajectoryPoint.timeDur = timeDur;
        trajectoryPoint.useAuxPID = useAuxPID;

        return trajectoryPoint;
    }
}