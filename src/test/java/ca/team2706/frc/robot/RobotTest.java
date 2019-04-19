package ca.team2706.frc.robot;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class RobotTest {

    static {
        try {
            Util.resetSubsystems();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private final Robot robot = new Robot();

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private VictorSPX intakeMotor;

    @Mocked
    private PWM pwm;

    @Mocked
    private Relay relays;

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

    @Mocked(stubOutClassInitialization = true)
    private BuffTrajPointStreamJNI jni2;

    @Mocked
    private NetworkTableInstance networkTableInstance;

    @Mocked
    private NetworkTable networkTable;

    @Mocked
    private CameraServer cameraServer;

    @Mocked
    private LiveWindow liveWindow;

    @Mocked
    private DriverStation driverStation;

    @Mocked
    private GenericHID genericHID;

    @Injectable
    private SensorCollection sensorCollection;


    @Mocked
    private DoubleSolenoid solenoid;

    @Mocked
    private DigitalInput input;

    @Before
    public void setUp() throws IOException, NoSuchFieldException, IllegalAccessException {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
            minTimes = 0;

            genericHID.getRawAxis(0);
            result = 0;
            minTimes = 0;
        }};

        new Expectations(Pathfinder.class) {{
            Pathfinder.readFromCSV((File) any);
            result = new Trajectory(0);
            minTimes = 0;
        }};

        Util.resetSubsystems();
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
     * Tests whether the absolute gyro is reset when in a match
     */
    @Test
    public void testAbsoluteReset() {
        robot.robotInit();

        robot.autonomousInit();

        new Verifications() {{
            pigeon.setYaw(0, anyInt);
            times = 2;
        }};
    }


    /**
     * Tests that the commands are correctly set and run
     *
     * @throws NoSuchFieldException   Reflection exception
     * @throws IllegalAccessException Relfection exception
     */
    @Test
    public void testCommandSelector() throws NoSuchFieldException, IllegalAccessException {
        new Expectations() {{
            analogInput.getAverageVoltage();
            returns(0.0, 3.0, 3.3, 4.45, 4.45);
        }};

        EmptyCommand a = new EmptyCommand();
        EmptyCommand b = new EmptyCommand();
        EmptyCommand c = new EmptyCommand();
        EmptyCommand d = new EmptyCommand();

        robot.robotInit();

        setCommands(robot, a, b, null, c, d);

        robot.autonomousInit();

        assertEquals(a, getCurrentCommand(robot));
        assertTrue(a.isRunning());

        robot.autonomousInit();

        assertEquals(a, getCurrentCommand(robot));
        assertTrue(a.isRunning());

        robot.autonomousInit();

        assertEquals(c, getCurrentCommand(robot));
        assertTrue(c.isRunning());

        robot.autonomousInit();

        assertEquals(a, getCurrentCommand(robot));
        assertTrue(a.isRunning());

        setCommands(robot, null, b, null, c, d);

        robot.autonomousInit();

        assertNull(getCurrentCommand(robot));
    }

    /**
     * Tests that the autonomous commands can be interrupted
     *
     * @throws NoSuchFieldException   Reflection exception
     * @throws IllegalAccessException Relfection exception
     */
    @Test
    public void testInterrupt() throws NoSuchFieldException, IllegalAccessException {
        new Expectations() {{
            analogInput.getAverageVoltage();
            result = 0.0;

            driverStation.isAutonomous();
            result = true;
        }};

        robot.robotInit();

        EmptyCommand a = new EmptyCommand();

        setCommands(robot, a);

        robot.autonomousInit();

        assertTrue(a.isRunning());

        Robot.interruptCurrentCommand();

        assertFalse(a.isRunning());
    }

    /**
     * Sets the list of commands to run
     *
     * @param robot    The robot with the commands
     * @param commands The commands to set
     * @throws NoSuchFieldException   Reflection exception
     * @throws IllegalAccessException Reflection exception
     */
    public static void setCommands(Robot robot, Command... commands) throws NoSuchFieldException, IllegalAccessException {
        Field commandsField = Robot.class.getDeclaredField("commands");
        commandsField.setAccessible(true);
        commandsField.set(robot, commands);
    }

    /**
     * Gets the current autonomous command
     *
     * @param robot The robot with the commands
     * @return The current command that should be running
     * @throws NoSuchFieldException   Reflection exception
     * @throws IllegalAccessException Reflection exception
     */
    public static Command getCurrentCommand(Robot robot) throws NoSuchFieldException, IllegalAccessException {
        Field currentCommandField = Robot.class.getDeclaredField("currentCommand");
        currentCommandField.setAccessible(true);
        return (Command) currentCommandField.get(robot);
    }

    /**
     * Empty command that keeps track of when it's run
     */
    public static class EmptyCommand extends Command {

        private boolean isRunning;

        @Override
        public void start() {
            isRunning = true;
        }

        @Override
        protected boolean isFinished() {
            return false;
        }

        @Override
        public void cancel() {
            isRunning = false;
        }

        /**
         * Whether the command is running
         *
         * @return True after calling {@code start()} and false after calling {@code cancel()}
         */
        @Override
        public boolean isRunning() {
            return isRunning;
        }
    }
}