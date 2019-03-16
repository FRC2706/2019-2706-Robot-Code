package ca.team2706.frc.robot;

import ca.team2706.frc.robot.commands.auto.DriveOffHab;
import ca.team2706.frc.robot.commands.auto.LevelOneCentreHatch;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.subsystems.*;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Main Robot class
 */
public class Robot extends TimedRobot {
    private boolean isInitialized;

    private Command[] commands;

    private static Robot latestInstance;

    private final List<Consumer<RobotState>> stateListeners = new ArrayList<>();

    public Robot() {
        latestInstance = this;
    }

    /**
     * Method run on robot initialization.
     */
    @Override
    public void robotInit() {
        setOnStateChange((state) -> Log.i("Robot State: " + state.name()));

        onStateChange(RobotState.ROBOT_INIT);
        isInitialized = true;

        Config.init();

        // Initialize subsystems
        Bling.init();
        DriveBase.init();

        Intake.init();
        Pneumatics.init();
        Lift.init();
        RingLight.init();

        // Make sure that this is last initialized subsystem
        SensorExtras.init();

        // OI depends on subsystems, so initialize it after
        OI.init();

        // The USB camera used on the Robot, not enabled during simulation mode
        if (Config.ENABLE_CAMERA) {
            UsbCamera camera0 = CameraServer.getInstance().startAutomaticCapture();

            // Prevents crashing of simulation robot
            if (isReal()) {
                camera0.setConnectVerbose(0);
            }
        }

        commands = new Command[]{
                OI.getInstance().driveCommand,                                             // 0
                null,                                                                      // 1
                null,                                                                      // 2
                OI.getInstance().driveCommand,                                             // 3
                new DriveOffHab(),                                                         // 4
                new LevelOneCentreHatch(),                                                 // 5
        };
    }

    /**
     * Called periodically (every cycle) while the robot is on.
     */
    @Override
    public void robotPeriodic() {
    }

    /**
     * Called when the robot enters the disabled state.
     */
    @Override
    public void disabledInit() {
        if (Config.DISABLE_WARNING) {
            disableLoopOverrun();
        }

        // If test mode was run, disable live window, and start scheduler
        if (LiveWindow.isEnabled()) {
            LiveWindow.setEnabled(false);
        }

        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.DISABLED);
    }

    /**
     * Disables the warning that prints if the loop is overrun
     */
    private void disableLoopOverrun() {
        try {
            Field m_watchdogField = IterativeRobotBase.class.getDeclaredField("m_watchdog");
            m_watchdogField.setAccessible(true);
            Watchdog m_watchdog = (Watchdog) m_watchdogField.get(this);
            m_watchdog.setTimeout(Double.POSITIVE_INFINITY);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e("Could not disable loop overrun warning", e);
        }
    }

    /**
     * Called periodically (every cycle) while the robot is disabled.
     */
    @Override
    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    /**
     * Called at the beginning of autonomous.
     */
    @Override
    public void autonomousInit() {
        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.AUTONOMOUS);

        logFMSData();

        selectorInit();
    }

    // The command currently being run on the robot
    private static Command currentCommand;

    /**
     * Checks to see if the desired command is assigned and runs 0 or does nothing if not
     */
    private void selectorInit() {
        // The index based the voltage of the selector
        final int index = DriveBase.getInstance().getAnalogSelectorIndex();

        Log.d("Selector switch set to " + index);

        // Check to see if the command exists in the desired index
        if (index < commands.length && commands[index] != null) {
            currentCommand = commands[index];
        } else if (commands.length > 0 && commands[0] != null) {
            currentCommand = commands[0];
        } else {
            currentCommand = null;
        }

        if (currentCommand != null) {
            currentCommand.start();
            Log.i("Running autonomous command: " + currentCommand);
        } else {
            Log.w("Not running autonomous command");
        }
    }

    /**
     * Called periodically (every cycle) while the robot is in autonomous mode.
     */
    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    /**
     * Called when the robot enters teleop mode.
     */
    @Override
    public void teleopInit() {
        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.TELEOP);

        logFMSData();
    }

    /**
     * Called periodically (every cycle) while the robot is in teleop mode.
     */
    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    /**
     * Called when the robot enters test mode.
     */
    @Override
    public void testInit() {
        // Disable scheduler and run live window
        LiveWindow.setEnabled(true);

        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.TEST);
    }

    /**
     * Called periodically during test mode.
     */
    @Override
    public void testPeriodic() {

    }

    /**
     * Main method, called when the robot code is run like a desktop application.
     *
     * @param args Arguments passed on startup
     */
    public static void main(String[] args) {
        Log.init();

        Runtime.getRuntime().addShutdownHook(new Thread(Robot::shutdown));

        RobotBase.startRobot(Robot::new);
    }

    /**
     * Adds a method to be called when the robot's state is changed.
     *
     * @param listener The listener to be added.
     */
    public void addStateListener(Consumer<RobotState> listener) {
        stateListeners.add(listener);
    }

    /**
     * Sets the given listener to be called when the robot is disabled.
     *
     * @param listener The listener to be invoked when the robot is disabled.
     */
    public static void setOnStateChange(Consumer<RobotState> listener) {
        if (latestInstance != null) {
            latestInstance.addStateListener(listener);
        }
    }

    /**
     * Removes a state listener.
     *
     * @param listener The listener to be removed.
     */
    public void removeListener(Consumer<RobotState> listener) {
        stateListeners.remove(listener);
    }

    /**
     * Removes a state listener so that it is no longer subscribed to robot state change events.
     *
     * @param listener The listener to be removed.
     */
    public static void removeStateListener(Consumer<RobotState> listener) {
        if (latestInstance != null) {
            latestInstance.removeListener(listener);
        }
    }

    /**
     * Determines if the current instance of the robot has been initialized.
     *
     * @return True if the robot has been initialized, false otherwise.
     */
    public static boolean isRobotInitialized() {
        return latestInstance != null && latestInstance.isInitialized();
    }

    /**
     * Determines if this robot has been initialized properly.
     *
     * @return True if initialized, false otherwise.
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Calls the state change event, executing the listeners.
     *
     * @param newState The robot's current (new) state.
     */
    private void onStateChange(RobotState newState) {
        // Make shallow copy of this.
        ArrayList<Consumer<RobotState>> listeners = new ArrayList<>(stateListeners);
        listeners.forEach(action -> action.accept(newState));
    }

    /**
     * Interrupt the current autonomous command and start teleop mode
     */
    public static void interruptCurrentCommand() {
        if (currentCommand != null && currentCommand.isRunning() && DriverStation.getInstance().isAutonomous() && currentCommand != OI.getInstance().driveCommand && currentCommand != OI.getInstance().liftCommand) {
            DriverStation.reportWarning("Interupting auto", false);
            Log.w("Interrupting auto");
            currentCommand.cancel();
        }
    }

    /**
     * Called when the robot is shutting down.
     */
    private static void shutdown() {
        // Iterate through each of the state-change listeners and call them.
        latestInstance.onStateChange(RobotState.SHUTDOWN);
    }

    private void logFMSData() {
        if(DriverStation.getInstance().isFMSAttached()) {
            Log.d("FMS: " + DriverStation.getInstance().getMatchType().name() + " " + DriverStation.getInstance().getMatchNumber() + " at " + DriverStation.getInstance().getMatchTime());
        }
    }
}
