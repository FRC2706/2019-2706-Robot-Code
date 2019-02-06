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

    @Test
    public void testCorrectState(@Injectable("0.0") double speed, @Injectable("0.0") double position, @Injectable("1") int minDoneCycles) {
        assertEquals(DriveBase.DriveMode.Disabled, DriveBase.getInstance().getDriveMode());
        straightDrive.initialize();
        assertEquals(DriveBase.DriveMode.PositionNoGyro, DriveBase.getInstance().getDriveMode());
        assertEquals(true, DriveBase.getInstance().isBrakeMode());

        straightDrive.end();
        assertEquals(DriveBase.DriveMode.Disabled, DriveBase.getInstance().getDriveMode());
    }

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

    private static double feetToTicks(double feet) {
        return feet / Config.DRIVE_ENCODER_DPP;
    }

    private static int intFeetToTicks(double feet) {
        return (int) (feetToTicks(feet));
    }
}