package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
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
import util.Util;

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

    @Mocked(stubOutClassInitialization = true)
    private BuffTrajPointStreamJNI jni2;

    @Injectable
    private SensorCollection sensorCollection;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
            minTimes = 0;
        }};

        new Expectations(ErrorCode.class) {{
            ErrorCode.worstOne((ErrorCode)any, (ErrorCode)any);
            result = ErrorCode.OK;
            minTimes = 0;
        }};

        Util.resetSubsystems();
    }

    /**
     * Tests that the setpoint commands are called and speed is limited each tick
     *
     * @param speed         The speed to inject
     * @param angle         The rotation to inject
     * @param minDoneCycles The min cycles to inject
     */
    @Test
    public void testSetting(@Injectable("5") double angle, @Injectable("1") int minDoneCycles) {
        rotateWithGyro.initialize();

        for (int i = 0; i < 3; i++) {
            rotateWithGyro.execute();
        }

        new Verifications() {{
            talon.set(ControlMode.Position, 0, DemandType.AuxPID, degreesToTicksDouble(5));
            times = 3;
        }};
    }

    /**
     * Tests that the setpoint commands are called and speed is limited each tick
     *
     * @param speed         The speed to inject
     * @param angle         The rotation to inject
     * @param minDoneCycles The min cycles to inject
     */
    @Test
    public void testSettingMirror(@Injectable("5") double angle, @Injectable("1") int minDoneCycles) {
        rotateWithGyro.mirror();

        rotateWithGyro.initialize();

        for (int i = 0; i < 3; i++) {
            rotateWithGyro.execute();
        }

        new Verifications() {{
            talon.set(ControlMode.Position, 0, DemandType.AuxPID, -degreesToTicksDouble(5));
            times = 3;
        }};
    }

    /**
     * Tests that the command finishes in the right conditions
     *
     * @param speed         The speed to inject
     * @param angle         The rotation to inject
     * @param minDoneCycles The min cycles to inject
     */
    @Test
    public void testFinished(@Injectable("0.0") double speed, @Injectable("180") double angle, @Injectable("3") int minDoneCycles) {
        new Expectations() {{
            talon.getClosedLoopError(0);
            returns(
                    degreesToTicks(0),
                    degreesToTicks(-165),
                    degreesToTicks(-225),
                    degreesToTicks(40),
                    degreesToTicks(-540),
                    degreesToTicks(-6),
                    degreesToTicks(6),
                    degreesToTicks(-4),
                    degreesToTicks(0),
                    degreesToTicks(4));
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

        rotateWithGyro.initialize();

        assertFalse(rotateWithGyro.isFinished());
    }

    private int degreesToTicks(double degrees) {
        return (int) (degrees / Config.PIGEON_DPP);
    }

    private double degreesToTicksDouble(double degrees) {
        return degrees / Config.PIGEON_DPP;
    }
}