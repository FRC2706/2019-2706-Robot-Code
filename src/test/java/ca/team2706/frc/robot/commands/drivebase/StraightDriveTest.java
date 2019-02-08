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
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StraightDriveTest {

    @Tested
    private StraightDrive straightDrive;

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
     * Tests that the command puts the drivetrain into the correct state
     *
     * @param speed         The speed to create the command with
     * @param position      The position to create the command with
     * @param minDoneCycles The minimum cycles to use
     */
    @Test
    public void testCorrectState(@Injectable("0.0") double speed, @Injectable("0.0") double position, @Injectable("1") int minDoneCycles) {
        assertEquals(DriveBase.DriveMode.Disabled, DriveBase.getInstance().getDriveMode());
        straightDrive.initialize();
        assertEquals(DriveBase.DriveMode.PositionNoGyro, DriveBase.getInstance().getDriveMode());
        assertEquals(true, DriveBase.getInstance().isBrakeMode());

        straightDrive.end();
        assertEquals(DriveBase.DriveMode.Disabled, DriveBase.getInstance().getDriveMode());
    }

    /**
     * Tests that the setpoint commands are called and speed is limited each tick
     *
     * @param speed         The speed to inject
     * @param position      The position to inject
     * @param minDoneCycles The min cycles to inject
     */
    @Test
    public void testSetting(@Injectable("0.0") double speed, @Injectable("0.5") double position, @Injectable("1") int minDoneCycles) {
        straightDrive.initialize();

        straightDrive.execute();
        straightDrive.execute();
        straightDrive.execute();

        straightDrive.end();

        new Verifications() {{
            talon.set(ControlMode.Position, position / Config.DRIVE_ENCODER_DPP);
            times = 3;
            talon.configClosedLoopPeakOutput(0, speed);
            times = 6;
        }};
    }

    /**
     * Tests that the command finishes in the right conditions
     *
     * @param speed         The speed to inject
     * @param position      The position to inject
     * @param minDoneCycles The min cycles to inject
     */
    @Test
    public void testFinished(@Injectable("0.0") double speed, @Injectable("5") double position, @Injectable("3") int minDoneCycles) {
        new Expectations() {{
            talon.getClosedLoopError(0);
            returns(intFeetToTicks(5), intFeetToTicks(4), intFeetToTicks(1), intFeetToTicks(0.25), intFeetToTicks(1), intFeetToTicks(0.25),
                    intFeetToTicks(-0.4), intFeetToTicks(0), intFeetToTicks(0), intFeetToTicks(0), intFeetToTicks(0));
        }};


        Scheduler.getInstance().disable();

        straightDrive.initialize();

        assertEquals(false, straightDrive.isFinished());
        assertEquals(false, straightDrive.isFinished());
        assertEquals(false, straightDrive.isFinished());
        assertEquals(false, straightDrive.isFinished());
        assertEquals(false, straightDrive.isFinished());
        assertEquals(false, straightDrive.isFinished());
        assertEquals(false, straightDrive.isFinished());
        assertEquals(false, straightDrive.isFinished());
        assertEquals(false, straightDrive.isFinished());
        assertEquals(true, straightDrive.isFinished());

        straightDrive.end();

        straightDrive.initialize();

        assertEquals(false, straightDrive.isFinished());

        straightDrive.end();
    }

    /**
     * Converts feet to encoder ticks
     *
     * @param feet The distance in feet
     * @return The amount of ticks
     */
    private static double feetToTicks(double feet) {
        return feet / Config.DRIVE_ENCODER_DPP;
    }

    /**
     * Converts feet to integer encoder ticks
     *
     * @param feet The distance in feet
     * @return The amount of ticks as integer
     */
    private static int intFeetToTicks(double feet) {
        return (int) (feetToTicks(feet));
    }
}