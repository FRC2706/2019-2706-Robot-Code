package ca.team2706.frc.robot.commands.drivebase;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import org.junit.Before;
import org.junit.BeforeClass;
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
import util.Util;

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

    @Mocked
    private Notifier notifier;

    @Injectable
    private SensorCollection sensorCollection;

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
    private void testBrakeMode(boolean brake) throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();
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

        new Verifications() {{
            DriveBase.getInstance().setBrakeMode(brake);
        }};

        tankDrive.execute();

        tankDrive.end();

        new Verifications() {{
            DriveBase.getInstance().setBrakeMode(brake);
        }};
    }
}
