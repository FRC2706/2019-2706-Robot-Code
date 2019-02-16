package ca.team2706.frc.robot.commands.drivebase;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import org.junit.Before;
import org.junit.Test;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;

public class ArcadeDriveWithJoystickTest {
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

    @Injectable
    private SensorCollection sensorCollection;

    @Mocked
    private Joystick joy1;

    @Mocked
    private Joystick joy2;

    @Before
    public void setUp() {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
        }};
    }

    /**
     * Makes sure that values are negated correctly
     */
    @Test
    public void testNegate() {
        new Expectations() {{
            joy1.getRawAxis(Config.ARCADE_DRIVE_FORWARD);
            returns(1.0D, -1.0D, 0.0D, 0.1D);

            joy2.getRawAxis(Config.ARCADE_DRIVE_ROTATE);
            returns(1.0D, -1.0D, 0.0D, 0.1D);
        }};

        ArcadeDriveWithJoystick arcadeDrive = new ArcadeDriveWithJoystick(joy1, Config.ARCADE_DRIVE_FORWARD, true, joy2, Config.ARCADE_DRIVE_ROTATE, false);

        arcadeDrive.initialize();

        for (int i = 0; i < 4; i++) {
            arcadeDrive.execute();
        }

        arcadeDrive.end();

        new Verifications() {{
            DriveBase.getInstance().arcadeDrive(-1, 1, Config.TELEOP_SQUARE_JOYSTICK_INPUTS);
            DriveBase.getInstance().arcadeDrive(1, -1, Config.TELEOP_SQUARE_JOYSTICK_INPUTS);
            DriveBase.getInstance().arcadeDrive(-0.0, 0, Config.TELEOP_SQUARE_JOYSTICK_INPUTS);
            DriveBase.getInstance().arcadeDrive(-0.1, 0.1, Config.TELEOP_SQUARE_JOYSTICK_INPUTS);
        }};
    }
}
