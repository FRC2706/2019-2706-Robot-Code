package ca.team2706.frc.robot.commands.drivebase;

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
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RotateWithGyroTest {

    @Tested
    private RotateWithGyro rotateWithGyro;

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
        rotateWithGyro.initialize();
        assertEquals(DriveBase.DriveMode.Rotate, DriveBase.getInstance().getDriveMode());
        assertTrue(DriveBase.getInstance().isBrakeMode());

        rotateWithGyro.end();
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
    public void testSetting(@Injectable("0.0") double speed, @Injectable("5") double position, @Injectable("1") int minDoneCycles) {
        rotateWithGyro.initialize();

        rotateWithGyro.execute();
        rotateWithGyro.execute();
        rotateWithGyro.execute();

        rotateWithGyro.end();

        new Verifications() {{
            talon.set(ControlMode.Position, degreesToTicks(5));
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
    public void testSettingMirror(@Injectable("0.0") double speed, @Injectable("5") double position, @Injectable("1") int minDoneCycles) {
        rotateWithGyro.mirror();

        rotateWithGyro.initialize();

        rotateWithGyro.execute();
        rotateWithGyro.execute();
        rotateWithGyro.execute();

        rotateWithGyro.end();

        new Verifications() {{
            talon.set(ControlMode.Position, -degreesToTicks(5));
            times = 3;
            talon.configClosedLoopPeakOutput(0, speed);
            times = 6;
        }};
    }

    /**
     * Tests that the command finishes in the right conditions
     *
     * @param speed         The speed to inject
     * @param position      The rotation to inject
     * @param minDoneCycles The min cycles to inject
     */
    @Test
    public void testFinished(@Injectable("0.0") double speed, @Injectable("180") double position, @Injectable("3") int minDoneCycles) {
        new Expectations() {{
            talon.getClosedLoopError(0);
            returns(0, 15, -45, 220, -360, 174, 186, 176, 180, 184);
        }};


        Scheduler.getInstance().disable();

        rotateWithGyro.initialize();

        assertFalse(rotateWithGyro.isFinished());
        assertFalse(rotateWithGyro.isFinished());
        assertFalse(rotateWithGyro.isFinished());
        assertFalse(rotateWithGyro.isFinished());
        assertFalse(rotateWithGyro.isFinished());
        assertFalse(rotateWithGyro.isFinished());
        assertFalse(rotateWithGyro.isFinished());
        assertFalse(rotateWithGyro.isFinished());
        assertFalse(rotateWithGyro.isFinished());
        assertTrue(rotateWithGyro.isFinished());

        rotateWithGyro.end();

        rotateWithGyro.initialize();

        assertFalse(rotateWithGyro.isFinished());

        rotateWithGyro.end();
    }

    private int degreesToTicks(double degrees) {
        return (int) (degrees / Config.PIGEON_DPP);
    }
}