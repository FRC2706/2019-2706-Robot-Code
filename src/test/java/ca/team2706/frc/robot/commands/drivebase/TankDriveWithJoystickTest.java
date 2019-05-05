package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.Util;

public class TankDriveWithJoystickTest {
    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private PWM pwm;

    @Mocked
    private AnalogInput analogInput;

    @Mocked(stubOutClassInitialization = true)
    private PigeonIMU pigeon;

    @Mocked
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

    @Injectable
    private Joystick joy1;

    @Injectable
    private Joystick joy2;

    @BeforeClass
    public static void classSetUp() throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();
    }

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
            minTimes = 0;
        }};
    }

    /**
     * Makes sure that values are negated correctly
     */
    @Test
    public void testNegate() {
        new Expectations() {{
            joy1.getRawAxis(Config.TANK_DRIVE_LEFT);
            returns(1.0D, -1.0D, 0.0D, 0.1D);

            joy2.getRawAxis(Config.TANK_DRIVE_RIGHT);
            returns(1.0D, -1.0D, 0.0D, 0.1D);
        }};

        TankDriveWithJoystick tankDrive = new TankDriveWithJoystick(joy1, Config.TANK_DRIVE_LEFT, true, joy2, Config.TANK_DRIVE_RIGHT, false);

        tankDrive.initialize();

        for (int i = 0; i < 4; i++) {
            tankDrive.execute();
        }

        tankDrive.end();

        new Verifications() {{
            DriveBase.getInstance().tankDrive(-1, 1, Config.TELEOP_SQUARE_JOYSTICK_INPUTS);
            DriveBase.getInstance().tankDrive(1, -1, Config.TELEOP_SQUARE_JOYSTICK_INPUTS);
            DriveBase.getInstance().tankDrive(-0.0, 0, Config.TELEOP_SQUARE_JOYSTICK_INPUTS);
            DriveBase.getInstance().tankDrive(-0.1, 0.1, Config.TELEOP_SQUARE_JOYSTICK_INPUTS);
        }};
    }
}
