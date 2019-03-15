package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HandBrakeTest {

    @Tested
    private Handbrake handbrake;

    @Mocked
    private PWM pwm;

    @Mocked
    private AnalogInput analogInput;

    @Mocked
    private DigitalInput digitalInput;

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
        DriveBase.getInstance().setBrakeMode(false);
    }

    /**
     * Tests that hand brake enables and disables correctly
     */
    @Test
    public void testHandBrake() {
        assertFalse(DriveBase.getInstance().isBrakeMode());

        handbrake.initialize();

        assertTrue(DriveBase.getInstance().isBrakeMode());

        handbrake.end();

        assertFalse(DriveBase.getInstance().isBrakeMode());
    }
}
