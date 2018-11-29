package ca.team2706.frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;

import java.util.ArrayList;

public class Robot extends TimedRobot {

    public static void main(String[] args) {
        //RobotBase.startRobot(Robot::new);
    }

    private static final ArrayList<Runnable> DISABLED_LISTENERS = new ArrayList<>();

    /**
     * Sets the given listener to be called when the robot is disabled.
     * @param listener The listener to be invoked when the robot is disabled.
     */
    public static void setOnDisabled(Runnable listener) {
        DISABLED_LISTENERS.add(listener);
    }

    /**
     * Called when the robot enters the disabled state.
     */
    public void disabledInit() {
        // Iterate through each of the disabled listeners and call them.
        DISABLED_LISTENERS.forEach(action -> action.run());
    }
}
