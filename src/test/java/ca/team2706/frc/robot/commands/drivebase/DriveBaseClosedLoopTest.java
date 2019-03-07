package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DriveBaseClosedLoopTest {

    @Tested
    private EDriveBaseClosedLoopTest driveBaseClosedLoopTest;

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private PWM pwm;

    @Mocked
    private AnalogInput analogInput;

    @Mocked(stubOutClassInitialization = true)
    private PigeonIMU pigeon;

    private DifferentialDrive differentialDrive;

    @Mocked
    private SmartDashboard smartDashboard;

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
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
            minTimes = 0;
        }};
    }

    @Test
    public void testFinishing(@Injectable("3") int minDoneCycles, @Injectable("0.3") double targetRange) {
        new Expectations() {{
            talon.getClosedLoopError(0);
            returns(intFeetToTicks(5), intFeetToTicks(4), intFeetToTicks(1), intFeetToTicks(0.25), intFeetToTicks(1), intFeetToTicks(0.25),
                    intFeetToTicks(-0.4), intFeetToTicks(0), intFeetToTicks(0), intFeetToTicks(0), intFeetToTicks(0));
        }};


        Scheduler.getInstance().disable();

        driveBaseClosedLoopTest.initialize();

        assertFalse(driveBaseClosedLoopTest.isFinished());
        assertFalse(driveBaseClosedLoopTest.isFinished());
        assertFalse(driveBaseClosedLoopTest.isFinished());
        assertFalse(driveBaseClosedLoopTest.isFinished());
        assertFalse(driveBaseClosedLoopTest.isFinished());
        assertFalse(driveBaseClosedLoopTest.isFinished());
        assertFalse(driveBaseClosedLoopTest.isFinished());
        assertFalse(driveBaseClosedLoopTest.isFinished());
        assertFalse(driveBaseClosedLoopTest.isFinished());
        assertTrue(driveBaseClosedLoopTest.isFinished());

        driveBaseClosedLoopTest.end();

        driveBaseClosedLoopTest.initialize();

        assertFalse(driveBaseClosedLoopTest.isFinished());

        driveBaseClosedLoopTest.end();
    }


    private static class EDriveBaseClosedLoopTest extends DriveBaseCloseLoop {

        protected EDriveBaseClosedLoopTest(int minDoneCycles, double targetRange) {
            super(minDoneCycles, targetRange);
        }

        @Override
        public void end() {
        }
    }

    /**
     * Converts feet to encoder ticks
     *
     * @param feet The distance in feet
     * @return The amount of ticks
     */
    private static double feetToTicks(double feet) {
        return feet / Config.DRIVE_ENCODER_DPP;
    }

    /**
     * Converts feet to integer encoder ticks
     *
     * @param feet The distance in feet
     * @return The amount of ticks as integer
     */
    private static int intFeetToTicks(double feet) {
        return (int) (feetToTicks(feet));
    }
}
