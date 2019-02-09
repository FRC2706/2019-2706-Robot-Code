package ca.team2706.frc.robot.util;

import ca.team2706.frc.robot.Robot;
import ca.team2706.frc.robot.RobotState;
import ca.team2706.frc.robot.config.Config;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;

/**
 * Testing utility methods.
 */
public class Util {
    public static void resetConfigAndRobot() throws IllegalAccessException, NoSuchFieldException {
        Field listenersField = Robot.class.getDeclaredField("STATE_LISTENERS");
        listenersField.setAccessible(true);
        @SuppressWarnings("unchecked") List<Consumer<RobotState>> listener = (List<Consumer<RobotState>>) listenersField.get(null);
        listener.clear();

        Field initializedField = Robot.class.getDeclaredField("isInitialized");
        initializedField.setAccessible(true);
        initializedField.set(null, false);

        Field initializeConfigField = Config.class.getDeclaredField("initialized");
        initializeConfigField.setAccessible(true);
        initializeConfigField.set(null, false);

        Field configNt = Config.class.getDeclaredField("constantsTable");
        configNt.setAccessible(true);
        configNt.set(null, null);
    }
}
