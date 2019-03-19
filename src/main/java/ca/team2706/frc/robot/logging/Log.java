package ca.team2706.frc.robot.logging;

import ca.team2706.frc.robot.Robot;
import edu.wpi.first.wpilibj.DriverStation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Properties;

/**
 * Logs to USB and console at levels debug, info, warning, error
 */
public class Log {

    private static final Logger LOGGER = LogManager.getLogger(Robot.class.getName());
    private static final String BUILD_INFO_NAME = "/build-info.properties";

    /**
     * Starts logging
     */
    public static void init() {
        Log.i("Starting to log");

        Log.i("Robot Free Memory: " + ((double) Runtime.getRuntime().freeMemory()) / (1024 * 1024) + "MB");
        Log.i("Allocated: " + ((double) Runtime.getRuntime().totalMemory()) / (1024 * 1024) + "MB");
        Log.i("Available: " + ((double) Runtime.getRuntime().maxMemory()) / (1024 * 1024) + "MB");

        logBuildInfo();

        Log.i("Game specific message: " + DriverStation.getInstance().getGameSpecificMessage());
    }

    /**
     * Logs program information
     */
    private static void logBuildInfo() {
        Properties properties = new Properties();
        try {
            properties.load(Log.class.getResourceAsStream(BUILD_INFO_NAME));
        } catch (Exception e) {
            Log.w("Could not load build info");
        }

        Log.i("Project name: " + properties.getProperty("name", "unknown"));
        Log.i("Build timestamp: " + properties.getProperty("timestamp", "unknown"));
        Log.i("Commit hash: " + properties.getProperty("commit.hash", "unknown"));
        Log.i("Commit name: " + properties.getProperty("commit.name", "unknown"));
        Log.i("Commit branch: " + properties.getProperty("commit.branch", "unknown"));
        Log.i("Modified since commit: " + properties.getProperty("commit.modified", "unknown"));

        // Print warning unless we are sure that there are no modifications
        if (!properties.getProperty("commit.modified", "unknown").equalsIgnoreCase("false")) {
            DriverStation.reportWarning("Code may have been modified since last commit", false);
        }
    }

    /**
     * Debug log
     *
     * @param message The object (or String) message to log
     */
    public static void d(Object message) {
        LOGGER.debug(message);
    }

    /**
     * Debug log with exception
     *
     * @param message The object (or String) message to log
     * @param t       The Throwable to log
     */
    public static void d(Object message, Throwable t) {
        LOGGER.debug(message, t);
    }

    /**
     * Info log
     *
     * @param message The object (or String) message to log
     */
    public static void i(Object message) {
        LOGGER.info(message);
    }

    /**
     * Info log with exception
     *
     * @param message The object (or String) message to log
     * @param t       The Throwable to log
     */
    public static void i(Object message, Throwable t) {
        LOGGER.info(message, t);
    }

    /**
     * Warning log
     *
     * @param message The object (or String) message to log
     */
    public static void w(Object message) {
        LOGGER.warn(message);
    }

    /**
     * Warning log with exception
     *
     * @param message The object (or String) message to log
     * @param t       The Throwable to log
     */
    public static void w(Object message, Throwable t) {
        LOGGER.warn(message, t);
    }

    /**
     * Error log
     *
     * @param message The object (or String) message to log
     */
    public static void e(Object message) {
        LOGGER.error(message);
    }

    /**
     * Error log with exception
     *
     * @param message The object (or String) message to log
     * @param t       The Throwable to log
     */
    public static void e(Object message, Throwable t) {
        LOGGER.error(message, t);
    }

    /**
     * Returns the logger
     *
     * @return The logger used to log
     */
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Disables logging sets the log level
     *
     * @param doDisable whether to enable or disable logging
     */
    public static void disable(boolean doDisable) {
        if (doDisable) {
            Configurator.setLevel("ca.team2706.frc.robot.Robot", Level.ERROR);
        } else {
            Configurator.setLevel("ca.team2706.frc.robot.Robot", Level.ALL);
        }
    }
}
