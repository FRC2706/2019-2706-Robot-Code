package ca.team2706.frc.robot;

import ca.team2706.frc.robot.commands.auto.DriveOffHab;
import ca.team2706.frc.robot.commands.auto.LevelOneCentreHatch;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.logging.PeriodicLogger;
import ca.team2706.frc.robot.subsystems.*;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Main Robot class
 */
public class Robot extends TimedRobot {
    private boolean isInitialized;
    /*
    This keeps track of whether or not all subsystems are good for running auto. If one subsystem is not,
    this will be set to false and auto won't run.
    */
    private boolean canRunAuto = true;

    private Command[] commands;
    private Map<Integer, Integer> selectorOrientation;

    private static Robot latestInstance;

    private final List<Consumer<RobotState>> stateListeners = new ArrayList<>();
    private final List<Consumer<ConnectionState>> connectionListeners = new ArrayList<>();

    private boolean driverStationConnected, fmsConnected;

    public Robot() {
        latestInstance = this;
    }

    /**
     * Method run on robot initialization.
     */
    @Override
    public void robotInit() {
        setOnStateChange((state) -> Log.i("Robot State: " + state.name()));
        setOnConnectionChange((state) -> Log.i("Connection State: " + state.name()));
        setOnConnectionChange(Log::setupFMS);
        // Adding the match time to SmartDashboard for use by other processors.
        SmartDashboard.putNumber(Config.MATCH_TIME_NT_KEY, 0);

        onStateChange(RobotState.ROBOT_INIT);
        isInitialized = true;

        Config.init();

        // Initialize subsystems
        logInitialization(Bling.init(), Bling.getInstance());
        logInitialization(DriveBase.init(), DriveBase.getInstance());

        logInitialization(Intake.init(), Intake.getInstance());
        logInitialization(Pneumatics.init(), Pneumatics.getInstance());
        logInitialization(Lift.init(), Lift.getInstance());
        logInitialization(RingLight.init(), RingLight.getInstance());
        logInitialization(ClimberPneumatics.init(), ClimberPneumatics.getInstance());

        // Make sure that this is last initialized subsystem
        logInitialization(SensorExtras.init(), SensorExtras.getInstance());

        // OI depends on subsystems, so initialize it after
        OI.init();

        PeriodicLogger.initialize();

        // The USB camera used on the Robot, not enabled during simulation mode
        if (Config.ENABLE_CAMERA) {
            UsbCamera camera0 = CameraServer.getInstance().startAutomaticCapture();

            // Prevents crashing of simulation robot
            if (isReal()) {
                camera0.setConnectVerbose(0);
            }
        }

        commands = new Command[]{
                null,                                                                      // 0
                null,                                                                      // 1
                null,                                                                      // 2
                null,                                                                      // 3
                null,                                                                      // 4
                new DriveOffHab(),                                                         // 5
                new LevelOneCentreHatch(),                                                 // 6
        };

        selectorOrientation = Map.of(4, 270);
    }

    /**
     * Logs the initialization of a subsystem
     *
     * @param subsystemStatus The status that the subsystem initialized with
     * @param subsystem       The subsystem
     */
    private void logInitialization(SubsystemStatus subsystemStatus, Subsystem subsystem) {
        PeriodicLogger.register(subsystem);

        String message = subsystem.getName() + " had initialized with status " + subsystemStatus.name();

        switch (subsystemStatus) {
            case ERROR:
                Log.e(message);
                break;
            case DISABLE_AUTO:
                Log.w(message);
                canRunAuto = false;
                break;
            case WORKABLE:
                Log.w(message);
                break;
            default:
                Log.i(message);
                break;
        }
    }

    /**
     * Called periodically (every cycle) while the robot is on.
     */
    @Override
    public void robotPeriodic() {
        if (!fmsConnected && DriverStation.getInstance().isFMSAttached() && !DriverStation.getInstance().getEventName().isEmpty()) {
            fmsConnected = true;
            onConnectionChange(ConnectionState.FMS_CONNECT);
        } else if (fmsConnected && !DriverStation.getInstance().isFMSAttached()) {
            fmsConnected = false;
            onConnectionChange(ConnectionState.FMS_DISCONNECT);
        }

        if (driverStationConnected != DriverStation.getInstance().isDSAttached()) {
            driverStationConnected = DriverStation.getInstance().isDSAttached();
            onConnectionChange(driverStationConnected ? ConnectionState.DRIVERSTATION_CONNECT : ConnectionState.DRIVERSTATION_DISCONNECT);
        }

        // If it's a real match, output the match time for use by others.
        if (fmsConnected) {
            SmartDashboard.putNumber(Config.MATCH_TIME_NT_KEY, getMatchTime());
        }
    }

    /**
     * Gets the match time counting autonomous and the teleop period together. Will count down from 150 to 0,
     * 150 being start of the match at auto, 0 being end of teleop.
     * <b>This may not return accurate match times if it is not a real match.</b>
     *
     * @return The current match time in seconds.
     */
    public static double getMatchTime() {
        final double time;
        if (DriverStation.getInstance().isDisabled()) {
            time = 0;
        } else if (DriverStation.getInstance().isAutonomous()) {
            time = Timer.getMatchTime() + 135;
        } else if (DriverStation.getInstance().isOperatorControl()) {
            time = Timer.getMatchTime();
        } else {
            time = 0;
        }

        return time;
    }

    /**
     * Determines if the robot is in a real match.
     *
     * @return True if the robot is in a real match, false otherwise.
     */
    public static boolean isRealMatch() {
        return DriverStation.getInstance().isFMSAttached();
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

        if (canRunAuto) {
            selectorInit();
        }
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

        DriveBase.getInstance().resetAbsoluteGyro(selectorOrientation.getOrDefault(index, (int) (double) Config.ROBOT_START_ANGLE.value()));

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
     * Adds a method to be called when the connection robot's state is changed.
     *
     * @param listener The connection listener to be added.
     */
    public void addConnectionListener(Consumer<ConnectionState> listener) {
        connectionListeners.add(listener);
    }

    /**
     * Sets the given connection listener to be called when connection state is changed
     *
     * @param listener The listener to be invoked when the connection state is changed
     */
    public static void setOnConnectionChange(Consumer<ConnectionState> listener) {
        if (latestInstance != null) {
            latestInstance.addConnectionListener(listener);
        }
    }

    /**
     * Removes a connection state listener.
     *
     * @param listener The listener to be removed.
     */
    public void removeConnectionStateListener(Consumer<ConnectionState> listener) {
        connectionListeners.remove(listener);
    }

    /**
     * Removes a connection state listener so that it is no longer subscribed to robot state change events.
     *
     * @param listener The listener to be removed.
     */
    public static void removeConnectionListener(Consumer<ConnectionState> listener) {
        if (latestInstance != null) {
            latestInstance.removeConnectionStateListener(listener);
        }
    }

    /**
     * Calls the connection state change event, executing the listeners.
     *
     * @param newState The robot's current (new) connection state.
     */
    private void onConnectionChange(ConnectionState newState) {
        // Make shallow copy of this.
        ArrayList<Consumer<ConnectionState>> listeners = new ArrayList<>(connectionListeners);
        listeners.forEach(action -> action.accept(newState));
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
            DriverStation.reportWarning("Interrupting auto", false);
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
}
