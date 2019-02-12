package ca.team2706.frc.robot.commands.drivebase;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import org.junit.Before;
import org.junit.Test;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;
import ca.team2706.frc.robot.util.Util;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StraightDriveGyroTest {

    @Tested
    private StraightDriveGyro straightDriveGyro;

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private PWM pwm;

    @Mocked
    private AnalogInput analogInput;

    @Mocked(stubOutClassInitialization = true)
    private PigeonIMU pigeon;

    private DifferentialDrive differentialDrive;

    @Mocked
    private SmartDashboard smartDashboard;

    @Mocked(stubOutClassInitialization = true)
    private CTREJNIWrapper jni;

    @Mocked(stubOutClassInitialization = true)
    private MotControllerJNI motControllerJNI;

    @Injectable
    private SensorCollection sensorCollection;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
        }};

        Util.resetConfigAndRobot();
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
        straightDriveGyro.initialize();
        assertEquals(DriveBase.DriveMode.PositionGyro, DriveBase.getInstance().getDriveMode());
        assertTrue(DriveBase.getInstance().isBrakeMode());

        straightDriveGyro.end();
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
        straightDriveGyro.initialize();

        straightDriveGyro.execute();
        straightDriveGyro.execute();
        straightDriveGyro.execute();

        straightDriveGyro.end();

        new Verifications() {{
            talon.set(ControlMode.Position, position / Config.DRIVE_ENCODER_DPP, DemandType.AuxPID, 0);
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

        straightDriveGyro.initialize();

        assertFalse(straightDriveGyro.isFinished());
        assertFalse(straightDriveGyro.isFinished());
        assertFalse(straightDriveGyro.isFinished());
        assertFalse(straightDriveGyro.isFinished());
        assertFalse(straightDriveGyro.isFinished());
        assertFalse(straightDriveGyro.isFinished());
        assertFalse(straightDriveGyro.isFinished());
        assertFalse(straightDriveGyro.isFinished());
        assertFalse(straightDriveGyro.isFinished());
        assertTrue(straightDriveGyro.isFinished());

        straightDriveGyro.end();

        straightDriveGyro.initialize();

        assertFalse(straightDriveGyro.isFinished());

        straightDriveGyro.end();
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
