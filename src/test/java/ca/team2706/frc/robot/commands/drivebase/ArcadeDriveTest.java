package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import com.ctre.phoenix.CTREJNIWrapper;
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

import java.util.function.Supplier;

public class ArcadeDriveTest {

    @Mocked
    private Supplier<Double> forwardVal;

    @Mocked
    private Supplier<Double> rotateVal;

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
    public void setUp() {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
        }};
    }

    /**
     * Makes sure that the brake mode gets set to the correct value at the end of the match
     */
    private void testBrakeMode(boolean brake) {
        new Expectations() {{
            forwardVal.get();
            result = 0.0;

            rotateVal.get();
            result = 0.0;
        }};

        ArcadeDrive arcadeDrive = new ArcadeDrive(forwardVal, rotateVal, false, brake) {
            @Override
            public boolean isFinished() {
                return false;
            }
        };

        arcadeDrive.initialize();

        new Verifications() {{
            DriveBase.getInstance().setBrakeMode(brake);
        }};

        arcadeDrive.execute();

        arcadeDrive.end();

        new Verifications() {{
            DriveBase.getInstance().setBrakeMode(brake);
        }};
    }
}