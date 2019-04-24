package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogInput;
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

import java.util.function.Supplier;

public class TankDriveTest {
    @Injectable
    private Supplier<Double> leftSpeed;

    @Injectable
    private Supplier<Double> rightSpeed;

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

    @Mocked(stubOutClassInitialization = true)
    private BuffTrajPointStreamJNI jni2;

    @Mocked
    private Notifier notifier;

    @Injectable
    private SensorCollection sensorCollection;

    @Before
    public void setUp() {
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
    }

    @Test
    public void testBrakeModeOn() throws NoSuchFieldException, IllegalAccessException {
        testBrakeMode(true);
    }

    @Test
    public void testBrakeModeOff() throws NoSuchFieldException, IllegalAccessException {
        testBrakeMode(false);
    }

    @BeforeClass
    public static void classSetUp() throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();
    }

    /**
     * Makes sure that the brake mode gets set to the correct value at the end of the match
     */
    private void testBrakeMode(boolean brake) throws NoSuchFieldException, IllegalAccessException {
        new Expectations() {{
            leftSpeed.get();
            result = 0.0;

            rightSpeed.get();
            result = 0.0;
        }};

        TankDrive tankDrive = new TankDrive(leftSpeed, rightSpeed, false, brake) {
            @Override
            public boolean isFinished() {
                return false;
            }
        };

        tankDrive.initialize();

        tankDrive.execute();

        tankDrive.end();

        new Verifications() {{
            DriveBase.getInstance().setBrakeMode(brake);
        }};

    }
}
