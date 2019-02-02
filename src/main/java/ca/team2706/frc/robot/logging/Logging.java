package ca.team2706.frc.robot.logging;

import ca.team2706.frc.robot.Robot;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class Logging {

    private static final Logger LOGGER = LogManager.getLogger(Robot.class.getName());

    public static void init() {
        Logging.i("Starting to log");
    }

    public static void d(Object message) {
        LOGGER.debug(message);
    }

    public static void d(Object message, Throwable t) {
        LOGGER.debug(message, t);
    }

    public static void i(Object message) {
        LOGGER.info(message);
    }

    public static void i(Object message, Throwable t) {
        LOGGER.info(message, t);
    }

    public static void w(Object message) {
        LOGGER.warn(message);
    }

    public static void w(Object message, Throwable t) {
        LOGGER.warn(message, t);
    }

    public static void e(Object message) {
        LOGGER.error(message);
    }

    public static void e(Object message, Throwable t) {
        LOGGER.error(message, t);
    }

    public static Logger getLogger ()
    {
        return LOGGER;
    }

    public static void disable(boolean doDisable)
    {
        if (doDisable) {
            Configurator.setLevel("ca.team2706.frc.robot.Robot", Level.ERROR);
        }
        else {
            Configurator.setLevel("ca.team2706.frc.robot.Robot", Level.ALL);
        }
    }
}
