package ca.team2706.frc.robot.commands.drivebase;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import org.junit.Before;
import org.junit.Test;

import java.util.function.Supplier;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;

public class CurvatureDriveTest {
    @Mocked
    private Supplier<Double> forwardVal;

    @Mocked
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

            curveSpeed.get();
            result = 0.0;

            buttonPress.get();
            result = false;
        }};

        CurvatureDrive curvatureDrive = new CurvatureDrive(forwardVal, curveSpeed, brake, buttonPress) {
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
