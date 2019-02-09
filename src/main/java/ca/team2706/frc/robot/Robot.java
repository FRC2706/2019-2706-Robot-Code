package ca.team2706.frc.robot;

import ca.team2706.frc.robot.commands.drivebase.StraightDrive;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.subsystems.Bling;
import ca.team2706.frc.robot.subsystems.DriveBase;

import ca.team2706.frc.robot.subsystems.Intake;

import ca.team2706.frc.robot.subsystems.SensorExtras;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import ca.team2706.frc.robot.subsystems.ElevatorWithPID;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Main Robot class
 */
public class Robot extends TimedRobot {
    private static boolean isInitialized;

    private Command[] commands;

    /**
     * Method run on robot initialization.
     */
    @Override
    public void robotInit() {
        onStateChange(RobotState.ROBOT_INIT);
        isInitialized = true;

        Config.init();

        // Initialize subsystems
        Bling.init();
        DriveBase.init();

        Intake.init();
        ElevatorWithPID.init();


        // Make sure that this is last initialized subsystem
        SensorExtras.init();

        // OI depends on subsystems, so initialize it after
        OI.init();

        // The USB camera used on the Robot, not enabled during simulation mode
        if (Config.ENABLE_CAMERA) {
            UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();

            // Prevents crashing of simulation robot
            if (isReal()) {
                camera.setConnectVerbose(0);
            }
        }

        commands = new Command[]{
                OI.getInstance().driveCommand,                               // 0
                OI.getInstance().driveCommand,                               // 1
                new StraightDrive(0.2, 2.0, 100)  // 2
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
        // If test mode was run, disable live window, and start scheduler
        if (LiveWindow.isEnabled()) {
            LiveWindow.setEnabled(false);
        }

        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.DISABLED);
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

        selectorInit();
    }

    /**
     * Checks to see if the desired command is assigned and runs 0 or does nothing if not
     */
    private void selectorInit() {
        // The index based the voltage of the selector
        final int index = DriveBase.getInstance().getAnalogSelectorIndex();

        // Check to see if the command exists in the desired index
        if (DriveBase.getInstance().getAnalogSelectorIndex() < commands.length && commands[index] != null) {
            commands[DriveBase.getInstance().getAnalogSelectorIndex()].start();
        } else if (commands.length > 0 && commands[0] != null) {
            commands[0].start();
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
     * ArrayList of Robot State consumers to be invoked when the robot's state changes.
     */
    private static final List<Consumer<RobotState>> STATE_LISTENERS = new ArrayList<>();

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
     * Sets the given listener to be called when the robot is disabled.
     *
     * @param listener The listener to be invoked when the robot is disabled.
     */
    public static void setOnStateChange(Consumer<RobotState> listener) {
        STATE_LISTENERS.add(listener);
    }

    /**
     * Removes a state listener so that it is no longer subscribed to robot state change events.
     *
     * @param listener The listener to be removed.
     */
    public static void removeStateListener(Consumer<RobotState> listener) {
        STATE_LISTENERS.remove(listener);
    }

    /**
     * Determines if the current instance of the robot has been initialized.
     *
     * @return True if the robot has been initialized, false otherwise.
     */
    public static boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Calls the state change event, executing the listeners.
     *
     * @param newState The robot's current (new) state.
     */
    private static void onStateChange(RobotState newState) {
        // We want to make a copy of this so that we don't have concurrency problems.
        ArrayList<Consumer<RobotState>> stateListenerCopy = new ArrayList<>(STATE_LISTENERS);
        stateListenerCopy.forEach(action -> action.accept(newState));
    }

    /**
     * Called when the robot is shutting down.
     */
    private static void shutdown() {
        // Iterate through each of the state-change listeners and call them.
        Robot.onStateChange(RobotState.SHUTDOWN);
    }
}
