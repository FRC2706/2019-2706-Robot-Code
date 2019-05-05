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
import org.junit.Test;
import util.Util;

import java.util.function.Supplier;

public class CurvatureDriveTest {
    @Injectable
    private Supplier<Double> forwardVal;

    @Injectable
    private Supplier<Double> curveSpeed;

    @Mocked
    private Supplier<Boolean> buttonPress;

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

    @Test
    public void testBrakeModeOn() {
        testBrakeMode(true);
    }

    @Test
    public void testBrakeModeOff() {
        testBrakeMode(false);
    }

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();
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
     * Makes sure that the brake mode gets set to the correct value at the end of the match
     */
    private void testBrakeMode(boolean brake) {
        new Expectations() {{
            forwardVal.get();
            result = 0.0;

            curveSpeed.get();
            result = 0.0;

            buttonPress.get();
            result = false;
        }};

        CurvatureDrive curvatureDrive = new CurvatureDrive(forwardVal, curveSpeed, brake, buttonPress, false) {
            @Override
            public boolean isFinished() {
                return false;
            }
        };

        curvatureDrive.initialize();

        new Verifications() {{
            DriveBase.getInstance().setBrakeMode(brake);
        }};

        curvatureDrive.execute();

        curvatureDrive.end();

        new Verifications() {{
            DriveBase.getInstance().setBrakeMode(brake);
        }};
    }
}
