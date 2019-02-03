package ca.team2706.frc.robot.subsystems;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DriveBaseTest {

    @Tested
    private DriveBase driveBase;

    @Mocked
    private WPI_TalonSRX talon;

    private PWM pwm;

    private AnalogInput analogInput;

    @Mocked(stubOutClassInitialization = true)
    private PigeonIMU pigeon;

    private DifferentialDrive differentialDrive;

    @Mocked(stubOutClassInitialization = true)
    private CTREJNIWrapper jni;

    private MotControllerJNI motControllerJNI;

    @Injectable
    private SensorCollection sensorCollection;

    @Before
    public void setUp() {
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