package ca.team2706.frc.robot;

import ca.team2706.frc.robot.config.Config;
import edu.wpi.first.wpilibj.TimedRobot;

import java.util.ArrayList;

public class Robot extends TimedRobot {

    public static void main(String[] args) {
//        RobotBase.startRobot(Robot::new);
    }

    private static final ArrayList<StateConsumer> STATE_LISTENERS = new ArrayList<>();

    /**
     * Sets the given listener to be called when the robot is disabled.
     * @param listener The listener to be invoked when the robot is disabled.
     */
    public static void setOnStateChange(StateConsumer listener) {
        STATE_LISTENERS.add(listener);
    }

    /**
     * Called when the robot enters the disabled state.
     */
    @Override
    public void disabledInit() {
        // Iterate through each of the disabled listeners and call them.
        onStateChange(RobotState.DISABLED);
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void teleopPeriodic() {
    }

    @Override
    public void robotInit() {
        Config.initialize();
        onStateChange(RobotState.ROBOT_INIT);
    }

    /**
     * Calls the state change event, executing the listeners.
     * @param newState The robot's current (new) state.
     */
    private static void onStateChange(RobotState newState) {
        STATE_LISTENERS.forEach(action -> action.accept(newState));
    }
}
