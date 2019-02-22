package ca.team2706.frc.robot.sensors;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.OI;
import ca.team2706.frc.robot.Robot;
import ca.team2706.frc.robot.RobotState;
import ca.team2706.frc.robot.subsystems.DriveBase;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class DriverAssistToTargetWithVisionTest {

    private final Robot robot = new Robot();

    private final DriverAssistToTargetWithVision driverAssistToTargetWithVision = new DriverAssistToTargetWithVision();

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

    @Mocked
    private NetworkTableInstance ntInstance;

    /* Since OI is accessed statically (it's a singleton), we have to mock all instances of
    it and stub out the static initializer */
    @Mocked(stubOutClassInitialization = true)
    private OI oi;

    @Mocked(stubOutClassInitialization = true)
    private DriveBase driveBase;

    @Mocked(stubOutClassInitialization = true)
    private Config config;

    @Injectable
    private NetworkTableEntry ntEntry;

    @Injectable
    private NetworkTable constantsTable;

    @Injectable
    private SensorCollection sensorCollection;

    // Whether or not tests have been initialized.
    private static boolean isInitialized = false;

    @Before
    public void setUp() {
        if (!isInitialized) {
            isInitialized = true;

            new Expectations() {{
                // NetworkTable Instance is mocked
                // NetworkTable is injected, constantsTable really has nothing and only the method call to it is doing something
                ntInstance.getTable("PathFinder");
                result = constantsTable;
                minTimes = 0;

                talon.getSensorCollection();
                result = sensorCollection;
                minTimes = 0;
            }};

            Robot robot = new Robot();
            robot.robotInit();
        }
    }

/**
     * Tests that the trajectory is generated
     *
     * @throws NoSuchMethodException     In case the shutdown method can't be found
     * @throws InvocationTargetException In case the shutdown method can't be invoked
     * @throws IllegalAccessException    In case the reflection is illegal
     */
    @Test
    public void testStates2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        new Expectations() {{
            oi.getButtonDriverAssistVisionCargoAndLoading();
            result = false;
            minTimes = 0;

            oi.getButtonDriverAssistVisionRocket();
            result = true;
            minTimes = 0;

            driveBase.getAbsoluteHeading();
            result = 35.0;
            minTimes = 0;

            Config.get_ROBOT_TO_CAMERA_X_ROBOT();
            result = 0.8;
            minTimes = 0;

            Config.get_ROBOT_TO_CAMERA_Y_ROBOT();
            result = 0.9;
            minTimes = 0;
        }};

        double distanceCameraToTarget_Camera = 6.0;
        double yawAngleCameraToTarget_Camera = 27.0;   
        driverAssistToTargetWithVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera);

        assertTrue(true);
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
     * Test that LiveWindow is enabled and disabled at the correct times
     */
    @Test
    public void CargoTarget() {
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
}