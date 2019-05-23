package ca.team2706.frc.robot.input;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class EButtonTest {

    @Tested
    private IEButton button;

    @Mocked
    private GenericHID genericHID;

    @Mocked
    private DriverStation driverStation;

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private Relay relay;

    @Mocked
    private VictorSPX intakeMotor;

    @Mocked
    private DoubleSolenoid solenoids;

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

    @Mocked
    private SmartDashboard dashboard;

    @Before
    public void setUp() {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
            minTimes = 0;
        }};
    }

    /**
     * Tests that the command initializes when the button is first pressed, and releases ends when released
     */
    @Test
    public void whenHeldTest() {
        new Expectations() {{
            driverStation.isDisabled();
            result = false;

            genericHID.getRawAxis(anyInt);
            result = 0;
            minTimes = 0;
        }};

        Command command = new Command() {
            @Override
            protected boolean isFinished() {
                return false;
            }
        };

        button.expect(false, false, true, true, true, false);

        Scheduler.getInstance().enable();

        assertFalse(command.isRunning());

        button.whenHeld(command);
        Scheduler.getInstance().run();
        assertFalse(command.isRunning());

        Scheduler.getInstance().run();
        assertTrue(command.isRunning());

        Scheduler.getInstance().run();
        assertTrue(command.isRunning());

        Scheduler.getInstance().run();
        assertTrue(command.isRunning());

        Scheduler.getInstance().run();
        assertFalse(command.isRunning());

        Scheduler.getInstance().disable();
    }

    /**
     * Class to mock {@code get()} calls
     */
    private static class IEButton extends EButton {

        private boolean[] results;
        private int i;

        /**
         * Results to expect
         *
         * @param results All the results to expect
         */
        private void expect(boolean... results) {
            i = 0;
            this.results = results;
        }

        @Override
        public boolean get() {
            if (i < results.length) {
                return results[i++];
            }

            return false;
        }
    }
}