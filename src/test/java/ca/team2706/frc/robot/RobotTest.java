package ca.team2706.frc.robot;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RobotTest {

    private final Robot robot = new Robot();

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private PWM pwm;

    @Mocked
    private AnalogInput analogInput;

    @Mocked(stubOutClassInitialization = true)
    private PigeonIMU pigeon;

    private DifferentialDrive differentialDrive;

    @Mocked(stubOutClassInitialization = true)
    private CTREJNIWrapper jni;

    @Mocked(stubOutClassInitialization = true)
    private MotControllerJNI motControllerJNI;

    @Mocked
    private Notifier notifier;

    @Mocked
    private NetworkTableInstance networkTableInstance;

    @Mocked
    private NetworkTable networkTable;

    @Mocked
    private CameraServer cameraServer;

    @Mocked
    private LiveWindow liveWindow;

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

    /**
     * Tests that the state listener calls and sets the correct states
     *
     * @throws NoSuchMethodException     In case the shutdown method can't be found
     * @throws InvocationTargetException In case the shutdown method can't be invoked
     * @throws IllegalAccessException    In case the reflection is illegal
     */
    @Test
    public void testStates() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        StateTest stateTest = new StateTest();
        Robot.setOnStateChange(stateTest);

        assertNull(stateTest.robotState);
        assertFalse(stateTest.isInitialized);

        assertFalse(Robot.isRobotInitialized());

        robot.robotInit();

        assertEquals(RobotState.ROBOT_INIT, stateTest.robotState);
        assertFalse(stateTest.isInitialized);

        assertTrue(Robot.isRobotInitialized());

        robot.disabledInit();

        assertEquals(RobotState.DISABLED, stateTest.robotState);
        assertTrue(stateTest.isInitialized);

        robot.autonomousInit();
        assertEquals(RobotState.AUTONOMOUS, stateTest.robotState);
        assertTrue(stateTest.isInitialized);

        robot.teleopInit();
        assertEquals(RobotState.TELEOP, stateTest.robotState);
        assertTrue(stateTest.isInitialized);

        robot.testInit();
        assertEquals(RobotState.TEST, stateTest.robotState);
        assertTrue(stateTest.isInitialized);

        robot.disabledInit();

        Method shutdown = Robot.class.getDeclaredMethod("shutdown");
        shutdown.setAccessible(true);
        shutdown.invoke(null);

        assertEquals(RobotState.SHUTDOWN, stateTest.robotState);
        assertTrue(stateTest.isInitialized);

        Robot.removeStateListener(stateTest);
    }

    /**
     * Test that LiveWindow is enabled and disabled at the correct times
     */
    @Test
    public void testLiveWindowAndScheduler() {
        new Expectations() {{
            //noinspection ResultOfMethodCallIgnored
            LiveWindow.isEnabled();
            returns(false, true, false);
        }};

        robot.robotInit();

        robot.disabledInit();

        robot.testInit();

        robot.disabledInit();
        robot.disabledInit();

        new Verifications() {{
            LiveWindow.setEnabled(anyBoolean);
            times = 2;
        }};
    }

    /**
     * Holds state information
     */
    private static class StateTest implements Consumer<RobotState> {

        /**
         * The state that the robot was changed to
         */
        RobotState robotState;

        /**
         * Whether the robot was initialized
         */
        boolean isInitialized;

        @Override
        public void accept(RobotState robotState) {
            this.robotState = robotState;
            this.isInitialized = Robot.isRobotInitialized();
        }
    }
}