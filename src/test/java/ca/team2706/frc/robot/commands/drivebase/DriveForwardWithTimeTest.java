package ca.team2706.frc.robot.commands.drivebase;

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
import mockit.*;
import org.junit.Before;
import org.junit.Test;

public class DriveForwardWithTimeTest {

    @Tested
    private DriveForwardWithTime drive;

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

    @Before
    public void setUp() {
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
     * Tests the speed of the robot
     *
     * @param timeout Time to drive
     * @param speed   Speed to drive
     */
    @Test
    public void testSpeed(@Injectable("2.0") double timeout, @Injectable("0.2") double speed) {
        drive.initialize();
        drive.execute();
        drive.execute();
        drive.execute();

        new Verifications() {{
            differentialDrive.arcadeDrive(0.2, 0, false);
            times = 3;
        }};
    }
}
