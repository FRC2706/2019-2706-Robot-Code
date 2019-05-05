package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.SendablesTest;
import ca.team2706.frc.robot.config.Config;
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
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import mockit.*;
import org.junit.Before;
import org.junit.Test;
import util.Util;

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
            ErrorCode.worstOne((ErrorCode) any, (ErrorCode) any);
            result = ErrorCode.OK;
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
    public void testSetting(@Injectable("0.0") double speed, @Injectable("30") double angle, @Injectable("1") int minDoneCycles) {
        new Expectations() {{
            pigeon.getYawPitchRoll((double[]) any);
            // 300 positive
            result = SendablesTest.makePigeonExpectation(-3300.0 - Config.ROBOT_START_ANGLE.value());
        }};


        absoluteRotateWithGyro.initialize();

        for (int i = 0; i < 3; i++) {
            absoluteRotateWithGyro.execute();
        }

        new Verifications() {{
            talon.set(ControlMode.Position, 0, DemandType.AuxPID, degreesToTicksDouble(90));
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
    public void testSettingMirror(@Injectable("0.0") double speed, @Injectable("330") double angle, @Injectable("1") int minDoneCycles) {
        new Expectations() {{
            pigeon.getYawPitchRoll((double[]) any);
            // 60 positive
            result = SendablesTest.makePigeonExpectation(3300.0 - Config.ROBOT_START_ANGLE.value());
        }};

        absoluteRotateWithGyro.mirror();

        absoluteRotateWithGyro.initialize();

        for (int i = 0; i < 3; i++) {
            absoluteRotateWithGyro.execute();
        }

        new Verifications() {{
            talon.set(ControlMode.Position, 0, DemandType.AuxPID, degreesToTicksDouble(-30.0));
            times = 3;
        }};
    }

    private int degreesToTicks(double degrees) {
        return (int) (degrees / Config.PIGEON_DPP);
    }

    private double degreesToTicksDouble(double degrees) {
        return degrees / Config.PIGEON_DPP;
    }
}