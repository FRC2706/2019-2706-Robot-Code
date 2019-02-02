package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.sensors.AnalogSelector;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DriveBaseTest {

    @Tested
    private DriveBase driveBase;

    @Mocked(stubOutClassInitialization = true)
    private WPI_TalonSRX talon;

    @Mocked(stubOutClassInitialization = true)
    private PWM pwm;

    @Mocked(stubOutClassInitialization = true)
    private AnalogSelector analogSelector;

    @Mocked(stubOutClassInitialization = true)
    private PigeonIMU pigeon;

    @Mocked(stubOutClassInitialization = true)
    private DifferentialDrive differentialDrive;

    @Mocked(stubOutClassInitialization = true)
    private CTREJNIWrapper jni;

    @Mocked(stubOutClassInitialization = true)
    private MotControllerJNI motControllerJNI;

    @Injectable
    private SensorCollection sensorCollection;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
        }};
    }

    @Test
    public void testControlMode() {
        assertEquals(DriveBase.DriveMode.Disabled, driveBase.getDriveMode());
        driveBase.tankDrive(0, 0, false);
        assertEquals(DriveBase.DriveMode.OpenLoopVoltage, driveBase.getDriveMode());

        driveBase.arcadeDrive(0, 0, false);
        driveBase.curvatureDrive(0, 0, false);

        new Verifications() {{
            talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        }};
    }
}