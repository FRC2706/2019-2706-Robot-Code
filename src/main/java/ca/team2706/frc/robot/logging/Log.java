package ca.team2706.frc.robot.logging;

import ca.team2706.frc.robot.Robot;
import edu.wpi.first.wpilibj.DriverStation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * Logs to USB and console at levels debug, info, warning, error
 */
public class Log {

    private static final Logger LOGGER = LogManager.getLogger(Robot.class.getName());

    /**
     *Starts logging
     */
    public static void init() {
        Log.i("Starting to log");
        Log.e("this is a test error message");
    }

    /**
     * Debug log
     * @param message The object (or String) message to log
     */
    public static void d(Object message) {
        LOGGER.debug(message);
    }

    /**
     * Debug log with exception
     * @param message The object (or String) message to log
     * @param t The Throwable to log
     */
    public static void d(Object message, Throwable t) {
        LOGGER.debug(message, t);
    }

    /**
     * Info log
     * @param message The object (or String) message to log
     */
    public static void i(Object message) {
        LOGGER.info(message);
    }

    /**
     * Info log with exception
     * @param message The object (or String) message to log
     * @param t The Throwable to log
     */
    public static void i(Object message, Throwable t) {
        LOGGER.info(message, t);
    }

    /**
     * Warning log
     * @param message The object (or String) message to log
     */
    public static void w(Object message) {
        LOGGER.warn(message);
        DriverStation.reportWarning((String) message, true);
    }

    /**
     * Warning log with exception
     * @param message The object (or String) message to log
     * @param t The Throwable to log
     */
    public static void w(Object message, Throwable t) {
        LOGGER.warn(message, t);
        DriverStation.reportWarning((String) message, true);
    }

    /**
     * Error log
     * @param message The object (or String) message to log
     */
    public static void e(Object message) {
        LOGGER.error(message);
        DriverStation.reportError((String) message, true);
    }

    /**
     * Error log with exception
     * @param message The object (or String) message to log
     * @param t The Throwable to log
     */
    public static void e(Object message, Throwable t) {
        LOGGER.error(message, t);
        DriverStation.reportError((String) message, true);
    }

    /**
     * Returns the logger
     * @return The logger used to log
     */
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Disables logging sets the log level
     * @param doDisable
     */
    public static void disable(boolean doDisable) {
        if (doDisable) {
            Configurator.setLevel("ca.team2706.frc.robot.Robot", Level.ERROR);
        } else {
            Configurator.setLevel("ca.team2706.frc.robot.Robot", Level.ALL);
        }
    }
}
