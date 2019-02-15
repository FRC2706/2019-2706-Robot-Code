package ca.team2706.frc.robot.subsystems;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

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
            // TODO: Change when gyro method changes
            pigeon.getFusedHeading();
            returns(0.0, 19.0, -12.0, -12.0, 0.0, 90.0, 34.0, 34.0, 0.0);
        }};

        assertEquals(0.0, driveBase.getAbsoluteHeading(), 0.0);
        assertEquals(19.0, driveBase.getAbsoluteHeading(), 0.0);
        assertEquals(-12.0, driveBase.getAbsoluteHeading(), 0.0);

        driveBase.reset();

        assertEquals(-12.0, driveBase.getAbsoluteHeading(), 0.0);
        assertEquals(78.0, driveBase.getAbsoluteHeading(), 0.0);
        assertEquals(22.0, driveBase.getAbsoluteHeading(), 0.0);

        driveBase.reset();

        assertEquals(22.0, driveBase.getAbsoluteHeading(), 0.0);
    }
}