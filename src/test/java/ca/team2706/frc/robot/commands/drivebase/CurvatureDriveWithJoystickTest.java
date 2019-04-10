package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;
import com.ctre.phoenix.CTREJNIWrapper;
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
import org.junit.Test;
import util.Util;

import static org.junit.Assert.assertFalse;

public class CurvatureDriveWithJoystickTest {

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

    @Injectable
    private Joystick joy3;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
        }};
    }

    /**
     * Makes sure it's not in brake mode
     */
    @Test
    public void testBrakeMode() {

        CurvatureDriveWithJoystick curveDrive = new CurvatureDriveWithJoystick(joy1, Config.CURVATURE_DRIVE_FORWARD, true, joy2, Config.CURVATURE_CURVE_SPEED, false, joy3, Config.SLOW_MODE);

        curveDrive.initialize();

        assertFalse(DriveBase.getInstance().isBrakeMode());
    }


    /**
     * Makes sure that values are negated correctly
     */
    @Test
    public void testNegate() {
        new Expectations() {{
            joy1.getRawAxis(Config.CURVATURE_DRIVE_FORWARD);
            returns(1D, -1D);

            // 0.0D, 0.1D
            joy2.getRawAxis(Config.CURVATURE_CURVE_SPEED);
            returns(1D, -1D);

            joy3.getRawButton(Config.SLOW_MODE);
            returns(true, false);

        }};

        CurvatureDriveWithJoystick curveDrive = new CurvatureDriveWithJoystick(joy1, Config.CURVATURE_DRIVE_FORWARD, true, joy2, Config.CURVATURE_CURVE_SPEED, false, joy3, Config.SLOW_MODE, false);

        curveDrive.initialize();

        curveDrive.execute();
        curveDrive.execute();

        curveDrive.end();

        new Verifications() {{
            DriveBase.getInstance().curvatureDrive(-0.6, 1.0, false);
            DriveBase.getInstance().curvatureDrive(1.0, -1.0, false);
        }};
    }
}
