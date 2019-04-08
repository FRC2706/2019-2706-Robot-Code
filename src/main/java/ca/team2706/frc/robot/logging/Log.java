package ca.team2706.frc.robot.logging;

import ca.team2706.frc.robot.ConnectionState;
import ca.team2706.frc.robot.Robot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

/**
 * Logs to USB and console at levels debug, info, warning, error
 */
public class Log {

    private static final String LOG_FILE_KEY = "logFilename";
    private static final Path LOG_LOCATION = Path.of("/U/logs");

    private static boolean validDate;

    static {
        if (!Instant.now().isAfter(Instant.EPOCH)) {
            System.setProperty(LOG_FILE_KEY, logFile("latest"));
            validDate = false;
        } else {
            System.setProperty(LOG_FILE_KEY, logFile(formattedDate(0)));
            validDate = true;
        }
    }

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
     * Given the current date, formats the date for the start of the program
     *
     * @param secondsAgo The time in seconds since the program started
     * @return The formatted start time of the program
     */
    private static String formattedDate(double secondsAgo) {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Date.from(Instant.now().minus((long) (secondsAgo * 1000), ChronoUnit.MILLIS)));
    }

    /**
     * Sets up the logs for the newly connected driverstation and FMS
     *
     * @param state The new connection state
     */
    public static void setupFMS(ConnectionState state) {
        if (state == ConnectionState.FMS_CONNECT) {
            String eventName = DriverStation.getInstance().getEventName();
            String matchType = DriverStation.getInstance().getMatchType().name();
            int matchNumber = DriverStation.getInstance().getMatchNumber();
            int replayNumber = DriverStation.getInstance().getReplayNumber();
            double matchTime = DriverStation.getInstance().getMatchTime();

            Log.d("FMS: " + eventName + " " + matchType + " " + matchNumber + "-" + replayNumber + " at " + matchTime);

            String logFile = logFile(eventName + "-" + matchType + "-" + matchNumber + "-" + replayNumber);

            if (!logFile.equals(System.getProperty(LOG_FILE_KEY))) {
                validDate = true;

                changeLogFile(logFile);
            }
        } else if (state == ConnectionState.DRIVERSTATION_CONNECT && !validDate) {
            String logFile = logFile(formattedDate(Timer.getFPGATimestamp()));
            validDate = true;

            changeLogFile(logFile);
        }
    }

    /**
     * Changes the log file to a new location
     *
     * @param newFile The new location
     */
    private static void changeLogFile(String newFile) {
        Log.i("Changed log file from " + System.getProperty(LOG_FILE_KEY) + " to " + newFile);

        org.apache.logging.log4j.core.LoggerContext ctx =
                (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);

        try {
            Files.copy(Paths.get(System.getProperty(LOG_FILE_KEY)), Paths.get(newFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setProperty(LOG_FILE_KEY, newFile);

        ctx.reconfigure();
    }

    /**
     * Gets the path to a log file from the name
     *
     * @param name The name of the file to log
     * @return The path with a number at the end if the original alredy exists
     */
    private static String logFile(String name) {
        String fileName = LOG_LOCATION.resolve(name + ".log").toString();

        int i = 1;
        while (Files.exists(Path.of(fileName))) {
            fileName = LOG_LOCATION.resolve(name + "-" + i++ + ".log").toString();
        }

        return fileName;
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
