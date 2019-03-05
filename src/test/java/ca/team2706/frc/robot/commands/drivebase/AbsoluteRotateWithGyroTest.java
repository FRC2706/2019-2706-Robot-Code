package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.SendablesTest;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.ControlMode;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbsoluteRotateWithGyroTest {

    @Tested
    private AbsoluteRotateWithGyro absoluteRotateWithGyro;

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
     * Tests that the command puts the drivetrain into the correct state
     *
     * @param speed         The speed to create the command with
     * @param position      The rotation to create the command with
     * @param minDoneCycles The minimum cycles to use
     */
    @Test
    public void testCorrectState(@Injectable("0.0") double speed, @Injectable("0.0") double position, @Injectable("1") int minDoneCycles) {
        assertEquals(DriveBase.DriveMode.Disabled, DriveBase.getInstance().getDriveMode());
        absoluteRotateWithGyro.initialize();
        assertEquals(DriveBase.DriveMode.Rotate, DriveBase.getInstance().getDriveMode());
        assertTrue(DriveBase.getInstance().isBrakeMode());

        absoluteRotateWithGyro.end();
        assertEquals(DriveBase.DriveMode.Disabled, DriveBase.getInstance().getDriveMode());
    }

    /**
     * Tests that the setpoint commands are called and speed is limited each tick
     *
     * @param speed         The speed to inject
     * @param position      The rotation to inject
     * @param minDoneCycles The min cycles to inject
     */
    @Test
    public void testSetting(@Injectable("0.0") double speed, @Injectable("30") double position, @Injectable("1") int minDoneCycles) {
        new Expectations() {{
            pigeon.getYawPitchRoll((double[]) any);
            // 300 positive
            result = SendablesTest.makePigeonExpectation(-3300.0);
        }};


        absoluteRotateWithGyro.initialize();

        absoluteRotateWithGyro.execute();
        absoluteRotateWithGyro.execute();
        absoluteRotateWithGyro.execute();

        absoluteRotateWithGyro.end();

        new Verifications() {{
            talon.set(ControlMode.Position, degreesToTicks(90));
            times = 3;
            talon.configClosedLoopPeakOutput(0, speed);
            times = 6;
        }};
    }

    /**
     * Tests that the setpoint commands are called and speed is limited each tick
     *
     * @param speed         The speed to inject
     * @param position      The rotation to inject
     * @param minDoneCycles The min cycles to inject
     */
    @Test
    public void testSettingMirror(@Injectable("0.0") double speed, @Injectable("330") double position, @Injectable("1") int minDoneCycles) {
        new Expectations() {{
            pigeon.getYawPitchRoll((double[]) any);
            // 60 positive
            result = SendablesTest.makePigeonExpectation(3300.0);
        }};

        absoluteRotateWithGyro.mirror();

        absoluteRotateWithGyro.initialize();

        absoluteRotateWithGyro.execute();
        absoluteRotateWithGyro.execute();
        absoluteRotateWithGyro.execute();

        absoluteRotateWithGyro.end();

        new Verifications() {{
            talon.set(ControlMode.Position, degreesToTicks(90));
            times = 3;
            talon.configClosedLoopPeakOutput(0, speed);
            times = 6;
        }};
    }

    private int degreesToTicks(double degrees) {
        return (int) (degrees / Config.PIGEON_DPP);
    }
}