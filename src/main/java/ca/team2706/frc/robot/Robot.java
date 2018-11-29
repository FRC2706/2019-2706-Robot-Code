package ca.team2706.frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

import java.util.ArrayList;

public class Robot extends TimedRobot {

    public static void main(String[] args) {
        //RobotBase.startRobot(Robot::new);
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
        STATE_LISTENERS.forEach(action -> action.accept(RobotState.DISABLED));
    }

    public void disabledPeriodic() {
       // System.out.println(Config.DRIVER_PRESS_A.value());
    }

    public void teleopPeriodic() {
        //System.out.println(Config.DRIVER_PRESS_A.value());
    }

    public void robotInit() {
        STATE_LISTENERS.forEach(action -> action.accept(RobotState.ROBOT_INIT));
    }
}
