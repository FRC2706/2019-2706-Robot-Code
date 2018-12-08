package ca.team2706.frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import java.util.ArrayList;

public class Robot extends TimedRobot {

    @Override
    public void robotInit() {
        onStateChange(RobotState.ROBOT_INIT);
    }

    /**
     * Called when the robot enters the disabled state.
     */
    @Override
    public void disabledInit() {
        if(LiveWindow.isEnabled()) {
            LiveWindow.setEnabled(false);
        }

        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.DISABLED);
    }

    @Override
    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void autonomousInit() {
        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.AUTONOMOUS);
    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.TELEOP);
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void testInit() {
        LiveWindow.setEnabled(true);

        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.TEST);
    }

    @Override
    public void testPeriodic() { }


    private static final ArrayList<StateConsumer> STATE_LISTENERS = new ArrayList<>();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(Robot::shutdown));

        // TODO: Uncomment when using WPILib beta
//        RobotBase.startRobot(Robot::new);
    }

    /**
     * Sets the given listener to be called when the robot is disabled.
     * @param listener The listener to be invoked when the robot is disabled.
     */
    public static void setOnStateChange(StateConsumer listener) {
        STATE_LISTENERS.add(listener);
    }

    /**
     * Calls the state change event, executing the listeners.
     * @param newState The robot's current (new) state.
     */
    private static void onStateChange(RobotState newState) {
        STATE_LISTENERS.forEach(action -> action.accept(newState));
    }

    private static void shutdown() {
        // Iterate through each of the state-change listeners and call them.
        Robot.onStateChange(RobotState.SHUTDOWN);
    }
}
